package cc.unitmesh.genius

import cc.unitmesh.cf.code.GitCommand
import cc.unitmesh.cf.code.GitDiffer
import cc.unitmesh.genius.domain.review.CodeReviewAction
import cc.unitmesh.genius.domain.review.ReviewOption
import cc.unitmesh.genius.project.GeniusProject
import com.github.ajalt.clikt.core.CliktCommand
import com.github.ajalt.clikt.parameters.options.default
import com.github.ajalt.clikt.parameters.options.flag
import com.github.ajalt.clikt.parameters.options.option
import org.slf4j.LoggerFactory
import org.changelog.CommitParser
import org.changelog.ParserOptions
import com.charleskorn.kaml.PolymorphismStyle
import com.charleskorn.kaml.Yaml
import com.charleskorn.kaml.YamlConfiguration
import kotlinx.serialization.serializer
import java.io.File
import kotlin.io.path.Path
import kotlin.io.path.exists
import kotlin.io.path.readText

/**
 * The `CodeReviewCommand` class is a command-line interface for performing code reviews using AIGC (Artificial Intelligence for Code Review).
 * It extends the `CliktCommand` class from the `clikt` library and provides options and functionality for reviewing code in a Git repository.
 *
 * @property repo The path to the Git repository. It can be a local file path or a Git URL. Default value is "." (current directory).
 * @property branch The name of the Git branch to review. Default value is "master".
 * @property sinceCommit The revision of the first commit to include in the review. Default value is an empty string, which means the earliest commit in the repository.
 * @property untilCommit The revision of the last commit to include in the review. Default value is an empty string, which means the latest commit in the repository.
 * @property commitMessageOptionFile The path to the commit message option file. Default value is an empty string, which means no commit message options are used.
 * @property verbose A flag indicating whether to enable verbose output. Default value is false.
 * @property configFile The path to the configuration file. Default value is "devops-genius.yml".
 * @property project The `GeniusProject` instance representing the project being reviewed. It is lazily initialized based on the configuration file or the repository path.
 *
 * @constructor Creates a new instance of `CodeReviewCommand`.
 *
 * @see CliktCommand
 * @see GeniusProject
 */
class CodeReviewCommand : CliktCommand(help = "Code Review with AIGC") {
    private val repo by option(help = "Git repository path. Use local file path, or Git Url").default(".")
    private val branch by option(help = "Git branch name").default("master")
    private val sinceCommit by option(help = "Begin commit revision").default("")
    private val untilCommit by option(help = "End commit revision. Aka latest").default("")
    private val commitMessageOptionFile by option(help = "commit message option file").default("")
    private val verbose by option(help = "verbose").flag(default = false)
    private val configFile by option(help = "config file").default("devops-genius.yml")

    private val project: GeniusProject by lazy {
        val path = Path(configFile)
        if (path.exists()) {
            logger.info("load project from config file: ${path.toAbsolutePath()}")
            GeniusProject.fromYml(path.readText())
        } else {
            logger.info("load project from repo: $repo")
            GeniusProject(path = repo)
        }
    }

    override fun run() {
        val defaultLatestIds = GitCommand().latestCommitHash(2).stdout.split(System.lineSeparator())
        val sinceCommit = sinceCommit.ifEmpty {
            defaultLatestIds[defaultLatestIds.lastIndex]
        }
        val untilCommit = untilCommit.ifEmpty {
            defaultLatestIds[0]
        }

        val diff = GitDiffer(repo, branch)
        val repositoryUrl = diff.gitRepositoryUrl()
        logger.info("get repository url from .git/config: $repositoryUrl")
        project.repoUrl = repositoryUrl

        val reviewOption = ReviewOption(
            path = repo,
            repo = repositoryUrl,
            branch = branch,
            sinceCommit = sinceCommit,
            untilCommit = untilCommit,
            commitOptionFile = commitMessageOptionFile,
            verbose = verbose,
            project = project,
        )

        val commitParser = createCommitParser()
        CodeReviewAction(project, reviewOption, diff, commitParser).execute()
    }

    private fun createCommitParser(): CommitParser {
        val parserOptions = if (commitMessageOptionFile.isNotEmpty() and Path(commitMessageOptionFile).exists()) {
            val commitMsgOptionText = File(commitMessageOptionFile).readText()
            ParserOptions.fromString(commitMsgOptionText)
        } else {
            ParserOptions.defaultOptions()
        }

        if (parserOptions == null) {
            throw Exception("commit message option file is not valid: $commitMessageOptionFile")
        }

        return CommitParser(parserOptions)
    }

    companion object {
        private val logger = LoggerFactory.getLogger(CodeReviewCommand::class.java)!!
    }
}

private fun ParserOptions.Companion.fromString(content: String): ParserOptions? {
    return try {
        val conf = YamlConfiguration(polymorphismStyle = PolymorphismStyle.Property)
        val userOptions = Yaml(configuration = conf).decodeFromString<ParserOptions>(serializer(), content)
        // merge default options
        defaultOptions().copy(
            commentChar = userOptions.commentChar ?: defaultOptions().commentChar,
            mergePattern = userOptions.mergePattern ?: defaultOptions().mergePattern,
            mergeCorrespondence = userOptions.mergeCorrespondence ?: defaultOptions().mergeCorrespondence,
            headerPattern = userOptions.headerPattern ?: defaultOptions().headerPattern,
            breakingHeaderPattern = userOptions.breakingHeaderPattern
                ?: defaultOptions().breakingHeaderPattern,
            headerCorrespondence = userOptions.headerCorrespondence ?: defaultOptions().headerCorrespondence,
            revertPattern = userOptions.revertPattern ?: defaultOptions().revertPattern,
            revertCorrespondence = userOptions.revertCorrespondence ?: defaultOptions().revertCorrespondence,
            fieldPattern = userOptions.fieldPattern ?: defaultOptions().fieldPattern,
            noteKeywords = userOptions.noteKeywords ?: defaultOptions().noteKeywords,
            notesPattern = userOptions.notesPattern ?: defaultOptions().notesPattern,
            issuePrefixes = userOptions.issuePrefixes ?: defaultOptions().issuePrefixes,
            issuePrefixesCaseSensitive = userOptions.issuePrefixesCaseSensitive
                ?: defaultOptions().issuePrefixesCaseSensitive,
            referenceActions = userOptions.referenceActions ?: defaultOptions().referenceActions,
        )

    } catch (e: Exception) {
        e.printStackTrace()
        null
    }
}
