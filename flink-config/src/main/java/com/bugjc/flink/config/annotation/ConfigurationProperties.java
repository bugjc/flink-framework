package com.bugjc.flink.config.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 注解配置属性
 *
 * @author aoki
 * @date 2020/7/3
 **/
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
public @interface ConfigurationProperties {
    /**
     * 匹配字段的前缀
     *
     * @return
     */
    String prefix() default "";
}
