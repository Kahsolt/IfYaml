package parse;

public class SyntaxErrorException extends RuntimeException {

    public SyntaxErrorException() { super(); }
    public SyntaxErrorException(String message) { super(message); }
    public SyntaxErrorException(Throwable cause) { super(cause); }
    public SyntaxErrorException(String message, Throwable cause) { super(message, cause); }

}