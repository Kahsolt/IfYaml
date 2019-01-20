/*
 *  * Copyright (c)
 *  * Author : Kahsolt <kahsolt@qq.com>
 *  * Create Date : 2019-1-19
 *  * Update Date : 2019-1-19
 *  * Version : v0.1
 *  * License : GPLv3
 *  * Description : 主yaml处理引擎...
 */

import parse.Parser;
import tree.*;
import util.StringEx;
import util.TextEx;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.List;
import java.util.Map;

public class Yaml {

    private File file = null;   // default io file
    private Node root = null;
    private int indent = 2;

    // Yaml reader
    public Yaml() { }
    public Yaml(File file) { this.file = file ; load(); }
    public Yaml(String filename) {  this(new File(filename)); }
    // Yaml writer
    public Yaml(Node root) { this.root = root; }
    public Yaml(Tree tree) { this.root = tree.toNode(); }

    public void setIndent(int indent) { this.indent = indent; }

    // Yaml reader
    public Node load() { return root = load(file); }
    public Node load(File file) { return root = new Parser(file).parse(); }
    public Node loads(String text) { return root = new Parser(text).parse(); }

    // Yaml writer
    public void dump() { dump(file); }
    public void dump(File file) {
        try (FileWriter writer = new FileWriter(file)) {
            writer.write(dumps());
            writer.flush();
            writer.close();
        } catch (IOException e) { e.printStackTrace(); }
    }
    public String dumps() {
        if (root == null) return "";

        StringBuilder sb = new StringBuilder(400);
        _dumps(root, null, sb);
        return sb.toString();
    }
    private void _dumps(Node curnode, Node prevnode, StringBuilder sb) {
        if (curnode == null) return;
        String indent_prefix = StringEx.repeat(" ", curnode.getDepth() * indent);

        if (curnode instanceof TextNode) {
            TextNode txnode = (TextNode) curnode;
            boolean keepNewline = _dumpsOfComments(sb, txnode.getComments(), indent_prefix);
            switch (txnode.getType()) {
                case LINE:
                    if (keepNewline) sb.append(indent_prefix);
                    else sb.deleteCharAt(sb.length() - 1);
                    sb.append(txnode.getValue());
                    break;
                case MULTILINE:
                    sb.append(new TextEx(txnode.getValue()).wrap().prefix(indent_prefix));
                    break;
                case TEXT:
                    sb.insert(sb.length() - 1, "| ");
                    sb.append(new TextEx(txnode.getValue()).prefix(indent_prefix));
                    break;
            }
            sb.append('\n');
        } else if (curnode instanceof HashNode) {
            HashNode hsnode = (HashNode) curnode;
            for (Map.Entry<String, Map.Entry<Node, List<String>>> knc : hsnode.getChildCommentPairs()) {
                boolean keepNewline = _dumpsOfComments(sb, knc.getValue().getValue(), indent_prefix);
                if (keepNewline || !(prevnode instanceof ListNode)) sb.append(indent_prefix);
                else sb.deleteCharAt(sb.length() - 1);
                sb.append(knc.getKey());
                sb.append(": \n");
                _dumps(knc.getValue().getKey(), hsnode, sb);
            }
        } else if (curnode instanceof ListNode) {
            ListNode lsnode = (ListNode) curnode;
            for (Map.Entry<Node, List<String>> nc : lsnode.getItemCommentPairs()) {
                boolean keepNewline = _dumpsOfComments(sb, nc.getValue(), indent_prefix);
                if (keepNewline || !(prevnode instanceof ListNode)) sb.append(indent_prefix);
                else sb.deleteCharAt(sb.length() - 1);
                sb.append("- \n");
                _dumps(nc.getKey(), lsnode, sb);
            }
        }
    }
    private boolean _dumpsOfComments(StringBuilder sb, List<String> comments, String indent_prefix) {
        if (comments == null) return false;

        boolean hasComment = false;
        for (String comment : comments) {
            sb.append(indent_prefix);
            sb.append('#');
            sb.append(comment);
            sb.append('\n');
            hasComment = true;
        }
        return hasComment;
    }

}