# IfYaml

    A simplified yaml library in java that only 
    supports a limited minimized YAML grammar...


## NOTICE:
  - IfYaml is a micro-subset of compatible YAML, so please take care of your INDENTS if you read in other YAML file or edit manually
  - This library is meant to guarantee the idempotence of it's IO (see `IfYamlTest.testDumper() / IfYamlTest.testBuilder()`)

### Quick Start
```java
public class IfYamlTest extends Test { 
    private void testYaml() {
        Yaml config = new Yaml(exampleFile);
        config.set("I.hate", "Yaml");
        config.set("I.love", "Java");
        config.set("I.love", "Ruby");   // overwrite
        config.set("You.are", "Foolish");
        config.save();

        config.reload();
        assert "Yaml".equals(config.getString("I.hate"));
        assert "Ruby".equals(config.getString("I.love"));
        assert "Foolish".equals(config.getString("You.are"));
        assert null == config.getString("no.where");
        assert "novalue".equals(config.getString("no.where", "novalue"));
    }
}
```
You can learn more from [test.yml](/src/main/resources/test.yml) and [IfYamlTest.java](/src/test/java/IfYamlTest.java)

### Developer Quick Reference
```markdown
  - parse
    - Parser: parse YAML string to create AST tree
    - Dumpder: dump AST tree to YAML string
    - Builder: build any structurized Object to AST tree
  - tree
    - xNode: low level API concerning AST tree nodes
    - Tree: high level API operating on AST tree by path
  - util
    - StringEx: easydoing some string/text editing work 
    - Test*: hyperlight unittest framework :(
  - Yaml: the frontend = parser + AST tree + dumper
```

### features
  - accept minimized subset of pure block-style YAML grammar, indent & alignment are VERY sensitive; grammar in short:
    - types
      - natively support 10 data types: bool/byte/char/short/int/long/float/double/string*/datetime
      - string* type stands for 3 variants: string/multiline/text
    - structures
      - hash: recoginzed by `keyname: `
      - list: recoginzed by `- `
    - full-line comment (retention supported)
        - 注释只能是整行的，前导空格用于计数缩进层次，即`regex("^ *#.*$")`，e.g. "# This is a full line."
        - 注释向下黏著于最近的哈希键/列表项/值，且必须与被黏着的键项统一缩进；找不到合适黏着关系的孤儿注释会被删除掉
        - 多行文本即管道符号后文本中的注释符号不起作用，优先解释为多行文本内容而不是单行注释
        - 不建议在多段文本中使用分散的注释，否则会被聚合起来上移
  - CRUD operations via path (e.g. "Database.0.ConnectionPool.MaxConnection")
    - HashNode: section name is the key
    - ListNode: section name is the index number

----

by kahsolt
2019/01/15