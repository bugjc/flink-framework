package com.bugjc.flink.test.kafka.app.model;

import lombok.Data;

import java.io.Serializable;

@Data
public class KafkaEvent implements Serializable {
    private int id;
    private String word;
    private int frequency;
    private long timestamp;

    public KafkaEvent() {
    }

    public KafkaEvent(String word, int frequency, long timestamp) {
        this.word = word;
        this.frequency = frequency;
        this.timestamp = timestamp;
    }
}
