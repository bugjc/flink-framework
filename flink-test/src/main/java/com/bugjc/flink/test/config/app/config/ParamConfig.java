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
public class ParamConfig implements Serializable {

    /**
     * Basic
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
    private String iStringChild;
    private String[] array;

    /**
     * Enum
     */
    private PublishType publishType;

    /**
     * Map
     */
    private Map<String, String> map;
    private Map<String, Entity> map1;
    private Map<String, Map<String, String>> map2;
    private Map<String, Map<String, Entity>> map3;
    private Map<String, List<Entity>> map4;
    private Map<String, List<String>> map5;
    private Map<String, List<List<String>>> map6;
    private Map<String, List<List<Entity>>> map7;

    /**
     * List
     */
    private List<String> list;
    private List<Entity> list1;
    private List<List<String>> list2;
    private List<List<Entity>> list3;
    private List<Map<String, String>> list4;
    private List<Map<String, Entity>> list5;
    private List<Map<String, Map<String, String>>> list6;
    private List<Map<String, Map<String, Entity>>> list7;
    private List<Map<String, List<Entity>>> list8;
}
