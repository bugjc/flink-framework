//package com.bugjc.flink.config.util;
//
//import com.bugjc.flink.config.annotation.ConfigurationProperties;
//import com.bugjc.flink.config.parser.NewField;
//import com.bugjc.flink.config.parser.TypeUtil;
//
//import java.lang.reflect.ParameterizedType;
//import java.lang.reflect.Type;
//import java.util.Arrays;
//import java.util.List;
//import java.util.stream.Collectors;
//
///**
// * 生成配置文件工具
// *
// * @author aoki
// * @date 2020/9/30
// **/
//public class GenerateConfigurationFileUtil {
//    public static void gen(String path, Class<?> t) {
//        ConfigurationProperties configurationProperties = t.getAnnotation(ConfigurationProperties.class);
//        if (configurationProperties == null) {
//            throw new NullPointerException();
//        }
//
//        List<NewField> fields = Arrays.stream(t.getDeclaredFields()).map(field -> new NewField(field.getName(), field.getType(), field.getGenericType())).collect(Collectors.toList());
//        for (NewField field : fields) {
//            String key = null;
//            Type type = field.getGenericType();
//            if (TypeUtil.isBasic(type)) {
//                key = configurationProperties.prefix() + "." + field.getName() + "=";
//            } else if (TypeUtil.isJavaBean(type)) {
//
//            }
//
//        }
//    }
//
//    private String getKey(List<NewField> fields) {
//        if (fields == null || fields.isEmpty()) {
//            return null;
//        }
//        for (NewField field : fields) {
//            StringBuilder key = new StringBuilder();
//            Type type = field.getGenericType();
//            if (TypeUtil.isBasic(type)) {
//                key.append(".").append(field.getName()).append("=");
//            } else if (TypeUtil.isJavaBean(type)) {
//                return getKey(getEntityFields(type));
//            } else if (TypeUtil.isMap(type)) {
//                boolean isParameterizedType = type instanceof ParameterizedType;
//                if (!isParameterizedType) {
//                    throw new NullPointerException();
//                }
//
//                ParameterizedType parameterizedType = (ParameterizedType) type;
//                Type[] types = parameterizedType.getActualTypeArguments();
//
//                //获取最后的泛型类
//                if (isJavaBean(type)) {
//
//                } else {
//                    key.append(".").append("[key]");
//                }
//            }
//
//
//        }
//    }
//
//    private void getKey(){
//
//    }
//
//    /**
//     * 获取要处理的 entity 字段列表
//     *
//     * @param valueType
//     * @return
//     */
//    private List<NewField> getEntityFields(Type valueType) {
//        Class<?> entityClass = (Class<?>) valueType;
//        return Arrays.stream(entityClass.getDeclaredFields())
//                .map(fieldMap -> new NewField(fieldMap.getName(), fieldMap.getType(), fieldMap.getGenericType()))
//                .collect(Collectors.toList());
//    }
//
//    /**
//     * 判断字段类型是否为 Java Bean
//     *
//     * @param genericType
//     * @return
//     */
//    private static boolean isJavaBean(Type genericType) {
//        boolean isParameterizedType = genericType instanceof ParameterizedType;
//        if (isParameterizedType) {
//            ParameterizedType parameterizedType = (ParameterizedType) genericType;
//            Type[] types = parameterizedType.getActualTypeArguments();
//            //获取最后的泛型类
//            Type valueType = TypeUtil.getLastType(types[types.length - 1]);
//            return TypeUtil.isJavaBean(valueType);
//        }
//        return false;
//    }
//}
