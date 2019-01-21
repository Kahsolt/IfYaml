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

import util.StringEx;

import java.io.File;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

public class ExLexer {  // NOT inherit from Lexer

    private Lexer lexer = null;

    public ExLexer(String text) { lexer = new Lexer(text); }
    public ExLexer(File file) { lexer = new Lexer(file); }

    // reformed token buffer, typically several comment and one value token
    private LinkedList<Token> token_buff = new LinkedList<>();
    private Token prev_token = null;    // buffer last key/item token, for indent inference
    private Token next_token = null;    // buffer next token, 'cos don't have unreadToken()

    public Token nextToken() {
        if (!token_buff.isEmpty()) return token_buff.pollFirst();
        Token token;
        if (next_token != null) { token = next_token; next_token = null; }
        else token = lexer.nextToken();
        if (token == null) return null;

        switch (token.type) {
            case VALUE:
                ArrayList<String> values = new ArrayList<>();
                int indent = token.indent;
                do {
                    if (token.isComment()) token_buff.addLast(token);   // comments are ok
                    else if (token.isValue()) values.add(token.value);  // values need merge
                    else break;
                } while ((token = lexer.nextToken()) != null && token.indent > prev_token.indent);
                next_token = token;   // stash key token
                token_buff.addLast(values.size() == 1
                        ? new Token(TokenType.VALUE_LINE, indent, values.get(0))
                        : new Token(TokenType.VALUE_MULTILINE, indent, String.join(" ", values)));
                return nextToken(); // comment tokens have priority..
            case S_BOND:
                ArrayList<Token> value_tokens = new ArrayList<>();
                token = lexer.nextToken();
                indent = token.indent;
                if (indent > prev_token.indent) {
                    do { value_tokens.add(token); }
                    while ((token = lexer.nextToken()) != null && token.indent > prev_token.indent);
                    next_token = token; // stash key token
                    StringBuilder sb = new StringBuilder(100);
                    for (Token valtok : value_tokens) {
                        sb.append(StringEx.repeat(" ", valtok.indent - indent));
                        if (valtok.isComment()) sb.append('#');
                        sb.append(valtok.value);
                        sb.append('\n');
                    }
                    sb.deleteCharAt(sb.length() - 1);   // remove redundant '\n'
                    return new Token(TokenType.VALUE_TEXT, indent, sb.toString());
                } else new Token(TokenType.VALUE_LINE, indent);
            default:    // pass through when KEY/ITEM/COMMENT
                if (token.isKey() || token.isItem()) prev_token = token;
                return token;
        }
    }
    public List<Token> tokenize() {
        ArrayList<Token> tokens = new ArrayList<>(80);
        Token token;
        while ((token = nextToken()) != null)
            tokens.add(token);
        return tokens;
    }

}