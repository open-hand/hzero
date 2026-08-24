package org.hzero.iam.infra.repository.impl;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import io.choerodon.core.domain.Page;
import io.choerodon.mybatis.pagehelper.PageHelper;
import io.choerodon.mybatis.pagehelper.domain.PageRequest;

import org.hzero.iam.domain.entity.DocType;
import org.hzero.iam.domain.repository.DocTypeRepository;
import org.hzero.iam.infra.mapper.DocTypeMapper;
import org.hzero.mybatis.base.impl.BaseRepositoryImpl;

/**
 * 单据类型定义 资源库实现
 *
 * @author min.wang01@hand-china.com 2018-08-08 16:32:49
 */
@Component
public class DocTypeRepositoryImpl extends BaseRepositoryImpl<DocType> implements DocTypeRepository {

    private DocTypeMapper docTypeMapper;

    @Autowired
    public DocTypeRepositoryImpl(DocTypeMapper docTypeMapper) {
        this.docTypeMapper = docTypeMapper;
    }

    @Override
    public Page<DocType> pageDocType(Long tenantId, String docTypeCode, String docTypeName, PageRequest pageRequest) {
        return PageHelper.doPageAndSort(pageRequest, () -> docTypeMapper.pageDocType(tenantId, docTypeCode, docTypeName));
    }

    @Override
    public DocType queryDocType(Long tenantId, Long docTypeId, boolean includeAssign) {
        return docTypeMapper.queryDocType(tenantId, docTypeId, includeAssign);
    }

    @Override
    public List<DocType> queryDocTypeWithDimension(Long tenantId, List<Long> docTypeIds) {
        return docTypeMapper.queryDocTypeWithDimension(tenantId, docTypeIds);
    }
}
