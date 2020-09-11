package com.bugjc.flink.test.config.app.config;

import lombok.Data;

import java.io.Serializable;
import java.util.List;

@Data
public class Entity2 implements Serializable {
    private String field1;
    private String[] field2;
    private List<Entity2> field3;

    public Entity2(String s, String[] o) {
        this.field1 = s;
        this.field2 = o;

    }
}