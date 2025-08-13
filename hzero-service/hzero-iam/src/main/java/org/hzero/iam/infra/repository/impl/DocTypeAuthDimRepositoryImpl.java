package org.hzero.iam.infra.repository.impl;

import org.hzero.iam.domain.entity.DocTypeAuthDim;
import org.hzero.iam.domain.repository.DocTypeAuthDimRepository;
import org.hzero.iam.infra.mapper.DocTypeAuthDimMapper;
import org.hzero.mybatis.base.impl.BaseRepositoryImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * 单据类型权限维度定义 资源库实现
 *
 * @author min.wang01@hand-china.com 2018-08-08 19:55:53
 */
@Component
public class DocTypeAuthDimRepositoryImpl extends BaseRepositoryImpl<DocTypeAuthDim> implements DocTypeAuthDimRepository {

    @Autowired
    private DocTypeAuthDimMapper docTypeAuthDimMapper;

    @Override
    public List<DocTypeAuthDim> listAuthDim(Long tenantId, long docTypeId) {
        return docTypeAuthDimMapper.listAuthDim(tenantId, docTypeId);
    }
}
