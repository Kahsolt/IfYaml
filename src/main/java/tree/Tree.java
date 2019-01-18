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

    // High level API
    public boolean exist(String path) { return getNode(path) != null; }
    private String _get(String path) { Node node = getNode(path); return node instanceof ScalarNode ? ((ScalarNode) node).getValue() : null; }
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
    public void set(String path, Object value) { set(path, value, true); }
    public void set(String path, Object value, boolean restructurize) {
        if (restructurize && value == null) { removeNode(path); return; }

        Node node = restructurize ? makeNode(path) : getNode(path);
        if (!(node instanceof ScalarNode)) return;
        ScalarNode scnode = (ScalarNode) node;

        if (value instanceof String
                || value instanceof Integer
                || value instanceof Double
                || value instanceof Long
                || value instanceof Boolean
                || value instanceof Float
                || value instanceof Short
                || value instanceof Character
                || value instanceof Byte) scnode.setValue(String.valueOf(value));
        else if (value instanceof Date) scnode.setValue(_default_fmt.format(value));
        else scnode.setValue(value == null ? null : value.toString());
    }
    public void set(String path, Date value, SimpleDateFormat fmt) { set(path, fmt.format(value)); }

    // Low level API
    public Node getNode(String path) { return path == null ? null : _getNode(root, path); }
    private Node _getNode(Node node, String path) {
        if (node == null || path.isEmpty()) return node;

        int idx = path.indexOf(separator);
        String sect_name = idx == -1 ? path : path.substring(0, idx);
        String rest_path = idx == -1 ? ""   : path.substring(idx + 1);

        if (node instanceof HashNode)
            return _getNode(((HashNode) node).getChild(sect_name), rest_path);
        else if (node instanceof ListNode)
            try { return _getNode(((ListNode) node).getItem(Integer.valueOf(sect_name)), rest_path); }
            catch (NumberFormatException ignore) { }

        return null;
    }
    public Node makeNode(String path) { return path == null ? null : _makeNode(root, path); }
    private Node _makeNode(Node node, String path) {
        if (path.isEmpty()) return new ScalarNode(node);

        int idx = path.indexOf(separator);
        String sect_name = idx == -1 ? path : path.substring(0, idx);
        String rest_path = idx == -1 ? ""   : path.substring(idx);

        Node rtnode;
        if (root == null && node == root) {
            if (sect_name.isEmpty()) {
                return root = _makeNode(root, rest_path);
            } else {
                HashNode hsnode = new HashNode();
                root = hsnode;
                rtnode = _makeNode(hsnode, rest_path);
                hsnode.putChild(sect_name, rtnode);
                return rtnode;
            }
        } else if (node instanceof HashNode) {
            HashNode hsnode = (HashNode) node;
            rtnode = _makeNode(hsnode.getChild(sect_name), rest_path);
            hsnode.putChild(sect_name, rtnode);
            return rtnode;
        } else if (node instanceof ListNode) {
            ListNode lsnode = (ListNode) node;
            try {
                int i = Integer.valueOf(sect_name);
                rtnode = _makeNode(lsnode.getItem(i), rest_path);
                lsnode.insertItem(i, rtnode);
                return rtnode;
            } catch (NumberFormatException ignore) { }
        }

        return null;
    }
    public void removeNode(String path) { if (path != null) _removeNode(root, path); }
    private void _removeNode(Node node, String path) {
        if (node == null || path.isEmpty()) return;

        int idx = path.indexOf(separator);
        String sect_name = idx == -1 ? path : path.substring(0, idx);
        String rest_path = idx == -1 ? ""   : path.substring(idx + 1);

        if (node instanceof HashNode) {
            HashNode hsnode = (HashNode) node;
            if (rest_path.isEmpty())
                hsnode.removeChild(sect_name);
            else
                _removeNode(hsnode.getChild(sect_name), rest_path);
        } else if (node instanceof ListNode) {
            ListNode lsnode = (ListNode) node;
            try {
                int i = Integer.valueOf(sect_name);
                if (rest_path.isEmpty())
                    lsnode.removeItem(i);
                else
                    _removeNode(lsnode.getItem(Integer.valueOf(sect_name)), rest_path);
            } catch (NumberFormatException ignore) { }
        }
    }

    @Override
    public String toString() { return String.valueOf(root); }

}