package parse;

public class SyntaxErrorException extends Exception {

    public SyntaxErrorException() { super(); }
    public SyntaxErrorException(int lineno, int offset, String line) { super(String.format("Syntax error: (%d:%d) '%s'", lineno, offset, line)); }

}