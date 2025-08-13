package org.hzero.iam.infra.repository.impl;

import org.hzero.iam.domain.entity.DocTypePermission;
import org.hzero.iam.domain.repository.DocTypePermissionRepository;
import org.hzero.iam.infra.mapper.DocTypePermissionMapper;
import org.hzero.mybatis.base.impl.BaseRepositoryImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * @author qingsheng.chen@hand-china.com 2019-07-05 09:20
 */
@Component
public class DocTypePermissionRepositoryImpl extends BaseRepositoryImpl<DocTypePermission> implements DocTypePermissionRepository {
    private DocTypePermissionMapper docTypePermissionMapper;

    @Autowired
    public DocTypePermissionRepositoryImpl(DocTypePermissionMapper docTypePermissionMapper) {
        this.docTypePermissionMapper = docTypePermissionMapper;
    }

    @Override
    public List<DocTypePermission> listPermission(Long tenantId, Long docTypeId) {
        return docTypePermissionMapper.listPermission(tenantId, docTypeId);
    }

    @Override
    public List<DocTypePermission> listPermissionNotAssociated() {
        return docTypePermissionMapper.listPermissionNotAssociated();
    }
}
