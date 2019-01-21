/*
 *  * Copyright (c)
 *  * Author : Kahsolt <kahsolt@qq.com>
 *  * Create Date : 2019-1-19
 *  * Update Date : 2019-1-19
 *  * Version : v0.1
 *  * License : GPLv3
 *  * Description : fucking Java's naive class:String ...
 */

package util;

import java.util.HashMap;
import java.util.Random;

public class StringEx {

    private static final HashMap<String, String> repeat_cache = new HashMap<>();
    private static final int repeat_cache_size_limit = 50;

    public static String repeat(String string, int count) {
        if (string == null || count <= 0) return "";

        String name = count + (char) -1 + string;
        if (!repeat_cache.containsKey(name)) {
            StringBuilder sb = new StringBuilder(string.length() * count);
            for (int i = 0; i < count; i++) sb.append(string);
            repeat_cache.put(name, sb.toString());

            if (repeat_cache.size() > repeat_cache_size_limit) {
                Random random = new Random();
                repeat_cache.entrySet().removeIf((__) -> random.nextFloat() < 0.65);
            }
        }
        return repeat_cache.get(name);
    }
    public static String[] cut(String string, Character delimiter) {
        if (string == null) return new String[] { "", "" };

        int idx = string.indexOf(delimiter);
        String seg  = idx == -1 ? string : string.substring(0, idx);
        String rest = idx == -1 ? ""     : string.substring(idx + 1);
        return new String[] { seg, rest };
    }
    public static String dequote(String string) {
        if (string == null) return "";
        if (string.startsWith("'") && string.endsWith("'")
                ||string.startsWith("\"") && string.endsWith("\""))
            return string.substring(1, string.length() - 1);
        return string;
    }
    public static String deliteral(String string) {
        if (string == null) return "";
        if (!string.contains(" ")) return string;
        boolean hasSQuote = string.contains("'"), hasDQuote = string.contains("\"");
        return hasSQuote && hasDQuote || hasSQuote
                ? String.format("\"%s\"", string.replace("\"", "\\\""))
                : String.format("'%s'", string.replace("'", "\\'"));
    }
    public static String deliteral(String string, boolean singleQuote) {
        return string == null ? ""
                : !string.contains(" ") ? string
                    : singleQuote
                        ? String.format("'%s'", string.replace("'", "\\'"))
                        : String.format("\"%s\"", string.replace("\"", "\\\""));
    }

    private String string = "";

    public StringEx() { }
    public StringEx(String string) { this.string = string != null ? string : ""; }

    public StringEx escape() {
        return replace("\0", "\\0")
                .replace("\n", "\\n")
                .replace("\r", "\\r")
                .replace("\t","\\t")
                .replace("\\", "\\\\");
    }
    public StringEx unescape() {
        return replace("\\0", "\0")
                .replace("\\n", "\n")
                .replace("\\r", "\r")
                .replace("\\t","\t")
                .replace("\\\\", "\\");
    }
    public StringEx surround(String quoter) { return StringEx.format("%s%s%s", quoter, string, quoter); }
    public StringEx quote() { return quote(false); }
    public StringEx quote(boolean singleQuote) { return singleQuote ? surround("'") : surround("\"") ; }
    public StringEx excerpt() {
        int len = length();
        if (len <= 24) return excerpt(24);
        else if (len <= 80) return excerpt(len / 3);  // one line
        else if (len <= 160) return excerpt(100);  // double line
        return excerpt(150); // multi lines
    }
    public StringEx excerpt(int count) { return string.length() <= count ? this : StringEx.format("%s...", substring(0, count)); }
    public StringEx prefix(String prefix) {
        if (prefix == null || prefix.length() == 0) return this;

        StringBuilder sb = new StringBuilder(200);
        String[] lines = string.replace("\r", "").split("\n");
        for (int i = 0; i < lines.length; i++) {
            sb.append(prefix);
            sb.append(lines[i]);
            if (i != lines.length - 1) sb.append('\n');
        }
        string = sb.toString();
        return this;
    }
    public StringEx indent(int indent) {
        if (indent == 0) return this;

        String indent_prefix = StringEx.repeat(" ", indent > 0 ? indent : -indent);
        StringBuilder sb = new StringBuilder(200);
        if (indent > 0) prefix(indent_prefix);
        else {
            String[] lines = string.replace("\r", "").split("\n");
            for (int i = 0; i < lines.length; i++) {
                if (lines[i].startsWith(indent_prefix)) sb.append(lines[i].substring(-indent));
                else sb.append(lines[i]);
                if (i != lines.length - 1) sb.append('\n');
            }
            string = sb.toString();
        }
        return this;
    }
    public StringEx wrap() {
        StringBuilder sb = new StringBuilder(200);
        int len = 0;
        String[] segs = string.split("[ \n\r\t]");
        for (int i = 0; i < segs.length; i++) {
            while (i < segs.length && len + segs[i].length() <= 64) {
                sb.append(segs[i]); sb.append(' ');
                len += segs[i].length(); i++;
            }
            sb.deleteCharAt(sb.length() - 1);   // remove redundant ' '
            len = 0;
            if (i != segs.length - 1) sb.append('\n');
        }
        string = sb.toString();
        return this;
    }

    // wrapper for native java.lang.String
    public static StringEx format(String format, Object... args) { return new StringEx(String.format(format, args)); }
    public int length() { return string.length(); }
    public StringEx concat(String str) { string = string.concat(str); return this; }
    public StringEx trim() { string = string.trim(); return this; }
    public StringEx substring(int beginIndex) { string = string.substring(beginIndex); return this; }
    public StringEx substring(int beginIndex, int endIndex) { string = string.substring(beginIndex, endIndex); return this; }
    public StringEx replace(char oldChar, char newChar) { string = string.replace(oldChar, newChar); return this; }
    public StringEx replace(CharSequence target, CharSequence replacement) { string = string.replace(target, replacement); return this; }

    @Override
    public String toString() { return string; }

}