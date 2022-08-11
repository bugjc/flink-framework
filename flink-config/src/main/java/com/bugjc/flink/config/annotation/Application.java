package com.bugjc.flink.config.annotation;

import java.lang.annotation.*;

/**
 * 注释在 test 类中指定，提供框架基础功能。
 *
 * @author aoki
 * @date 2020/7/14
 **/
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface Application {

    /**
     * 基础包扫描注释组件
     *
     * @return 待扫描的基础包
     */
    String[] scanBasePackages() default {};

    /**
     * 排除特定的自动配置类
     * @return 待排除的配置类
     */
    Class<?>[] excludes() default {};
}
