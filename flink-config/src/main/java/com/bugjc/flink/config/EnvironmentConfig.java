package com.bugjc.flink.config;

import com.bugjc.flink.config.util.InitializeUtil;
import com.bugjc.flink.config.util.StopWatch;
import com.esotericsoftware.minlog.Log;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import org.apache.flink.api.java.ExecutionEnvironment;
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

    /**
     * 原始变量 key 不处理，统一使用对象类方式获取属性值
     */
    private final ParameterTool parameterTool;

    private ParameterTool getParameterTool() {
        return this.parameterTool;
    }

    private static final Gson GSON = new GsonBuilder().disableHtmlEscaping().create();

    /**
     * 构建配置文件
     * 配置优先级：system > Args > env > default
     * @param args          --自定义参数
     * @throws Exception    异常
     */
    public EnvironmentConfig(final String[] args) throws Exception {
        String method = Thread.currentThread().getStackTrace()[1].getMethodName();
        StopWatch stopWatch = new StopWatch(method);
        stopWatch.start();

        //加载用户配置
        ParameterTool parameterTool = InitializeUtil.loadUserProperties(args);

        //获取配置文件类
        Set<Class<?>> setClasses = InitializeUtil.scanConfig();

        //解析自定义参数
        Map<String, String> propertiesMap = InitializeUtil.parseConfig(parameterTool, setClasses);

        //将属性集暴露出去
        this.parameterTool = ParameterTool.fromMap(propertiesMap);

        stopWatch.stop();
        Log.info(stopWatch.prettyPrint());
    }

    /**
     * 获取流执行环境
     *
     * @return StreamExecutionEnvironment
     */
    public StreamExecutionEnvironment getStreamExecutionEnvironment() {
        final StreamExecutionEnvironment env = StreamExecutionEnvironment.getExecutionEnvironment();
        //--------设置作业全局参数------
        env.getConfig().setGlobalJobParameters(this.getParameterTool());
        return env;
    }

    /**
     * 获取批执行环境
     *
     * @return ExecutionEnvironment
     */
    public ExecutionEnvironment getExecutionEnvironment() {
        final ExecutionEnvironment env = ExecutionEnvironment.getExecutionEnvironment();
        //--------设置作业全局参数------
        env.getConfig().setGlobalJobParameters(this.getParameterTool());
        return env;
    }

    /**
     * 获取组件属性集
     *
     * @param c     --java bean
     * @return Properties
     */
    public Properties getComponentProperties(Class<?> c) {
        return GSON.fromJson(this.parameterTool.get(c.getName()), Properties.class);
    }

    /**
     * 获取组件属性配置文件
     *
     * @param c     --java bean
     * @return T
     */
    public <T> T getComponent(Class<T> c) {
        T t = GSON.fromJson(this.parameterTool.get(c.getName()), c);
        if (t == null) {
            throw new NullPointerException("this class is not a component!");
        }
        return t;
    }

}