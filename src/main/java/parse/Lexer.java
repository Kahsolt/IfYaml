/*
 *  * Copyright (c)
 *  * Author : Kahsolt <kahsolt@qq.com>
 *  * Create Date : 2019-1-15
 *  * Update Date : 2019-1-15
 *  * Version : v0.1
 *  * License : GPLv3
 *  * Description : 面向字符流的词法分析器...
 */

package parse;

import java.io.*;

public class Lexer {

    private BufferedReader buffer;

    public Lexer() { buffer = new BufferedReader(new StringReader("")); }
    public Lexer(String text) { buffer = new BufferedReader(new StringReader(text)); }
    public Lexer(File file) { this(_readFile(file)); }
    private static String _readFile(File file) {
        StringBuilder sb = new StringBuilder();
        try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
            String line;
            while ((line = reader.readLine()) != null) {
                sb.append(line); sb.append('\n');
            }
        } catch (IOException e) { e.printStackTrace(); }
        return sb.toString();
    }

    public Token nextToken() {
        if (isEOF() || isEOL()) { readline(); if (isEOF()) return null; }  // prepare line buf
        int indent; String value;   // temp vars for token

        // redundant blanks
        if (isBlank()) skipBlanks();
        // 0.full-line comment
        if (isHash()) {
            indent = index; read();         // offset of '#'
            value = new String(line).substring(index);  // exclude '#'
            readline();
            return new Token(TokenType.COMMENT, indent, value);
        }
        // 1.item
        else if (isHyphen() && hasRightGap()) {
            indent = index; read(2);    // offset of '-'
            return new Token(TokenType.ITEM, indent);
        }
        // 2. bond
        else if (isPipe() || isRAngle()) {
            indent = index; read();         // offset of '|' or '>'
            return new Token(TokenType.S_BOND, indent);
        }
        // 3.key / value
        else if (isText()) {
            indent = index;
            value = extractText();
            if (isColon() && hasRightGap()) {
                read(2);
                return new Token(TokenType.KEY, indent, value);
            } else
                return new Token(TokenType.VALUE, indent, value);
        }

        // -1. bad token
        throw new SyntaxErrorException(String.format("Syntax error: (%d:%d) '%s'", lineno, index, line));
    }
    private String extractText() {
        buf.delete(0, buf.length());    // clear

        if (isSQuote()) {
            read(); // skip '\''
            while (!isSQuote() && !isEOL()) { buf.append(cur()); read(); }
            read(); // skip '\''
        } else if (isDQuote()) {
            read(); // skip '"'
            while (!isDQuote() && !isEOL()) {
                if (isBSlash()) {
                    read(); switch (cur()) {
                        case '0':   buf.append('\0'); break;
                        case 'n':   buf.append('\n'); break;
                        case 'r':   buf.append('\r'); break;
                        case 't':   buf.append('\t'); break;
                        case '\"':  buf.append('\"'); break;
                        case '\\':  buf.append('\\'); break;
                        default: buf.append('\\'); buf.append(cur()); break;
                    }
                } else buf.append(cur());
                read();
            }
            read(); // skip '"'
        } else {
            while (!isEOF() && !isEOL() && !(isColon() && hasRightGap()) && !(isHash() && hasLeftGap())) {
                buf.append(cur());
                read();
            }
        }
        if (isEOL()) readline();

        String text = buf.toString().trim();
        return text.isEmpty() ? null: text;
    }

    private char[] line;    // current processing text line
    private int lineno = 0; // current line number
    private int index = 0;  // current char cursor
    private StringBuilder buf = new StringBuilder(256);  // buf to build text value
    private void readline() {
        try {
            String buf; do { buf = buffer.readLine(); lineno++; } while (buf != null && buf.trim().isEmpty());
            line = buf != null ? buf.toCharArray() : null;
            index = 0;
        } catch (IOException e) { e.printStackTrace(); }
    }
    private void read() { read(1); }
    private void read(int cnt) {
        index += cnt;
        if (index > line.length) {  // NO '=', allow read once EOL
            readline();   // scroll to next line
            index = 0;
        } else if (index < -1)  // NO '=', allow read once BOL
            System.err.println("unread to last line is not supported...");
    }
    private void unread() { read(-1); }
    private void unread(int cnt) { read(-cnt); }
    private static final char EOF = (char) -1;  // end of file, when line == null
    private static final char EOL = (char) -2;  // end of line, when index >= line.length
    private static final char BOL = (char) -3;  // begin of line, when index < 0
    private char ch(int index) {
        return line == null ? EOF
                : (index >= 0 && index < line.length) ? line[index]
                    : index >= line.length ? EOL : BOL;
    }
    private char prev() { return ch(index - 1); }
    private char cur()  { return ch(index); }
    private char next() { return ch(index + 1); }
    private boolean hasLeftGap()  { return prev() == ' ' || prev() == BOL; }
    private boolean hasRightGap() { return next() == ' ' || next() == EOL; }
    private boolean isEOF     () { return cur() == EOF; }
    private boolean isEOL     () { return cur() == EOL; }
    private boolean isBOL     () { return cur() == BOL; }
    private boolean isBSlash  () { return cur() == '\\'; }
    private boolean isBlank   () { return cur() == ' '; }
    private boolean isHyphen  () { return cur() == '-'; }
    private boolean isColon   () { return cur() == ':'; }
    private boolean isRAngle  () { return cur() == '>'; }
    private boolean isPipe    () { return cur() == '|'; }
    private boolean isHash    () { return cur() == '#'; }
    private boolean isSQuote  () { return cur() == '\''; }
    private boolean isDQuote  () { return cur() == '"'; }
    private boolean isText    () {
        return ! (cur() == EOF
                || cur() == '#'
                || cur() == '|'
                || (cur() == '-' && hasRightGap()));
    }
    private void skipBlanks() { while (isBlank()) read(); }

}