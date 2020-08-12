package com.bugjc.flink.config.model.application;

/**
 * 应用属性
 * @author aoki
 * @date 2020/8/11
 * **/
public interface ApplicationProperties {
    /**
     * 应用自动扫描包路径配置
     */
    String SCAN_BASE_PACKAGES = "packages";

    /**
     * 排除特定的自动配置类
     */
    String EXCLUDES = "excludes";
}
