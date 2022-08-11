package com.bugjc.flink.config.util;

import com.bugjc.flink.config.EnvironmentConfig;

import java.io.InputStream;

public class InputStreamUtil {

    private static final String DEFAULT_APPLICATION_PROPERTIES_NAME = "application.properties";
    private static final String DEFAULT_APPLICATION_PROPERTIES_PATH = "/" + DEFAULT_APPLICATION_PROPERTIES_NAME;

    /**
     * 获取默认配置文件的输入流
     *
     * @return  输入流
     */
    public static InputStream getDefaultPropertiesInputStream() {
        return EnvironmentConfig.class.getResourceAsStream(DEFAULT_APPLICATION_PROPERTIES_PATH);
    }

    /**
     * 获取自定义配置文件的输入流
     *
     * @param envName --要加载的配置文件，如：dev
     * @return  输入流
     */
    public static InputStream getPropertiesInputStream(String envName) {
        return EnvironmentConfig.class.getResourceAsStream("/" + DEFAULT_APPLICATION_PROPERTIES_NAME.replaceAll("\\.", "-" + envName + "."));
    }
}
