# IfYaml

    A simplified yaml library in java that only 
    supports a limited minimized YAML grammar...


### features
  - accept minimized subset of pure block-style YAML grammar, indent & alignment VERY sensitive
  - full-line comment retention supported (e.g. "# This is a full line.")
  - handle CRUD by path (e.g. "Database.0.ConnectionPool.MaxConnection")

#### comment retention
  - 注释只能是整行的、以#开始，前导空格用于计数缩进层次，即`regex("^ *#.*$")`
  - 注释向下黏著于最近的哈希键/列表项/值，且必须与被黏着的键项统一缩进，找不到合适黏着关系的孤儿注释会被堆积到底部
  - 多行文本即管道符号后文本中的注释符号不起作用，优先解释为多行文本内容而不是单行注释
  - 不建议在多段文本中使用分散的注释，否则会被聚合起来上移

----

by kahsolt
2019/01/15