# DevOpsGenius

<p align="center">
  <a href="https://github.com/unit-mesh/chocolate-factory">
    <img src="https://img.shields.io/badge/powered_by-chocolate_factory-blue?logo=kotlin&logoColor=fff" alt="Powered By" />
  </a>  
</p>

DevOpsGenius 旨在以结合 AIGC 改进软件开发和运维的 DevOps 实践。它结合了人工智能和自动化技术，
为团队提供自动的代码评审和拉取请求处理功能，提高开发流程的效率和质量。

- **自动代码评审**：DevOpsGenius使用先进的代码分析和静态分析技术，自动检测潜在的问题、错误和不规范的代码风格，并提供有针对性的建议和改进意见。
- **智能拉取请求处理**：DevOpsGenius能够智能地审查和处理拉取请求。它自动识别代码变更、冲突和合并请求，并以自动化的方式进行验证、测试和部署流程，以确保高质量的代码交付。
- **持续集成和持续交付**：DevOpsGenius集成了强大地持续集成和持续交付功能，能够与常用的构建工具和部署管道无缝集成，实现自动化的构建、测试和部署流程。
- **智能报告和统计**：DevOpsGenius生成详细的报告和统计数据，展示代码质量、团队绩效和项目进度等关键指标。这些洞察力有助于团队进行数据驱动的决策，优化开发流程和资源分配。

## CI/CD

Jenkins file: [https://www.jenkins.io/doc/pipeline/examples/](https://www.jenkins.io/doc/pipeline/examples/)

## CodeReview Design

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
