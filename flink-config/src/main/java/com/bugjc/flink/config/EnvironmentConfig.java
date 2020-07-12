package com.bugjc.flink.config;

import com.alibaba.fastjson.JSON;
import com.bugjc.flink.config.util.InitializeUtil;
import lombok.Getter;
import org.apache.flink.api.java.utils.ParameterTool;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;
import scala.Serializable;

import java.util.Map;
import java.util.Properties;
import java.util.Set;

/**
 * 加载执行环境工具类
 *
 * @author aoki
 * @date 2020/7/1
 **/
public class EnvironmentConfig implements Serializable {

    @Getter
    private final ParameterTool parameterTool;

    /**
     * 构建配置文件
     * 配置优先级：system > Args > env > default
     *
     * @param args
     * @return
     * @throws Exception
     */
    public EnvironmentConfig(final String[] args) throws Exception {

        //加载用户配置
        ParameterTool parameterTool = InitializeUtil.loadUserProperties(args);

        //获取配置文件类
        Set<Class<?>> setClasses = InitializeUtil.scanConfig(parameterTool);

        //解析自定义参数
        Map<String, String> propertiesMap = InitializeUtil.parseConfig(parameterTool, setClasses);

        //将属性集暴露出去
        this.parameterTool = ParameterTool.fromMap(propertiesMap);
    }

    /**
     * 获取流执行环节
     *
     * @return
     */
    public StreamExecutionEnvironment getStreamExecutionEnvironment() {
        final StreamExecutionEnvironment env = StreamExecutionEnvironment.getExecutionEnvironment();
        env.getConfig().setGlobalJobParameters(this.getParameterTool());
        return env;
    }

    /**
     * 获取组件属性集
     *
     * @param c
     * @return
     */
    public Properties getComponentProperties(Class<?> c) {
        return JSON.parseObject(this.parameterTool.get(c.getName()), Properties.class);
    }

    /**
     * 获取组件属性配置文件
     *
     * @param c
     * @param <T>
     * @return
     */
    public <T> T getComponent(Class<T> c) {
        T t = JSON.parseObject(this.parameterTool.get(c.getName()), c);
        if (t == null) {
            throw new NullPointerException("this class is not a component!");
        }
        return t;
    }

}