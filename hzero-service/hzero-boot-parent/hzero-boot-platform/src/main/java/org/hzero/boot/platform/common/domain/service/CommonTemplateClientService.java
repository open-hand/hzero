package org.hzero.boot.platform.common.domain.service;

import org.hzero.boot.platform.common.domain.vo.RenderResult;

import java.util.Map;

/**
 * 通用模板服务接口
 *
 * @author bergturing 2020/08/18 14:45
 */
public interface CommonTemplateClientService {
    /**
     * 渲染模板内容，如果渲染过程中出现问题，会抛出异常
     *
     * @param tenantId     租户ID
     * @param templateCode 通用模板编码
     * @param lang         语言
     * @param args         渲染参数
     * @return 渲染结果内容
     * @see io.choerodon.core.exception.CommonException
     * @see RuntimeException
     */
    String renderContent(Long tenantId, String templateCode, String lang, Map<String, Object> args);

    /**
     * 模板渲染，如果渲染过程中出现问题，会将错误信息封装到结果对象中
     *
     * @param tenantId     租户ID
     * @param templateCode 通用模板编码
     * @param lang         语言
     * @param args         渲染参数
     * @return 渲染结果对象
     */
    RenderResult render(Long tenantId, String templateCode, String lang, Map<String, Object> args);
}
