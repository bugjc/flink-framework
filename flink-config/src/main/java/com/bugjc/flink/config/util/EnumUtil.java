package com.bugjc.flink.config.util;

import java.lang.reflect.Method;
import java.security.InvalidParameterException;

/**
 * 枚举工具类
 *
 * @author aoki
 * @date 2020/8/14
 **/
public class EnumUtil {

    /**
     * 获取指定枚举对象
     *
     * @param enumReference --枚举引用值，如：com.bugjc.flink.test.config.app.config.TypeEnum.Open
     * @return 枚举对象
     * @throws Exception
     */
    public static Object getEnum(String enumReference) throws InvalidParameterException {
        try {
            String className = enumReference.substring(0, enumReference.lastIndexOf("."));
            String enumName = enumReference.substring(enumReference.lastIndexOf(".") + 1);

            //获取Class对象
            Class<?> enumClass = Class.forName(className);

            // 获取所有常量
            Object[] objects = enumClass.getEnumConstants();

            //获取指定方法
            Method method = enumClass.getMethod("name");
            for (Object obj : objects) {
                if (method.invoke(obj).equals(enumName)) {
                    //返回指定方法名的枚举对象
                    return obj;
                }
            }
            return null;
        } catch (Exception ex) {
            throw new InvalidParameterException();
        }

    }
}
