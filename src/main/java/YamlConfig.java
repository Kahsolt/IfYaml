/*
 *  * Copyright (c)
 *  * Author : Kahsolt <kahsolt@qq.com>
 *  * Create Date : 2019-1-18
 *  * Update Date : 2019-1-18
 *  * Version : v0.1
 *  * License : GPLv3
 *  * Description : yaml解析树的封装...
 */

import parse.Parser;
import tree.Tree;

import java.io.File;

public class YamlConfig extends Tree {

    private File file = null;   // the text file

    public YamlConfig() { }
    public YamlConfig(File file) { this.file = file; reload(); }
    public YamlConfig(String filename) { this(new File(filename)); }

    public void reload() { root = new Parser(file).parse(); }
    public void save() { throw new RuntimeException(); }

}