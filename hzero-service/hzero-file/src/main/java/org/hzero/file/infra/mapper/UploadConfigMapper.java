package org.hzero.file.infra.mapper;

import java.util.List;

import org.apache.ibatis.annotations.Param;
import org.hzero.file.domain.entity.UploadConfig;

import io.choerodon.mybatis.common.BaseMapper;

/**
 * 文件上传配置Mapper
 *
 * @author shuangfei.zhu@hand-china.com 2018-09-26 15:05:19
 */
public interface UploadConfigMapper extends BaseMapper<UploadConfig> {


    /**
     * 查询行
     *
     * @param tenantId 租户Id
     * @return 配置列表
     */
    List<UploadConfig> listUploadConfig(@Param("tenantId") Long tenantId);

    /**
     * 根据唯一索引查询
     *
     * @param bucketName 桶
     * @param directory  上传目录
     * @param tenantId   租户Id
     * @return 文件上传配置
     */
    UploadConfig selectByTenantId(@Param("bucketName") String bucketName,
                                  @Param("directory") String directory,
                                  @Param("tenantId") Long tenantId);
}
