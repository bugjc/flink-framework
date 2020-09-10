package com.bugjc.flink.config.model.component;

import com.bugjc.flink.config.core.constant.Constants;
import com.bugjc.flink.config.core.enums.ContainerType;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;

/**
 * 分组容器
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
    private ContainerType currentContainerType;
    /**
     * 当前分组名（全局唯一）
     */
    @Getter
    private String currentGroupName;

    /**
     * 当前容器名
     */
    @Getter
    private String currentContainerName;

    /**
     * 上级容器类型
     */
    @Getter
    private ContainerType upperContainerType;

    /**
     * 上级分组名（全局唯一）
     */
    @Getter
    private String upperGroupName;


    public GroupContainer setCurrentContainerType(ContainerType containerType) {
        this.currentContainerType = containerType;
        return this;
    }

    public GroupContainer setCurrentGroupName(String currentGroupName) {
        if (currentGroupName.endsWith(Constants.SUFFIX)) {
            currentGroupName = currentGroupName.substring(0, currentGroupName.length() - 1);
        }

        this.currentGroupName = currentGroupName;
        return this;
    }

    public GroupContainer setUpperContainerType(ContainerType containerType) {
        this.upperContainerType = containerType;
        return this;
    }

    public GroupContainer build() {

        if (this.currentGroupName == null) {
            throw new NullPointerException();
        }

        this.upperGroupName = this.currentGroupName;
        this.currentContainerName = this.currentGroupName.substring(this.currentGroupName.lastIndexOf(Constants.SUFFIX) + 1);
        return this;
    }


    public GroupContainer buildLevel1() {
        if (this.currentContainerType == null) {
            throw new NullPointerException();
        }

        if (this.currentGroupName == null) {
            throw new NullPointerException();
        }

        String upperGroupName = this.currentGroupName.substring(0, this.currentGroupName.lastIndexOf(Constants.SUFFIX));
        String currentContainerName = this.currentGroupName.substring(this.currentGroupName.lastIndexOf(Constants.SUFFIX) + 1);
        this.upperGroupName = upperGroupName;
        this.currentContainerName = currentContainerName;
        return this;
    }


    public GroupContainer buildLevel2() {

        if (this.currentContainerType == null) {
            throw new NullPointerException();
        }

        if (this.currentGroupName == null) {
            throw new NullPointerException();
        }

        String upperGroupName = this.currentGroupName.substring(0, this.currentGroupName.lastIndexOf(Constants.SUFFIX));
        //list 的上级分组需要回退 2 步
        String newUpperGroupName = upperGroupName.substring(0, upperGroupName.lastIndexOf(Constants.SUFFIX));
        this.upperGroupName = newUpperGroupName;
        this.currentContainerName = upperGroupName.replace(newUpperGroupName + Constants.SUFFIX, "");
        return this;
    }
}
