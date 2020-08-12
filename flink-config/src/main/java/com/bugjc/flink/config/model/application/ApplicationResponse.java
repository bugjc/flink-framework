package com.bugjc.flink.config.model.application;

import lombok.Data;

import java.util.List;
import java.util.Set;

/**
 * 应用配置的元数据
 * @author aoki
 * @date 2020/8/12
 * **/
@Data
public class ApplicationResponse {
    private Set<String> scanBasePackages;
    private List<Class<?>> excludes;
}
