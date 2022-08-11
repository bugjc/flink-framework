package com.bugjc.flink.connector.jdbc.annotation;

import java.lang.annotation.*;

/**
 * 数据库表相关
 * @author aoki
 * @date 2020/7/12
 */
@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
public @interface TableName {

    /**
     * 实体对应的表名
     */
    String value() default "";
}
