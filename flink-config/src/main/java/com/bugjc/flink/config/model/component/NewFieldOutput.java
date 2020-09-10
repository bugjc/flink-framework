package com.bugjc.flink.config.model.component;

import com.bugjc.flink.config.core.enums.ContainerType;
import com.bugjc.flink.config.model.tree.TrieValue;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;

import java.util.HashMap;
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
     * 当前存储数据的对象引用
     */
    private Map<String, Map<String, Object>> tempObject = new HashMap<>();

    /**
     * 按类型获取容器的指针
     *
     * @return 返回容器的指针
     */
    public Map<String, Object> getContainer(GroupContainer groupContainer) {
        TrieValue.print();
        ContainerType upperContainerType = groupContainer.getUpperContainerType();
        ContainerType currentContainerType = groupContainer.getCurrentContainerType();
        if (upperContainerType == ContainerType.None && currentContainerType == ContainerType.Basic) {
            log.info("顶层容器下的基础字段");
            //基础类型直接返回顶层容器
            return data;
        } else if (upperContainerType == ContainerType.None && currentContainerType == ContainerType.HashMap) {
            log.info("顶层容器下的 Map<String,Basic> 容器字段");
            //返回对象引用
            return tempObject.get(groupContainer.getUpperGroupName());

        } else if (upperContainerType == ContainerType.None && currentContainerType == ContainerType.HashMap_Entity) {
            log.info("顶层容器下的 Map<String,Entity> 容器字段");
            //返回对象引用
            return tempObject.get(groupContainer.getUpperGroupName());

        } else if (upperContainerType == ContainerType.HashMap_Entity && currentContainerType == ContainerType.Virtual) {
            log.info("非顶层容器 Map 下的基础字段");
            //返回对象引用
            return tempObject.get(groupContainer.getUpperGroupName());
        } else if (upperContainerType == ContainerType.HashMap_Entity && currentContainerType == ContainerType.Basic) {
            log.info("非顶层容器 Map 下的基础字段");
            //返回对象引用
            return tempObject.get(groupContainer.getUpperGroupName());
        } else if (upperContainerType == ContainerType.HashMap_Entity && currentContainerType == ContainerType.HashMap_Entity) {
            log.info("非顶层容器 Map 下的基础字段");
            //返回对象引用
            return tempObject.get(groupContainer.getUpperGroupName());
        } else {
            return null;
        }
    }


    /**
     * 添加存储数据的容器
     *
     * @param groupContainer
     */
    public void putContainer(GroupContainer groupContainer) {
        TrieValue.insert(groupContainer.getCurrentGroupName());

        //创建并关联容器
        Map<String, Object> upperContainer = tempObject.get(groupContainer.getUpperGroupName());
        if (upperContainer == null) {
            //关联顶层容器
            if (data.containsKey(groupContainer.getCurrentContainerName())) {
                return;
            }

            data.put(groupContainer.getCurrentContainerName(), create(groupContainer));
            return;
        }

        //关联一个新的容器
        upperContainer.put(groupContainer.getCurrentContainerName(), create(groupContainer));
    }


    /**
     * 创建容器
     *
     * @param groupContainer
     * @return
     */
    private Map<String, Object> create(GroupContainer groupContainer) {
        ContainerType upperContainerType = groupContainer.getUpperContainerType();
        ContainerType currentContainerType = groupContainer.getCurrentContainerType();
        //获取当前组容器指定的对象，没有则创建对象并与容器建立从属关系
        Map<String, Object> object = tempObject.get(groupContainer.getCurrentGroupName());
        if (object == null) {
            if (upperContainerType == ContainerType.None && currentContainerType == ContainerType.Basic) {
                log.info("顶层容器下的基础字段");

            } else if (upperContainerType == ContainerType.None && currentContainerType == ContainerType.HashMap) {
                log.info("顶层容器下的 Map<String,Basic> 容器字段");
                object = new HashMap<String, Object>();
                tempObject.put(groupContainer.getCurrentGroupName(), object);

            } else if (upperContainerType == ContainerType.None && currentContainerType == ContainerType.HashMap_Entity) {
                log.info("顶层容器下的 Map<String,Entity> 容器字段");
                object = new HashMap<String, Object>();
                tempObject.put(groupContainer.getCurrentGroupName(), object);

            } else if (upperContainerType == ContainerType.HashMap_Entity && currentContainerType == ContainerType.Virtual) {
                log.info("非顶层容器 Map 下的基础字段");
                object = new HashMap<String, Object>();
                //Map<String,Entity> 的索引是上级组名
                tempObject.put(groupContainer.getCurrentGroupName(), object);
            } else if (upperContainerType == ContainerType.HashMap_Entity && currentContainerType == ContainerType.Basic) {
                log.info("非顶层容器 Map 下的基础字段");
                object = new HashMap<String, Object>();
                //Map<String,Entity> 的索引是上级组名
                tempObject.put(groupContainer.getCurrentGroupName(), object);
            } else if (upperContainerType == ContainerType.HashMap_Entity && currentContainerType == ContainerType.HashMap_Entity) {
                log.info("非顶层容器 Map 下的基础字段");
                object = new HashMap<String, Object>();
                //Map<String,Entity> 的索引是上级组名
                tempObject.put(groupContainer.getCurrentGroupName(), object);
            }
        }
        return object;
    }


    /**
     * 按类型选择容器存储数据
     *
     * @param object
     * @param key
     * @param value
     */
    public void putValue(Map<String, Object> object, String key, Object value) {
        object.put(key, value);
    }
}
