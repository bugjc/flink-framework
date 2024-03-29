package com.bugjc.flink.config.parser;

import com.bugjc.flink.config.parser.converter.NewFieldValueConverterUtil;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;

import java.lang.reflect.Type;
import java.util.*;

/**
 * 解析的组件数据对象
 *
 * @author aoki
 * @date 2020/8/12
 **/
@Data
@Slf4j
public class Container {

    /**
     * 存储解析好的组件数据
     */
    private Map<String, Object> data = new HashMap<>();

    /**
     * 当前存储数据的对象引用（HashMap * 类型）
     */
    private Map<String, Object> objectReferenceTable = new HashMap<>();

    /**
     * 当前分组容器
     */
    private GroupContainer currentGroupContainer;

    /**
     * 按类型获取容器的指针
     *
     * @return 返回容器的指针
     */
    private Object getContainer(GroupContainer groupContainer) {
        ContainerType upperContainerType = groupContainer.getUpperContainerType();
        if (upperContainerType == ContainerType.None) {
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

        if (upperContainerType == ContainerType.ArrayList) {
            //返回对象引用
            return objectReferenceTable.get(groupContainer.getCurrentGroupName());
        }

        throw new NullPointerException("getContainer() 缺少" + upperContainerType + "容器类型的处理方式。");
    }


    /**
     * 添加存储数据的容器
     *
     * @param groupContainer --当前分组容器
     */
    public void putContainer(GroupContainer groupContainer) {
        //创建并关联容器
        this.currentGroupContainer = groupContainer;
        Object upperContainer = objectReferenceTable.get(groupContainer.getUpperGroupName());
        if (upperContainer == null) {
            if (data.containsKey(groupContainer.getCurrentContainerName())) {
                return;
            }

            //关联顶层容器
            data.put(groupContainer.getCurrentContainerName(), createContainerObject(groupContainer));
            return;
        }

        if (upperContainer instanceof List) {
            List<Object> list = (List<Object>) upperContainer;
            //上级容器关联一个新的容器
            Object object = createContainerObject(groupContainer);
            if (object == null) {
                return;
            }
            list.add(object);
            return;

        } else if (upperContainer instanceof Map) {
            Map<String, Object> map = (Map<String, Object>) upperContainer;
            if (map.containsKey(groupContainer.getCurrentContainerName())) {
                return;
            }
            //上级容器关联一个新的容器
            map.put(groupContainer.getCurrentContainerName(), createContainerObject(groupContainer));
            return;
        }

        throw new NullPointerException();

    }


    /**
     * 创建容器的对象引用
     *
     * @param groupContainer --当前分组容器
     * @return 返回当前分组容器的引用
     */
    private Object createContainerObject(GroupContainer groupContainer) {
        ContainerType currentContainerType = groupContainer.getCurrentContainerType();
        //获取当前组容器指定的对象，没有则创建对象并与容器建立从属关系
        Object object = objectReferenceTable.get(groupContainer.getCurrentGroupName());
        if (object == null) {

            if (currentContainerType == ContainerType.Basic || currentContainerType == ContainerType.Virtual_HashMap) {
                return null;
            }

            // HashMap
            if (currentContainerType == ContainerType.HashMap
                    || currentContainerType == ContainerType.HashMap_Entity
                    || currentContainerType == ContainerType.Virtual_ArrayList_Entity
                    || currentContainerType == ContainerType.Virtual_HashMap_Entity) {
                object = new HashMap<String, Object>();
                objectReferenceTable.put(groupContainer.getCurrentGroupName(), object);
                return object;
            }

            // ArrayList
            if (currentContainerType == ContainerType.ArrayList
                    || currentContainerType == ContainerType.Virtual_ArrayList
                    || currentContainerType == ContainerType.ArrayList_Entity) {
                object = new ArrayList<>();
                objectReferenceTable.put(groupContainer.getCurrentGroupName(), object);
                return object;
            }

            throw new NullPointerException();
        }
        return object;
    }


    /**
     * 选择当前容器插入数据
     * @param fieldName     --字段名
     * @param type          --字段类型
     * @param value         --字段值
     */
    public void putContainerValue(String fieldName, Type type, String value) {
        Object object = this.getContainer(currentGroupContainer);
        if (object == null) {
            throw new NullPointerException();
        }

        putContainerValue(object, fieldName, type, value);
    }

    /**
     * 插入数据到指定的容器中
     * @param object        --容器引用
     * @param fieldName     --字段名
     * @param type          --字段类型
     * @param value         --字段值
     */
    private void putContainerValue(Object object, String fieldName, Type type, String value) {
        if (object instanceof List) {
            List<Object> list = ((List<Object>) object);
            list.addAll((Collection) Objects.requireNonNull(NewFieldValueConverterUtil.getNewFieldValue(type, value)));
        } else {
            Map<String, Object> map = ((Map<String, Object>) object);
            if (map.containsKey(fieldName) && map.get(fieldName) != null) {
                object = map.get(fieldName);
                putContainerValue(object, fieldName, type, value);
                return;
            }
            map.put(fieldName, NewFieldValueConverterUtil.getNewFieldValue(type, value));
        }
    }
}
