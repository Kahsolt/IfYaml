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
import java.util.LinkedList;
import java.util.ListIterator;

public class Parser {

    private Lexer lexer;

    public Parser(File file) { lexer = new ExLexer(file); }
    public Parser(String text) { lexer = new ExLexer(text); }

    public Node parse() { return root == null ? root = parseNode(null) : root; }

    private Node parseNode(Node parent) {
        if (token == null) { readtoken(); if (token == null) return null; }

        switch (token.type) {
            case COMMENT:
                stashComments();
                return parseNode(parent);
            case ITEM:
                return parseListNode(parent, token.indent);
            case KEY:
                return parseHashNode(parent, token.indent);
            case VALUE:
                return parseScalarNode(parent, token.indent);
            default:
                return null;
        }
    }
    private ListNode parseListNode(Node parent, int indent) {
        ListNode node = new ListNode(parent);
        attachComments(node, indent);

        while (token != null && token.indent == indent && token.isItem()) {
            readtoken();    // skip '- '
            if (token != null) node.addItem(parseNode(node));
            else node.addItem(null);
            stashComments();
        }

        return node;
    }
    private HashNode parseHashNode(Node parent, int indent) {
        HashNode node = new HashNode(parent);
        attachComments(node, indent);

        while (token != null && token.indent == indent && token.isKey()) {
            String key = token.value; readtoken();    // skip 'id: '
            node.putChild(key, parseNode(node));
            stashComments();
        }

        return node;
    }
    private ScalarNode parseScalarNode(Node parent, int indent) {
        ScalarNode node = new ScalarNode(parent);
        attachComments(node, indent);

        node.setValue(token.value); readtoken();    // absorb value

        return node;
    }
    private void attachComments(Node node, int indent) {
        ListIterator<Token> iter = comments.listIterator(comments.size());
        while (iter.hasPrevious()) {
            Token cmttok = iter.previous();
            if (cmttok.indent != indent) break;
        }
        while (iter.hasNext()) {
            Token cmttok = iter.next();
            if (cmttok.indent == indent)
                node.addComment(cmttok.value);
            else break;
        }
        comments.forEach(cmttok -> orphan_comments.add(cmttok.value));
        comments.clear();
    }
    private void stashComments() { while (token != null && token.isComment()) { comments.addLast(token); readtoken(); } }

    private Node root = null;   // root tree node
    private Token token = null; // current token
    private LinkedList<Token> comments = new LinkedList<>();            // affinity comments buf
    private LinkedList<String> orphan_comments = new LinkedList<>();    // orphan comments buf
    private void readtoken() { token = lexer.nextToken(); }

}