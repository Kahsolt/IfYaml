/*
 *  * Copyright (c)
 *  * Author : Kahsolt <kahsolt@qq.com>
 *  * Create Date : 2019-1-17
 *  * Update Date : 2019-1-17
 *  * Version : v0.1
 *  * License : GPLv3
 *  * Description : 按路径访问解析树...
 */

package tree;

import util.StringEx;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class Tree {

    private static final SimpleDateFormat _default_fmt = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

    protected Character separator = '.';
    protected Node root = null;

    public Tree() { }
    public Tree(Node root) { this.root = root; }
    public Tree(Node root, Character separator) { this(root); this.separator = separator; }

    public void setSeparator(Character separator) { this.separator = separator; }
    public Node toNode() { return root; }

    // High level API
    public boolean exist(String path) { return getNode(path) != null; }
    private String _get(String path) { Node node = getNode(path); return node instanceof TextNode ? ((TextNode) node).getValue() : null; }
    public Boolean getBoolean(String path) { String value = _get(path); if (value == null) return null; try {return Boolean.valueOf(value); } catch (NullPointerException ignore) { return null; } }
    public Boolean getBoolean(String path, Boolean defaultval) { return getBoolean(path) != null ? getBoolean(path) : defaultval; }
    public Byte getByte(String path) { String value = _get(path); if (value == null) return null; try { return Byte.valueOf(value); } catch (NumberFormatException | NullPointerException ignore) { return null; } }
    public Byte getByte(String path, Byte defaultval) { return getByte(path) != null ? getByte(path) : defaultval; }
    public Short getShort(String path) { String value = _get(path); if (value == null) return null; try { return Short.valueOf(value); } catch (NumberFormatException | NullPointerException ignore) { return null; } }
    public Short getShort(String path, Short defaultval) { return getShort(path) != null ? getShort(path) : defaultval; }
    public Integer getInteger(String path) { String value = _get(path); if (value == null) return null; try { return Integer.valueOf(value); } catch (NumberFormatException | NullPointerException ignore) { return null; } }
    public Integer getInteger(String path, Integer defaultval) { return getInteger(path) != null ? getInteger(path) : defaultval; }
    public Long getLong(String path) { String value = _get(path); if (value == null) return null; try { return Long.valueOf(value); } catch (NumberFormatException | NullPointerException ignore) { return null; } }
    public Long getLong(String path, Long defaultval) { return getLong(path) != null ? getLong(path) : defaultval; }
    public Float getFloat(String path) { String value = _get(path); if (value == null) return null; try { return Float.valueOf(value); } catch (NumberFormatException | NullPointerException ignore) { return null; } }
    public Float getFloat(String path, Float defaultval) { return getFloat(path) != null ? getFloat(path) : defaultval; }
    public Double getDouble(String path) { String value = _get(path); if (value == null) return null; try { return Double.valueOf(value); } catch (NumberFormatException | NullPointerException ignore) { return null; } }
    public Double getDouble(String path, Double defaultval) { return getDouble(path) != null ? getDouble(path): defaultval; }
    public Character getCharacter(String path) { String value = _get(path); if (value == null) return null; try { return value.charAt(0); } catch (IndexOutOfBoundsException | NullPointerException ignore) { return null; } }
    public Character getCharacter(String path, Character defaultval) { return getCharacter(path) != null ? getCharacter(path) : defaultval; }
    public String getString(String path) { return _get(path); }
    public String getString(String path, String defaultval) { return getString(path) != null ? getString(path): defaultval; }
    public Date getDatetime(String path) { String value = _get(path); if (value == null) return null; try { return _default_fmt.parse(value); } catch (ParseException | NullPointerException ignore) { return null; } }
    public Date getDatetime(String path, Date defaultval) { return getDatetime(path) != null ? getDatetime(path): defaultval; }
    public Date getDatetime(String path, SimpleDateFormat fmt) { String value = _get(path); if (value == null) return null; try { return fmt.parse(value); } catch (ParseException | NullPointerException ignore) { return null; } }
    public boolean set(String path, Object value) { return set(path, value, true); }
    public boolean set(String path, Object value, boolean restructurize) {
        if (restructurize) { if (value == null) return removeNode(path); makeNode(path); }

        Node node = getNode(path);
        if (!(node instanceof TextNode)) return false;
        TextNode txnode = (TextNode) node;

        if (value instanceof String
                || value instanceof Integer
                || value instanceof Double
                || value instanceof Long
                || value instanceof Boolean
                || value instanceof Float
                || value instanceof Short
                || value instanceof Character
                || value instanceof Byte) txnode.setValue(String.valueOf(value));
        else if (value instanceof Date) txnode.setValue(_default_fmt.format(value));
        else txnode.setValue(value == null ? null : value.toString());
        return true;
    }
    public boolean set(String path, Date value, SimpleDateFormat fmt) { return set(path, fmt.format(value)); }

    // Low level API
    public Node getNode(String path) { return path == null ? null : _getNode(root, path); }
    private Node _getNode(Node node, String path) {
        if (node == null || path.isEmpty()) return node;

        String[] sr = StringEx.cut(path, separator);
        String sect_name = sr[0], rest_path = sr[1];

        if (node instanceof HashNode)
            return _getNode(((HashNode) node).getChild(sect_name), rest_path);
        else if (node instanceof ListNode)
            try { return _getNode(((ListNode) node).getItem(Integer.valueOf(sect_name)), rest_path); }
            catch (NumberFormatException ignore) { }

        return null;
    }
    public void makeNode(String path) { if (path != null) _makeNode(root, path); }
    private Node _makeNode(Node node, String path) {
        if (root != null && node == null) return null;

        String sect_name, next_sect_name, rest_path;
        String[] sr = StringEx.cut(path, separator);
        sect_name = sr[0]; rest_path = sr[1];
        sr = StringEx.cut(rest_path, separator);
        next_sect_name = sr[0];

        if (root == null) {
            if (path.isEmpty()) root = new TextNode(node);
            else try {
                int i = Integer.valueOf(sect_name);
                return root = _makeNode(root = new ListNode(node), path);
            } catch (NumberFormatException ignore) {
                return root = _makeNode(root = new HashNode(node), path);
            }
        }

        if (node instanceof HashNode) {
            HashNode hsnode = (HashNode) node;
            if (rest_path.isEmpty()) {
                if (!hsnode.hasChild(sect_name))
                    hsnode.putChild(sect_name, new TextNode(hsnode));
            } else try {
                int i = Integer.valueOf(next_sect_name);
                if (!hsnode.hasChild(sect_name))
                    hsnode.putChild(sect_name, _makeNode(new ListNode(node), rest_path));
                else _makeNode(hsnode.getChild(sect_name), rest_path);
            } catch (NumberFormatException ignore) {
                if (!hsnode.hasChild(sect_name))
                    hsnode.putChild(sect_name, _makeNode(new HashNode(node), rest_path));
                else _makeNode(hsnode.getChild(sect_name), rest_path);
            }
        } else if (node instanceof ListNode) {
            ListNode lsnode = (ListNode) node;
            try {
                int i = Integer.valueOf(sect_name);
                if (rest_path.isEmpty()) {
                    if (!lsnode.hasItem(i))
                        lsnode.insertItem(i, new TextNode(lsnode));
                } else try {
                    i = Integer.valueOf(next_sect_name);
                    if (!lsnode.hasItem(i))
                        lsnode.insertItem(i, _makeNode(new ListNode(node), rest_path));
                    else _makeNode(lsnode.getItem(i), rest_path);
                } catch (NumberFormatException ignore) {
                    if (!lsnode.hasItem(i))
                        lsnode.insertItem(i, _makeNode(new HashNode(node), rest_path));
                    else _makeNode(lsnode.getItem(i), rest_path);
                }
            } catch (NumberFormatException ignore) { }
        }

        return node;    // MUST return the trace node to construct tree recursively
    }
    public boolean removeNode(String path) { return path != null && _removeNode(root, path); }
    private boolean _removeNode(Node node, String path) {
        if (node == null) { if (node == root && path.isEmpty()) { root = null; return true; } return false; }

        String[] sr = StringEx.cut(path, separator);
        String sect_name = sr[0], rest_path = sr[1];

        if (node instanceof HashNode) {
            HashNode hsnode = (HashNode) node;
            if (!rest_path.isEmpty())
                _removeNode(hsnode.getChild(sect_name), rest_path);
            else if (hsnode.hasChild(sect_name)) {
                hsnode.removeChild(sect_name);
                return true;
            }
        } else if (node instanceof ListNode) {
            ListNode lsnode = (ListNode) node;
            try {
                int i = Integer.valueOf(sect_name);
                if (!rest_path.isEmpty())
                    _removeNode(lsnode.getItem(Integer.valueOf(sect_name)), rest_path);
                else if (lsnode.hasItem(i)) {
                    lsnode.removeItem(i);
                    return true;
                }
            } catch (NumberFormatException ignore) { }
        }
        return false;
    }

    @Override
    public String toString() { return root == null ? "" : root.toAst(); }

}