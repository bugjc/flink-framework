## 项目介绍
> 快速开发 Flink 应用
### flink-config（管理环境配置文件）
基于 `Spring Boot` 约定优于配置的编程原则，在结合 `flink` 提供的 `ParameterTool` 工具类而编写的一种简单的配置文件管理工具。

### flink-connector（封装常用的连接器）
基于 flink-config 自动初始化常用连接器，形成基础组件，方便开发人员快速的通过配置在 flink 应用中使用连接器。当前已封装的连接器如下所示：
- jdbc-connector（JDBC 连接器）
- kafka-connector（Kafka 连接器）
