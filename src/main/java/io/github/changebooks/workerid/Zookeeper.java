package io.github.changebooks.workerid;

import org.apache.curator.RetryPolicy;
import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.CuratorFrameworkFactory;
import org.apache.curator.framework.imps.CuratorFrameworkState;
import org.apache.curator.retry.ExponentialBackoffRetry;
import org.apache.zookeeper.CreateMode;
import org.apache.zookeeper.data.Stat;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.Assert;
import org.springframework.util.StringUtils;

import java.util.Objects;
import java.util.Optional;

/**
 * Zookeeper Client
 *
 * @author changebooks@qq.com
 */
public class Zookeeper {

    private static final Logger LOGGER = LoggerFactory.getLogger(Zookeeper.class);

    /**
     * 路径分隔符
     */
    private static final String PATH_SEPARATOR = "/";

    /**
     * 默认的地址
     */
    public static final String CONNECT_STRING = "127.0.0.1:2181";

    /**
     * 默认的根路径
     */
    public static final String BASE_PATH = "worker_id";

    /**
     * 地址，IP:2181
     */
    private final String connectString;

    /**
     * 根路径
     */
    private final String basePath;

    /**
     * 最大重试次数
     */
    private final Integer maxRetries;

    /**
     * 等待重试，初始睡眠时间
     */
    private final Integer baseSleepMs;

    /**
     * 等待重试，最大睡眠时间
     */
    private final Integer maxSleepMs;

    /**
     * 客户端
     */
    private final CuratorFramework client;

    public Zookeeper(ZookeeperProperties properties) {
        Assert.notNull(properties, "properties can't be null");

        String connectString = StringUtils.trimWhitespace(properties.getConnectString());
        String basePath = StringUtils.trimWhitespace(properties.getBasePath());
        Integer maxRetries = properties.getMaxRetries();
        Integer baseSleepMs = properties.getBaseSleepMs();
        Integer maxSleepMs = properties.getMaxSleepMs();

        this.connectString = StringUtils.hasText(connectString) ? connectString : CONNECT_STRING;
        this.basePath = StringUtils.hasText(basePath) ? basePath : BASE_PATH;
        this.maxRetries = maxRetries;
        this.baseSleepMs = baseSleepMs;
        this.maxSleepMs = maxSleepMs;
        this.client = newClient();
    }

    /**
     * 新建临时文件
     *
     * @param directory 目录名
     * @param id        文件id
     * @return Id Exists Or Create Failed ? Returns False
     */
    public boolean createEphemeralPath(String directory, int id) {
        String path = joinPath(directory, id);
        Assert.hasText(path, "path can't be empty");

        try {
            if (checkPathExists(path)) {
                LOGGER.info("createEphemeralPath path exists, directory: {}, id: {}, path: {}",
                        directory, id, path);
                return false;
            }
        } catch (Exception ex) {
            LOGGER.error("createEphemeralPath check failed, directory: {}, id: {}, path: {}, throwable: ",
                    directory, id, path, ex);
            return false;
        }

        try {
            createEphemeralPath(path);
            LOGGER.info("createEphemeralPath trace, directory: {}, id: {}, path: {}",
                    directory, id, path);
            return true;
        } catch (Exception ex) {
            LOGGER.error("createEphemeralPath create failed, directory: {}, id: {}, path: {}, throwable: ",
                    directory, id, path, ex);
            return false;
        }
    }

    /**
     * 拼接路径，[/根路径][/目录名]/文件id
     *
     * @param directory 目录名
     * @param id        文件id
     * @return 路径，[/basePath][/directory]/id
     */
    public String joinPath(String directory, int id) {
        String path = "";

        String basePath = getBasePath();
        if (StringUtils.hasText(basePath)) {
            path += PATH_SEPARATOR + basePath;
        }

        if (StringUtils.hasText(directory)) {
            path += PATH_SEPARATOR + directory;
        }

        path += PATH_SEPARATOR + id;
        return path;
    }

    /**
     * 新建临时路径
     *
     * @param path 路径
     * @throws Exception             创建失败，抛出异常
     * @throws IllegalStateException if no start, Expected state [STARTED] was [LATENT]
     */
    public void createEphemeralPath(String path) throws Exception {
        client.create().
                creatingParentsIfNeeded().
                withMode(CreateMode.EPHEMERAL).
                forPath(path);
    }

    /**
     * 路径存在？
     *
     * @param path 路径
     * @return if Exists, Returns True
     * @throws Exception             检查失败，抛出异常
     * @throws IllegalStateException if no start, Expected state [STARTED] was [LATENT]
     */
    public boolean checkPathExists(String path) throws Exception {
        Stat stat = client.checkExists().forPath(path);
        return stat != null;
    }

    /**
     * 启动客户端
     */
    public void start() {
        client.start();

        CuratorFrameworkState state = client.getState();
        LOGGER.info("Zookeeper client start, state: {}, connectString: {}, basePath: {}, maxRetries: {}, baseSleepMs: {}, maxSleepMs: {}",
                state, connectString, basePath, maxRetries, baseSleepMs, maxSleepMs);
    }

    /**
     * 关闭客户端
     */
    public void close() {
        client.close();

        CuratorFrameworkState state = client.getState();
        LOGGER.info("Zookeeper client close, state: {}, connectString: {}, basePath: {}, maxRetries: {}, baseSleepMs: {}, maxSleepMs: {}",
                state, connectString, basePath, maxRetries, baseSleepMs, maxSleepMs);
    }

    /**
     * 新建客户端
     *
     * @return {@link CuratorFramework} instance
     */
    public CuratorFramework newClient() {
        String connectString = getConnectString();
        RetryPolicy retryPolicy = newRetryPolicy();

        return CuratorFrameworkFactory.newClient(connectString, retryPolicy);
    }

    /**
     * 新建重试策略
     *
     * @return {@link RetryPolicy} instance
     */
    public RetryPolicy newRetryPolicy() {
        int maxRetries = Optional.ofNullable(getMaxRetries()).orElse(0);
        int baseSleepMs = Optional.ofNullable(getBaseSleepMs()).orElse(0);
        Integer maxSleepMs = getMaxSleepMs();

        if (Objects.nonNull(maxSleepMs)) {
            return new ExponentialBackoffRetry(baseSleepMs, maxRetries, maxSleepMs);
        } else {
            return new ExponentialBackoffRetry(baseSleepMs, maxRetries);
        }
    }

    public String getConnectString() {
        return connectString;
    }

    public String getBasePath() {
        return basePath;
    }

    public Integer getMaxRetries() {
        return maxRetries;
    }

    public Integer getBaseSleepMs() {
        return baseSleepMs;
    }

    public Integer getMaxSleepMs() {
        return maxSleepMs;
    }

    public CuratorFramework getClient() {
        return client;
    }

}
