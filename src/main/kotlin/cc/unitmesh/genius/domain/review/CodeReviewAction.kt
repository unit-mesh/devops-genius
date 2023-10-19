package cc.unitmesh.genius.domain.review

import cc.unitmesh.cf.code.GitDiffer
import cc.unitmesh.genius.context.GeniusAction
import cc.unitmesh.genius.devops.Issue
import cc.unitmesh.genius.project.GeniusProject
import org.changelog.CommitParser
import org.slf4j.LoggerFactory

class CodeReviewAction(
    val project: GeniusProject,
    private val option: ReviewOption,
    private val diff: GitDiffer,
    private val commitParser: CommitParser,
) : GeniusAction {
    private val promptFactory = CodeReviewPromptFactory()
    private val context = CodeReviewContext()

    /**
     * Executes the full process of reviewing commits.
     *
     * This method performs the following steps:
     * 1. Retrieves the commit messages between the specified `sinceCommit` and `untilCommit`.
     * 2. Parses the commit messages to obtain the Commit objects and their references.
     * 3. Filters out the commits that do not require review based on the project's configuration.
     * 4. Retrieves the titles of the User Stories associated with the referenced issues.
     * 5. Generates a patch file to obtain the diff content and filters out files that do not require review.
     * 6. Creates a prompt to collect the review results.
     * 7. Sends the prompt to the project's connector for completion.
     * 8. Prints the completion results.
     *
     * @return An empty string.
     */
    override fun execute(): Any {
        // 获取 sinceCommit 到 untilCommit 之间的 commit message
        val commitMessages = diff.commitMessagesBetween(option.sinceCommit, option.untilCommit)
        context.fullMessage = commitMessages.map { it.value }.joinToString(System.lineSeparator())

        // 解析 commit message 为 Commit 对象，以获取其中的 references
        val parsedMsgs = commitMessages.map {
            commitParser.parse(it.value)
        }

        // 从配置文件中读取，并过滤掉不需要 review 的 commit，诸如 chore、ci, docs 等
        // 如果没有配置，则全部需要 review
        val filterCommits = parsedMsgs.filter {
            if (it.meta.containsKey("type")) {
                val type = it.meta["type"] as String
                project.commitLog?.isIgnoreType(type) ?: true
            } else {
                true
            }
        }

        if (option.verbose) {
            println("parsedMsgs: $parsedMsgs")
            println("filterCommits: $filterCommits")
        }

        if (filterCommits.isEmpty()) {
            logger.info("commit don't need review")
        }

        // 获取所有的 issue id，以获取对应的 User Story 的标题信息，作为业务的上下文使用
        val storyIds = parsedMsgs.map { it.references }.flatten()
        val stories = storyIds.map {
            try {
                project.fetchStory(it.issue)
            } catch (e: Exception) {
                logger.error("fetch story error: $it", e)
                null
            }
        }.filterNotNull()

        context.businessContext = stories.joinToString(System.lineSeparator(), transform = Issue::title)

        // 生成 patch 文件，以获取 diff 的内容，并过滤掉不需要 review 的文件，诸如 .json、.yaml 等
        val patch = diff.patchBetween(option.sinceCommit, option.untilCommit)
        context.changes = patch.filter {
            project.commitLog?.isIgnoreFile(it.key) ?: true
        }.map {
            it.value.content
        }.joinToString(System.lineSeparator())

        // 生成 prompt，以获取 review 的结果
        promptFactory.context = context
        val messages = promptFactory.createPrompt(project, "")

        logger.info("messages: $messages")

        project.connector().streamCompletion(messages).blockingForEach {
            print(it)
        }

        return ""
    }

    companion object {
        val logger = LoggerFactory.getLogger(CodeReviewAction::class.java)
    }
}
