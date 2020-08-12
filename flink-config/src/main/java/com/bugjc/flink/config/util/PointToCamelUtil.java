package com.bugjc.flink.config.util;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * 骆驼命名和点命名相互转换
 * @author aoki
 * @date 2020/8/11
 * **/
public class PointToCamelUtil {

    private static Pattern POINT2CAMEL = Pattern.compile("([A-Za-z\\d]+)(.)?");
    private static Pattern CAMEL2POINT = Pattern.compile("[A-Z]");

    /**
     * 点转驼峰法
     *
     * @param point 源字符串
     * @return 转换后的字符串
     */
    public static String point2Camel(String point) {
        if (point == null || "".equals(point)) {
            return "";
        }

        StringBuilder sb = new StringBuilder();

        Matcher matcher = POINT2CAMEL.matcher(point);
        while (matcher.find()) {
            String word = matcher.group();
            sb.append(matcher.start() == 0 ? Character.toLowerCase(word.charAt(0)) : Character.toUpperCase(word.charAt(0)));
            int index = word.lastIndexOf('.');
            if (index > 0) {
                sb.append(word.substring(1, index).toLowerCase());
            } else {
                sb.append(word.substring(1).toLowerCase());
            }
        }
        return sb.toString();
    }

    /**
     * 驼峰法转下划线
     *
     * @param point 源字符串
     * @return 转换后的字符串
     */
    public static String camel2Point(String point) {
        Matcher matcher = CAMEL2POINT.matcher(point);
        StringBuffer sb = new StringBuffer();
        while (matcher.find()) {
            matcher.appendReplacement(sb, "." + matcher.group(0).toLowerCase());
        }
        matcher.appendTail(sb);
        return sb.toString();
    }

    public static void main(String[] args) {
        String point = "com.bugjc.flink.job";
        String camel = point2Camel(point);
        System.out.println(camel);
        System.out.println(camel2Point(camel));
    }
}
