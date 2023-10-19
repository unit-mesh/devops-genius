---
layout: default
title: Code Review
nav_order: 90
has_children: true
permalink: /code-review
---


# CodeReview Design

DevOps 平台：[https://devops.phodal.com](https://devops.phodal.com/checklists/code-review) 的代码检视检查清单。

- 业务逻辑。
    - 文档信息。
- 代码逻辑。
    - 代码是否符合编码规范。
    - 代码是否符合设计规范。
- 需求信息。标准的提交格式：
    - feat(devops): init first review command #8

## Prompt 策略

1. 如果变更的代码行数少，则只审核业务含义 —— 根据提交信息，解析对应的 story 名称，然后进行检查。
2. 根据变更的代码，生成对应的代码信息，作为上下文的一部分。
3. 如果变更的行数多，则需要进行代码逻辑的检查，以及对应的语法检查。
4. 如果单次变更的行数过多，则需要进行拆分。

### 条件过滤

- 根据 commit id

### 重写比例

1. 如果重写比例过高，则需要进行代码逻辑的检查，结合更多的上下文。（重写比例：重写的代码行数 / 总代码行数，建议小于 0.5，行数大于
   30 / 2 行）
2. 当出现重大变化时，建议进行人工检查。

### Patch 优化

1. 如果变更的代码行数少，则只审核业务含义。
2. 如果超过 10 个文件，则需要拆分。
    - 忽略数据文件。
    - 忽略配置文件。
3. 处理文件目录移动，文件重命名的情况。（即忽略文件的变更）
4. 使用传统工具，检测语法问题，诸如 pre-commit 的情况。
