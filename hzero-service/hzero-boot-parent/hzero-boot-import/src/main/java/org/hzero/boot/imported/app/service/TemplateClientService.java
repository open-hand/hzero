package org.hzero.boot.imported.app.service;

import org.hzero.boot.imported.domain.entity.Template;

import javax.servlet.http.HttpServletResponse;

/**
 * <p>
 * 模板客户端Service
 * </p>
 *
 * @author qingsheng.chen 2019/1/25 星期五 17:35
 */
public interface TemplateClientService {
    /**
     * 导出excel模板
     *
     * @param getTemplate 获取模板信息
     * @param response    响应
     */
    void exportExcel(GetTemplate getTemplate, HttpServletResponse response);

    /**
     * 导出excel模板
     *
     * @param template 模板信息
     * @param response 响应
     */
    void exportExcel(Template template, HttpServletResponse response);

    /**
     * 获取模板
     *
     * @param organizationId 租户ID
     * @param templateCode   模板编码
     * @return 模板
     */
    Template getTemplate(Long organizationId, String templateCode);

    /**
     * 获取多语言模板
     *
     * @param organizationId 租户ID
     * @param templateCode   模板编码
     * @return 模板
     */
    Template getTemplateWithMulti(Long organizationId, String templateCode);

    @FunctionalInterface
    interface GetTemplate {
        /**
         * 获取模板
         *
         * @return 模板
         */
        Template getTemplate();
    }

    /**
     * 导出csv模板
     *
     * @param getTemplate 获取模板信息
     * @param response    响应
     */
    void exportCsv(GetTemplate getTemplate, HttpServletResponse response);

    /**
     * 导出csv模板
     *
     * @param template 模板信息
     * @param response 响应
     */
    void exportCsv(Template template, HttpServletResponse response);
}
