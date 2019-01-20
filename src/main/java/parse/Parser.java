/*
 *  * Copyright (c)
 *  * Author : Kahsolt <kahsolt@qq.com>
 *  * Create Date : 2019-1-15
 *  * Update Date : 2019-1-15
 *  * Version : v0.1
 *  * License : GPLv3
 *  * Description : 解析产生yaml树...
 */

package parse;

import tree.*;

import java.io.File;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.ListIterator;

public class Parser {

    private Lexer lexer;

    public Parser(File file) { lexer = new ExLexer(file); }
    public Parser(String text) { lexer = new ExLexer(text); }

    public Node parse() { return root == null ? root = parseNode(null, -1) : root; }

    private Node parseNode(Node parent, int indent) {   // as inferior node
        if (token == null) { readtoken(); if (token == null) return null; }

        if (token.indent <= indent) return new TextNode(parent);;
        switch (token.type) {
            case COMMENT: stashComments(); return parseNode(parent, indent);
            case ITEM: return parseListNode(parent, token.indent);
            case KEY: return parseHashNode(parent, token.indent);
            case VALUE:
            case VALUE_LINE:
            case VALUE_MULTILINE:
            case VALUE_TEXT: return parseTextNode(parent, token.indent);
            default: return null;
        }
    }
    private ListNode parseListNode(Node parent, int indent) {
        ListNode node = new ListNode(parent);

        int cnt = 0;
        while (token != null && token.indent == indent && token.isItem()) {
            readtoken();    // skip '- '
            if (token != null) {
                node.addComments(cnt, collectComments(indent));
                node.addItem(parseNode(node, indent));
            }
            stashComments();
            cnt++;
        }
        return node;
    }
    private HashNode parseHashNode(Node parent, int indent) {
        HashNode node = new HashNode(parent);

        while (token != null && token.indent == indent && token.isKey()) {
            String key = token.value; readtoken();    // skip 'id: '
            if (token != null) {
                node.addComments(key, collectComments(indent));
                node.putChild(key, parseNode(node, indent));
            }
            stashComments();
        }
        return node;
    }
    private TextNode parseTextNode(Node parent, int indent) {
        TextNode node = new TextNode(parent);

        node.addComments(collectComments(indent));
        switch (token.type) {
            case VALUE_LINE:
                node.setType(TextType.LINE);
                node.setValue(token.value);
                readtoken(); break;
            case VALUE_MULTILINE:
                node.setType(TextType.MULTILINE);
                node.setValue(token.value);
                readtoken(); break;
            case VALUE_TEXT:
                node.setType(TextType.TEXT);
                node.setValue(token.value);
                readtoken(); break;
            default:
                node.setValue("");
        }
        return node;
    }
    private List<String> collectComments(int indent) {
        ListIterator<Token> iter = comments.listIterator(comments.size());
        while (iter.hasPrevious()) {
            Token cmttok = iter.previous();
            if (cmttok.indent != indent) break;
        }
        List<String> res = new ArrayList<>();
        while (iter.hasNext()) {
            Token cmttok = iter.next();
            res.add(cmttok.value);
        }

        comments.forEach(cmttok -> orphan_comments.add(cmttok.value));
        comments.clear();
        return res;
    }
    private void stashComments() { while (token != null && token.isComment()) { comments.add(token); readtoken(); } }

    private Node root = null;   // root tree node
    private Token token = null; // current token
    private List<Token> comments = new LinkedList<>();          // affinity comments buf
    private List<String> orphan_comments = new ArrayList<>();   // orphan comments buf
    private void readtoken() { token = lexer.nextToken(); }

}