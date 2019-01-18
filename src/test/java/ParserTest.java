import parse.ExLexer;
import parse.Lexer;
import parse.Parser;
import parse.Token;
import tree.*;

import java.io.File;

public class ParserTest {

    private String fp = "src\\main\\resources\\test.yml";
    private File file = new File(fp);

    public static void main(String[] args) {
        ParserTest test = new ParserTest();
//        test.testLexer();
//        test.testParser();
//        test.testReadTree();
        test.testWriteTree();
    }

    private void testLexer() {
        Lexer lexer; Token token;

        lexer = new Lexer(file);
        while ((token = lexer.nextToken()) != null)
            System.out.println(token);
        printSectionSpliter();

        lexer = new ExLexer(file);
        while ((token = lexer.nextToken()) != null)
            System.out.println(token);
        printSectionSpliter();
    }

    private void testParser() {
        Parser parser = new Parser(file);
        Node root = parser.parse();
        System.out.println(root);
        printSectionSpliter();

        HashNode hsnode; ListNode lsnode; ScalarNode scnode;

        hsnode = (HashNode) root;
        hsnode = (HashNode) hsnode.getChild("test");
        scnode = (ScalarNode) hsnode.getChild("text");
        System.out.println(scnode.getValue());
        scnode = (ScalarNode) hsnode.getChild("longtext");
        System.out.println(scnode.getValue());

        hsnode = (HashNode) root;
        hsnode = (HashNode) hsnode.getChild("test");
        lsnode = (ListNode) hsnode.getChild("dep1");
        hsnode = (HashNode) lsnode.getItem(0);
        lsnode = (ListNode) hsnode.getChild("dep2");
        scnode = (ScalarNode) lsnode.getItem(0);
        System.out.println(scnode.getValue());

        printSectionSpliter();
    }

    private void testReadTree() {
        Parser parser = new Parser(file);
        Tree tree = new Tree(parser.parse());

        System.out.println(tree.getBoolean  ("test.types.bool"));
        System.out.println(tree.getByte     ("test.types.byte"));
        System.out.println(tree.getShort    ("test.types.short"));
        System.out.println(tree.getInteger  ("test.types.int"));
        System.out.println(tree.getLong     ("test.types.long"));
        System.out.println(tree.getFloat    ("test.types.float"));
        System.out.println(tree.getDouble   ("test.types.double"));
        System.out.println(tree.getCharacter("test.types.char"));
        System.out.println(tree.getString   ("test.types.string"));
        System.out.println(tree.getDatetime ("test.types.datetime"));

        System.out.println(tree.getString   ("test.text"));
        System.out.println(tree.getString   ("test.longtext"));

        System.out.println(tree.getString   ("test.list.0"));
        System.out.println(tree.getString   ("test.list.1.key"));
        System.out.println(tree.getString   ("test.list.2.0"));

        System.out.println(tree.getString   ("test.dep1.0.dep2.0"));

        printSectionSpliter();
    }

    private void testWriteTree() {
        Tree tree = new Tree();
        tree.set("key1", 123);
        System.out.println(tree.getString("key1"));
        tree.set("key2", "asdas");
        tree.set("key3.key", 3.14);
        System.out.println(tree.getString("key3.key"));

        System.out.println(tree);

        printSectionSpliter();
    }

    private void printSectionSpliter() {
        System.out.println(String.format("%" + 5 + "d", 111111).replace("1", "==========="));
    }
}