package org.hzero.report.infra.mapper;

import org.apache.ibatis.annotations.Param;
import org.hzero.report.domain.entity.LabelTemplate;

import java.time.LocalDate;
import java.util.List;

import io.choerodon.mybatis.common.BaseMapper;

/**
 * 标签模板Mapper
 *
 * @author fanghan.liu@hand-china.com 2019-11-27 10:35:39
 */
public interface LabelTemplateMapper extends BaseMapper<LabelTemplate> {

    /**
     * 查询标签模板列表
     *
     * @param tenantId     租户ID
     * @param templateCode 模板编码
     * @param templateName 模板名字
     * @param datasetName  数据集名称,hrpt_dataset
     * @param enabledFlag  启用
     * @return 查询结果
     */
    List<LabelTemplate> listLabelTemplate(@Param("tenantId") Long tenantId,
                                          @Param("templateCode") String templateCode,
                                          @Param("templateName") String templateName,
                                          @Param("datasetName") String datasetName,
                                          @Param("enabledFlag") Integer enabledFlag);

    /**
     * 查询租户级标签模板列表
     *
     * @param tenantId     租户ID
     * @param templateCode 模板编码
     * @param templateName 模板名字
     * @param datasetName  数据集名称,hrpt_dataset
     * @param enabledFlag  启用
     * @param now          当前时间
     * @return 查询结果
     */
    List<LabelTemplate> listTenantLabelTemplate(@Param("tenantId") Long tenantId,
                                                @Param("templateCode") String templateCode,
                                                @Param("templateName") String templateName,
                                                @Param("datasetName") String datasetName,
                                                @Param("enabledFlag") Integer enabledFlag,
                                                @Param("now") LocalDate now);

    /**
     * 查询标签模板
     *
     * @param labelTemplateId 模板ID
     * @return 标签模板
     */
    LabelTemplate getLabelTemplateById(Long labelTemplateId);

    /**
     * 获取标签模板
     *
     * @param labelTemplateCode 模板编码
     * @param roleId            角色id
     * @param tenantId          租户id
     * @param now               当前时间
     * @return 查询结果
     */
    LabelTemplate selectLabelTemplate(@Param("labelTemplateCode") String labelTemplateCode,
                                      @Param("roleId") Long roleId,
                                      @Param("tenantId") Long tenantId,
                                      @Param("now") LocalDate now);
}
