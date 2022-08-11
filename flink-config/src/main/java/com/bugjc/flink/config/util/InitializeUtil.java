package com.bugjc.flink.config.util;

import com.bugjc.flink.config.Config;
import com.bugjc.flink.config.annotation.Application;
import com.bugjc.flink.config.annotation.ApplicationTest;
import com.bugjc.flink.config.annotation.ConfigurationProperties;
import com.bugjc.flink.config.exception.ApplicationContextException;
import com.bugjc.flink.config.model.application.ApplicationResponse;
import com.bugjc.flink.config.model.tree.Trie;
import com.bugjc.flink.config.parser.*;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.apache.flink.api.java.utils.ParameterTool;
import org.reflections.Reflections;
import org.reflections.ReflectionsException;

import java.io.IOException;
import java.util.*;
import java.util.stream.Collectors;

/**
 * 初始化配置工具类
 *
 * @author aoki
 * @date 2020/7/8
 **/
@Slf4j
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
        if (StringUtils.isNotBlank(envNameStr)) {
            String[] envNameArr = envNameStr.split(",");
            for (String envName : envNameArr) {
                parameterTool = ParameterTool.fromPropertiesFile(InputStreamUtil.getPropertiesInputStream(envName)).mergeWith(parameterTool);
            }
        }

        //最后在加载运行 jar 时用户输入的参数
        if (args != null) {
            parameterTool = ParameterTool.fromArgs(args).mergeWith(parameterTool);
        }

        //系统参数
        //parameterTool = ParameterTool.fromSystemProperties().mergeWith(parameterTool);
        return parameterTool;
    }

    /**
     * 扫描出配置文件类。
     * 查找出定义了 @ConfigProperty 注解、且直接或间接实现了 Config 接口类的 class.
     *
     * @return
     */
    public static Set<Class<?>> scanConfig() {
        //扫描项目配置的基本包路径
        ApplicationResponse applicationResponse = findApplicationPackages();
        Set<String> scanBasePackages = applicationResponse.getScanBasePackages();
        List<Class<?>> excludeList = applicationResponse.getExcludes();
        if (scanBasePackages == null || scanBasePackages.isEmpty()) {
            throw new ApplicationContextException("启动类缺少 @Application 或 @ApplicationTest 注解");
        }

        Reflections reflections;
        Set<Class<?>> allSetClasses = new HashSet<>();
        try {
            reflections = new Reflections(scanBasePackages);
            allSetClasses = reflections.getTypesAnnotatedWith(ConfigurationProperties.class);
        } catch (ReflectionsException reflectionsException) {
            //ignore
        }

        //注解方式
        Set<Class<?>> setClasses = new HashSet<>();
        for (Class<?> setClass : allSetClasses) {
            if (existExcludeClass(setClass, excludeList)) {
                setClasses.add(setClass);
            }
        }

        //接口方式
        ServiceLoader<Config> serviceLoader = ServiceLoader.load(Config.class);
        for (Config config : serviceLoader) {
            if (existExcludeClass(config.getClass(), excludeList)) {
                setClasses.add(config.getClass());
            }
        }

        return setClasses;
    }

    /**
     * 判断扫描路径是否在排除自动扫描的列表项中
     *
     * @param c
     * @param excludeList
     * @return
     */
    private static boolean existExcludeClass(Class<?> c, List<Class<?>> excludeList) {
        if (excludeList == null) {
            return true;
        }
        for (Class<?> aClass : excludeList) {
            if (c.equals(aClass)) {
                return false;
            }
        }
        return true;
    }

    /**
     * 解析配置
     *
     * @param parameterTool
     * @param setClasses
     * @return
     */
    public static Map<String, String> parseConfig(ParameterTool parameterTool, Set<Class<?>> setClasses) {


        //统一 key 的风格
        Map<String, String> newParameter = new HashMap<>();
        for (Map.Entry<String, String> entry : parameterTool.toMap().entrySet()) {
            String key = entry.getKey();
            newParameter.put(key, entry.getValue());
            //构建前缀树
            Trie.insert(key);
        }

        //解析属性集合中按规则定义的各个组件配置属性值
        Map<String, String> componentConfigProperties = new HashMap<>();
        for (Class<?> setClass : setClasses) {
            ConfigurationProperties configurationProperties = setClass.getAnnotation(ConfigurationProperties.class);
            if (configurationProperties != null) {
                String prefix = configurationProperties.prefix();
                List<NewField> fields = Arrays.stream(setClass.getDeclaredFields()).map(field -> new NewField(field.getName(), field.getType(), field.getGenericType())).collect(Collectors.toList());

                GroupContainer initGroupContainer = GroupContainer.create(ContainerType.None, prefix, ContainerType.None);
                Params input = Params.create(initGroupContainer, fields, newParameter);
                Container output = new Container();
                PropertyParser.deconstruction(input, output);

                String data =  new GsonBuilder().disableHtmlEscaping().create().toJson(output.getData());
                componentConfigProperties.put(setClass.getName(), data);
                log.info("Auto load component configuration：{}", data);
            }
        }


        //最后，合并属性集合和组件属性集合
        Map<String, String> combineResultMap = new HashMap<String, String>();
        combineResultMap.putAll(newParameter);
        combineResultMap.putAll(componentConfigProperties);
        return combineResultMap;
    }

    /**
     * 查找注解 @Application or @ ApplicationTest 所在包路径及其它配置信息
     *
     * @return
     */
    private static ApplicationResponse findApplicationPackages() {
        ApplicationResponse applicationResponse = new ApplicationResponse();
        StackTraceElement[] stackTrace = new RuntimeException().getStackTrace();
        for (StackTraceElement stackTraceElement : stackTrace) {
            String className = stackTraceElement.getClassName();
            if (className.startsWith("sun.")) {
                continue;
            } else if (className.startsWith("java.")) {
                continue;
            } else if (className.startsWith("org.")) {
                continue;
            }

            Class<?> currentClassName = null;
            try {
                currentClassName = Class.forName(stackTraceElement.getClassName());
            } catch (ClassNotFoundException ex) {
                log.warn(ex.getMessage());
                continue;
            }

            ApplicationTest applicationTest = currentClassName.getAnnotation(ApplicationTest.class);
            if (applicationTest != null) {
                Set<String> scanBasePackagesTreeSet = new TreeSet<>();
                scanBasePackagesTreeSet.add(currentClassName.getPackage().getName());
                if (applicationTest.classes().length != 0) {
                    Arrays.stream(applicationTest.classes()).forEach(appClass -> {
                        scanBasePackagesTreeSet.add(appClass.getPackage().getName());
                    });
                }
                applicationResponse.setScanBasePackages(scanBasePackagesTreeSet);
            }

            Application application = currentClassName.getAnnotation(Application.class);
            if (application != null) {
                Set<String> scanBasePackagesTreeSet = new TreeSet<>();
                scanBasePackagesTreeSet.add(currentClassName.getPackage().getName());
                if (application.scanBasePackages().length != 0) {
                    scanBasePackagesTreeSet.addAll(Arrays.asList(application.scanBasePackages()));
                }
                applicationResponse.setScanBasePackages(scanBasePackagesTreeSet);

                if (application.excludes().length != 0) {
                    applicationResponse.setExcludes(Arrays.asList(application.excludes()));
                }
            }
        }
        return applicationResponse;
    }
}
