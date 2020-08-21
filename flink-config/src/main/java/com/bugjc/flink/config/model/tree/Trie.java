package com.bugjc.flink.config.model.tree;

import com.bugjc.flink.config.util.PointToCamelUtil;

import java.util.List;

/**
 * 构建前缀树
 *
 * @author aoki
 * @date 2020/8/10
 **/
public class Trie {
    /**
     * 前缀树
     */
    private static TrieNode root = new TrieNode("/", true);


    public static void main(String[] args) {
        Trie.insert("com.bugjc.flink.jdbc");
        Trie.insert("com.bugjc.flink.jdbcJob");
        Trie.insert("com.bugjc.flink.kafka.consumer.url");
        Trie.print("", root.getChildren());

        TrieNode trieNode = Trie.find("com.bugjc.flink");
        Trie.print("", trieNode.getChildren());
    }


    /**
     * 构建一个 Trie 树
     *
     * @param key
     */
    public static void insert(String key) {
        //构建
        String[] keyArr = PointToCamelUtil.camel2Point(key).split("\\.");
        childrenInsert(0, keyArr, root.getChildren());
    }

    /**
     * 插入子节点
     *
     * @param index    --前缀节点索引
     * @param keyArr   --前缀节点数组集合
     * @param children --子节点列表
     */
    private static void childrenInsert(int index, String[] keyArr, List<TrieNode> children) {
        if (keyArr.length <= index) {
            return;
        }
        String childData = keyArr[index];
        TrieNode childTrieNode = findChildren(childData, children);
        if (childTrieNode == null) {
            childTrieNode = new TrieNode(childData, true);
            children.add(childTrieNode);
        }

        childrenInsert(++index, keyArr, childTrieNode.getChildren());
    }

    /**
     * 根据前缀查找其所有子节点
     *
     * @param prefix
     * @return
     */
    public static TrieNode find(String prefix) {
        String[] keyArr = prefix.split("\\.");
        TrieNode childTrieNode = null;
        List<TrieNode> children = root.getChildren();
        for (String key : keyArr) {
            childTrieNode = findChildren(key, children);
            if (childTrieNode == null) {
                return null;
            }
            children = childTrieNode.getChildren();
        }
        return childTrieNode;
    }

    /**
     * 在子节点集合列表中查找指定节点的位置
     *
     * @param node     --指定查询的节点
     * @param children --节点集合列表
     * @return 存在返回节点，不存在返回 null
     */
    private static TrieNode findChildren(String node, List<TrieNode> children) {
        if (children == null) {
            return null;
        }

        for (TrieNode child : children) {
            if (child.getData().equals(node)) {
                return child;
            }
            findChildren(node, child.getChildren());
        }
        return null;
    }


    /**
     * 打印前缀树
     */
    public static void print() {
        print("", root.getChildren());
    }

    /**
     * 打印指定子节点前缀树
     *
     * @param indent   --缩进
     * @param children --子节点列表
     */
    public static void print(String indent, List<TrieNode> children) {
        if (children == null) {
            return;
        }

        indent += "\t";
        for (TrieNode child : children) {
            System.out.println(indent + child.getData());
            print(indent, child.getChildren());
        }
    }

}
