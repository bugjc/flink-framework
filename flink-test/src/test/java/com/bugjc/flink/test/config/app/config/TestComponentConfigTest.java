package com.bugjc.flink.test.config.app.config;


import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

@Slf4j
class TestComponentConfigTest {

    @Data
    private static class TestComponentConfig implements Serializable {
        /**
         * map 实体对象
         */
        private Map<String, Entity> map1;
    }

    @Test
    void testJson() {
        //复杂对象的序列化与反序列化
        TestComponentConfig testComponentConfig = new TestComponentConfig();
        testComponentConfig.setMap1(new HashMap<String, Entity>() {{
            put("key1", new Entity("1", new String[]{"1.1", "1.2"}, null));
            put("key2", new Entity("1", new String[]{"2.1", "2.2"}, null));
            put("key3", new Entity("1", new String[]{"3.1", "3.2"}, new HashMap<String, Entity2>() {{
                put("field1", new Entity2("field1.1", null));
                put("field2", new Entity2("field2.1", new String[]{"2.2", "2.3"}));
            }}));
        }});

        Gson gson =  new GsonBuilder().disableHtmlEscaping().create();
        String json = gson.toJson(testComponentConfig);
        testComponentConfig = gson.fromJson(json, TestComponentConfig.class);
        Assertions.assertEquals(testComponentConfig.getMap1().get("key3").getEntity3().get("field1").getField1(), "field1.1");
    }

}