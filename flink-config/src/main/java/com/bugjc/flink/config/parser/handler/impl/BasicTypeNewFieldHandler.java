package com.bugjc.flink.config.parser.handler.impl;

import com.bugjc.flink.config.parser.Container;
import com.bugjc.flink.config.parser.NewField;
import com.bugjc.flink.config.parser.Params;
import com.bugjc.flink.config.parser.handler.NewFieldHandler;
import org.apache.commons.lang3.StringUtils;

import java.util.Map;
import java.util.TreeMap;

/**
 * 基础字段处理器
 *
 * @author aoki
 * @date 2020/9/16
 **/
public class BasicTypeNewFieldHandler implements NewFieldHandler {

    public final static BasicTypeNewFieldHandler INSTANCE = new BasicTypeNewFieldHandler();

    @Override
    public void process(Params input, Container output) {
        NewField currentField = input.getCurrentField();
        String value = getValue(input.getOriginalData(), output.getCurrentGroupContainer().getCurrentGroupName());
        //基础数据类型的值处理
        output.putContainerValue(currentField.getName(), currentField.getType(), value);
    }

    /**
     * 获取 Value
     *
     * @param originalData --原数据键值对
     * @param key          --当前分组名（全局唯一）
     * @return 属性字段值
     */
    private static String getValue(Map<String, String> originalData, String key) {
        String value = originalData.get(key);
        if (StringUtils.isBlank(value)) {
            return null;
        }

        //获取变量集合，注意：不允许嵌套变量，如：${parent${child}} 是错误的写法，正确的应该是 ${parent}${child}
        Map<String, String> variableMap = new TreeMap<>();
        for (int i = 0; i < value.length(); i++) {
            if (value.charAt(i) == '$' && value.charAt(++i) == '{') {
                int begin = i - 1;
                for (int j = ++i; j < value.length(); j++) {
                    if (value.charAt(j) == '}') {
                        int end = j + 1;
                        i = j;
                        String varKey = value.substring(begin, end);
                        String valueKey = varKey.substring(2, varKey.length() - 1);
                        String varValue = originalData.get(valueKey);
                        variableMap.put(varKey, varValue);
                        break;
                    }
                }
            }
        }

        //替换变量的值
        for (String varKey : variableMap.keySet()) {
            value = value.replace(varKey, variableMap.get(varKey));
        }

        return value;
    }
}
