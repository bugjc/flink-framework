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
public class TrieNode {
    private String data;

    private List<TrieNode> children = new ArrayList<>(4);

    public TrieNode(String data) {
        this.data = data;
    }
}
