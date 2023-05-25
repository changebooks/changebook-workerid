package io.github.changebooks.workerid;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.Assert;
import org.springframework.util.StringUtils;

/**
 * @author changebooks@qq.com
 */
public class WorkerIdImpl implements WorkerId {

    private static final Logger LOGGER = LoggerFactory.getLogger(WorkerIdImpl.class);

    /**
     * Zookeeper Client
     */
    private final Zookeeper zookeeper;

    /**
     * 最小机器id，包含
     */
    private final int minId;

    /**
     * 最大机器id，不包含
     */
    private final int maxId;

    /**
     * 分组id，同组机器id唯一
     */
    private final String groupId;

    /**
     * 机器id
     */
    private final int holdId;

    public WorkerIdImpl(Zookeeper zookeeper, WorkerIdProperties properties) {
        Assert.notNull(zookeeper, "zookeeper can't be null");
        Assert.notNull(properties, "properties can't be null");

        Integer minId = properties.getMinId();
        Assert.notNull(minId, "minId can't be null");
        Assert.isTrue(minId >= 0, "minId can't be less than 0");

        Integer maxId = properties.getMaxId();
        Assert.notNull(maxId, "maxId can't be null");
        Assert.isTrue(maxId > 0, "maxId must be greater than 0");
        Assert.isTrue(minId < maxId, String.format("minId %d must be less than maxId %d", minId, maxId));

        String groupId = StringUtils.trimWhitespace(properties.getGroupId());
        Assert.hasText(groupId, "groupId can't be empty");

        this.zookeeper = zookeeper;
        this.minId = minId;
        this.maxId = maxId;
        this.groupId = groupId;
        this.holdId = nextId();

        LOGGER.info("WorkerId trace, holdId: {}, minId: {}, maxId: {}, groupId: {}",
                holdId(), minId(), maxId(), groupId());
    }

    @Override
    public int holdId() {
        return holdId;
    }

    @Override
    public int nextId() {
        int minId = minId();
        int maxId = maxId();
        String groupId = groupId();

        for (int id = minId; id < maxId; id++) {
            if (zookeeper.createEphemeralPath(groupId, id)) {
                LOGGER.info("nextId trace, id: {}, minId: {}, maxId: {}, groupId: {}",
                        id, minId, maxId, groupId);
                return id;
            }
        }

        throw new RuntimeException(String.format("next worker id failed, [%d, %d) be used, group-id: %s", minId, maxId, groupId));
    }

    @Override
    public int minId() {
        return minId;
    }

    @Override
    public int maxId() {
        return maxId;
    }

    @Override
    public String groupId() {
        return groupId;
    }

}
