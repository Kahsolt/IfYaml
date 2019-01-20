import parse.*;
import tree.*;
import util.*;

import java.io.File;
import java.util.Date;

@TestCase
public class IfYamlTest extends Test {

    private static String res_dir =         "src\\main\\resources\\";
    private static String test_fn =         res_dir + "test.yml";
    private static String test_dump_fn =    res_dir + "test_dump.yml";
    private static String example_fn =      res_dir + "example.yml";

    private File testFile, testdumpFile, exampleFile;
    private Node root;
    private Tree tree;

    @Override
    protected void initialize() {
        // System.setProperty("user.dir", res_dir);    // WARN: DO NOT enable this!!
        testFile = new File(test_fn);
        assert testFile.exists();
        assert testFile.setReadOnly();
        testdumpFile = new File(test_dump_fn);
        assert testdumpFile.exists();
        exampleFile = new File(example_fn);
        assert exampleFile.exists();
    }
    @Override
    protected void setup() {
        root = new Parser(testFile).parse();
        tree = root.toTree();
    }

    public static void main(String[] args) {
        IfYamlTest test = new IfYamlTest();
        test.register(IfYamlTest.class);
        test.run();
    }

    private void testLexer() {
        Lexer lexer; Token token;

        lexer = new Lexer(testFile);
        while ((token = lexer.nextToken()) != null)
            System.out.println(token);
        System.out.println();

        lexer = new ExLexer(testFile);
        while ((token = lexer.nextToken()) != null)
            System.out.println(token);
    }

    private void testParser() { System.out.println(tree); }

    private void testNode() {
        HashNode hsnode; ListNode lsnode; TextNode txnode;

        hsnode = (HashNode) root;
        txnode = (TextNode) hsnode.getChild("test");
        assert  null == txnode.getValue();

        txnode = (TextNode) hsnode.getChild("multiline");
        assert "line 1 line 2 line 3" == txnode.getValue();
        txnode = (TextNode) hsnode.getChild("text");
        assert "double function(double x) {\n  // do somthing\n}" == txnode.getValue();

        hsnode = (HashNode) root;
        lsnode = (ListNode) hsnode.getChild("hash1");
        hsnode = (HashNode) lsnode.getItem(0);
        lsnode = (ListNode) hsnode.getChild("hash2");
        txnode = (TextNode) lsnode.getItem(0);
        assert "the treasure is deep" == txnode.getValue();

        txnode.setValue("new val");
        assert "new val" == txnode.getValue();
    }

    private void testReadTree() {
        assert null == tree.getString                   ("test");

        assert true == tree.getBoolean                  ("types.bool");
        assert (byte) -1 == tree.getByte                ("types.byte");
        assert 3200 == tree.getShort                    ("types.short");
        assert -2100000000 == tree.getInteger           ("types.int");
        assert 1234321425321L == tree.getLong           ("types.long");
        assert 3.1415926F == tree.getFloat              ("types.float");
        assert 123413.4567654567654 == tree.getDouble   ("types.double");
        assert 'c' == tree.getCharacter                 ("types.char");
        assert "this is a string" == tree.getString     ("types.string");
        assert "line 1 line 2 line 3" == tree.getString ("types.multiline");
        assert "double function(double x) {\n  // do somthing\n}" == tree.getString("types.text");
        assert new Date("1997-01-06 23:12:10") == tree.getDatetime              ("types.datetime");

        assert "Text" == tree.getString         ("list.0");
        assert "val" == tree.getString          ("list.1.key");
        assert "ListOfList" == tree.getString   ("list.2.0");

        assert "Text" == tree.getString         ("hash.name1");
        assert "val" == tree.getString          ("hash.name2.key");
        assert "HashOfList" == tree.getString   ("hash.name3.0");

        assert "the treasure is deep" == tree.getString ("hash1.0.hash2.0");
    }

    private void testModifyTree() {
        tree.removeNode("types");
        assert !tree.exist("types");
        tree.removeNode("hash1.0.hash2");
        assert tree.exist("hash1.0");
        assert !tree.exist("hash1.0.hash2");

        tree.makeNode("color.red");
        assert tree.getNode("color") instanceof HashNode;
        assert tree.getNode("color.red") instanceof HashNode;
        tree.makeNode("color.blue.hue");
        assert !tree.exist("color.blue.hue");
        tree.makeNode("color.0");
        assert !tree.exist("color.0");
        tree.makeNode("item.0");
        assert tree.getNode("item") instanceof HashNode;
        assert tree.getNode("item.0") instanceof ListNode;

        System.out.println(tree);

        tree.removeNode("");
        assert !tree.exist("");
    }

    private void testWriteTree() {
        Tree tree = new Tree();

        tree.set("key1", 123);
        assert 123 == tree.getByte("key1");
        tree.set("key2.key3", 3.14);
        assert 3.14 == tree.getFloat("key2.key3");
        tree.set("list.0", "line1");
        tree.set("list.1", "line2");
        assert "line1" == tree.getString("list.0");
        assert "line2" == tree.getString("list.1");

        System.out.println(tree);
    }

    private void testYamlEngine() {
        Yaml yaml = new Yaml(testFile);
        String text = yaml.dumps();
        yaml.dump(testdumpFile);
        yaml.load(testdumpFile);
        String text2 = yaml.dumps();
        assert text == text2;   // idempotence
    }

    private void testYamlConfig() {
        YamlConfig config = new YamlConfig(exampleFile);
        config.set("I.hate", "Yaml");
        config.set("I.love", "Java");
        config.set("I.love", "Ruby");   // overwrite
        config.set("You.are", "Foolish");
        config.save();

        config.reload();
        assert "Yaml" == config.getString("I.hate");
        assert "Ruby" == config.getString("I.love");
        assert "Foolish" == config.getString("You.are");
        assert null == config.getString("no.where");
        assert "novalue" == config.getString("no.where", "novalue");
    }

}