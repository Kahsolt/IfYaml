package parse;

public enum TokenType {
    ITEM,       // '- '
    KEY,        // 'id: '
    VALUE,      // literal/quoted string
    COMMENT,    // '#comment'

    // for ExLexer after value merging
    VALUE_LINE,
    VALUE_MULTILINE,
    VALUE_TEXT,

    // auxiliar symbols
    S_BOND,       // '|', modifier of VALUE, keep literal
    S_LINE,       // '>', modifier of VALUE, keep ending '\n', FIXME: currently functions as '|'
}