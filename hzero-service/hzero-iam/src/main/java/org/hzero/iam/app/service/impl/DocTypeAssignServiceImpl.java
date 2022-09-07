package org.hzero.iam.app.service.impl;

import java.util.List;
import java.util.Set;

import org.hzero.core.base.BaseConstants;
import org.hzero.iam.app.service.DocTypeAssignService;
import org.hzero.iam.domain.entity.DocTypeAssign;
import org.hzero.iam.domain.repository.DocTypeAssignRepository;
import org.hzero.iam.infra.constant.Constants;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;

import io.choerodon.core.domain.Page;
import io.choerodon.mybatis.pagehelper.domain.PageRequest;

/**
 * 单据类型分配应用服务默认实现
 *
 * @author min.wang01@hand-china.com 2018-08-08 19:55:53
 */
@Service
public class DocTypeAssignServiceImpl implements DocTypeAssignService {

    private DocTypeAssignRepository docTypeAssignRepository;

    @Autowired
    public DocTypeAssignServiceImpl(DocTypeAssignRepository docTypeAssignRepository) {
        this.docTypeAssignRepository = docTypeAssignRepository;
    }

    @Override
    public List<DocTypeAssign> listAssign(Long tenantId, Long docTypeId) {
        return docTypeAssignRepository.listAssign(tenantId, docTypeId);
    }

    @Override
    public Page<DocTypeAssign> pageAssign(Long tenantId, long docTypeId, PageRequest pageRequest) {
        return docTypeAssignRepository.pageAssign(tenantId, docTypeId, pageRequest);
    }

    @Override
    public void createAssign(Long docTypeId, Long tenantId, List<DocTypeAssign> docTypeAssigns) {
        if (!CollectionUtils.isEmpty(docTypeAssigns)) {
            docTypeAssigns.stream().distinct().forEach(docTypeAssign ->
                    docTypeAssignRepository.insert(docTypeAssign.setDocTypeId(docTypeId).setTenantId(tenantId))
            );
        }
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void createAssign(long doctypeId, List<DocTypeAssign> docTypeAssignList) {
        if (!CollectionUtils.isEmpty(docTypeAssignList)) {
            docTypeAssignList.stream().distinct().forEach(docTypeAssign ->
                    docTypeAssignRepository.insert(docTypeAssign.setDocTypeId(doctypeId).setTenantId(BaseConstants.DEFAULT_TENANT_ID))
            );
        }
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void updateAssign(long docTypeId, List<DocTypeAssign> docTypeAssignList) {
        if (!CollectionUtils.isEmpty(docTypeAssignList)) {
            docTypeAssignList.forEach(docTypeAssign -> {
                if (Constants.DocType.ACTION_TYPE_DELETE.equals(docTypeAssign.getActionType())) {
                    docTypeAssignRepository.deleteByPrimaryKey(docTypeAssign);
                } else {
                    docTypeAssignRepository.insert(docTypeAssign.setDocTypeId(docTypeId).setTenantId(BaseConstants.DEFAULT_TENANT_ID));
                }
            });
        }
    }

    @Override
    public void updateAssign(Long docTypeId, Long tenantId, List<DocTypeAssign> docTypeAssigns) {
        if (!CollectionUtils.isEmpty(docTypeAssigns)) {
            docTypeAssigns.forEach(docTypeAssign -> {
                if (Constants.DocType.ACTION_TYPE_DELETE.equals(docTypeAssign.getActionType())) {
                    docTypeAssignRepository.deleteByPrimaryKey(docTypeAssign);
                } else {
                    docTypeAssignRepository.insert(docTypeAssign.setDocTypeId(docTypeId).setTenantId(tenantId));
                }
            });
        }
    }

    @Override
    public Set<Long> getAssignTenantIdsByDocIds(List<Long> tenantDocIds) {
        return docTypeAssignRepository.getAssignTenantIdsByDocIds(tenantDocIds);
    }
}
