/*
 *  * Copyright (c)
 *  * Author : Kahsolt <kahsolt@qq.com>
 *  * Create Date : 2019-1-20
 *  * Update Date : 2019-1-20
 *  * Version : v0.1
 *  * License : GPLv3
 *  * Description : fucking java.text.*, and do simple text process ...
 */

package util;

public class TextEx {

    private String text = "";

    public TextEx() { }
    public TextEx(String text) { this.text = text != null ? text : ""; }

    public TextEx prefix(String prefix) {
        if (prefix == null || prefix.length() == 0) return this;

        StringBuilder sb = new StringBuilder(200);
        String[] lines = text.replace("\r", "").split("\n");
        for (int i = 0; i < lines.length; i++) {
            sb.append(prefix);
            sb.append(lines[i]);
            if (i != lines.length - 1) sb.append('\n');
        }
        text = sb.toString();
        return this;
    }
    public TextEx indent(int indent) {
        if (indent == 0) return this;

        String indent_prefix = StringEx.repeat(" ", indent > 0 ? indent : -indent);
        StringBuilder sb = new StringBuilder(200);
        if (indent > 0) prefix(indent_prefix);
        else {
            String[] lines = text.replace("\r", "").split("\n");
            for (int i = 0; i < lines.length; i++) {
                if (lines[i].startsWith(indent_prefix)) sb.append(lines[i].substring(-indent));
                else sb.append(lines[i]);
                if (i != lines.length - 1) sb.append('\n');
            }
            text = sb.toString();
        }
        return this;
    }
    public TextEx wrap() {
        StringBuilder sb = new StringBuilder(200);
        int len = 0;
        String[] segs = text.split("[ \n\r\t]");
        for (int i = 0; i < segs.length; i++) {
            while (i < segs.length && len + segs[i].length() <= 64) {
                sb.append(segs[i]); sb.append(' ');
                len += segs[i].length(); i++;
            }
            len = 0;
            if (i != segs.length - 1) sb.append('\n');
        }
        text = sb.toString();
        return this;
    }

    public int length() { return text.length(); }

    @Override
    public String toString() { return text; }

}