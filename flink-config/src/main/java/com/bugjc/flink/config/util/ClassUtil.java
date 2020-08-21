package com.bugjc.flink.config.util;

import com.alibaba.fastjson.JSON;
import lombok.extern.slf4j.Slf4j;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * 类工具
 *
 * @author aoki
 * @date 2020/8/14
 **/
@Slf4j
public class ClassUtil {

    /**
     * 打印对象的属性值
     *
     * @param object
     * @throws Exception
     */
    public static void print(Object object) throws Exception {

        //实例对象的 class
        Class<?> clz = object.getClass();

        // 获取实体类的所有属性，返回Field数组
        Field[] fields = clz.getDeclaredFields();


        for (Field field : fields) {

            // 如果类型是 Byte
            if (isTargetClassType(field, Byte.class)) {
                String method = "get" + getMethodName(field.getName());
                Method m = object.getClass().getMethod(method);
                Byte val = (Byte) m.invoke(object);
                log.info("Byte {} = {}", field.getName(), val);
            }

            // 如果类型是 Short
            if (isTargetClassType(field, Short.class)) {
                String method = "get" + getMethodName(field.getName());
                Method m = object.getClass().getMethod(method);
                Short val = (Short) m.invoke(object);
                log.info("Short {} = {}", field.getName(), val);
            }

            // 如果类型是 Integer
            if (isTargetClassType(field, Integer.class)) {
                String method = "get" + getMethodName(field.getName());
                Method m = object.getClass().getMethod(method);
                Integer val = (Integer) m.invoke(object);
                log.info("Integer {} = {}", field.getName(), val);
            }

            // 如果类型是 Long
            if (isTargetClassType(field, Long.class)) {
                String method = "get" + getMethodName(field.getName());
                Method m = object.getClass().getMethod(method);
                Long val = (Long) m.invoke(object);
                log.info("Long {} = {}", field.getName(), val);
            }

            // 如果类型是 Float
            if (isTargetClassType(field, Float.class)) {
                String method = "get" + getMethodName(field.getName());
                Method m = object.getClass().getMethod(method);
                Float val = (Float) m.invoke(object);
                log.info("Float {} = {}", field.getName(), val);
            }

            // 如果类型是 Double
            if (isTargetClassType(field, Double.class)) {
                String method = "get" + getMethodName(field.getName());
                Method m = object.getClass().getMethod(method);
                Double val = (Double) m.invoke(object);
                log.info("Double {} = {}", field.getName(), val);
            }

            // 如果类型是 Double
            if (isTargetClassType(field, Character.class)) {
                String method = "get" + getMethodName(field.getName());
                Method m = object.getClass().getMethod(method);
                Character val = (Character) m.invoke(object);
                log.info("Character {} = {}", field.getName(), val);
            }

            // 如果类型是Boolean 是封装类
            if (isTargetClassType(field, Boolean.class)) {
                String method = "get" + getMethodName(field.getName());
                Method m = object.getClass().getMethod(method);
                Boolean val = (Boolean) m.invoke(object);
                log.info("Boolean {} = {}", field.getName(), val);
            }

            // 如果类型是Date
            if (isTargetClassType(field, Date.class)) {
                String method = "get" + getMethodName(field.getName());
                Method m = object.getClass().getMethod(method);
                Date val = (Date) m.invoke(object);
                log.info("Date {} = {}", field.getName(), val);
            }

            // 如果类型是 String
            if (isTargetClassType(field, String.class)) {
                String method = "get" + getMethodName(field.getName());
                Method m = object.getClass().getMethod(method);
                String val = (String) m.invoke(object);
                log.info("String {} = {}", field.getName(), val);
            }

            // 如果类型是 Character[]
            if (isTargetClassType(field, Character[].class)) {
                String method = "get" + getMethodName(field.getName());
                Method m = object.getClass().getMethod(method);
                Character[] val = (Character[]) m.invoke(object);
                log.info("Character[] {} = {}", field.getName(), JSON.toJSONString(val));
            }

            // 如果类型是 String[]
            if (isTargetClassType(field, String[].class)) {
                String method = "get" + getMethodName(field.getName());
                Method m = object.getClass().getMethod(method);
                String[] val = (String[]) m.invoke(object);
                log.info("String[] {} = {}", field.getName(), JSON.toJSONString(val));
            }

            // 如果类型是 List
            if (isTargetClassType(field, List.class)) {
                String method = "get" + getMethodName(field.getName());
                Method m = object.getClass().getMethod(method);
                List val = (List) m.invoke(object);
                log.info("List {} = {}", field.getName(), JSON.toJSONString(val));
            }

            // 如果类型是 Map
            if (isTargetClassType(field, Map.class)) {
                String method = "get" + getMethodName(field.getName());
                Method m = object.getClass().getMethod(method);
                Map val = (Map) m.invoke(object);
                log.info("Map {} = {}", field.getName(), JSON.toJSONString(val));
            }

            // 如果类型是 Enum
            if (isTargetClassType(field, Enum.class)) {
                String method = "get" + getMethodName(field.getName());
                Method m = object.getClass().getMethod(method);
                Enum val = (Enum) m.invoke(object);
                log.info("Enum {} = {}", field.getName(), JSON.toJSONString(val));
            }

        }
    }

    /**
     * 把一个字符串的第一个字母大写、效率是最高的、
     *
     * @param fieldName
     * @return
     * @throws Exception
     */
    private static String getMethodName(String fieldName) throws Exception {
        byte[] items = fieldName.getBytes();
        items[0] = (byte) ((char) items[0] - 'a' + 'A');
        return new String(items);
    }

    /**
     * 判断Field是否是指定的类
     *
     * @param field
     * @param targetType
     * @return
     */
    private static boolean isTargetClassType(Field field, Class targetType) {
        return field.getType() == targetType;
    }


}
