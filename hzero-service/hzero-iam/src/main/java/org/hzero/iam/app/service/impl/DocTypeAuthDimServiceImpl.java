package org.hzero.iam.app.service.impl;

import java.util.List;

import org.apache.commons.lang.ObjectUtils;
import org.hzero.iam.app.service.DocTypeAuthDimService;
import org.hzero.iam.domain.entity.DocTypeAuthDim;
import org.hzero.iam.domain.repository.DocTypeAuthDimRepository;
import org.hzero.iam.infra.constant.Constants;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;

/**
 * 单据类型权限维度定义应用服务默认实现
 *
 * @author min.wang01@hand-china.com 2018-08-08 19:55:53
 */
@Service
public class DocTypeAuthDimServiceImpl implements DocTypeAuthDimService {

    @Autowired
    private DocTypeAuthDimRepository docTypeAuthDimRepository;

    @Override
    public List<DocTypeAuthDim> listAuthDim(Long tenantId, long docTypeId) {
        return docTypeAuthDimRepository.listAuthDim(tenantId, docTypeId);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public List<DocTypeAuthDim> saveAuthDim(Long tenantId, long docTypeId, List<DocTypeAuthDim> docTypeAuthDims) {
        if (CollectionUtils.isEmpty(docTypeAuthDims)) {
            return docTypeAuthDims;
        }
        docTypeAuthDims.forEach(docTypeAuthDim -> {
            docTypeAuthDim.setTenantId(tenantId);
            if (ObjectUtils.equals(docTypeAuthDim.getActionType(), Constants.DocType.ACTION_TYPE_DELETE)) {
                // 若去除勾选，则在后台删除对应维度
                docTypeAuthDimRepository.deleteByPrimaryKey(docTypeAuthDim.getAuthDimId());
            } else if (ObjectUtils.equals(docTypeAuthDim.getActionType(), Constants.DocType.ACTION_TYPE_ADD) && docTypeAuthDim.getAuthDimId() != null) {
                docTypeAuthDimRepository.updateOptional(docTypeAuthDim.setDocTypeId(docTypeId), DocTypeAuthDim.FIELD_RULE_TYPE, DocTypeAuthDim.FIELD_SOURCE_MATCH_TABLE, DocTypeAuthDim.FIELD_SOURCE_MATCH_FIELD);
            } else {
                docTypeAuthDimRepository.insert(docTypeAuthDim.setDocTypeId(docTypeId));
            }
        });
        return docTypeAuthDims;
    }
}
