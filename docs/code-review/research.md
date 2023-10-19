---
layout: default
title: Code Review Research
parent: Code Review
nav_order: 90
permalink: /code-review/research
---

# Code Review Research


## Article

[团队的Code Review实践](https://www.thoughtworks.com/zh-cn/insights/blog/agile-engineering-practices/how-to-code-review](https://www.thoughtworks.com/zh-cn/insights/blog/agile-engineering-practices/how-to-code-review)

- 互相学习，知识共享
- 统一风格，提高代码质量
- 尽早暴露问题，降低修复成本

工具：[https://github.com/MTWGA/thoughtworks-code-review-tools](https://github.com/MTWGA/thoughtworks-code-review-tools)


## Paper

### Expectations, outcomes, and challenges of modern code review

从我们的研究中，我们为开发人员提出以下建议：

- 质量保证：代码审查的期望与实际结果存在不匹配。根据我们的研究，审查通常不如项目成员期望的那样频繁地发现缺陷，尤其是深层次、微妙或“宏观”层面的问题。以这种方式依赖代码审查来确保质量可能会面临风险。
- 理解：当审阅者在上下文和代码方面具有先验知识时，他们能够更快地完成审查并向作者提供更有价值的反馈。团队应该努力提高开发人员的广泛理解（如果更改的作者是唯一的专家，她就没有潜在的审阅者），在使用审查来发现缺陷时，更改的作者应该尽可能包括代码所有者和其他理解代码的人。开发人员表示，当作者在审查中向他们提供上下文和指导时，他们可以更好、更快地做出反应。
- 超越缺陷：现代代码审查提供了除了发现缺陷以外的好处。代码审查可以用于改善代码风格，寻找替代解决方案，增加学习，分享代码所有权等。这应该指导代码审查政策。
- 沟通：尽管有支持代码审查的工具不断增长，开发人员在审查时仍需要比注释更丰富的沟通方式。团队应该提供面对面或至少同步沟通的机制。

### Modern code review: a case study at google

At Google, over 35% of the changes under consideration modify only a single file and about 90% modify fewer than 10 files.
Over 10% of changes modify only a single line of code, and the median number of lines modified is 24. The median change size
is significantly lower than reported by Rigby and Bird for companies such as AMD (44 lines), Lucent (263 lines),
and Bing, Office and SQL Server at Microsoft (somewhere between those boundaries),
but in line for change sizes in open source projects.

在谷歌，有超过35%的正在考虑的更改仅修改一个文件，约90%的更改修改不到10个文件。
超过10%的更改仅修改一行代码，中位数修改的行数为24。中位数的更改大小明显低于Rigby和Bird为像AMD（44行）、Lucent（263行）以及微软的Bing、
Office和SQL Server（在这些边界之间）等公司报告的更改大小，但与开源项目的更改大小保持一致。

### Pre Review

简介一下：业务上下文

# Prompt Demo

## JetBrains Explain AI

You are an senior software developer who can help me understand a commit with business.
Explain this commit.
Do not mention filenames.
Ignore any changes to imports and requires.
Keep the explanation under five sentences. Don't explain changes in test files.

Message: Use freeCompilerArgs += "-Xjsr305=strict"

See https://youtrack.jetbrains.com/issue/KT-41985

Changes:

Index: README.adoc
===================================================================
--- a/README.adoc	(revision b6ed535e3d4b6734a5695c32cc23ce8d5524b3eb)
+++ b/README.adoc	(revision 0906a3d831fea14898e4f0914d6b64531f6c3ade)
@@ -103,7 +103,7 @@
 ----
tasks.withType<KotlinCompile> {
kotlinOptions {
-		freeCompilerArgs = listOf("-Xjsr305=strict")
+		freeCompilerArgs += "-Xjsr305=strict"
     }
     }
 ----
Index: build.gradle.kts
===================================================================
--- a/build.gradle.kts	(revision b6ed535e3d4b6734a5695c32cc23ce8d5524b3eb)
+++ b/build.gradle.kts	(revision 0906a3d831fea14898e4f0914d6b64531f6c3ade)
@@ -39,7 +39,7 @@

tasks.withType<KotlinCompile> {
kotlinOptions {
-		freeCompilerArgs = listOf("-Xjsr305=strict")
+		freeCompilerArgs += "-Xjsr305=strict"
     }
     }
 

## AutoDev

You are a seasoned software developer, and I'm seeking your expertise to review the following code:

- Please provide an overview of the business objectives and the context behind this commit. This will ensure that the code aligns with the project's requirements and goals.
- Focus on critical algorithms, logical flow, and design decisions within the code. Discuss how these changes impact the core functionality and the overall structure of the code.
- Identify and highlight any potential issues or risks introduced by these code changes. This will help reviewers pay special attention to areas that may require improvement or further analysis.
- Emphasize the importance of compatibility and consistency with the existing codebase. Ensure that the code adheres to the established standards and practices for code uniformity and long-term maintainability.
- Lastly, provide a concise high-level summary that encapsulates the key aspects of this commit. This summary should enable reviewers to quickly grasp the major changes in this update.

PS: Your insights and feedback are invaluable in ensuring the quality and reliability of this code. Thank you for your assistance.
Commit Message: feat: update test for samples\n\nCode Changes:\n\nIndex: build.gradle.kts
--- a/build.gradle.kts
+++ b/build.gradle.kts
@@ -6,7 +6,6 @@

group = "cc.unitmesh.untitled"
version = "0.0.1-SNAPSHOT"
-java.sourceCompatibility = JavaVersion.VERSION_17

repositories {
mavenCentral()
@@ -17,6 +16,7 @@
implementation("org.springframework.boot:spring-boot-starter-jdbc")
implementation("org.springframework.boot:spring-boot-starter-data-jdbc")
implementation("org.springframework.boot:spring-boot-starter-data-jpa")
+    testImplementation("junit:junit:4.13.1")

     developmentOnly("org.springframework.boot:spring-boot-devtools")

Index: src/main/java/cc/unitmesh/untitled/demo/controller/BlogController.java
--- a/src/main/java/cc/unitmesh/untitled/demo/controller/BlogController.java
+++ b/src/main/java/cc/unitmesh/untitled/demo/controller/BlogController.java
@@ -19,7 +19,12 @@
this.blogService = blogService;
}

-    // create blog
+    @ApiOperation(value = "Get Blog by id")
+    @GetMapping("/{id}")
+    public BlogPost getBlog(@PathVariable Long id) {
+        return blogService.getBlogById(id);
+    }
+
@ApiOperation(value = "Create a new blog")
@PostMapping("/")
public BlogPost createBlog(@RequestBody CreateBlogRequest request) {
Index: src/main/java/cc/unitmesh/untitled/demo/entity/BlogPost.java
--- a/src/main/java/cc/unitmesh/untitled/demo/entity/BlogPost.java
+++ b/src/main/java/cc/unitmesh/untitled/demo/entity/BlogPost.java
@@ -25,6 +25,10 @@

}

+    public void setId(Long id) {
+        this.id = id;
+    }
+
public Long getId() {
return this.id;
}
Index: src/test/java/cc/unitmesh/untitled/demo/controller/BlogControllerTest.java
--- a/src/test/java/cc/unitmesh/untitled/demo/controller/BlogControllerTest.java
+++ b/src/test/java/cc/unitmesh/untitled/demo/controller/BlogControllerTest.java
@@ -1,19 +1,43 @@
package cc.unitmesh.untitled.demo.controller;

+import cc.unitmesh.untitled.demo.entity.BlogPost;
+import cc.unitmesh.untitled.demo.repository.BlogRepository;
import org.junit.jupiter.api.Test;
+import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
+import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
+import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;

+import java.util.Optional;
+
+import static org.hamcrest.Matchers.containsString;
+import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
+import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
+import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
+
@SpringBootTest
+@AutoConfigureMockMvc
class BlogControllerTest {

-    @SuppressWarnings("SpringJavaInjectionPointsAutowiringInspection")
     @Autowired
     private MockMvc mockMvc;
+
+    @MockBean
+    private BlogRepository blogRepository;
+
@Test
-    void should_get_blog_one_when_has_blog() throws Exception {
+    public void should_return_correct_blog_information_when_post_item() throws Exception {
+        BlogPost mockBlog = new BlogPost("Test Title", "Test Content", "Test Author");
+        mockBlog.setId(1L);

+        Mockito.when(blogRepository.findById(1L)).thenReturn(Optional.of(mockBlog));

+        mockMvc.perform(get("/blog/1"))
+                .andExpect(status().isOk())
+                .andExpect(content().string(containsString("Test Title")))
+                .andExpect(content().string(containsString("Test Content")));
  }
  -}
  \ No newline at end of file
  +}
+


