package com.bugjc.flink.config.model.tree;

import lombok.Data;

import java.util.ArrayList;
import java.util.List;

/**
 * 前缀树节点
 *
 * @author aoki
 * @date 2020/8/10
 **/
@Data
public class TrieValueNode {
    private String data;

    /**
     * true:表示是一个前缀节点，false:表示是一个值
     */
    private boolean isNode;
    private List<TrieValueNode> children = new ArrayList<>(4);

    public TrieValueNode(String data, boolean isNode) {
        this.data = data;
        this.isNode = isNode;
    }
}
