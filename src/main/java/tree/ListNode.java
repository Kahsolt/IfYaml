package tree;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

@KeyNode
public class ListNode extends Node {

    private List<Node> items = new LinkedList<>();

    public ListNode() { }
    public ListNode(Node parent) { super(parent); }
    public ListNode(Node parent, List<Node> items) { super(parent); this.items = items; }

    public List<Node> getItems() { return items; }

    public boolean hasItem(int index) { return index >= 0 && index < items.size(); }
    public Node getItem(int index) { try { return items.get(index); } catch (IndexOutOfBoundsException ignore) { return null; } }
    public void addItem(Node node) { items.add(node); }
    public void insertItem(int index, Node node) { items.add(index, node); }
    public void removeItem(int index) { try { items.remove(index); } catch (IndexOutOfBoundsException ignore) { } }
    public void removeItem(Node node) { items.remove(node); }
    public void clearItems() { items.clear(); }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder(300);
        sb.append(_toStringOfIndents());
        sb.append("<ListNode items=");
        if (!items.isEmpty()) {
            sb.append("[\n");
            boolean isFirst = true;
            for (Node item : items) {
                if (!isFirst) sb.append(",\n");
                sb.append(item != null ? item : "  (NULL)");
                isFirst = false;
            }
            sb.append('\n');
            sb.append(_toStringOfIndents());
            sb.append(']');
        }
        sb.append(_toStringOfComments());
        sb.append(">");
        return sb.toString();
    }

}