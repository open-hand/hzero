package org.hzero.file.app.service;

import org.hzero.boot.file.dto.GenerateHtmlByKeyDTO;
import org.hzero.boot.file.dto.GenerateHtmlByUrlDTO;
import org.hzero.file.api.dto.OnlyOfficeCallbackDTO;
import org.hzero.file.api.dto.SaveCallbackParamDTO;

/**
 * description
 *
 * @author shuangfei.zhu@hand-china.com 2019/06/27 15:57
 */
public interface OnlyOfficeService {

    /**
     * onlyOffice 回调保存
     *
     * @param saveCallbackParam 回调参数
     */
    void updateFile(SaveCallbackParamDTO saveCallbackParam);

    /**
     * onlyOffice 回调保存
     *
     * @param callback 回调信息
     */
    void update(OnlyOfficeCallbackDTO callback);

    /**
     * 根据文件url获取在线编辑html
     *
     * @param tenantId          租户ID
     * @param generateHtmlParam 生成html参数
     * @return html
     */
    String generateHtmlByUrl(Long tenantId, GenerateHtmlByUrlDTO generateHtmlParam);

    /**
     * 根据文件key获取在线编辑html
     *
     * @param tenantId          租户ID
     * @param generateHtmlParam 生成html参数
     * @return html
     */
    String generateHtmlByKey(Long tenantId, GenerateHtmlByKeyDTO generateHtmlParam);
}
