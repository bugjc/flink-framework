package com.bugjc.flink.data.stream.api.data.sources;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.io.Serializable;

@Data
@AllArgsConstructor
public class MySource implements Serializable {
    private String key1;

}
