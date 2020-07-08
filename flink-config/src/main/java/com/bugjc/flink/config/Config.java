package com.bugjc.flink.config;

/**
 * 组件配置文件自动初始化标记类。
 * 此类会在组件的配置文件初始化后执行
 * 注意：仅支持 @ConfigurationProperties 注解标记的类
 * @author aoki
 * @date 2020/7/8
 **/
public interface Config {
    /**
     * 配置属性初始化后自动执行此方法
     */
    void init();
}
