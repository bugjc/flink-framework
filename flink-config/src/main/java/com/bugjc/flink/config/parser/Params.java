package com.bugjc.flink.config.parser;

import com.bugjc.flink.config.model.tree.TrieNode;
import lombok.Getter;
import lombok.Setter;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * 数据分组
 *
 * @author aoki
 * @date 2020/8/12
 **/
public class Params {

    /**
     * 上级分组容器
     */
    @Getter
    private final GroupContainer upperGroupContainer;

    /**
     * 当前处理的字段
     */
    @Getter
    @Setter
    private NewField currentField;

    /**
     * 前缀节点（辅助 Map 、List 类型的对象解析）
     */
    @Getter
    @Setter
    private TrieNode trieNode;

    /**
     * 要处理的字段列表
     */
    @Getter
    private final List<NewField> fields;

    /**
     * 原始数据
     */
    @Getter
    private final Map<String, String> originalData;

    /**
     * 初始化参数对象
     *
     * @param upperGroupContainer   --上级分组容器
     * @param fields                --要处理的字段列表
     * @param originalData          --原数据键值对
     */
    public Params(GroupContainer upperGroupContainer, List<NewField> fields, Map<String, String> originalData) {
        this.upperGroupContainer = upperGroupContainer;
        this.fields = fields;
        this.originalData = originalData;
    }

    /**
     * 创建新的参数对象
     *
     * @param groupContainer        --分组容器
     * @param fields                --要处理的字段列表
     * @param originalData          --原数据键值对
     * @return  参数对象
     */
    public static Params create(GroupContainer groupContainer, List<NewField> fields, Map<String, String> originalData) {
        return new Params(groupContainer, fields, originalData);
    }


    /**
     * 获取要处理的基础类型字段列表
     * @param keyType               --属性字段类型
     * @param valueType             --属性字段值类型
     * @param containerType         --容器类型
     * @return  要处理的字段列表
     */
    public List<NewField> getFields(Class<?> keyType, Type valueType, ContainerType containerType) {
        List<NewField> valueFields = new ArrayList<>();
        List<TrieNode> children = trieNode.getChildren();
        for (TrieNode child : children) {
            NewField newField = new NewField(child.getData(), keyType, valueType, containerType);
            valueFields.add(newField);
        }
        return valueFields;
    }

    /**
     * 获取要处理的 entity 字段列表
     *
     * @param valueType             --属性字段值类型（Class entity）
     * @return  要处理的字段列表
     */
    public List<NewField> getEntityFields(Type valueType) {
        Class<?> entityClass = (Class<?>) valueType;
        return Arrays.stream(entityClass.getDeclaredFields())
                .map(fieldMap -> new NewField(fieldMap.getName(), fieldMap.getType(), fieldMap.getGenericType()))
                .collect(Collectors.toList());
    }


}
