package com.bugjc.flink.connector.jdbc.test.entity;

import com.bugjc.flink.connector.jdbc.annotation.TableField;
import com.bugjc.flink.connector.jdbc.annotation.TableName;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;

@Data
@AllArgsConstructor
@TableName("tbs_job")
public class JobEntity implements Serializable {
    @TableField("job_id")
    private String jobId;
    private int status;
    @TableField("exec_time")
    private Date execTime;
}
