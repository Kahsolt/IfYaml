import parse.Dumper;
import parse.Parser;
import tree.Node;
import util.Test;
import util.TestCase;

import java.io.File;

public class ConfigTest {

    private static String fn = "src\\main\\resources\\config.yml";
    private File file;

    private void initialize() {
        file = new File(fn);
        assert file.exists();
    }

    public static void main(String[] args) {
        Test test = new Test();
        test.register(ConfigTest.class);
        test.run();
    }

    @TestCase
    private void parseConfig() {
        Parser parser = new Parser(file);
        Node root = parser.parse();
        System.out.println(root);

        Dumper dumper = new Dumper(root);
        System.out.println(dumper.dumps());
    }

}