package tree;

public enum TextType {
    LINE,       // single line string
    MULTILINE,  // multiple line or leading by '>', join by ' '
    TEXT,       // leading by '|', keep literal
}
