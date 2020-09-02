package com.bugjc.flink.config.util;

import com.bugjc.flink.config.model.component.NewField;
import com.bugjc.flink.config.model.component.NewFieldInput;
import com.bugjc.flink.config.model.component.NewFieldOutput;
import com.bugjc.flink.config.model.tree.Trie;
import com.bugjc.flink.config.model.tree.TrieNode;
import lombok.extern.slf4j.Slf4j;

import java.lang.reflect.ParameterizedType;
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

        // list 容器每次调用会先创建一个容器并返回引用，这里固先保存引用，在其后的循环获取 list 属性的时候使用 output.getCurrentObject() 获取同一个对象的引用。

        for (NewField field : input.getFields()) {
            String fieldName = field.getName();
            String camelName = PointToCamelUtil.camel2Point(fieldName);
            String key = input.getGroupPrefix() + camelName;
            //尝试获取值
            String value = input.getOriginalData().get(key);
            field.setKey(key);
            field.setValue(value);

            //解构层级
            if (!input.getOriginalData().containsKey(key)) {
                // trie 则用来检测 map 和 list 容器
                TrieNode trieNode = Trie.find(key);
                if (trieNode == null) {
                    //表示配置文件没有配置此属性
                    continue;
                }

                //当前仅处理参数化类型的字段
                boolean isParameterizedType = field.getGenericType() instanceof ParameterizedType;
                if (!isParameterizedType) {
                    continue;
                }

                ParameterizedType parameterizedType = (ParameterizedType) field.getGenericType();
                if (isTargetClassType(field, List.class)) {
                    //仅支持 list bean 类型的解析
                    Class<?> valueType = (Class<?>) parameterizedType.getActualTypeArguments()[0];
                    if (!TypeUtil.isJavaBean(valueType)) {
                        continue;
                    }

                    List<NewField> newFields = Arrays.stream(valueType.getDeclaredFields())
                            .map(fieldMap -> new NewField(fieldMap.getName(), fieldMap.getType(), fieldMap.getGenericType()))
                            .collect(Collectors.toList());
                    List<TrieNode> children = trieNode.getChildren();
                    for (TrieNode child : children) {
                        //重写 list 前缀用以匹配字段
                        String groupPrefix = getGroupPrefix(key,child.getData());
                        output.putContainer(NewFieldInput.Type.ArrayList, fieldName, groupPrefix);
                        NewFieldInput newInput = new NewFieldInput(fieldName, NewFieldInput.Type.ArrayList, groupPrefix, newFields, input.getOriginalData());
                        deconstruction(newInput, output);
                    }
                }

                if (isTargetClassType(field, Map.class)) {
                    // 获取泛型第一个 class 对象
                    Class<?> keyType = (Class<?>) parameterizedType.getActualTypeArguments()[0];
                    Class<?> valueType = (Class<?>) parameterizedType.getActualTypeArguments()[1];

                    List<NewField> newFields;
                    if (TypeUtil.isJavaBean(valueType)) {
                        newFields = Arrays.stream(valueType.getDeclaredFields())
                                .map(fieldMap -> new NewField(fieldMap.getName(), fieldMap.getType(), fieldMap.getGenericType()))
                                .collect(Collectors.toList());

                        List<TrieNode> children = trieNode.getChildren();
                        for (TrieNode child : children) {
                            //重写 MAP 前缀用以匹配字段
                            String groupPrefix = getGroupPrefix(key, child.getData());
                            output.putContainer(NewFieldInput.Type.HashMap_Entity, fieldName, groupPrefix);
                            NewFieldInput newInput = new NewFieldInput(fieldName, NewFieldInput.Type.HashMap_Entity, groupPrefix, newFields, input.getOriginalData());
                            deconstruction(newInput, output);
                        }
                    } else {
                        newFields = TypeUtil.getNewFields(valueType, trieNode);
                        String groupPrefix = getGroupPrefix(key);
                        output.putContainer(NewFieldInput.Type.HashMap, fieldName, groupPrefix);
                        NewFieldInput newInput = new NewFieldInput(fieldName, NewFieldInput.Type.HashMap, groupPrefix, newFields, input.getOriginalData());
                        deconstruction(newInput, output);
                    }

                }
            } else {
                //基础数据类型的值处理
                Map<String, Object> object = (Map<String, Object>) output.getContainer(input.getGroupType(), input.getGroupName(), input.getGroupPrefix());
                output.putValue(object, fieldName, TypeUtil.getTypeData(field));
            }


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
    private static void basicDataTypeProcessing(NewFieldInput input, NewFieldOutput output) {

//        NewField field = input.getCurrentNewField();
//        String fieldName = field.getName();
//        String camelName = PointToCamelUtil.camel2Point(fieldName);
//        String key = input.getPrefix() + camelName;
//
//        //获取值
//        String value = input.getParameterTool().get(key);
//        if (StringUtils.isBlank(value)) {
//            return;
//        }
//
//        Map<String, Object> object = (Map<String, Object>) output.getContainer(input.getType(), input.getName(), input.getPrefix());
//        output.putValue(object, fieldName, TypeUtil.getTypeData(field));

        //基本数据类型
//        if (isTargetClassType(field, Byte.class)) {
//            output.putValue(object, fieldName, new Byte(value));
//        }
//
//        if (isTargetClassType(field, Short.class)) {
//            output.putValue(object, fieldName, new Short(value));
//        }
//
//        if (isTargetClassType(field, Integer.class)) {
//            output.putValue(object, fieldName, new Integer(value));
//        }
//
//        if (isTargetClassType(field, Long.class)) {
//            output.putValue(object, fieldName, new Long(value));
//        }
//
//        if (isTargetClassType(field, Float.class)) {
//            output.putValue(object, fieldName, new Float(value));
//        }
//
//        if (isTargetClassType(field, Double.class)) {
//            output.putValue(object, fieldName, new Double(value));
//        }
//
//        if (isTargetClassType(field, Character.class)) {
//            //默认，取第一个字符
//            output.putValue(object, fieldName, value.charAt(0));
//        }
//
//        if (isTargetClassType(field, Boolean.class)) {
//            output.putValue(object, fieldName, Boolean.valueOf(value));
//        }
//
//        if (isTargetClassType(field, String.class)) {
//            output.putValue(object, fieldName, value);
//        }
//
//        if (isTargetClassType(field, Object.class)) {
//            output.putValue(object, fieldName, value);
//        }
//
//        if (isTargetClassType(field, Character[].class)) {
//            output.putValue(object, fieldName, value.toCharArray());
//        }
//
//        if (isTargetClassType(field, String[].class)) {
//            output.putValue(object, fieldName, value.split(","));
//        }
//
//        if (isTargetClassType(field, List.class)) {
//            //仅处理泛型是基本类型的 list
//            output.putValue(object, fieldName, Arrays.stream(value.split(",")).collect(Collectors.toList()));
//        }
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

    /**
     * 判断Field是否是指定的类
     *
     * @param type
     * @param targetType
     * @return
     */
    private static boolean isTargetClassType(Class<?> type, Class targetType) {
        return type == targetType;
    }

    /**
     * 获取组前缀
     *
     * @param params
     * @return
     */
    private static String getGroupPrefix(String... params) {
        String suffix = ".";
        StringBuilder sb = new StringBuilder();
        for (String param : params) {
            sb.append(param);
            if (!param.endsWith(suffix)) {
                sb.append(suffix);
            }
        }
        return sb.toString();
    }


}
