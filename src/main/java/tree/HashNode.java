package tree;

import java.util.HashMap;

@KeyNode
public class HashNode extends Node {

    private HashMap<String, Node> children = new HashMap<>();

    public HashNode() { }
    public HashNode(Node parent) { super(parent); }
    public HashNode(Node parent, HashMap<String, Node> children) { super(parent); this.children = children; }

    public HashMap<String, Node> getChildren() { return children; }

    public boolean hasChild(String name) { return children.containsKey(name); }
    public Node getChild(String name) { return children.get(name); }
    public void putChild(String name, Node node) { children.put(name, node); }
    public void removeChild(String name) { children.remove(name); }
    public void clearChildren() { children.clear(); }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder(500);
        sb.append(_toStringOfIndents());
        sb.append("<HashNode children=");
        if (!children.isEmpty()) {
            sb.append('{');
            boolean isFirst = true;
            for (HashMap.Entry<String, Node> kv : children.entrySet()) {
                if (!isFirst) sb.append(", ");
                sb.append('"');
                sb.append(kv.getKey());
                sb.append("\":\n");
                sb.append(kv.getValue() != null ? kv.getValue() : "  (NULL)");
                isFirst = false;
            }
            sb.append('\n');
            sb.append(_toStringOfIndents());
            sb.append('}');
        }
        sb.append(_toStringOfComments());
        sb.append(">");
        return sb.toString();
    }

}