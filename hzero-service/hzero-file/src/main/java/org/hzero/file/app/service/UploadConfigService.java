package org.hzero.file.app.service;

import org.hzero.file.domain.entity.File;
import org.hzero.file.domain.entity.UploadConfig;

/**
 * 并发请求应用服务
 *
 * @author shuangfei.zhu@hand-china.com 2018/09/20 11:25
 */
public interface UploadConfigService {

    /**
     * 查询桶配置
     *
     * @param bucketName 桶
     * @param tenantId   租户
     * @param directory  目录
     * @return 配置详情
     */
    UploadConfig detailConfig(String bucketName, Long tenantId, String directory);

    /**
     * 新建
     *
     * @param uploadConfig 文件上传配置对象
     * @return 新建的对象
     */
    UploadConfig createUploadConfig(UploadConfig uploadConfig);

    /**
     * 更新
     *
     * @param uploadConfig 文件上传配置对象
     * @return 更新的对象
     */
    UploadConfig updateUploadConfig(UploadConfig uploadConfig);

    /**
     * 删除
     *
     * @param uploadConfigId 主键
     */
    void deleteUploadConfig(Long uploadConfigId);

    /**
     * 校验文件大小通用方法
     *
     * @param file     文件对象
     * @param fileCode 文件头编码
     */
    void validateFileSize(File file, String fileCode);

    /**
     * 校验Byte文件大小
     *
     * @param file 文件对象
     */
    void validateByteFileSize(File file);
}
