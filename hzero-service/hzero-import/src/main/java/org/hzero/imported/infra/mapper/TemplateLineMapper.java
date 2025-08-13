package org.hzero.imported.infra.mapper;

import java.util.List;

import org.apache.ibatis.annotations.Param;
import org.hzero.imported.domain.entity.TemplateLine;
import org.hzero.imported.domain.entity.TemplateLineTl;

import io.choerodon.mybatis.common.BaseMapper;

/**
 * @author xiaowei.zhang@hand-china.com
 * @date 2018/5/30
 * <p>
 * changeBy shuangfei.zhu@hand-china.com
 */
public interface TemplateLineMapper extends BaseMapper<TemplateLine> {
    /**
     * 删除模板行
     *
     * @param templateId 模板ID
     * @return 删除数量
     */
    int deleteByHeaderId(@Param("templateId") Long templateId);

    /**
     * 删除模板行
     *
     * @param targetId 模板目标ID
     * @return 删除数量
     */
    int deleteByTargetId(@Param("targetId") Long targetId);

    /**
     * 通过模板ID查询模板行
     *
     * @param templateHeaderId 模板头ID
     * @return 模板行
     */
    List<TemplateLine> listTemplateLine(Long templateHeaderId);

    /**
     * 获取列名多语言
     *
     * @param templateLineId 列ID
     * @return 列多语言
     */
    List<TemplateLineTl> getColumnNameTl(Long templateLineId);

    /**
     * 通过模板sheetID查询模板行
     *
     * @param targetId   模板target ID
     * @param columnCode 列编码
     * @param columnName 列名称
     * @return 模板行
     */
    List<TemplateLine> listTemplateLineByTargetId(@Param("targetId") Long targetId,
                                                  @Param("columnCode") String columnCode,
                                                  @Param("columnName") String columnName);

    /**
     * 获取模板行详情
     *
     * @param templateLineId 行id
     * @return 查询结果
     */
    TemplateLine getTemplateLine(Long templateLineId);
}
