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

    private String string;
    private String[] array;
    private List<Entity> list;
    private Map<String, Object> map;

}
