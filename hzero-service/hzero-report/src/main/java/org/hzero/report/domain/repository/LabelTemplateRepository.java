package org.hzero.report.domain.repository;

import org.hzero.mybatis.base.BaseRepository;
import org.hzero.report.domain.entity.LabelTemplate;

import java.time.LocalDate;

import io.choerodon.core.domain.Page;
import io.choerodon.mybatis.pagehelper.domain.PageRequest;

/**
 * 标签模板资源库
 *
 * @author fanghan.liu@hand-china.com 2019-11-27 10:35:39
 */
public interface LabelTemplateRepository extends BaseRepository<LabelTemplate> {

    /**
     * 查询标签模板列表
     *
     * @param tenantId     租户ID
     * @param templateCode 模板编码
     * @param templateName 模板名字
     * @param datasetName  数据集名称,hrpt_dataset
     * @param enabledFlag  启用
     * @param pageRequest  分页
     * @return 查询结果
     */
    Page<LabelTemplate> listLabelTemplate(Long tenantId, String templateCode, String templateName, String datasetName, Integer enabledFlag, PageRequest pageRequest);

    /**
     * 查询租户标签模板列表
     *
     * @param tenantId     租户ID
     * @param templateCode 模板编码
     * @param templateName 模板名字
     * @param datasetName  数据集名称,hrpt_dataset
     * @param enabledFlag  启用
     * @param pageRequest  分页
     * @return 查询结果
     */
    Page<LabelTemplate> listTenantLabelTemplate(Long tenantId, String templateCode, String templateName, String datasetName, Integer enabledFlag, PageRequest pageRequest);


    /**
     * 查询标签模板
     *
     * @param labelTemplateId 模板ID
     * @return 查询结果
     */
    LabelTemplate getLabelTemplateById(Long labelTemplateId);

    /**
     * 获取标签模板
     *
     * @param labelTemplateCode 模板编码
     * @param roleId            角色id
     * @param tenantId          租户id
     * @param date              时间
     * @return 查询结果
     */
    LabelTemplate selectLabelTemplate(String labelTemplateCode, Long roleId, Long tenantId, LocalDate date);
}
