package com.bugjc.flink.config.util;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

/**
 * Gson 单例工具类
 *
 * @author 杨青 2022/8/11
 **/
public class GsonUtil {
    private Gson gson;
    private static volatile GsonUtil mInstance = null;

    private GsonUtil() {
        gson = new GsonBuilder().disableHtmlEscaping().create();
    }

    public static GsonUtil getInstance() {
        if (mInstance == null) {
            synchronized (GsonUtil.class) {
                if (mInstance == null) {
                    mInstance = new GsonUtil();
                }
            }
        }
        return mInstance;
    }

    public Gson getGson() {
        return gson;
    }
}
