package org.hzero.file.infra.constant;

/**
 * 文件服务类型
 *
 * @author shuangfei.zhu@hand-china.com 2019/07/01 19:49
 */
public enum FileServiceType {
    /**
     * 阿里云OSS
     */
    ALIYUN(1),
    /**
     * 华为OBS
     */
    HUAWEI(2),
    /**
     * Minio
     */
    MINIO(3),
    /**
     * 腾讯云
     */
    QCLOUD(4),
    /**
     * 七牛云
     */
    QINIU(5),
    /**
     * 本地存储
     */
    LOCAL(6),
    /**
     * 京东云
     */
    JDCLOUD(7),
    /**
     * AWS S3
     */
    AWS(8),
    /**
     * 百度
     */
    BAIDU(9),
    /**
     * 服务器
     */
    SERVER(10),
    /**
     * 11:微软
     */
    MICROSOFT(11),
    /**
     * 12:微软
     */
    CEPH(12);

    private int value;

    FileServiceType(int value) {
        this.value = value;
    }

    public int getValue() {
        return value;
    }

    public static boolean contain(int value) {
        for (FileServiceType item : FileServiceType.values()) {
            if (item.value == value) {
                return true;
            }
        }
        return false;
    }
}