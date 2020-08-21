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
     * 按类型获取容器的指针
     *
     * @param type --数据类型
     * @param key      --容器名
     * @return 返回容器的指针
     */
    public Map<String, Object> getContainer(NewFieldInput.Type type, String key) {
        if (type == NewFieldInput.Type.ArrayList) {
            List<Object> list = (List<Object>) data.get(key);
            Map<String, Object> object = new HashMap<>();
            list.add(object);
            // ArrayList 返回一个新的对象引用
            return object;
        } else if (type == NewFieldInput.Type.HashMap) {
            // HashMap 返回对象的引用
            return (Map<String, Object>) data.get(key);
        } else {
            //基础类型直接返回顶层容器
            return data;
        }

    }


    /**
     * 按类型添加一个容器
     *
     * @param typeEnum --数据类型
     * @param key      --容器名
     */
    public void putContainer(Enum<NewFieldInput.Type> typeEnum, String key) {
        if (typeEnum == NewFieldInput.Type.ArrayList) {
            data.put(key, new ArrayList<>());
        } else if (typeEnum == NewFieldInput.Type.HashMap) {
            data.put(key, new HashMap<>());
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
