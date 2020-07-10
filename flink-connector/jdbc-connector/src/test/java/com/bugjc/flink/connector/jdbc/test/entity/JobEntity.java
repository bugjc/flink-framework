package com.bugjc.flink.connector.jdbc.test.entity;

import lombok.Data;

import java.io.Serializable;

@Data
public class JobEntity implements Serializable {
    private int id;
    private String word;
    private int frequency;
    private long timestamp;

    public JobEntity() {
    }

    public JobEntity(String word, int frequency, long timestamp) {
        this.word = word;
        this.frequency = frequency;
        this.timestamp = timestamp;
    }
}
