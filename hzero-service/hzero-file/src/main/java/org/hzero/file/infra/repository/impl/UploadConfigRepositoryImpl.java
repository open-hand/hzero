package org.hzero.file.infra.repository.impl;

import java.util.List;

import org.hzero.file.domain.entity.UploadConfig;
import org.hzero.file.domain.repository.UploadConfigRepository;
import org.hzero.file.infra.mapper.UploadConfigMapper;
import org.hzero.mybatis.base.impl.BaseRepositoryImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import io.choerodon.core.domain.Page;
import io.choerodon.mybatis.pagehelper.PageHelper;
import io.choerodon.mybatis.pagehelper.domain.PageRequest;

/**
 * 并发请求 资源库实现
 *
 * @author shuangfei.zhu@hand-china.com 2018/09/20 11:15
 */
@Component
public class UploadConfigRepositoryImpl extends BaseRepositoryImpl<UploadConfig> implements UploadConfigRepository {

    @Autowired
    private UploadConfigMapper uploadConfigMapper;

    @Override
    public Page<UploadConfig> pageUploadConfig(Long tenantId, PageRequest pageRequest) {
        return PageHelper.doPageAndSort(pageRequest, () -> uploadConfigMapper.listUploadConfig(tenantId));
    }

    @Override
    public UploadConfig selectByTenantId(String bucketName, String directory, Long tenantId) {
        return uploadConfigMapper.selectByTenantId(bucketName, directory, tenantId);
    }

    @Override
    public List<UploadConfig> listUploadConfig(Long tenantId) {
        return uploadConfigMapper.listUploadConfig(tenantId);
    }
}
