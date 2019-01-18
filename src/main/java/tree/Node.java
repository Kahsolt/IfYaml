/*
 *  * Copyright (c)
 *  * Author : Kahsolt <kahsolt@qq.com>
 *  * Create Date : 2019-1-15
 *  * Update Date : 2019-1-15
 *  * Version : v0.1
 *  * License : GPLv3
 *  * Description : 散列/列表节点(枝)或者值节点(叶)
 */

package tree;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.ListIterator;

@KeyNode
@ValueNode
public abstract class Node {

    protected Node parent = null;
    protected int layer = 0;
    protected List<String> comments = null;

    public Node() { }
    public Node(Node parent) { this.parent = parent; this.layer = parent != null ? parent.layer + 1 : 0; }

    public Node getParent() { return parent; }

    public void addComment(String comment) { if (comments == null) comments = new ArrayList<>(); comments.add(comment); }

    protected String _toStringOfIndents() { return layer <= 0 ? "" : String.format("%" + layer * 2 + "d", 0).replace("0", " "); }
    protected String _toStringOfComments() {
        StringBuilder sb = new StringBuilder(100);
        if (comments != null) {
            sb.append(" comments=[");
            boolean isFirst = true;
            ListIterator iter = comments.listIterator();
            for (String comment : comments) {
                if (!isFirst) sb.append(", ");
                sb.append(comment.length() <= 12
                        ? String.format("'%s'", comment)
                        : String.format("'%s...'", comment.substring(0, 12)));
                isFirst = false;
            }
            sb.append("]");
        }
        return sb.toString();
    }

}