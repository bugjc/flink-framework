package com.bugjc.flink.test.config.app.config;

import com.bugjc.flink.config.annotation.ConfigurationProperties;
import lombok.Data;

import java.io.Serializable;
import java.util.List;
import java.util.Map;

/**
 * 测试组件自动注入属性值
 *
 * @author aoki
 * @date 2020/8/10
 **/
@Data
@ConfigurationProperties(prefix = "test.component.")
public class TestComponentConfig implements Serializable {

    /**
     * 基本数据类型
     */
    private Byte aByte;
    private Short bShort;
    private Integer cInt;
    private Long dLong;
    private Float eFloat;
    private Double fDouble;
    private Character gChar;
    private Boolean hBoolean;
    private String iString;

    /**
     * 数组
     */
    private Character[] character;
    private String[] array;

    private List<Entity> list;
    private Map<String, Object> map;

}
