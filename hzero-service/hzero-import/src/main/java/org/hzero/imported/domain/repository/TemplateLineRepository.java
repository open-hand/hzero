package org.hzero.imported.domain.repository;

import java.util.List;

import org.hzero.imported.domain.entity.TemplateLine;
import org.hzero.imported.domain.entity.TemplateLineTl;
import org.hzero.mybatis.base.BaseRepository;

/**
 * 模板行配置资源库
 *
 * @author shuangfei.zhu@hand-china.com 2018/11/29 11:06
 */
public interface TemplateLineRepository extends BaseRepository<TemplateLine> {
    /**
     * 删除模板行
     *
     * @param templateId 模板ID
     * @return 删除数量
     */
    int deleteByHeaderId(Long templateId);

    /**
     * 删除模板行
     *
     * @param targetId 模板ID
     * @return 删除数量
     */
    int deleteByTargetId(Long targetId);

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
    List<TemplateLine> listTemplateLineByTargetId(Long targetId, String columnCode, String columnName);

    /**
     * 获取模板行详情
     *
     * @param templateLineId 行id
     * @return 查询结果
     */
    TemplateLine getTemplateLine(Long templateLineId);
}
