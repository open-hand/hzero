package org.hzero.report.app.service;

import com.alibaba.fastjson.JSONObject;
import org.hzero.report.domain.entity.LabelPrint;
import org.hzero.report.domain.entity.LabelTemplate;

import javax.servlet.http.HttpServletRequest;
import java.util.Map;

/**
 * 标签打印应用服务
 *
 * @author fanghan.liu@hand-china.com 2019-11-27 10:35:39
 */
public interface LabelPrintService {

    /**
     * 标签模板布局明细
     *
     * @param tenantId          租户ID
     * @param labelTemplateCode 模板编码
     * @return 查询结果
     */
    LabelPrint detailLabelPrint(Long tenantId, String labelTemplateCode);

    /**
     * 创建或修改标签打印配置
     *
     * @param labelPrint 打印配置
     * @return 打印配置
     */
    LabelPrint createOrUpdatePrint(LabelPrint labelPrint);

    /**
     * 获取标签Json数据
     *
     * @param labelTemplateCode 标签模板编码
     * @param data              标签数据
     * @param request           请求
     */
    void getLabelData(String labelTemplateCode, JSONObject data, HttpServletRequest request);

    /**
     * 获取标签Json数据
     *
     * @param tenantId          租户ID
     * @param labelTemplateCode 标签模板编码
     * @param data              标签数据
     * @param request           请求
     */
    void getLabelData(Long tenantId, String labelTemplateCode, JSONObject data, HttpServletRequest request);

    /**
     * 构建标签参数
     *
     * @param parameterMap 参数
     * @return 标签参数
     */
    Map<String, Object> getBuildInParameters(Map<String, String[]> parameterMap);

    /**
     * 获取标签明细
     *
     * @param labelTemplateCode 模板便阿门
     * @param buildInParams     标签参数
     * @return 标签明细
     */
    LabelTemplate selectLabel(String labelTemplateCode, Map<String, Object> buildInParams);

    /**
     * 获取标签明细
     *
     * @param tenantId          租户ID
     * @param labelTemplateCode 模板便阿门
     * @param buildInParams     标签参数
     * @return 标签明细
     */
    LabelTemplate selectLabel(Long tenantId, String labelTemplateCode, Map<String, Object> buildInParams);

    /**
     * 获取标签Json数据
     *
     * @param labelTemplateCode 标签模板编码
     * @param request           请求
     */
    JSONObject getLabelHtmlData(String labelTemplateCode, HttpServletRequest request);

    /**
     * 获取标签Json数据
     *
     * @param tenantId          租户ID
     * @param labelTemplateCode 标签模板编码
     * @param request           请求
     */
    JSONObject getLabelHtmlData(Long tenantId, String labelTemplateCode, HttpServletRequest request);

}
