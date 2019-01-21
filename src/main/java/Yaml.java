/*
 *  * Copyright (c)
 *  * Author : Kahsolt <kahsolt@qq.com>
 *  * Create Date : 2019-1-18
 *  * Update Date : 2019-1-18
 *  * Version : v0.1
 *  * License : GPLv3
 *  * Description : 结合解析树与引擎的糟糕异质封装...
 */

import parse.Dumper;
import parse.Parser;
import tree.Tree;

import java.io.File;

public class Yaml extends Tree {  // FIXME: ugly inheritance!!

    private File file = null;   // the text file

    public Yaml(File file) { this.file = file; reload(); }
    public Yaml(String filename) { this(new File(filename)); }

    public void reload() { root = new Parser(file).parse(); }
    public void save() { new Dumper(root).dump(file); }

}