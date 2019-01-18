package parse;

public enum TokenType {
    ITEM,       // '- '
    KEY,        // 'id: '
    VALUE,      // literal/quoted string
    COMMENT,    // '#comment'

    BOND,       // '|', special case indicator of VALUE
}