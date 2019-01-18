/*
 *  * Copyright (c)
 *  * Author : Kahsolt <kahsolt@qq.com>
 *  * Create Date : 2019-1-18
 *  * Update Date : 2019-1-18
 *  * Version : v0.1
 *  * License : GPLv3
 *  * Description : 包装的词法分析器，重组tokens：合并多行的值、调整注释位置...
 */

package parse;

import java.io.File;
import java.util.ArrayList;
import java.util.LinkedList;

public class ExLexer extends Lexer {

    public ExLexer() { super(); }
    public ExLexer(String text) { super(text); }
    public ExLexer(File file) { super(file); }

    // reformed token buffer, typically several comment and one value token
    private LinkedList<Token> token_buff = new LinkedList<>();
    private Token prev_token = null;    // buffer last key/item token, for indent inference
    private Token next_token = null;    // buffer next token, 'cos don't have unreadToken()

    @Override
    public Token nextToken() {
        if (!token_buff.isEmpty()) return token_buff.pollFirst();
        Token token;
        if (next_token != null) { token = next_token; next_token = null; }
        else token = super.nextToken();
        if (token == null) return null;

        switch (token.type) {
            case VALUE:
                ArrayList<String> values = new ArrayList<>();
                int indent = token.indent;
                values.add(token.value);
                while ((token = super.nextToken()) != null) {
                    if (token.indent <= prev_token.indent) break;

                    if (token.isComment())      // comments are ok
                        token_buff.addLast(token);
                    else if (token.isValue())   // values need merge
                        values.add(token.value);
                    else break;
                }
                next_token = token;   // stash key token
                token_buff.addLast(new Token(TokenType.VALUE, indent, String.join(" ", values)));
                return nextToken();
            case BOND:
                ArrayList<Token> value_tokens = new ArrayList<>();
                token = super.nextToken();
                indent = token.indent;
                if (token.isComment() || token.isValue()) {
                    value_tokens.add(token);
                    while ((token = super.nextToken()) != null) {
                        if (token.indent <= prev_token.indent) break;
                        value_tokens.add(token);
                    }
                    StringBuilder sb = new StringBuilder(100);
                    for (Token valtok : value_tokens) {
                        sb.append(valtok.indent <= indent ? ""
                                : String.format("%" + (valtok.indent - indent) + "d", 0)
                                    .replace("0", " "));
                        if (valtok.isComment()) sb.append('#');
                        sb.append(valtok.value);
                        sb.append('\n');
                    }
                    return new Token(TokenType.VALUE, indent, sb.toString());
                } else {
                    try { throw new SyntaxErrorException(); }
                    catch (SyntaxErrorException e) { e.printStackTrace(); }
                    next_token = token;
                    // let's fucking skip it
                    return nextToken();
                }
            default:    // pass through when KEY/ITEM/COMMENT
                if (token.isKey() || token.isItem()) prev_token = token;
                return token;
        }
    }

}