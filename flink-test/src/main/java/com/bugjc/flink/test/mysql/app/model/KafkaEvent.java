package com.bugjc.flink.test.mysql.app.model;

import com.bugjc.flink.connector.jdbc.annotation.TableName;
import lombok.Data;

import java.io.Serializable;

/**
 * KafkaEvent 实体类
 * @author aoki
 * @date 2020/7/14
 * **/
@Data
@TableName("odl_kafka_event_test")
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
