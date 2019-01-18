package tree;

@ValueNode
public class ScalarNode extends Node {

    private String value = null;

    public ScalarNode() { }
    public ScalarNode(Node parent) { super(parent); }
    public ScalarNode(Node parent, String value) { super(parent); this.value = value; }

    public String getValue() { return value; }

    public void setValue(String value) { this.value = value; }
    public void clearValue() { value = null; }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder(100);
        sb.append(_toStringOfIndents());
        sb.append("<ScalarNode value=");
        if (value != null)
            sb.append(value.length() <= 12
                    ? String.format("'%s'", value)
                    : String.format("'%s...'", value.substring(0, 12)
                        .replace("\n", "\\n")
                        .replace("\r", "\\r")
                        .replace("\t","\\t")));
        if (comments != null) {
            sb.append('\n');
            sb.append(_toStringOfIndents());
            sb.append("           ");  // FIXME: magical fix indent of "<ScalarNode "
            sb.append(_toStringOfComments());
        }
        sb.append('>');
        return sb.toString();
    }

}