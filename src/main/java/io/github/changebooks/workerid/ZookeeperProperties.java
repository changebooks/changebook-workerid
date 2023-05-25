package io.github.changebooks.workerid;

import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * Configuration properties for Zookeeper.
 *
 * @author changebooks@qq.com
 */
@ConfigurationProperties(prefix = "zookeeper")
public class ZookeeperProperties {
    /**
     * 地址，IP:2181
     */
    private String connectString;

    /**
     * 根路径
     */
    private String basePath;

    /**
     * 最大重试次数，小于等于29
     */
    private Integer maxRetries;

    /**
     * 等待重试，初始睡眠时间
     */
    private Integer baseSleepMs;

    /**
     * 等待重试，最大睡眠时间
     */
    private Integer maxSleepMs;

    public String getConnectString() {
        return connectString;
    }

    public void setConnectString(String connectString) {
        this.connectString = connectString;
    }

    public String getBasePath() {
        return basePath;
    }

    public void setBasePath(String basePath) {
        this.basePath = basePath;
    }

    public Integer getMaxRetries() {
        return maxRetries;
    }

    public void setMaxRetries(Integer maxRetries) {
        this.maxRetries = maxRetries;
    }

    public Integer getBaseSleepMs() {
        return baseSleepMs;
    }

    public void setBaseSleepMs(Integer baseSleepMs) {
        this.baseSleepMs = baseSleepMs;
    }

    public Integer getMaxSleepMs() {
        return maxSleepMs;
    }

    public void setMaxSleepMs(Integer maxSleepMs) {
        this.maxSleepMs = maxSleepMs;
    }

}
