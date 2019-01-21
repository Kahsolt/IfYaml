/*
 *  * Copyright (c)
 *  * Author : Kahsolt <kahsolt@qq.com>
 *  * Create Date : 2019-1-20
 *  * Update Date : 2019-1-20
 *  * Version : v0.1
 *  * License : GPLv3
 *  * Description : 从数据结构体构建yaml树...
 */

package parse;

import tree.HashNode;
import tree.ListNode;
import tree.Node;
import tree.TextNode;
import util.StringEx;

import java.util.List;
import java.util.Map;

public class Builder {

    public Object object = null;

    public Builder(Object object) { this.object = object; }

    public Node build() { return object == null ? null : _build(null, object);}
    private Node _build(Node node, Object object) {
        if (object instanceof List) {
            ListNode lsnode = new ListNode(node);
            for (Object item : (List) object)
                lsnode.addItem(_build(lsnode, item));
            return lsnode;
        } else if (object instanceof Map) {
            HashNode hsnode = new HashNode(node);
            for (Map.Entry<Object, Object> kv : ((Map<Object, Object>) object).entrySet())
                hsnode.putChild(StringEx.deliteral(kv.getKey().toString()), _build(hsnode, kv.getValue()));
            return hsnode;
        } else return new TextNode(node, object.toString());
    }

}