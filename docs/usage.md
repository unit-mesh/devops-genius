---
layout: default
title: Usage
nav_order: 1
permalink: /usage
---

# Usage

## QuickStart

1. 下载 CLI：https://github.com/unit-mesh/devops-genius/releases
2. 配置 `devops-genius.yml`，参考 [配置文件](#配置文件)

### 配置文件

```yaml
name: "ChocolateFactory"
repo: "." # relative to the project root, or GitHub repo, like "unitmesh/chocolate-factory"

# 配置对应的模型
connection: connection.yml

# store the changelog in the repo
store:
  indexName: "unitmesh/chocolate-factory" # default to github repo

# 用于获取需求，关联到对应的 issue/用户故事/特征
kanban:
  type: GitHub
  token: "xxx"

# 根据 commit message 忽略对应的提交和文件
commitLog:
  ignoreType: [ "chore", "docs", "style" ]
  ignorePatterns:
    - "**/*.md"
    - "**/*.json"
    - "**/*.yml"
    - "**/*.yaml"
    - "**/*.vm"
    - ".gitignore"
```

### connection.yml 用于配置对应的模型

示例：

```yaml
name: open_ai_connection
type: OpenAI
configs:
  api-host: https://api.aios.chat/ # 如果有的话
secrets:
  api-key: "xxx"
```

更详细的模型支持见：[https://framework.unitmesh.cc/prompt-script/connection-config](https://framework.unitmesh.cc/prompt-script/connection-config)
