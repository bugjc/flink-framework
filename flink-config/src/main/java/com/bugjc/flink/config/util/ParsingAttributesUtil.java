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

            ContainerType currentContainerType = ContainerType.getType(field);
            GroupContainer currentGroupContainer = null;
            if (upperContainerType == ContainerType.None && currentContainerType == ContainerType.Basic) {
                log.info("顶层容器下的基础字段");
                currentGroupContainer = new GroupContainer()
                        .setCurrentContainerType(ContainerType.getType(field))
                        .setCurrentGroupName(currentGroupName)
                        .setUpperContainerType(upperContainerType)
                        .build();

            } else if (upperContainerType == ContainerType.None && currentContainerType == ContainerType.HashMap) {
                log.info("顶层容器下的 Map<String,Basic> 容器字段");
                currentGroupContainer = new GroupContainer()
                        .setCurrentContainerType(currentContainerType)
                        .setCurrentGroupName(currentGroupName)
                        .setUpperContainerType(upperContainerType)
                        .build();

            } else if (upperContainerType == ContainerType.None && currentContainerType == ContainerType.HashMap_Entity) {
                log.info("顶层容器下的 Map<String,Entity> 容器字段");
                currentGroupContainer = new GroupContainer()
                        .setCurrentContainerType(currentContainerType)
                        .setCurrentGroupName(currentGroupName)
                        .setUpperContainerType(upperContainerType)
                        .build();

            } else if (upperContainerType == ContainerType.HashMap_Entity && currentContainerType == ContainerType.Virtual) {
                log.info("非顶层容器 Map 下的虚拟字段");
                currentGroupContainer = new GroupContainer()
                        .setCurrentContainerType(currentContainerType)
                        .setCurrentGroupName(currentGroupName)
                        .setUpperContainerType(upperContainerType)
                        .buildLevel1();
            } else if (upperContainerType == ContainerType.HashMap_Entity && currentContainerType == ContainerType.Basic) {
                log.info("非顶层容器 Map 下的基础字段");
                currentGroupContainer = new GroupContainer()
                        .setCurrentContainerType(currentContainerType)
                        .setCurrentGroupName(currentGroupName)
                        .setUpperContainerType(upperContainerType)
                        .buildLevel1();
            } else if (upperContainerType == ContainerType.HashMap_Entity && currentContainerType == ContainerType.HashMap_Entity) {
                currentGroupContainer = new GroupContainer()
                        .setCurrentContainerType(currentContainerType)
                        .setCurrentGroupName(currentGroupName)
                        .setUpperContainerType(upperContainerType)
                        .buildLevel1();
            } else {
                throw new NullPointerException();
            }

            //为当前字段创建并关联一个容器
            assert currentGroupContainer != null;
            output.putContainer(currentGroupContainer);

            //尝试获取值
            String value = getValue(input.getOriginalData(), currentGroupContainer.getCurrentGroupName());
            field.setKey(currentGroupContainer.getCurrentGroupName());
            field.setValue(value);

            //基础字段处理
            if (currentGroupContainer.getCurrentContainerType() == ContainerType.Basic) {
                //基础数据类型的值处理
                Map<String, Object> object = output.getContainer(currentGroupContainer);
                output.putValue(object, fieldName, TypeUtil.getTypeData(field));
                continue;
            }

            // trie 则用来检测 map 和 list 容器
            TrieNode trieNode = Trie.find(currentGroupContainer.getCurrentGroupName());
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
//            if (currentContainerType == ContainerType.ArrayList) {
//                //仅支持 list bean 类型的解析
//                Class<?> valueType = (Class<?>) parameterizedType.getActualTypeArguments()[0];
//                if (!TypeUtil.isJavaBean(valueType)) {
//                    continue;
//                }
//
//                List<NewField> newFields = Arrays.stream(valueType.getDeclaredFields())
//                        .map(fieldMap -> new NewField(fieldMap.getName(), fieldMap.getType(), fieldMap.getGenericType()))
//                        .collect(Collectors.toList());
//                List<TrieNode> children = trieNode.getChildren();
//                for (TrieNode child : children) {
//                    //list 分组名需要往前多走一级
//                    String nextGroupName = getGroupName(currentGroupContainer.getCurrentGroupName(), child.getData());
//                    GroupContainer nextGroupContainer = new GroupContainer()
//                            .setCurrentContainerType(ContainerType.ArrayList)
//                            .setCurrentGroupName(nextGroupName)
//                            .buildLevel2();
//                    output.putContainer(nextGroupContainer);
//                    NewFieldInput newInput = new NewFieldInput(nextGroupContainer, newFields, input.getOriginalData());
//                    deconstruction(newInput, output);
//                }
//            }

            if (currentContainerType == ContainerType.HashMap_Entity) {
                //遍历 Map 的 key,然后为每个 key 递归构建 entity 对象
                List<NewField> valueFields = new ArrayList<>();
                List<TrieNode> children = trieNode.getChildren();
                for (TrieNode child : children) {
                    NewField newField = new NewField(child.getData(), field.getType(), field.getGenericType(), true);
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
            }

            //当前字段是一个 MAP,递归处理
            if (currentGroupContainer.getCurrentContainerType() == ContainerType.HashMap) {
                Type[] types = parameterizedType.getActualTypeArguments();
                Class<?> valueType = (Class<?>) parameterizedType.getActualTypeArguments()[types.length - 1];


                List<NewField> valueFields = new ArrayList<>();
                List<TrieNode> children = trieNode.getChildren();
                for (TrieNode child : children) {
                    NewField newField = new NewField(child.getData(), valueType, valueType, true);
                    valueFields.add(newField);
                }

                GroupContainer nextGroupContainer = new GroupContainer()
                        .setCurrentContainerType(ContainerType.HashMap)
                        .setCurrentGroupName(currentGroupContainer.getCurrentGroupName())
                        .build();

                NewFieldInput newInput = new NewFieldInput(nextGroupContainer, valueFields, input.getOriginalData());
                deconstruction(newInput, output);
            }

            if (currentContainerType == ContainerType.Virtual) {
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
            }
        }
    }

    /**
     * 获取组前缀
     *
     * @param params
     * @return
     */
    private static String getGroupPrefix(String... params) {
        return format(params);
    }

    private static String getGroupName(String... params) {
        return format(params);
    }

    private static String format(String... params) {
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
