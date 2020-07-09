package com.bugjc.flink.config.util;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.annotation.JSONField;
import com.bugjc.flink.config.Config;
import com.bugjc.flink.config.annotation.ConfigurationProperties;
import org.apache.commons.lang3.StringUtils;
import org.apache.flink.api.java.utils.ParameterTool;
import org.reflections.Reflections;
import org.reflections.ReflectionsException;

import java.io.IOException;
import java.lang.reflect.Field;
import java.util.*;
import java.util.stream.Collectors;

/**
 * 初始化配置工具类
 *
 * @author aoki
 * @date 2020/7/8
 **/
public class InitializeUtil {

    private static final String ENV_PROPERTY_NAME = "flink.profiles.active";
    private static final String SCAN_BASE_PACKAGES = "flink.scanBasePackages";

    /**
     * 加载用户配置
     *
     * @return
     * @throws IOException
     */
    public static ParameterTool loadUserProperties(String[] args) throws IOException {
        ParameterTool parameterTool = ParameterTool.fromPropertiesFile(InputStreamUtil.getDefaultPropertiesInputStream());
        String envNameStr = parameterTool.get(ENV_PROPERTY_NAME);
        if (StringUtils.isBlank(envNameStr)) {
            throw new NullPointerException("`flink.profiles.active` attribute value is not configured");
        }

        String[] envNameArr = envNameStr.split(",");
        for (String envName : envNameArr) {
            parameterTool = ParameterTool.fromPropertiesFile(InputStreamUtil.getPropertiesInputStream(envName)).mergeWith(parameterTool);
        }


        //最后在加载运行 jar 时用户输入的参数
        if (args != null) {
            parameterTool = ParameterTool.fromArgs(args).mergeWith(parameterTool);
        }

        parameterTool = ParameterTool.fromSystemProperties().mergeWith(parameterTool);
        return parameterTool;
    }

    /**
     * 扫描出配置文件类。
     * 查找出定义了 @ConfigProperty 注解、且直接或间接实现了 Config 接口类的 class.
     *
     * @param parameterTool
     * @return
     */
    public static Set<Class<?>> scanConfig(ParameterTool parameterTool) {
        //扫描项目配置的基本包路径
        String scanBasePackages = parameterTool.get(SCAN_BASE_PACKAGES);
        if (StringUtils.isBlank(scanBasePackages)) {
            //未配置默认扫描启动类 Main 路径下的配置文件
            scanBasePackages = Objects.requireNonNull(deduceMainApplicationClass()).getPackage().getName();
        }

        Reflections reflections;
        Set<Class<?>> setClasses = new HashSet<>();
        try {
            reflections = new Reflections(scanBasePackages);
            setClasses = reflections.getTypesAnnotatedWith(ConfigurationProperties.class);
        } catch (ReflectionsException reflectionsException) {
            //ignore
        }

        ServiceLoader<Config> serviceLoader = ServiceLoader.load(Config.class);
        for (Config config : serviceLoader) {
            setClasses.add(config.getClass());
        }

        return setClasses;
    }

    /**
     * 解析配置
     *
     * @param parameterTool
     * @param setClasses
     * @return
     */
    public static Map<String, String> parseConfig(ParameterTool parameterTool, Set<Class<?>> setClasses) {

        // map to list,and sorted
        List<Map.Entry<String, String>> propertyList = new ArrayList<>(parameterTool.toMap().entrySet());
        propertyList.sort(Map.Entry.comparingByKey());

        //解析属性集合中按规则定义的各个组件配置属性值
        Map<String, String> componentConfigProperties = new HashMap<>();
        Map<String, Map<String, String>> tempComponentConfigProperties = new HashMap<>();

        Iterator<Map.Entry<String, String>> iterator = propertyList.iterator();
        while (iterator.hasNext()) {
            Map.Entry<String, String> map = iterator.next();
            String key = map.getKey();
            String value = map.getValue();
            for (Class<?> setClass : setClasses) {
                ConfigurationProperties configurationProperties = setClass.getAnnotation(ConfigurationProperties.class);
                if (configurationProperties != null && key.startsWith(configurationProperties.prefix())) {
                    //按注解配置的 prefix 为分组 key，合并成一个属性配置类
                    if (!tempComponentConfigProperties.containsKey(configurationProperties.prefix())) {
                        tempComponentConfigProperties.put(configurationProperties.prefix(), new HashMap<>());
                    }

                    //合并的属性字段默认去掉前缀
                    Map<String, String> values = tempComponentConfigProperties.get(configurationProperties.prefix());
                    String fieldName = parseKey(setClass, configurationProperties.prefix(), key);
                    values.put(fieldName, value);
                    componentConfigProperties.put(setClass.getName(), JSON.toJSONString(values));
                    iterator.remove();
                }
            }

        }

        //最后，合并属性集合和组件属性集合
        Map<String, String> combineResultMap = new HashMap<String, String>();
        combineResultMap.putAll(propertyList.stream().collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue)));
        combineResultMap.putAll(componentConfigProperties);
        return combineResultMap;
    }

    /**
     * 对于组件配置的键，默认去除前缀后的值作为键。如用户定义了 @JSONField 注解则使用其 name 字段作为属性的键。
     *
     * @param prefix
     * @param key
     * @return
     */
    private static String parseKey(Class<?> setClass, String prefix, String key) {
        String fieldName = key.replaceAll(prefix, "");
        try {
            Field field = setClass.getDeclaredField(fieldName);
            JSONField jsonField = field.getAnnotation(JSONField.class);
            if (jsonField == null) {
                return fieldName;
            }
            fieldName = jsonField.name();
        } catch (NoSuchFieldException e) {
            //ignore
        }
        return fieldName;
    }


    /**
     * 获取 main 所在路径
     *
     * @return
     */
    private static Class<?> deduceMainApplicationClass() {
        try {
            StackTraceElement[] stackTrace = new RuntimeException().getStackTrace();
            for (StackTraceElement stackTraceElement : stackTrace) {
                if ("main".equals(stackTraceElement.getMethodName())) {
                    return Class.forName(stackTraceElement.getClassName());
                }
            }
        } catch (ClassNotFoundException ex) {
            // Swallow and continue
        }
        return null;
    }
}
