package io.github.changebooks.workerid;

/**
 * 机器id
 *
 * @author changebooks@qq.com
 */
public interface WorkerId {
    /**
     * 获取上次生成的id
     *
     * @return 机器id，仅生成一次
     */
    int holdId();

    /**
     * 生成新id
     *
     * @return 机器id，[minId, maxId)
     */
    int nextId();

    /**
     * 获取最小机器id
     *
     * @return 最小id，包含
     */
    int minId();

    /**
     * 获取最大机器id
     *
     * @return 最大id，不包含
     */
    int maxId();

    /**
     * 获取分组id
     *
     * @return 分组id，同组机器id唯一
     */
    String groupId();

}
