package com.bugjc.flink.config.util;

import com.bugjc.flink.config.model.component.NewField;
import com.bugjc.flink.config.model.component.NewFieldInput;
import com.bugjc.flink.config.model.component.NewFieldOutput;
import com.bugjc.flink.config.model.tree.Trie;
import com.bugjc.flink.config.model.tree.TrieNode;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
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
public class ParsingAttributesUtil {


    /**
     * 解构对象字段数据
     *
     * @param input
     * @param output
     */
    public static void deconstruction(NewFieldInput input, NewFieldOutput output) {

        Map<String, Object> object = output.getContainer(input.getTypeEnum(), input.getName());

        for (NewField field : input.getFields()) {
            String fieldName = field.getName();
            String camelName = PointToCamelUtil.camel2Point(fieldName);
            String fieldPrefix = input.getPrefix() + camelName;
            TrieNode trieNode = Trie.find(fieldPrefix);
            if (trieNode == null) {
                continue;
            }

            if (isTargetClassType(field, String.class)) {
                String data = trieNode.getChildren().stream().map(TrieNode::getData).collect(Collectors.joining(","));
                output.putValue(object, fieldName, data);

            } else if (isTargetClassType(field, String[].class)) {
                String[] data = trieNode.getChildren().stream().map(TrieNode::getData).toArray(String[]::new);
                output.putValue(object, fieldName, data);

            } else if (isTargetClassType(field, List.class)) {
                // 当前集合的泛型类型
                Type genericType = field.getGenericType();
                ParameterizedType parameterizedType = (ParameterizedType) genericType;
                // 获取泛型第一个 class 对象
                Class<?> entityType = (Class<?>) parameterizedType.getActualTypeArguments()[0];
                List<NewField> newFields = Arrays.stream(entityType.getDeclaredFields()).map(fieldMap -> new NewField(fieldMap.getName(), fieldMap.getType(), fieldMap.getGenericType())).collect(Collectors.toList());

                output.putContainer(NewFieldInput.Type.ArrayList, fieldName);
                NewFieldInput input1 = new NewFieldInput(fieldName, NewFieldInput.Type.ArrayList, fieldPrefix, newFields);
                deconstruction(input1, output);

            } else if (isTargetClassType(field, Map.class)) {
                // 当前 Map 的泛型类型
                Type genericType = field.getGenericType();
                ParameterizedType parameterizedType = (ParameterizedType) genericType;
                // 获取泛型第一个 class 对象
                Class<?> keyType = (Class<?>) parameterizedType.getActualTypeArguments()[0];
                Class<?> ValueType = (Class<?>) parameterizedType.getActualTypeArguments()[1];
                String[] data = trieNode.getChildren().stream().map(TrieNode::getData).toArray(String[]::new);
                List<NewField> newFields = new ArrayList<>();
                for (String datum : data) {
                    newFields.add(new NewField(datum, keyType, keyType));
                }

                output.putContainer(NewFieldInput.Type.HashMap, fieldName);
                NewFieldInput input1 = new NewFieldInput(fieldName, NewFieldInput.Type.HashMap, fieldPrefix, newFields);
                deconstruction(input1, output);
            }
        }

    }

    /**
     * 判断Field是否是指定的类
     *
     * @param field
     * @param targetType
     * @return
     */
    public static boolean isTargetClassType(NewField field, Class targetType) {
        return field.getType() == targetType;
    }
}
