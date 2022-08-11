package com.bugjc.flink.connector.custom;

import com.bugjc.flink.config.Config;
import com.bugjc.flink.config.annotation.ConfigurationProperties;
import lombok.Data;

import java.io.Serializable;

/**
 * 自定义连接器示例
 *
 * @author aoki
 * @date 2020/7/15
 **/
@Data
@ConfigurationProperties(prefix = "custom.")
public class CustomConfig implements Config, Serializable {
    private String property1;
    private String property2;

    //TODO 这里写创建该组件的连接对象的方法
}
