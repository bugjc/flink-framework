package com.bugjc.flink.config.model.component;

import lombok.Data;

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
public class NewFieldOutput {

    /**
     * 存储解析好的组件数据
     */
    private Map<String, Object> data = new HashMap<>();

    /**
     * 当前存储数据的对象引用
     */
    private Map<String, Object> tempObject = new HashMap<>();

    /**
     * 按类型获取容器的指针
     *
     * @param groupType   --容器数据类型
     * @param groupName   --容器组名（取自类的属性名）     如：datasourceList
     * @param groupPrefix --容器组前缀                    如：com.bugjc.map.key1
     * @return 返回容器的指针
     */
    public Object getContainer(NewFieldInput.Type groupType, String groupName, String groupPrefix) {
        groupPrefix = groupPrefix.substring(0, groupPrefix.length() - 1);
        if (groupType == NewFieldInput.Type.ArrayList) {
            //返回一个对象引用
            String dataObjectKey = groupName + groupPrefix;
            return tempObject.get(dataObjectKey);
        } else if (groupType == NewFieldInput.Type.HashMap) {
            // HashMap 返回对象的引用
            return data.get(groupName);
        } else if (groupType == NewFieldInput.Type.HashMap_Entity) {
            Map<String, Object> map = (Map<String, Object>) data.get(groupName);
            String key = groupPrefix.substring(groupPrefix.lastIndexOf(".") + 1);
            //返回 HashMap key 的引用
            return map.get(key);
        } else {
            //基础类型直接返回顶层容器
            return data;
        }
    }


    /**
     * 按类型添加一个容器
     *
     * @param groupType   --容器数据类型
     * @param groupName   --容器组名（取自类的属性名）     如：datasourceList
     * @param groupPrefix --容器组前缀                    如：com.bugjc.map.key1
     *                    备注：基本类型的数据直接共享顶层的{@link data}容器
     */
    public void putContainer(Enum<NewFieldInput.Type> groupType, String groupName, String groupPrefix) {
        //格式前缀
        groupPrefix = groupPrefix.substring(0, groupPrefix.length() - 1);
        if (groupType == NewFieldInput.Type.ArrayList) {
            List<Map<String, Object>> listContainer = (List<Map<String, Object>>) data.get(groupName);
            if (listContainer == null) {
                listContainer = new ArrayList<>();
            }
            String childContainer = groupName + groupPrefix;
            Map<String, Object> object = (Map<String, Object>) tempObject.get(childContainer);
            if (object == null) {
                object = new HashMap<>();
                listContainer.add(object);
                data.put(groupName, listContainer);
                tempObject.put(childContainer, object);
            }

        } else if (groupType == NewFieldInput.Type.HashMap) {
            //创建组名的 HashMap 对象
            data.put(groupName, new HashMap<>());
        } else if (groupType == NewFieldInput.Type.HashMap_Entity) {
            Map<String, Object> mapContainer = (Map<String, Object>) data.get(groupName);
            if (mapContainer == null) {
                mapContainer = new HashMap<>();
            }
            String key = groupPrefix.substring(groupPrefix.lastIndexOf(".") + 1);
            mapContainer.put(key, new HashMap<>(8));
            //顶层容器加入组名的 mapContainer
            data.put(groupName, mapContainer);
        }

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
