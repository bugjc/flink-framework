package com.bugjc.flink.test.config.app.config;

/**
 * 发布类型
 *
 * @author aoki
 * @date 2020/8/17
 **/
public enum PublishType {
    /**
     * 状态
     */
    Private(0, "不公开"),
    Public(1, "公开");

    private final int type;
    private final String desc;

    PublishType(int type, String desc) {
        this.type = type;
        this.desc = desc;
    }

    public int getType() {
        return type;
    }

    public String getDesc() {
        return desc;
    }
}
