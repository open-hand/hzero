package org.hzero.report.infra.repository.impl;

import org.hzero.mybatis.base.impl.BaseRepositoryImpl;
import org.hzero.report.domain.entity.LabelPrint;
import org.hzero.report.domain.repository.LabelPrintRepository;
import org.hzero.report.infra.mapper.LabelPrintMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * 标签打印 资源库实现
 *
 * @author fanghan.liu@hand-china.com 2019-11-27 10:35:39
 */
@Component
public class LabelPrintRepositoryImpl extends BaseRepositoryImpl<LabelPrint> implements LabelPrintRepository {

    @Autowired
    private LabelPrintMapper labelPrintMapper;

    @Override
    public LabelPrint selectLabelPrintAttribute(Long tenantId, String labelTemplateCode) {
        return labelPrintMapper.selectLabelPrintAttribute(tenantId, labelTemplateCode);
    }
}
