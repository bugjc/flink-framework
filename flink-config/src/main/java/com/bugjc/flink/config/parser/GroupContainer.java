package com.bugjc.flink.config.parser;

import lombok.Getter;
import lombok.extern.slf4j.Slf4j;

/**
 * 分组容器，辅助类
 * 不可变对象,用于定位容器存储对象
 *
 * @author aoki
 * @date 2020/9/9
 **/
@Slf4j
public class GroupContainer {
    /**
     * 容器类型
     */
    @Getter
    private final ContainerType currentContainerType;

    /**
     * 当前分组名（全局唯一）
     */
    @Getter
    private final String currentGroupName;

    /**
     * 当前容器名
     */
    @Getter
    private final String currentContainerName;

    /**
     * 上级容器类型
     */
    @Getter
    private final ContainerType upperContainerType;

    /**
     * 上级分组名（全局唯一）
     */
    @Getter
    private final String upperGroupName;

    /**
     * 初始化容器
     *
     * @param currentContainerType --当前容器类型
     * @param currentGroupName     --当前分组容器名
     * @param upperContainerType   --上级容器类型
     */
    public GroupContainer(ContainerType currentContainerType, String currentGroupName, ContainerType upperContainerType) {
        if (currentGroupName.endsWith(Constants.SUFFIX)) {
            currentGroupName = currentGroupName.substring(0, currentGroupName.length() - 1);
        }
        this.currentContainerType = currentContainerType;
        this.currentGroupName = currentGroupName;
        this.currentContainerName = currentGroupName.substring(currentGroupName.lastIndexOf(Constants.SUFFIX) + 1);;
        this.upperContainerType = upperContainerType;

        if (currentContainerType == ContainerType.None) {
            this.upperGroupName = this.currentGroupName;
        } else {
            this.upperGroupName = this.currentGroupName.substring(0, this.currentGroupName.lastIndexOf(Constants.SUFFIX));
        }
    }

    /**
     * 创建一个新的容器
     *
     * @param currentContainerType --当前容器类型
     * @param currentGroupName     --当前分组名
     * @param upperContainerType   --上级容器类型
     * @return 新的分组容器
     */
    public static GroupContainer create(ContainerType currentContainerType, String currentGroupName, ContainerType upperContainerType){
        return new GroupContainer(currentContainerType, currentGroupName, upperContainerType);
    }
}
