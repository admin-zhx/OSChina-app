package net.oschina.gitapp.bean;

import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import static java.lang.String.CASE_INSENSITIVE_ORDER;
import static net.oschina.gitapp.bean.CodeTree.TYPE_BLOB;
import static net.oschina.gitapp.bean.CodeTree.TYPE_TREE;

/**
 * 仓库代码树
 * Created by 火蚁 on 15/4/22.
 */
public class CodeFullTree {

    // 根文件夹
    public final Folder root;

    public final String branch;

    public CodeFullTree(String branch) {
        this.root = new Folder();
        this.branch = branch;
    }

    public static class  CodeEntity implements Comparable<CodeEntity> {

        // 父文件夹
        public final Folder parent;

        public final CodeTree codeTree;

        public final String name;

        private CodeEntity() {
            this.parent = null;
            this.codeTree = null;
            this.name = null;
        }

        private CodeEntity(CodeTree codeTree, Folder parent) {
            this.codeTree = codeTree;
            this.parent = parent;
            this.name = codeTree.getPath();
        }

        @Override
        public int compareTo(CodeEntity other) {
            return CASE_INSENSITIVE_ORDER.compare(this.name, other.name);
        }
    }

    public static class Folder extends CodeEntity {

        /**
         * 子文件夹集
         */
        public final Map<String, Folder> folders = new TreeMap<String, Folder>(
                CASE_INSENSITIVE_ORDER);

        /**
         * 文件集
         */
        public final Map<String, CodeEntity> files = new TreeMap<String, CodeEntity>(
                CASE_INSENSITIVE_ORDER);

        public Folder() {
            super();
        }

        public Folder(CodeTree codeTree, Folder parent) {
            super(codeTree, parent);
        }

        public void addFloder(final CodeTree codeTree) {
            Folder floder = new Folder(codeTree, this);
            folders.put(floder.name, floder);
        }

        public void addFile(final CodeTree codeTree) {
            CodeEntity codeEntity = new CodeEntity(codeTree, this);
            files.put(codeTree.getName(), codeEntity);
        }

        public void add(final CodeTree codeTree) {
            String type = codeTree.getType();
            String path = codeTree.getPath();

            if (TYPE_BLOB.equals(type)) {
                addFile(codeTree);
            } else if (TYPE_TREE.equals(type)) {
                addFloder(codeTree);
            }
        }

        public void add(List<CodeTree> codeTrees) {
            for (CodeTree codeTree : codeTrees) {
                add(codeTree);
            }
        }
    }

}
