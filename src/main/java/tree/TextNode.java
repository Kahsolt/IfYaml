package tree;

import util.StringEx;

import java.util.Collection;
import java.util.Collections;
import java.util.List;

@ValueNode
public class TextNode extends Node {

    private String value = "";
    private TextType type = TextType.LINE;
    private List<String> comments = null;

    public TextNode() { }
    public TextNode(Node parent) { super(parent); }
    public TextNode(Node parent, String value) { this(parent); this.value = value; }
    public TextNode(Node parent, String value, TextType type) { this(parent, value); this.type = type; }

    public String getValue() { return value; }
    public TextType getType() { return type; }
    public void setType(TextType type) { this.type = type; }
    public List<String> getComments() { return comments != null ? comments : Collections.EMPTY_LIST; }
    public void addComments(Collection<String> comments) { if (this.comments == null) this.comments = _initCommentSlot(); this.comments.addAll(comments); }

    public void setValue(String value) { this.value = value; }
    public void clearValue() { value = ""; }

    @Override
    public String toString() { return String.format("<TextNode value='%s' %s>", value, super.toString()); }
    public String toAst() {
        StringBuilder sb = new StringBuilder(100);
        sb.append(_toStringOfIndents());
        sb.append("<TextNode value=");
        if (value != null) sb.append(new StringEx(value).excerpt().escape().quote(true));
        sb.append(_toAstOfComments());
        sb.append('>');
        return sb.toString();
    }
    private String _toAstOfComments() {
        StringBuilder sb = new StringBuilder(100);
        if (comments != null && !comments.isEmpty()) {
            sb.append('\n');
            sb.append(_toStringOfIndents());
            sb.append(StringEx.repeat(" ", 10)); // FIXME: magical fix indent of "<TextNode "
            sb.append("comments=");
            StringBuilder _sb = new StringBuilder(40);
            for (String comment : comments) { _sb.append(comment); _sb.append(' '); }
            sb.append(new StringEx(_sb.toString()).excerpt(40).quote(true));
        }
        return sb.toString();
    }

}