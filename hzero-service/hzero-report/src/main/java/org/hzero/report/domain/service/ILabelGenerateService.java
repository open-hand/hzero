package org.hzero.report.domain.service;

import javax.servlet.http.HttpServletRequest;

import org.hzero.report.domain.entity.LabelParameter;
import org.jsoup.nodes.Document;

/**
 * 标签生成 领域服务接口类
 *
 * @author fanghan.liu 2019/12/04 14:08
 */
public interface ILabelGenerateService {

    /**
     * 生成标签内容
     *
     * @param labelTemplateCode 标签模板编码
     * @param request           请求
     * @return 标签HTML内容
     */
    String generateLabel(String labelTemplateCode, HttpServletRequest request);

    /**
     * 生成标签内容
     *
     * @param tenantId          租户ID
     * @param labelTemplateCode 标签模板编码
     * @param request           请求
     * @return 标签HTML内容
     */
    String generateLabel(Long tenantId, String labelTemplateCode, HttpServletRequest request);

    /**
     * 解析文本参数
     *
     * @param document       html内容
     * @param labelParameter 标签参数
     */
    void parseText(Document document, LabelParameter labelParameter);

    /**
     * 解析图片参数
     *
     * @param document       html内容
     * @param labelParameter 标签参数
     */
    void parseImg(Document document, LabelParameter labelParameter);

    /**
     * 解析条形码参数
     *
     * @param document       html内容
     * @param labelParameter 标签参数
     */
    void parseBarCode(Document document, LabelParameter labelParameter);

    /**
     * 解析二维码参数
     *
     * @param document       html内容
     * @param labelParameter 标签参数
     */
    void parseQrCode(Document document, LabelParameter labelParameter);
}
