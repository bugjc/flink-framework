package com.bugjc.flink.config.util;

import com.bugjc.flink.config.model.component.NewField;
import com.bugjc.flink.config.model.component.NewFieldInput;
import com.bugjc.flink.config.model.component.NewFieldOutput;
import com.bugjc.flink.config.model.tree.Trie;
import com.bugjc.flink.config.model.tree.TrieNode;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.ArrayUtils;

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
@Slf4j
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

            //基本数据类型
            if (isTargetClassType(field, Byte.class)) {
                Byte data = trieNode.getChildren().size() > 0 ? new Byte(trieNode.getChildren().get(0).getData()) : null;
                output.putValue(object, fieldName, data);
            }

            if (isTargetClassType(field, Short.class)) {
                Short data = trieNode.getChildren().size() > 0 ? new Short(trieNode.getChildren().get(0).getData()) : null;
                output.putValue(object, fieldName, data);
            }

            if (isTargetClassType(field, Integer.class)) {
                Integer data = trieNode.getChildren().size() > 0 ? new Integer(trieNode.getChildren().get(0).getData()) : null;
                output.putValue(object, fieldName, data);
            }

            if (isTargetClassType(field, Long.class)) {
                Long data = trieNode.getChildren().size() > 0 ? new Long(trieNode.getChildren().get(0).getData()) : null;
                output.putValue(object, fieldName, data);
            }

            if (isTargetClassType(field, Float.class)) {
                Float data = trieNode.getChildren().size() > 0 ? new Float(trieNode.getChildren().get(0).getData()) : null;
                output.putValue(object, fieldName, data);
            }

            if (isTargetClassType(field, Double.class)) {
                Double data = trieNode.getChildren().size() > 0 ? new Double(trieNode.getChildren().get(0).getData()) : null;
                output.putValue(object, fieldName, data);
            }

            if (isTargetClassType(field, Character.class)) {
                //默认，区第一个字符
                Character data = trieNode.getChildren().size() > 0 ? trieNode.getChildren().get(0).getData().charAt(0) : null;
                output.putValue(object, fieldName, data);
            }

            if (isTargetClassType(field, Boolean.class)) {
                Boolean data = trieNode.getChildren().size() > 0 ? Boolean.valueOf(trieNode.getChildren().get(0).getData()) : null;
                output.putValue(object, fieldName, data);
            }

            if (isTargetClassType(field, String.class)) {
                String data = trieNode.getChildren().stream().map(TrieNode::getData).collect(Collectors.joining(","));
                output.putValue(object, fieldName, data);
            }

            if (isTargetClassType(field, Character[].class)) {
                Character[] data = trieNode.getChildren().size() > 0 ? ArrayUtils.toObject(trieNode.getChildren().get(0).getData().toCharArray()) : null;
                output.putValue(object, fieldName, data);
            }

            if (isTargetClassType(field, String[].class)) {
                String[] data = trieNode.getChildren().stream().map(TrieNode::getData).toArray(String[]::new);
                output.putValue(object, fieldName, data);
            }

            if (isTargetClassType(field, List.class)) {
                // 当前集合的泛型类型
                Type genericType = field.getGenericType();
                ParameterizedType parameterizedType = (ParameterizedType) genericType;
                // 获取泛型第一个 class 对象
                Class<?> entityType = (Class<?>) parameterizedType.getActualTypeArguments()[0];
                List<NewField> newFields = Arrays.stream(entityType.getDeclaredFields()).map(fieldMap -> new NewField(fieldMap.getName(), fieldMap.getType(), fieldMap.getGenericType())).collect(Collectors.toList());

                output.putContainer(NewFieldInput.Type.ArrayList, fieldName);
                NewFieldInput input1 = new NewFieldInput(fieldName, NewFieldInput.Type.ArrayList, fieldPrefix, newFields);
                deconstruction(input1, output);
            }

            if (isTargetClassType(field, Map.class)) {
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

//            if (isTargetClassType(field, Enum.class)) {
//                String enumReference = trieNode.getChildren().get(0).getData();
//                Object data = EnumUtil.getEnum(enumReference);
//                output.putValue(object, fieldName, data);
//            }
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
