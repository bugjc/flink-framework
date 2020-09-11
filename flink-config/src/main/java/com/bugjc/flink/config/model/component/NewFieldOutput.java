package com.bugjc.flink.config.model.component;

import com.bugjc.flink.config.core.enums.ContainerType;
import com.bugjc.flink.config.model.tree.TrieValue;
import com.bugjc.flink.config.util.TypeUtil;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 解析的组件数据对象
 *
 * @author aoki
 * @date 2020/8/12
 **/
@Data
@Slf4j
public class NewFieldOutput {

    /**
     * 存储解析好的组件数据
     */
    private Map<String, Object> data = new HashMap<>();

    /**
     * 当前存储数据的对象引用（HashMap * 类型）
     */
    private Map<String, Object> objectReferenceTable = new HashMap<>();


    /**
     * 按类型获取容器的指针
     *
     * @return 返回容器的指针
     */
    private Object getContainer(GroupContainer groupContainer) {
        TrieValue.print();
        ContainerType upperContainerType = groupContainer.getUpperContainerType();
        ContainerType currentContainerType = groupContainer.getCurrentContainerType();
        if (upperContainerType == ContainerType.None && currentContainerType == ContainerType.Basic) {
            //基础类型直接返回顶层容器
            return data;
        }

        if (upperContainerType == ContainerType.HashMap_Entity) {
            //返回对象引用
            return objectReferenceTable.get(groupContainer.getUpperGroupName());
        }

        if (upperContainerType == ContainerType.HashMap) {
            //返回对象引用
            return objectReferenceTable.get(groupContainer.getUpperGroupName());
        }

        if (upperContainerType == ContainerType.ArrayList_Entity) {
            //返回对象引用
            return objectReferenceTable.get(groupContainer.getUpperGroupName());
        }

        throw new NullPointerException();
    }


    /**
     * 添加存储数据的容器
     *
     * @param groupContainer
     */
    public void putContainer(GroupContainer groupContainer) {
        TrieValue.insert(groupContainer.getCurrentGroupName());
        //创建并关联容器
        Object upperContainer = objectReferenceTable.get(groupContainer.getUpperGroupName());
        if (upperContainer == null) {
            if (data.containsKey(groupContainer.getCurrentContainerName())) {
                return;
            }

            //关联顶层容器
            data.put(groupContainer.getCurrentContainerName(), create(groupContainer));
            return;
        }

        if (upperContainer instanceof List) {
            List<Map<String, Object>> list = (List<Map<String, Object>>) upperContainer;
            //上级容器关联一个新的容器
            Object object = create(groupContainer);
            if (object == null) {
                return;
            }
            list.add((Map<String, Object>) object);
            return;

        } else if (upperContainer instanceof Map) {
            Map<String, Object> map = (Map<String, Object>) upperContainer;
            if (map.containsKey(groupContainer.getCurrentContainerName())) {
                return;
            }
            //上级容器关联一个新的容器
            map.put(groupContainer.getCurrentContainerName(), create(groupContainer));
            return;
        }

        throw new NullPointerException();

    }


    /**
     * 创建容器
     *
     * @param groupContainer
     * @return
     */
    private Object create(GroupContainer groupContainer) {
        ContainerType upperContainerType = groupContainer.getUpperContainerType();
        ContainerType currentContainerType = groupContainer.getCurrentContainerType();
        //获取当前组容器指定的对象，没有则创建对象并与容器建立从属关系
        Object object = objectReferenceTable.get(groupContainer.getCurrentGroupName());
        if (object == null) {

            if (currentContainerType == ContainerType.Basic) {
                return null;
            }

            //None And HashMap
            if ((upperContainerType == ContainerType.None && currentContainerType == ContainerType.HashMap)
                    || (upperContainerType == ContainerType.None && currentContainerType == ContainerType.HashMap_Entity)) {
                object = new HashMap<String, Object>();
                objectReferenceTable.put(groupContainer.getCurrentGroupName(), object);
                return object;
            }

            //None And ArrayList
            if ((upperContainerType == ContainerType.None && currentContainerType == ContainerType.ArrayList)
                    || (upperContainerType == ContainerType.None && currentContainerType == ContainerType.ArrayList_Entity)) {
                object = new ArrayList<>();
                objectReferenceTable.put(groupContainer.getCurrentGroupName(), object);
                return object;
            }

            //ArrayList_Entity
            if ((upperContainerType == ContainerType.ArrayList_Entity && currentContainerType == ContainerType.Virtual_ArrayList_Entity)
                    || (upperContainerType == ContainerType.ArrayList_Entity && currentContainerType == ContainerType.HashMap_Entity)) {
                object = new HashMap<String, Object>();
                objectReferenceTable.put(groupContainer.getCurrentGroupName(), object);
                return object;
            }

            //HashMap_Entity
            if ((upperContainerType == ContainerType.HashMap_Entity && currentContainerType == ContainerType.HashMap_Entity)
                    || (upperContainerType == ContainerType.HashMap_Entity && currentContainerType == ContainerType.Virtual_HashMap_Entity)
                    || (upperContainerType == ContainerType.HashMap_Entity && currentContainerType == ContainerType.HashMap)
                    || (upperContainerType == ContainerType.HashMap_Entity && currentContainerType == ContainerType.Virtual_HashMap)) {
                object = new HashMap<String, Object>();
                objectReferenceTable.put(groupContainer.getCurrentGroupName(), object);
                return object;
            }

            //HashMap
            if ((upperContainerType == ContainerType.HashMap && currentContainerType == ContainerType.Virtual_HashMap)) {
                object = new HashMap<String, Object>();
                objectReferenceTable.put(groupContainer.getCurrentGroupName(), object);
                return object;
            }


            throw new NullPointerException();
        }
        return object;
    }


    /**
     * 按类型选择容器存储数据
     *
     * @param currentGroupContainer --当前容器组
     * @param currentField          --当前解析到的字段数据
     */
    public void putValue(GroupContainer currentGroupContainer, NewField currentField) {
        Object object = this.getContainer(currentGroupContainer);
        ((Map) object).put(currentField.getName(), TypeUtil.getTypeData(currentField));
    }
}
