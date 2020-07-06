package com.bugjc.flink.config;

import cn.hutool.core.util.StrUtil;
import com.alibaba.fastjson.JSON;
import com.bugjc.flink.config.annotation.ConfigurationProperties;
import com.bugjc.flink.config.util.InputStreamUtil;
import lombok.Getter;
import org.apache.flink.api.java.utils.ParameterTool;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;
import org.reflections.Reflections;
import org.reflections.ReflectionsException;
import scala.Serializable;

import java.util.*;
import java.util.stream.Collectors;

/**
 * 加载执行环境工具类
 *
 * @author aoki
 * @date 2020/7/1
 **/
public class EnvironmentConfig implements Serializable {

    private static final String ENV_PROPERTY_NAME = "flink.profiles.active";
    private static final String SCAN_BASE_PACKAGES = "flink.scanBasePackages";
    @Getter
    private ParameterTool parameterTool;

    /**
     * 构建配置文件
     * 配置优先级：system > Args > env > default
     *
     * @param args
     * @return
     * @throws Exception
     */
    public EnvironmentConfig(final String[] args) throws Exception {
        //首先加载默认配置文件
        this.parameterTool = ParameterTool.fromPropertiesFile(InputStreamUtil.getDefaultPropertiesInputStream());

        //然后在尝试加载环境配置文件，多个用逗号分隔，具体同 springboot 规则一致
        String envNameStr = this.parameterTool.get(ENV_PROPERTY_NAME);
        if (StrUtil.isBlank(envNameStr)) {
            throw new NullPointerException("`flink.profiles.active` attribute value is not configured");
        }

        String[] envNameArr = envNameStr.split(",");
        for (String envName : envNameArr) {
            this.parameterTool = ParameterTool.fromPropertiesFile(InputStreamUtil.getPropertiesInputStream(envName)).mergeWith(this.parameterTool);
        }

        //扫描项目配置的基本包路径的属性自动配置类
        String scanBasePackages = this.parameterTool.get(SCAN_BASE_PACKAGES);
        if (StrUtil.isBlank(scanBasePackages)) {
            throw new NullPointerException("`flink.scanBasePackages` attribute value is not configured");
        }

        Reflections reflections;
        Set<Class<?>> setClasses = new HashSet<>();
        try {
            reflections = new Reflections(scanBasePackages);
            setClasses = reflections.getTypesAnnotatedWith(ConfigurationProperties.class);
        } catch (ReflectionsException reflectionsException) {
            //ignore
        }

        //解析自定义参数
        List<Map.Entry<String, String>> propertyList = new ArrayList<>(this.parameterTool.toMap().entrySet());
        propertyList.sort(Map.Entry.comparingByKey());

        Map<String, String> autoComponentProperties = new HashMap<>();
        Map<String, Map<String, String>> componentGroupProperties = new HashMap<>();

        Iterator<Map.Entry<String, String>> iterator = propertyList.iterator();
        while (iterator.hasNext()) {
            Map.Entry<String, String> map = iterator.next();
            String key = map.getKey();
            String value = map.getValue();
            for (Class<?> setClass : setClasses) {
                ConfigurationProperties configurationProperties = setClass.getAnnotation(ConfigurationProperties.class);
                if (key.startsWith(configurationProperties.prefix())) {
                    if (!componentGroupProperties.containsKey(configurationProperties.prefix())) {
                        componentGroupProperties.put(configurationProperties.prefix(), new HashMap<>());
                    }
                    Map<String, String> values = componentGroupProperties.get(configurationProperties.prefix());
                    String newKey = key.replaceAll(configurationProperties.prefix(), "");
                    values.put(newKey, value);
                    componentGroupProperties.put(configurationProperties.prefix(), values);
                    autoComponentProperties.put(setClass.getName(), JSON.toJSONString(values));
                    iterator.remove();
                }
            }

        }

        Map<String, String> combineResultMap = new HashMap<String, String>();
        combineResultMap.putAll(propertyList.stream().collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue)));
        combineResultMap.putAll(autoComponentProperties);

        this.parameterTool = ParameterTool.fromMap(combineResultMap);

        //最后在加载运行 jar 时用户输入的参数
        if (args != null) {
            this.parameterTool = ParameterTool.fromArgs(args).mergeWith(parameterTool);
        }

        this.parameterTool = ParameterTool.fromSystemProperties().mergeWith(parameterTool);
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
        return JSON.parseObject(this.parameterTool.get(c.getName()), c);
    }

    /**
     * 获取指定 key 的值
     *
     * @param key
     * @return
     */
    public String get(String key) {
        return this.parameterTool.get(key);
    }

}