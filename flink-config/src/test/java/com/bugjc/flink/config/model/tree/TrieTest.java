package com.bugjc.flink.config.model.tree;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class TrieTest {

    @Test
    void insertAndPrint() {
        Trie.insert("com.bugjc.flink.jdbc");
        Trie.insert("com.bugjc.flink.jdbcJob");
        Trie.insert("com.bugjc.flink.kafka.consumer.url");
        Trie.print();
    }
}