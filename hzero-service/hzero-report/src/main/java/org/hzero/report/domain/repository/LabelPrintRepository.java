package org.hzero.report.domain.repository;

import org.hzero.mybatis.base.BaseRepository;
import org.hzero.report.domain.entity.LabelPrint;

/**
 * 标签打印资源库
 *
 * @author fanghan.liu@hand-china.com 2019-11-27 10:35:39
 */
public interface LabelPrintRepository extends BaseRepository<LabelPrint> {

    /**
     * 查询标签打印属性
     *
     * @param tenantId          租户ID
     * @param labelTemplateCode 标签模板编码
     * @return 标签打印属性
     */
    LabelPrint selectLabelPrintAttribute(Long tenantId, String labelTemplateCode);
}
