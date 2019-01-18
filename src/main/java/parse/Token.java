/*
 *  * Copyright (c)
 *  * Author : Kahsolt <kahsolt@qq.com>
 *  * Create Date : 2019-1-18
 *  * Update Date : 2019-1-18
 *  * Version : v0.1
 *  * License : GPLv3
 *  * Description : 单词结构...
 */

package parse;

public class Token {

    public int indent = -1;
    public String value = null;
    public TokenType type = null;

    public Token() { }
    public Token(TokenType type, int indent) { this.type = type; this.indent = indent; }
    public Token(TokenType type, int indent, String value) { this(type, indent); this.value = value; }

    public boolean isKey() { return type == TokenType.KEY; }
    public boolean isItem() { return type == TokenType.ITEM; }
    public boolean isValue() { return type == TokenType.VALUE; }
    public boolean isComment() { return type == TokenType.COMMENT; }

    @Override
    public String toString() { return String.format("<Token [%s] %d: %s>", type, indent, value == null ? "" : String.format("'%s'", value)); }

}