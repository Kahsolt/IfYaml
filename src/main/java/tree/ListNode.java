package tree;

import util.StringEx;

import java.util.*;

@KeyNode
public class ListNode extends Node {

    private List<Node> items = new ArrayList<>();
    private List<List<String>> comments = new ArrayList<>();

    public ListNode() { }
    public ListNode(Node parent) { super(parent); }
    public ListNode(Node parent, List<Node> items) { super(parent); this.items = items; }

    public List<Map.Entry<Node, List<String>>> getItemCommentPairs() {
        List<Map.Entry<Node, List<String>>> pairs = new ArrayList<>();
        for (int i = 0; i < items.size(); i++)
            pairs.add(new HashMap.SimpleEntry<>(items.get(i), comments.get(i)));
        return pairs;
    }
    public void addComments(int index, Collection<String> comments) {
        while (this.comments.size() <= index) this.comments.add(_initCommentSlot());
        this.comments.get(index).addAll(comments);
    }

    public boolean hasItem(int index) { return index >= 0 && index < items.size(); }
    public Node getItem(int index) { try { return items.get(index); } catch (IndexOutOfBoundsException ignore) { return null; } }
    public void addItem(Node node) { items.add(node); }
    public void insertItem(int index, Node node) { try { items.add(index, node); comments.add(index, _initCommentSlot()); } catch (IndexOutOfBoundsException ignore) { } }
    public void removeItem(int index) { try { items.remove(index); comments.remove(index); } catch (IndexOutOfBoundsException ignore) { } }
    public void clearItems() { items.clear(); comments.clear(); }

    @Override
    public String toString() { return String.format("<ListNode items(%d) %s>", items.size(), super.toString()); }
    public String toAst() {
        StringBuilder sb = new StringBuilder(300);
        sb.append(_toStringOfIndents());
        sb.append("<ListNode items=");
        if (!items.isEmpty()) {
            sb.append("[");
            for (int i = 0; i < items.size(); i++) {
                if (i > 0) sb.append(", ");
                sb.append(String.format("%d: ", i));
                sb.append(_toAstOfComments(i));
                sb.append('\n');
                sb.append(items.get(i).toAst());
            }
            sb.append('\n');
            sb.append(_toStringOfIndents());
            sb.append(']');
        }
        sb.append(">");
        return sb.toString();
    }
    private String _toAstOfComments(int index) {
        StringBuilder sb = new StringBuilder(100);
        if (comments.get(index) != null && !comments.get(index).isEmpty()) {
            sb.append("comments=");
            StringBuilder _sb = new StringBuilder(40);
            for (String comment : comments.get(index)) { _sb.append(comment); _sb.append(' '); }
            sb.append(new StringEx(_sb.toString()).excerpt().quote(true));
        }
        return sb.toString();
    }

}