package tree;

import util.StringEx;

import java.util.*;

@KeyNode
public class HashNode extends Node {

    private Map<String, Node> children = new LinkedHashMap<>();
    private Map<String, List<String>> comments = new HashMap<>();

    public HashNode() { }
    public HashNode(Node parent) { super(parent); }
    public HashNode(Node parent, Map<String, Node> children) { super(parent); this.children = children; }

    public List<Map.Entry<String, Map.Entry<Node, List<String>>>> getChildCommentPairs() {
        List<Map.Entry<String, Map.Entry<Node, List<String>>>> pairs = new ArrayList<>();
        for (Map.Entry<String, Node> kv : children.entrySet())
            pairs.add(new HashMap.SimpleEntry<>(kv.getKey(),
                    new HashMap.SimpleEntry<>(kv.getValue(),
                            comments.getOrDefault(kv.getKey(), Collections.EMPTY_LIST))));
        return pairs;
    }
    public void addComments(String name, Collection<String> comments) {
        if (!this.comments.containsKey(name))
            this.comments.put(name, _initCommentSlot());
        this.comments.get(name).addAll(comments);
    }

    public boolean hasChild(String name) { return children.containsKey(name); }
    public Node getChild(String name) { return children.get(name); }
    public void putChild(String name, Node node) { children.put(name, node); }
    public void removeChild(String name) { children.remove(name); comments.remove(name); }
    public void clearChildren() { children.clear(); }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder(500);
        sb.append(_toStringOfIndents());
        sb.append("<HashNode children=");
        if (!children.isEmpty()) {
            sb.append('{');
            boolean isFirst = true;
            for (Map.Entry<String, Node> kv : children.entrySet()) {
                if (!isFirst) sb.append(", ");
                sb.append('"');
                sb.append(kv.getKey());
                sb.append("\": ");
                sb.append(_toStringOfComments(kv.getKey()));
                sb.append('\n');
                sb.append(kv.getValue());
                isFirst = false;
            }
            sb.append('\n');
            sb.append(_toStringOfIndents());
            sb.append('}');
        }
        sb.append(">");
        return sb.toString();
    }
    private String _toStringOfComments(String name) {
        StringBuilder sb = new StringBuilder(100);
        if (comments.get(name) != null && !comments.get(name).isEmpty()) {
            sb.append("comments=");
            StringBuilder _sb = new StringBuilder(40);
            for (String comment : comments.get(name)) { _sb.append(comment); _sb.append(' '); }
            sb.append(new StringEx(_sb.toString()).excerpt().quote(true));
        }
        return sb.toString();
    }

}