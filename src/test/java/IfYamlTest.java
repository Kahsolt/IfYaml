import parse.*;
import tree.*;
import util.*;

import java.io.File;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

@TestCase
public class IfYamlTest extends Test {  // inheritance is optional

    private String res_dir = "src\\main\\resources\\";

    private File file;  // text.yml
    private Node root;
    private Tree tree;

    @Override
    protected void initialize() {
        // System.setProperty("user.dir", res_dir);    // WARN: DO NOT enable this!!
        file = new File(res_dir + "test.yml");
        assert file.exists();
        assert file.setReadOnly();
    }
    @Override
    protected void setup() {
        root = new Parser(file).parse();
        tree = root.toTree();
    }

    public static void main(String[] args) {
        IfYamlTest test = new IfYamlTest();
        test.register(IfYamlTest.class);
        test.run();
    }

    private void testLexer() {
        for (Token token : new Lexer(file).tokenize())
            System.out.println(token);
        System.out.println();

        for (Token token : new ExLexer(file).tokenize())
            System.out.println(token);
    }

    private void testParser() { System.out.println(tree); }

    private void testNode() {
        HashNode hsnode; ListNode lsnode; TextNode txnode;

        hsnode = (HashNode) root;
        txnode = (TextNode) hsnode.getChild("test");
        assert "".equals(txnode.getValue());

        hsnode = (HashNode) root;
        hsnode = (HashNode) hsnode.getChild("types");
        txnode = (TextNode) hsnode.getChild("multiline");
        assert "line 1 line 2 line 3".equals(txnode.getValue());
        txnode = (TextNode) hsnode.getChild("text");
        assert "double function(double x) {\n  // do something\n}".equals(txnode.getValue());

        hsnode = (HashNode) root;
        lsnode = (ListNode) hsnode.getChild("hash1");
        hsnode = (HashNode) lsnode.getItem(0);
        lsnode = (ListNode) hsnode.getChild("hash2");
        txnode = (TextNode) lsnode.getItem(0);
        assert "the treasure is deep".equals(txnode.getValue());

        txnode.setValue("new val");
        assert "new val".equals(txnode.getValue());
    }

    private void testReadTree() {
        assert "".equals(tree.getString                 ("test"));
        assert "the value".equals(tree.getString        ("the key"));

        assert true == tree.getBoolean                  ("types.bool");
        assert (byte) -1 == tree.getByte                ("types.byte");
        assert 3200 == tree.getShort                    ("types.short");
        assert -2100000000 == tree.getInteger           ("types.int");
        assert 1234321425321L == tree.getLong           ("types.long");
        assert 3.1415926F == tree.getFloat              ("types.float");
        assert 123413.4567654567654 == tree.getDouble   ("types.double");
        assert 'c' == tree.getCharacter                 ("types.char");
        assert "this is a string".equals(tree.getString ("types.string"));
        assert "line 1 line 2 line 3".equals(tree.getString("types.multiline"));
        assert "double function(double x) {\n  // do something\n}".equals(tree.getString("types.text"));
        try { assert new SimpleDateFormat().parse("1997-01-06 23:12:10")
                .equals(tree.getDatetime("types.datetime"));
        } catch (ParseException ignore) { }

        assert "Text".equals(tree.getString         ("list.0"));
        assert "val".equals(tree.getString          ("list.1.key"));
        assert "ListOfList".equals(tree.getString   ("list.2.0"));
        assert "Text".equals(tree.getString         ("hash.name1"));
        assert "val".equals(tree.getString          ("hash.name2.key"));
        assert "HashOfList".equals(tree.getString   ("hash.name3.0"));
        assert "the treasure is deep".equals(tree.getString ("hash1.0.hash2.0"));
    }

    private void testModifyTree() {
        tree.removeNode("types");
        assert !tree.exist("types");
        tree.removeNode("hash1.0.hash2");
        assert tree.exist("hash1.0");
        assert !tree.exist("hash1.0.hash2");

        tree.makeNode("color.red");
        assert tree.getNode("color") instanceof HashNode;
        assert tree.getNode("color.red") instanceof TextNode;
        tree.makeNode("color.blue.hue");
        assert tree.getNode("color.blue") instanceof HashNode;
        assert tree.exist("color.blue.hue");
        tree.makeNode("item.0.what");
        assert tree.getNode("item") instanceof ListNode;
        assert tree.getNode("item.0") instanceof HashNode;

        System.out.println(tree);

        tree.removeNode("");
        assert !tree.exist("");
    }

    private void testWriteTree() {
        Tree tree = new Tree();

        tree.set("key1", 123);
        assert 123 == tree.getByte("key1");
        tree.set("key2.key3", 3.14);
        assert 3.14F - tree.getFloat("key2.key3") < 1e-8;
        tree.set("list.0", "line1");
        tree.set("list.1", "line2");
        assert "line1".equals(tree.getString("list.0"));
        assert "line2".equals(tree.getString("list.1"));

        System.out.println(tree);
    }

    private void testDumper() {
        File testdumpFile = new File(res_dir + "test_dump.yml");
        Dumper dumper = new Dumper(root);
        String text = dumper.dumps();
        dumper.dump(testdumpFile);

        assert text.equals(new Dumper(new Parser(testdumpFile).parse()).dumps());
    }

    private void testBuilder() {
        List struct = new ArrayList<Object>() {{
            add("Text");
            add(new HashMap<String, String>() {{ put("key", "val"); }});
            add(new ArrayList<String>(){{ add("ListOfList"); }});
        }};

        Builder builder = new Builder(struct);
        tree = builder.build().toTree();
        System.out.println(tree);
        System.out.println();
        String text = new Dumper(tree).dumps();
        System.out.println(text);

        // idempotence
        assert text.equals(new Dumper(new Parser(text).parse()).dumps());
    }

    private void testYaml() {
        File exampleFile = new File(res_dir + "example.yml");

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

        assert exampleFile.delete();
    }

    private void testMalformat() {
        File malformatFile = new File(res_dir + "test_malformat.yml");
        assert malformatFile.exists();

        for (Token token : new Lexer(malformatFile).tokenize())
            System.out.println(token);
        System.out.println();
        for (Token token : new ExLexer(malformatFile).tokenize())
            System.out.println(token);

        root = new Parser(malformatFile).parse();
        System.out.println(root);
        System.out.println(new Dumper(root).dumps());
    }

}