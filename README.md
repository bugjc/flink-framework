## 一、项目介绍
> 快速开发 Flink 应用
### flink-config（管理环境配置文件）
基于 `Spring Boot` 约定优于配置的编程原则，在结合 `flink` 提供的 `ParameterTool` 工具类而编写的一种简单的配置文件管理工具。

### flink-connector（封装常用的连接器）
基于 flink-config 自动初始化常用连接器，形成基础组件，方便开发人员快速的通过配置在 flink 应用中使用连接器。当前已封装的连接器如下所示：
- jdbc-connector（JDBC 连接器）
- kafka-connector（Kafka 连接器）

### flink-test（功能测试）
- kafka.app（Kafka 相关功能测试）
- mysql.app（MySQL 相关功能测试）

## 二、使用步骤

### 1. 配置
在 resource 目录下创建环境配置文件 `application.properties`.如果项目中分了多个配置文件可以通过在 `application.properties` 文件中增加如下配置来加载指定配置文件.
```
# 其中 dev 表示加载 `application-dev.properties` 配置文件，要加载多个配置文件可以使用逗号分隔
flink.profiles.active=dev
```

### 2. 启动 Flink 应用
#### 添加 Maven 依赖
```
<dependency>
    <groupId>com.bugjc.flink.config</groupId>
    <artifactId>flink-config</artifactId>
    <version>1.10.0</version>
</dependency>
```
#### Main 方法中初始化环境
示例：
```
@Application
public class ConfigApplication {

    public static void main(String[] args) throws Exception {
        //1.环境参数配置
        EnvironmentConfig environmentConfig = new EnvironmentConfig(args);
        final ExecutionEnvironment env = environmentConfig.getExecutionEnvironment();

        //2.使用配置值作为数据源
        DataSource<String> dataSource = env.fromCollection(env.getConfig().getGlobalJobParameters().toMap().values());

        //3.打印
        dataSource.print();
    }
}
```

### 3. JDBC 连接器（可选）
#### 添加 Maven 依赖
```
<dependency>
    <groupId>com.bugjc.flink.connector.jdbc</groupId>
    <artifactId>jdbc-connector</artifactId>
    <version>1.10.0</version>
</dependency>
```

#### 配置属性
```
# 数据源配置:off
flink.datasource.driverClassName=com.mysql.cj.jdbc.Driver
flink.datasource.url=jdbc:mysql://192.168.0.108:4000/agriculture_big_data?useSSL=false&autoReconnect=true&rewriteBatchedStatements=true&useServerPrepStmts=true&allowMultiQueries=true&useConfigs=maxPerformance
flink.datasource.username=root
flink.datasource.password=
flink.datasource.className=com.alibaba.druid.pool.DruidDataSource
flink.datasource.initialSize=10
flink.datasource.maxTotal=50
flink.datasource.minIdle=2
# 数据源配置:on
```
#### 获取 JDBC 连接器
```
DataSourceConfig dataSourceConfig = environmentConfig.getComponent(DataSourceConfig.class))
```

#### 获取 JDBC SinkFunction
```
## 方式一：自定义 SQL
String sql = "insert ignore into tbs_job(job_id, status) values(?, ?)";
dataSourceConfig.createJdbcInsertBatchSink(sql);

## 方式二：自动生成 SQL
dataSourceConfig.createJdbcInsertBatchSink();
```
无论是使用哪种方式获取 `SinkFunction` 都需要建立插入的实体对象映射关系，具体示例如下所示：
```
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
```
使用 `@TableName` 和 `@TableField` 注解分别指明实体对象与数据库对象之间的映射关系。

### 4. kafka 连接器（可选）
#### 添加 Maven 依赖
```
<dependency>
    <groupId>com.bugjc.flink.connector.kafka</groupId>
    <artifactId>kafka-connector</artifactId>
    <version>1.10.0</version>
</dependency>
```

#### 获取 Kafka 连接器
Kafka 连接器分 `Consumer` 和 `Producer` 两种.

##### Kafka Consumer SourceFunction
在配置文件中增加如下 `Consumer` 配置:
```
# kafka 消费者配置:off
flink.kafka.consumer.bootstrap.servers=192.168.0.103:9092
flink.kafka.consumer.zookeeper.connect=192.168.0.103:2181
flink.kafka.consumer.group.id=group1
flink.kafka.consumer.key.deserializer=org.apache.kafka.common.serialization.StringDeserializer
flink.kafka.consumer.value.deserializer=org.apache.kafka.common.serialization.StringDeserializer
flink.kafka.consumer.auto.offset.reset=latest
## 等价于 flink.partition-discovery.interval-millis 参数，用来开启 topic 分区自动感知
flink.kafka.consumer.automaticPartition=10000
flink.kafka.consumer.topic=testTopicPartition-[0-9]
# kafka 消费者配置:on
```
使用如下代码获取 `SourceFunction`：
```
KafkaConsumerConfig kafkaConsumerConfig = environmentConfig.getComponent(KafkaConsumerConfig.class);
FlinkKafkaConsumer011<KafkaEvent> consumer011 = kafkaConsumerConfig.getKafkaConsumer(KafkaEvent.class);
```

备注:  
配置文件中的 `flink.kafka.consumer.topic `参数值有三类，分别对应：`单 topic`、`多 topic` 和 `topic 正则表达式`消费数据的方式。
     * 例：单 topic --> testTopicName;多 topic --> testTopicName,testTopicName1;topic 正则表达式 --> testTopicName[0-9].

##### Kafka Producer SinkFunction
在配置文件中增加如下 `Consumer` 配置:
```
# kafka 生产者配置:off
flink.kafka.producer.bootstrap.servers=192.168.0.103:9092
flink.kafka.producer.key.serializer=org.apache.kafka.common.serialization.StringSerializer
flink.kafka.producer.value.serializer=org.apache.kafka.common.serialization.StringSerializer
flink.kafka.producer.topic=KafkaEvent-1
# kafka 生产者配置:on
```
使用如下代码获取 `SinkFunction`：
```
KafkaProducerConfig kafkaProducerConfig = environmentConfig.getComponent(KafkaProducerConfig.class);
FlinkKafkaProducer011<KafkaEvent> producer011 = kafkaProducerConfig.createKafkaSink(KafkaEvent.class);
```
为了便于测试可通过调用 `kafkaProducerConfig.createKafkaProducer()` 方法快速创建一个 kafka 生产者来发送测试数据。
   

## 三、自定义连接器
TODO