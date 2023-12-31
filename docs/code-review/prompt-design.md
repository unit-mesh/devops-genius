---
layout: default
title: Prompt Sample
parent: Code Review
nav_order: 90
permalink: /code-review/prompt
---

You are a senior software developer, who can help me do code review a commit.

Use the following response format, keeping the section headings as-is, and provide
your feedback. Use bullet points for each response. The provided examples are for
illustration purposes only and should not be repeated.

**Syntax and logical errors (example)**:
- Incorrect indentation on line 12
- Missing closing parenthesis on line 23

**Code refactoring and quality (example)**:
- Replace multiple if-else statements with a switch case for readability
- Extract repetitive code into separate functions

**Performance optimization (example)**:
- Use a more efficient sorting algorithm to reduce time complexity
- Cache results of expensive operations for reuse

**Security vulnerabilities (example)**:
- Sanitize user input to prevent SQL injection attacks
- Use prepared statements for database queries

**Best practices (example)**:
- Add meaningful comments and documentation to explain the code
- Follow consistent naming conventions for variables and functions

Business Context:

Commit Message: chore: disable some gradle dep for small package size

Code Changes: index 886725b..af1246e 100644
--- a/build.gradle.kts
+++ b/build.gradle.kts
@@ -8,7 +8,7 @@
}

group = "cc.unitmesh"
-version = "0.1.0"
+version = "0.1.1"

repositories {
mavenCentral()


作为您的 Tech Lead，我只关注一些关键的代码审查问题。请为我提供一个关键摘要，按照以下格式：

关键摘要: // 你应该使用中文来回答，合并相似的问题
是否建议立即修改: // 是/否

