package io.github.changebooks.workerid;

import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * Configuration properties for Worker Id.
 *
 * @author changebooks@qq.com
 */
@ConfigurationProperties(prefix = "worker-id")
public class WorkerIdProperties {
    /**
     * 最小机器id，包含
     */
    private Integer minId;

    /**
     * 最大机器id，不包含
     */
    private Integer maxId;

    /**
     * 分组id，同组机器id唯一
     */
    private String groupId;

    public Integer getMinId() {
        return minId;
    }

    public void setMinId(Integer minId) {
        this.minId = minId;
    }

    public Integer getMaxId() {
        return maxId;
    }

    public void setMaxId(Integer maxId) {
        this.maxId = maxId;
    }

    public String getGroupId() {
        return groupId;
    }

    public void setGroupId(String groupId) {
        this.groupId = groupId;
    }

}
