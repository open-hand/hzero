package org.hzero.iam.infra.repository.impl;

import java.util.List;

import javax.annotation.Nonnull;

import org.hzero.iam.domain.entity.DocTypeDimension;
import org.hzero.iam.domain.repository.DocTypeDimensionRepository;
import org.hzero.iam.infra.mapper.DocTypeDimensionMapper;
import org.hzero.mybatis.base.impl.BaseRepositoryImpl;
import org.hzero.mybatis.domian.Condition;
import org.hzero.mybatis.util.Sqls;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import io.choerodon.core.domain.Page;
import io.choerodon.mybatis.pagehelper.PageHelper;
import io.choerodon.mybatis.pagehelper.domain.PageRequest;

/**
 * 单据类型维度 资源库实现
 *
 * @author zifeng.ding@hand-china.com 2019-09-18 11:48:01
 */
@Component
public class DocTypeDimensionRepositoryImpl extends BaseRepositoryImpl<DocTypeDimension>
        implements DocTypeDimensionRepository {
    @Autowired
    private DocTypeDimensionMapper docTypeDimensionMapper;

    @Override
    public Page<DocTypeDimension> pageAndSortDocTypeDimension(Long organizationId, String dimensionCode,
                                                              String dimensionName, PageRequest pageRequest, String dimensionType, String valueSourceType,
                                                              Integer enabledFlag) {

        Condition condition = Condition.builder(DocTypeDimension.class)
                .andWhere(Sqls.custom().andEqualTo(DocTypeDimension.FIELD_TENANT_ID, organizationId)
                        .andLike(DocTypeDimension.FIELD_DIMENSION_CODE, dimensionCode, true)
                        .andLike(DocTypeDimension.FIELD_DIMENSION_NAME, dimensionName, true)
                        .andEqualTo(DocTypeDimension.FIELD_DIMENSION_TYPE, dimensionType, true)
                        .andEqualTo(DocTypeDimension.FIELD_VALUE_SOURCE_TYPE, valueSourceType, true)
                        .andEqualTo(DocTypeDimension.FIELD_ENABLED_FLAG, enabledFlag, true))
                .build();
        return PageHelper.doPageAndSort(pageRequest, () -> this.selectByCondition(condition));
    }

    @Override
    public List<DocTypeDimension> listBizDocTypeDimension(long tenantId) {
        return docTypeDimensionMapper.listBizDocTypeDimension(tenantId);
    }

    @Override
    public DocTypeDimension selectDocTypeDimensionById(Long dimensionId) {
        return docTypeDimensionMapper.selectDocTypeDimensionById(dimensionId);
    }

    @Override
    public List<DocTypeDimension> listDocTypeDimensionBydimensionCodes(List<String> dimensionCodes) {
        return docTypeDimensionMapper.listDocTypeDimensionBydimensionCodes(dimensionCodes);
    }

    @Override
    public List<DocTypeDimension> listDocTypeDimensionByDocTypeId(Long docTypeId) {
        return docTypeDimensionMapper.listDocTypeDimensionByDocType(docTypeId);
    }

    @Override
    public DocTypeDimension findDocTypeDimensionByDimensionCode(@Nonnull String dimensionCode) {
        // 设置查询条件
        DocTypeDimension condition = new DocTypeDimension();
        condition.setDimensionCode(dimensionCode);

        // 查询数据并返回结果
        return this.selectOne(condition);
    }

    @Override
    public List<Long> selectDocTypeIdByDimensionCode(DocTypeDimension docTypeDimension) {
        return docTypeDimensionMapper.selectDocTypeIdByDimensionCode(docTypeDimension);
    }

    @Override
    public List<Long> selectDisabledPermissionRuleId() {
        return docTypeDimensionMapper.selectDisabledPermissionRuleId();
    }
}
