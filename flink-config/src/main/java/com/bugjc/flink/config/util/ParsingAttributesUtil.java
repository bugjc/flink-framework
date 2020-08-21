package com.bugjc.flink.config.util;

import com.bugjc.flink.config.model.component.NewField;
import com.bugjc.flink.config.model.component.NewFieldInput;
import com.bugjc.flink.config.model.component.NewFieldOutput;
import com.bugjc.flink.config.model.tree.Trie;
import com.bugjc.flink.config.model.tree.TrieNode;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;

import java.lang.reflect.ParameterizedType;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * 解构对象数据
 *
 * @author aoki
 * @date 2020/8/12
 **/
@Slf4j
public class ParsingAttributesUtil {


    /**
     * 解构对象字段数据
     *
     * @param input
     * @param output
     */
    public static void deconstruction(NewFieldInput input, NewFieldOutput output) {

        for (NewField field : input.getFields()) {
            String fieldName = field.getName();
            String camelName = PointToCamelUtil.camel2Point(fieldName);
            String fieldPrefix = input.getPrefix() + camelName;
            TrieNode trieNode = null;

            //解构层级
            if (!input.getParameterTool().containsKey(fieldPrefix)) {
                // trie 则用来检测 map 和 list 容器
                trieNode = Trie.find(fieldPrefix);
                if (trieNode == null) {
                    continue;
                }

                //当前仅处理参数化类型的字段
                boolean isParameterizedType = field.getGenericType() instanceof ParameterizedType;
                if (!isParameterizedType) {
                    continue;
                }

                ParameterizedType parameterizedType = (ParameterizedType) field.getGenericType();
                if (isTargetClassType(field, List.class)) {
                    output.putContainer(NewFieldInput.Type.ArrayList, fieldName);
                    List<TrieNode> children = trieNode.getChildren();
                    for (TrieNode child : children) {
                        //重写 list 前缀用以匹配字段
                        String newFieldPrefix = fieldPrefix + "." + child.getData();
                        Class<?> entityType = (Class<?>) parameterizedType.getActualTypeArguments()[0];
                        List<NewField> newFields = Arrays.stream(entityType.getDeclaredFields())
                                .map(fieldMap -> new NewField(fieldMap.getName(), fieldMap.getType(), fieldMap.getGenericType()))
                                .collect(Collectors.toList());

                        NewFieldInput newInput = new NewFieldInput(fieldName, NewFieldInput.Type.ArrayList, newFieldPrefix, newFields, input.getParameterTool());
                        deconstruction(newInput, output);
                    }
                }

                if (isTargetClassType(field, Map.class)) {
                    // 获取泛型第一个 class 对象
                    Class<?> keyType = (Class<?>) parameterizedType.getActualTypeArguments()[0];
                    Class<?> valueType = (Class<?>) parameterizedType.getActualTypeArguments()[1];
                    String[] childKeys = trieNode.getChildren().stream().map(TrieNode::getData).toArray(String[]::new);
                    List<NewField> newFields = new ArrayList<>();
                    for (String childKey : childKeys) {
                        newFields.add(new NewField(childKey, valueType, valueType));
                    }

                    output.putContainer(NewFieldInput.Type.HashMap, fieldName);
                    NewFieldInput newInput = new NewFieldInput(fieldName, NewFieldInput.Type.HashMap, fieldPrefix, newFields, input.getParameterTool());
                    deconstruction(newInput, output);
                }
            }


            //基础数据类型的值处理
            input.setCurrentNewField(field);
            basicDataTypeProcessing(input, output);


//            if (isTargetClassType(field, Enum.class)) {
//                String enumReference = trieNode.getChildren().get(0).getData();
//                Object data = EnumUtil.getEnum(enumReference);
//                output.putValue(object, fieldName, data);
//            }
        }

    }

    /**
     * 基础数据类型处理
     */
    private static void basicDataTypeProcessing(NewFieldInput input, NewFieldOutput output){

        NewField field = input.getCurrentNewField();
        String fieldName = field.getName();
        String camelName = PointToCamelUtil.camel2Point(fieldName);
        String fieldPrefix = input.getPrefix() + camelName;

        //获取值
        String value = input.getParameterTool().get(fieldPrefix);
        if (StringUtils.isBlank(value)) {
            return;
        }

        Map<String, Object> object = output.getContainer(input.getType(), input.getName());
        //基本数据类型
        if (isTargetClassType(field, Byte.class)) {
            output.putValue(object, fieldName, new Byte(value));
        }

        if (isTargetClassType(field, Short.class)) {
            output.putValue(object, fieldName, new Short(value));
        }

        if (isTargetClassType(field, Integer.class)) {
            output.putValue(object, fieldName, new Integer(value));
        }

        if (isTargetClassType(field, Long.class)) {
            output.putValue(object, fieldName, new Long(value));
        }

        if (isTargetClassType(field, Float.class)) {
            output.putValue(object, fieldName, new Float(value));
        }

        if (isTargetClassType(field, Double.class)) {
            output.putValue(object, fieldName, new Double(value));
        }

        if (isTargetClassType(field, Character.class)) {
            //默认，取第一个字符
            output.putValue(object, fieldName, value.charAt(0));
        }

        if (isTargetClassType(field, Boolean.class)) {
            output.putValue(object, fieldName, Boolean.valueOf(value));
        }

        if (isTargetClassType(field, String.class)) {
            output.putValue(object, fieldName, value);
        }

        if (isTargetClassType(field, Object.class)) {
            output.putValue(object, fieldName, value);
        }

        if (isTargetClassType(field, Character[].class)) {
            output.putValue(object, fieldName, value.toCharArray());
        }

        if (isTargetClassType(field, String[].class)) {
            output.putValue(object, fieldName, value.split(","));
        }
    }

    /**
     * 判断Field是否是指定的类
     *
     * @param field
     * @param targetType
     * @return
     */
    private static boolean isTargetClassType(NewField field, Class targetType) {
        return field.getType() == targetType;
    }


}
