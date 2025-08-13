package org.hzero.report.infra.mapper;

import org.apache.ibatis.annotations.Param;
import org.hzero.report.domain.entity.LabelPrint;

import io.choerodon.mybatis.common.BaseMapper;

/**
 * 标签打印Mapper
 *
 * @author fanghan.liu@hand-china.com 2019-11-27 10:35:39
 */
public interface LabelPrintMapper extends BaseMapper<LabelPrint> {

    /**
     * 获取标签打印配置
     *
     * @param tenantId          租户ID
     * @param labelTemplateCode 标签模板编码
     * @return 查询结果
     */
    LabelPrint selectLabelPrintAttribute(@Param("tenantId") Long tenantId,
                                         @Param("labelTemplateCode") String labelTemplateCode);
}
