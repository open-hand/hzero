package org.hzero.imported.app.service;

import org.hzero.imported.domain.entity.TemplateHeader;

import javax.servlet.http.HttpServletResponse;

/**
 * description
 *
 * @author shuangfei.zhu@hand-china.com 2018/11/26 14:35
 */
public interface TemplateManagerService {

    /**
     * 导出Excel
     *
     * @param tenantId     租户Id
     * @param templateCode 模板编码
     * @param response     response
     */
    void exportExcel(Long tenantId, String templateCode, HttpServletResponse response);

    /**
     * 导出Csv
     *
     * @param tenantId     租户Id
     * @param templateCode 模板编码
     * @param response     response
     */
    void exportCsv(Long tenantId, String templateCode, HttpServletResponse response);

    /**
     * 获取模板头行详细信息
     *
     * @param tenantId     租户Id
     * @param templateCode 模板编码
     * @return 头行信息
     */
    TemplateHeader getTemplateInfo(Long tenantId, String templateCode);

    /**
     * 获取模板头行详细信息
     *
     * @param tenantId     租户Id
     * @param templateCode 模板编码
     * @return 头行信息
     */
    TemplateHeader getTemplateInfoNoMulti(Long tenantId, String templateCode);

}
