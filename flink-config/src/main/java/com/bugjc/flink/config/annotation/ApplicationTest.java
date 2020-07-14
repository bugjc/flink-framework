package com.bugjc.flink.config.annotation;

import java.lang.annotation.*;

/**
 * 注释在 test 类中指定，提供框架基础功能。
 * @author aoki
 * @date 2020/7/14
 * **/
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface ApplicationTest {

    /**
     * 程序会加载指定 class 下的包作为扫描路径
     * @return      --返回指定 class 所在包路径
     */
    Class<?>[] classes() default {};
}
