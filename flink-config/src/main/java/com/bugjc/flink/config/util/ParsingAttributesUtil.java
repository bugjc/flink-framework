package com.bugjc.flink.config.util;

import com.bugjc.flink.config.core.constant.Constants;
import com.bugjc.flink.config.core.enums.ContainerType;
import com.bugjc.flink.config.model.component.GroupContainer;
import com.bugjc.flink.config.model.component.NewField;
import com.bugjc.flink.config.model.component.NewFieldInput;
import com.bugjc.flink.config.model.component.NewFieldOutput;
import com.bugjc.flink.config.model.tree.Trie;
import com.bugjc.flink.config.model.tree.TrieNode;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.*;
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

        ContainerType upperContainerType = input.getGroupContainer().getUpperContainerType();
        String groupName = input.getGroupContainer().getCurrentGroupName();
        for (NewField field : input.getFields()) {
            String fieldName = field.getName();
            String camelName = PointToCamelUtil.camel2Point(fieldName);
            String currentGroupName = getGroupName(groupName, camelName);

            //1.获取当前容器对象
            ContainerType currentContainerType = ContainerType.getType(field);
            GroupContainer currentGroupContainer = getCurrentGroupContainer(upperContainerType, currentContainerType, currentGroupName);

            //2.为当前容器创建一个存储数据的对象并将对象的应用保存到表中
            output.putContainer(currentGroupContainer);

            //3.检测是否配置了字段对应的属性
            TrieNode trieNode = Trie.find(currentGroupContainer.getCurrentGroupName());
            if (trieNode == null) {
                //表示配置文件没有配置此属性
                continue;
            }

            //基础字段处理
            if (currentContainerType == ContainerType.Basic) {
                saveData(input, output, currentGroupContainer, field);
                continue;
            }

            if (currentContainerType == ContainerType.ArrayList_Entity) {

                List<NewField> valueFields = new ArrayList<>();
                List<TrieNode> children = trieNode.getChildren();
                for (TrieNode child : children) {
                    NewField newField = new NewField(child.getData(), field.getType(), field.getGenericType(), ContainerType.Virtual_ArrayList_Entity);
                    valueFields.add(newField);
                }

                //获取组名
                GroupContainer nextGroupChildContainer = new GroupContainer()
                        .setCurrentGroupName(currentGroupContainer.getCurrentGroupName())
                        .setUpperContainerType(ContainerType.ArrayList_Entity)
                        .build();

                //递归
                NewFieldInput newInput = new NewFieldInput(nextGroupChildContainer, valueFields, input.getOriginalData());
                deconstruction(newInput, output);
                continue;
            }

            if (currentContainerType == ContainerType.HashMap_Entity) {
                List<NewField> valueFields = new ArrayList<>();
                List<TrieNode> children = trieNode.getChildren();
                for (TrieNode child : children) {
                    NewField newField = new NewField(child.getData(), field.getType(), field.getGenericType(), ContainerType.Virtual_HashMap_Entity);
                    valueFields.add(newField);
                }

                //获取组名
                GroupContainer nextGroupChildContainer = new GroupContainer()
                        .setCurrentGroupName(currentGroupContainer.getCurrentGroupName())
                        .setUpperContainerType(ContainerType.HashMap_Entity)
                        .build();

                //递归
                NewFieldInput newInput = new NewFieldInput(nextGroupChildContainer, valueFields, input.getOriginalData());
                deconstruction(newInput, output);
                continue;
            }

            if (currentGroupContainer.getCurrentContainerType() == ContainerType.HashMap) {
                ParameterizedType parameterizedType = (ParameterizedType) field.getGenericType();
                Type[] types = parameterizedType.getActualTypeArguments();
                Class<?> valueType = (Class<?>) parameterizedType.getActualTypeArguments()[types.length - 1];

                List<NewField> valueFields = new ArrayList<>();
                List<TrieNode> children = trieNode.getChildren();
                for (TrieNode child : children) {
                    NewField newField = new NewField(child.getData(), valueType, valueType, ContainerType.Virtual_HashMap);
                    valueFields.add(newField);
                }

                GroupContainer nextGroupContainer = new GroupContainer()
                        .setCurrentGroupName(currentGroupContainer.getCurrentGroupName())
                        .setUpperContainerType(ContainerType.HashMap)
                        .build();

                NewFieldInput newInput = new NewFieldInput(nextGroupContainer, valueFields, input.getOriginalData());
                deconstruction(newInput, output);
                continue;
            }

            if (currentContainerType == ContainerType.Virtual_HashMap_Entity) {
                ParameterizedType parameterizedType = (ParameterizedType) field.getGenericType();
                Type[] types = parameterizedType.getActualTypeArguments();
                Class<?> valueType = (Class<?>) parameterizedType.getActualTypeArguments()[types.length - 1];
                //获取要处理的 entity 字段
                List<NewField> valueFields = Arrays.stream(valueType.getDeclaredFields())
                        .map(fieldMap -> new NewField(fieldMap.getName(), fieldMap.getType(), fieldMap.getGenericType()))
                        .collect(Collectors.toList());

                GroupContainer nextGroupChildContainer = new GroupContainer()
                        .setCurrentGroupName(currentGroupContainer.getCurrentGroupName())
                        .setUpperContainerType(upperContainerType)
                        .build();

                //递归
                NewFieldInput newInput = new NewFieldInput(nextGroupChildContainer, valueFields, input.getOriginalData());
                deconstruction(newInput, output);
                continue;
            }

            if (currentContainerType == ContainerType.Virtual_HashMap) {
                //普通 HashMap 当基础字段处理
                saveData(input, output, currentGroupContainer, field);
                continue;
            }

            if (currentContainerType == ContainerType.Virtual_ArrayList_Entity) {
                ParameterizedType parameterizedType = (ParameterizedType) field.getGenericType();
                Type[] types = parameterizedType.getActualTypeArguments();
                Class<?> valueType = (Class<?>) parameterizedType.getActualTypeArguments()[types.length - 1];
                //获取要处理的 entity 字段
                List<NewField> valueFields = Arrays.stream(valueType.getDeclaredFields())
                        .map(fieldMap -> new NewField(fieldMap.getName(), fieldMap.getType(), fieldMap.getGenericType()))
                        .collect(Collectors.toList());

                GroupContainer nextGroupChildContainer = new GroupContainer()
                        .setCurrentGroupName(currentGroupContainer.getCurrentGroupName())
                        .setUpperContainerType(upperContainerType)
                        .build();

                //递归
                NewFieldInput newInput = new NewFieldInput(nextGroupChildContainer, valueFields, input.getOriginalData());
                deconstruction(newInput, output);
                continue;
            }

            throw new NullPointerException();
        }
    }

    /**
     * 保存数据
     *
     * @param input
     * @param output
     * @param currentGroupContainer
     * @param currentField
     */
    private static void saveData(NewFieldInput input, NewFieldOutput output, GroupContainer currentGroupContainer, NewField currentField) {
        String value = getValue(input.getOriginalData(), currentGroupContainer.getCurrentGroupName());
        currentField.setKey(currentField.getName());
        currentField.setValue(value);
        //基础数据类型的值处理
        output.putValue(currentGroupContainer, currentField);
    }

    /**
     * 获取当前容器
     *
     * @param upperContainerType
     * @param currentContainerType
     * @param currentGroupName
     * @return
     */
    private static GroupContainer getCurrentGroupContainer(ContainerType upperContainerType, ContainerType currentContainerType, String currentGroupName) {
        GroupContainer currentGroupContainer = null;
        if (upperContainerType == ContainerType.None) {
            currentGroupContainer = new GroupContainer()
                    .setCurrentContainerType(currentContainerType)
                    .setCurrentGroupName(currentGroupName)
                    .setUpperContainerType(upperContainerType)
                    .build();
        } else if ((upperContainerType == ContainerType.HashMap_Entity) || upperContainerType == ContainerType.HashMap) {
            currentGroupContainer = new GroupContainer()
                    .setCurrentContainerType(currentContainerType)
                    .setCurrentGroupName(currentGroupName)
                    .setUpperContainerType(upperContainerType)
                    .buildLevel1();
        } else if ((upperContainerType == ContainerType.ArrayList_Entity) || (upperContainerType == ContainerType.ArrayList)) {
            currentGroupContainer = new GroupContainer()
                    .setCurrentContainerType(currentContainerType)
                    .setCurrentGroupName(currentGroupName)
                    .setUpperContainerType(upperContainerType)
                    .buildLevel1();
        } else {
            throw new NullPointerException();
        }
        return currentGroupContainer;
    }

    /**
     * 获取分组名
     *
     * @param params
     * @return
     */
    private static String getGroupName(String... params) {
        StringBuilder sb = new StringBuilder();
        for (String param : params) {
            sb.append(param);
            if (!param.endsWith(Constants.SUFFIX)) {
                sb.append(Constants.SUFFIX);
            }
        }
        return sb.toString();
    }

    /**
     * 获取 Value
     *
     * @param originalData
     * @param key
     * @return
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
