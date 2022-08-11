package com.bugjc.flink.connector.jdbc.annotation;

import java.lang.annotation.*;


/**
 * 表字段相关
 * @author aoki
 * @date 2020/7/12
 */
@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.FIELD)
public @interface TableField {

    /**
     * 字段值（驼峰命名方式,该值可无）
     */
    String value() default "";
}
