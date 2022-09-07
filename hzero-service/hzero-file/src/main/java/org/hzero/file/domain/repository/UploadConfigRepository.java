package org.hzero.file.domain.repository;

import java.util.List;

import org.hzero.file.domain.entity.UploadConfig;
import org.hzero.mybatis.base.BaseRepository;

import io.choerodon.core.domain.Page;
import io.choerodon.mybatis.pagehelper.domain.PageRequest;

/**
 * 并发请求资源库
 *
 * @author shuangfei.zhu@hand-china.com 2018/09/20 11:18
 */
public interface UploadConfigRepository extends BaseRepository<UploadConfig> {


    /**
     * 分页查询行
     *
     * @param tenantId    租户Id
     * @param pageRequest 分页
     * @return 文件上传配置分页
     */
    Page<UploadConfig> pageUploadConfig(Long tenantId, PageRequest pageRequest);

    /**
     * 根据唯一索引查询
     *
     * @param bucketName 桶名
     * @param directory  上传目录
     * @param tenantId   租户Id
     * @return 文件上传配置
     */
    UploadConfig selectByTenantId(String bucketName, String directory, Long tenantId);

    /**
     * 查询所有行
     *
     * @param tenantId 租户Id
     * @return 配置
     */
    List<UploadConfig> listUploadConfig(Long tenantId);
}
