package com.bugjc.flink.config.exception;

import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 业务异常基类
 *
 * @author aoki
 */
@EqualsAndHashCode(callSuper = true)
@Data
public class ApplicationContextException extends RuntimeException {

    private static final long serialVersionUID = -5875371379845226068L;

    /**
     * 异常信息
     */
    private String msg;

    /**
     * 具体异常码
     */
    private int code;

    /**
     * 数据
     */
    private String data;

    public ApplicationContextException(String msgFormat) {
        super(msgFormat);
        msg = msgFormat;
    }

    public ApplicationContextException(int code, String msgFormat) {
        super(msgFormat);
        this.code = code;
        msg = msgFormat;
    }

    public ApplicationContextException(int code, String data, String msgFormat) {
        super(msgFormat);
        this.code = code;
        this.data = data;
        msg = msgFormat;
    }

}
