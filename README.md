## 一、项目介绍
> 快速开发 Flink 应用
### flink-config（管理环境配置文件）
基于 `Spring Boot` 约定优于配置的编程原则，在结合 `flink` 提供的 `ParameterTool` 工具类而编写的一种简单的配置文件管理工具。

### flink-connector（封装常用的连接器）
基于 flink-config 自动初始化常用连接器，形成基础组件，方便开发人员快速的通过配置在 flink 应用中使用连接器。当前已封装的连接器如下所示：
- jdbc-connector（JDBC 连接器）
- kafka-connector（Kafka 连接器）

## 二、使用步骤

#### 1. 在 resource 目录下创建环境配置文件 `application.properties`.
如果项目中分了多个配置文件可以通过在 `application.properties` 文件中增加如下配置来加载指定配置文件.
```
# 其中 dev 表示加载 `application-dev.properties` 配置文件，要加载多个配置文件可以使用逗号分隔
flink.profiles.active=dev
```

#### 2. 获取环境配置
##### 添加 Maven 依赖
```
<dependency>
    <groupId>com.bugjc.flink.config</groupId>
    <artifactId>flink-config</artifactId>
    <version>1.10.0</version>
</dependency>
```
##### Main 构建环境配置
```
EnvironmentConfig environmentConfig = new EnvironmentConfig(args);
final StreamExecutionEnvironment env = environmentConfig.getStreamExecutionEnvironment();
```

#### 3. 获取 JDBC 连接器（可选）
##### 添加 Maven 依赖
```
<dependency>
    <groupId>com.bugjc.flink.connector.jdbc</groupId>
    <artifactId>jdbc-connector</artifactId>
    <version>1.10.0</version>
</dependency>
```

##### 在配置文件中增加如下配置属性.
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
##### 配置好后，代码中通过如下方法获取 JDBC 连接器：
```
DataSourceConfig dataSourceConfig = environmentConfig.getComponent(DataSourceConfig.class))
```

#### 4. 获取 kafka 连接器（可选）
```
<dependency>
    <groupId>com.bugjc.flink.connector.kafka</groupId>
    <artifactId>kafka-connector</artifactId>
    <version>1.10.0</version>
</dependency>
```

##### kafka 连接器分 消费者 和 生产者 两种.对于消费者连接器需要配置如下属性:
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

##### 配置好后，代码中通过如下方法获取消费者连接器：
```
KafkaConsumerConfig kafkaConsumerConfig = environmentConfig.getComponent(KafkaConsumerConfig.class);
FlinkKafkaConsumer011<KafkaEvent> consumer011 = kafkaConsumerConfig.getKafkaConsumer(KafkaEvent.class);
```
##### 备注
配置文件中的 `flink.kafka.consumer.topic `参数值有三类，分别对应：`单 topic`、`多 topic` 和 `topic 正则表达式`消费数据的方式。
     * 例：单 topic --> testTopicName;多 topic --> testTopicName,testTopicName1;topic 正则表达式 --> testTopicName[0-9].
     
     
## 三、自定义连接器
TODO