package org.hzero.imported.app.service;

import java.util.List;

import org.hzero.imported.domain.entity.TemplateLine;

import io.choerodon.core.domain.Page;
import io.choerodon.mybatis.pagehelper.domain.PageRequest;

/**
 * @author shuangfei.zhu@hand-china.com 2018-12-7 16:42:53
 */
public interface TemplateLineService {

    /**
     * 通过模板头主键分页查询模板行
     *
     * @param targetId    目标ID
     * @param columnCode  列编码
     * @param columnName  列名称
     * @param pageRequest 分页
     * @return 行
     */
    Page<TemplateLine> pageTemplateLine(Long targetId, String columnCode, String columnName, PageRequest pageRequest);

    /**
     * 查询明细
     *
     * @param lineId   行Id
     * @param tenantId 租户Id
     * @return 行
     */
    TemplateLine detailTemplateLine(Long lineId, Long tenantId);

    /**
     * 通过模板ID查询模板行
     *
     * @param templateHeaderId 模板头ID
     * @return 模板行
     */
    List<TemplateLine> listTemplateLine(Long templateHeaderId);

    /**
     * 创建模板行信息
     *
     * @param templateLines 创建参数
     * @return 行
     */
    TemplateLine createTemplateLine(TemplateLine templateLines);

    /**
     * 更新模板行信息
     *
     * @param templateLines 更新参数
     * @return 行
     */
    TemplateLine updateTemplateLine(TemplateLine templateLines);

    /**
     * 删除模板ID
     *
     * @param lineId 模板行ID
     */
    void deleteTemplateLine(Long lineId);
}
