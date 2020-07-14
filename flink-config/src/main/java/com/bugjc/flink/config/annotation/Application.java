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
     * @return
     */
    String[] scanBasePackages() default {};
}
