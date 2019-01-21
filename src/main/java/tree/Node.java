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

import util.StringEx;

import java.util.ArrayList;
import java.util.List;

@KeyNode
@ValueNode
public abstract class Node {

    protected Node parent = null;
    protected int depth = 0;

    public Node() { }
    public Node(Node parent) { this.parent = parent; this.depth = parent != null ? parent.depth + 1 : 0; }

    public Node getParent() { return parent; }
    public int getDepth() { return depth; }
    public Tree toTree() { return new Tree(this); }

    protected String _toStringOfIndents() { return StringEx.repeat("  ", depth); }
    protected List<String> _initCommentSlot() { return new ArrayList<>(4); }

}