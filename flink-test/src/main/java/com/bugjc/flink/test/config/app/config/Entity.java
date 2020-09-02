package com.bugjc.flink.test.config.app.config;

import lombok.Data;

@Data
public class Entity{
    private String field1;
    private String[] field2;

    public Entity(String field1, String[] field2) {
        this.field1 = field1;
        this.field2 = field2;
    }
}