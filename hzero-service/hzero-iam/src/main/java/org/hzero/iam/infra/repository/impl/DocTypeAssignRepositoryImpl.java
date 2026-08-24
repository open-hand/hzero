package org.hzero.iam.infra.repository.impl;

import io.choerodon.core.domain.Page;
import io.choerodon.mybatis.pagehelper.PageHelper;
import io.choerodon.mybatis.pagehelper.domain.PageRequest;

import com.google.common.collect.Sets;
import org.apache.commons.collections4.CollectionUtils;
import org.hzero.iam.domain.entity.DocTypeAssign;
import org.hzero.iam.domain.repository.DocTypeAssignRepository;
import org.hzero.iam.infra.mapper.DocTypeAssignMapper;
import org.hzero.mybatis.base.impl.BaseRepositoryImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Set;

/**
 * 单据类型分配 资源库实现
 *
 * @author min.wang01@hand-china.com 2018-08-08 19:55:53
 */
@Component
public class DocTypeAssignRepositoryImpl extends BaseRepositoryImpl<DocTypeAssign> implements DocTypeAssignRepository {

    @Autowired
    private DocTypeAssignMapper docTypeAssignMapper;

    @Override
    public Page<DocTypeAssign> pageAssign(Long tenantId, long docTypeId, PageRequest pageRequest) {
        return PageHelper.doPage(pageRequest, () -> docTypeAssignMapper.listAssign(tenantId, docTypeId));
    }

    @Override
    public List<DocTypeAssign> listAssign(Long tenantId, long docTypeId) {
        return docTypeAssignMapper.listAssign(tenantId, docTypeId);
    }

    @Override
    public int deleteByDocTypeId(Long docTypeId) {
        return docTypeAssignMapper.deleteByDocTypeId(docTypeId);
    }

    @Override
    public List<DocTypeAssign> selectByDocTypeId(Long docTypeId) {
        return docTypeAssignMapper.selectByDocTypeId(docTypeId);
    }

    @Override
    public Set<Long> getAssignTenantIdsByDocIds(List<Long> tenantDocIds) {
        if (CollectionUtils.isNotEmpty(tenantDocIds)) {
            return docTypeAssignMapper.getAssignTenantIdsByDocIds(tenantDocIds);
        } else {
            return Sets.newHashSet();
        }
    }
}
