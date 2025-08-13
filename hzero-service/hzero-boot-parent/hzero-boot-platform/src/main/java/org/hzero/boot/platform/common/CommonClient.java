package org.hzero.boot.platform.common;

import org.hzero.boot.platform.common.domain.service.CommonTemplateClientService;
import org.hzero.boot.platform.common.domain.service.ContentTemplateClientService;
import org.hzero.boot.platform.common.domain.service.SystemConfigClientService;
import org.hzero.boot.platform.common.domain.vo.RenderResult;
import org.hzero.boot.platform.common.vo.TemplateConfigVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Map;
import java.util.Set;

/**
 * 通用客户端，封装通用客户端方法，减少平台服务客户端数量
 *
 * @author xiaoyu.zhao@hand-china.com 2019/07/11 11:33
 */
@Component
public class CommonClient {
    /**
     * 内容模板服务对象
     */
    private final ContentTemplateClientService contentTemplateClientService;
    /**
     * 系统配置服务对象
     */
    private final SystemConfigClientService systemConfigClientService;
    /**
     * 通用模板服务对象
     */
    private final CommonTemplateClientService commonTemplateClientService;

    @Autowired
    public CommonClient(ContentTemplateClientService contentTemplateClientService,
                        SystemConfigClientService systemConfigClientService,
                        CommonTemplateClientService commonTemplateClientService) {
        this.contentTemplateClientService = contentTemplateClientService;
        this.systemConfigClientService = systemConfigClientService;
        this.commonTemplateClientService = commonTemplateClientService;
    }

// ------------------------------------ContentTemplate------------------------------------------------------------------

    /**
     * 查询缓存获取某域名下指定模版编码的模板配置值
     */
    public Set<TemplateConfigVO> getTemplateConfigValues(String domainUrl, String sourceType, String templateCode, String configCode) {
        return this.contentTemplateClientService.getTemplateConfigValues(domainUrl, sourceType, templateCode, configCode);
    }

    /**
     * 查询缓存获取域名管理下指定模版编码的模板配置值
     */
    public Set<TemplateConfigVO> getTemplateConfigValues(String domainUrl, String templateCode, String configCode) {
        return this.contentTemplateClientService.getTemplateConfigValues(domainUrl, templateCode, configCode);
    }

    /**
     * 查询缓存获取某域名下指定模版编码的单一模板配置值
     */
    public TemplateConfigVO getOneTemplateConfigValue(String domainUrl, String sourceType, String templateCode, String configCode) {
        return this.contentTemplateClientService.getOneTemplateConfigValue(domainUrl, sourceType, templateCode, configCode);
    }

    /**
     * 查询缓存获取某域名下指定模版编码的单一模板配置值
     */
    public TemplateConfigVO getOneTemplateConfigValue(String domainUrl, String templateCode, String configCode) {
        return this.contentTemplateClientService.getOneTemplateConfigValue(domainUrl, templateCode, configCode);
    }

    /**
     * 获取默认模板缓存值
     */
    public Set<TemplateConfigVO> getDefaultTplConfigValues(String domainUrl, String sourceType, String configCode) {
        return this.contentTemplateClientService.getDefaultTplConfigValues(domainUrl, sourceType, configCode);
    }

    /**
     * 获取默认模板缓存值
     */
    public Set<TemplateConfigVO> getDefaultTplConfigValues(String domainUrl, String configCode) {
        return this.contentTemplateClientService.getDefaultTplConfigValues(domainUrl, configCode);
    }

    /**
     * 获取默认模板单一缓存值
     */
    public TemplateConfigVO getOneDefaultTplConfigValue(String domainUrl, String sourceType, String configCode) {
        return this.contentTemplateClientService.getOneDefaultTplConfigValue(domainUrl, sourceType, configCode);
    }

    /**
     * 获取默认模板单一缓存值
     */
    public TemplateConfigVO getOneDefaultTplConfigValue(String domainUrl, String configCode) {
        return this.contentTemplateClientService.getOneDefaultTplConfigValue(domainUrl, configCode);
    }

// ------------------------------------SystemConfig---------------------------------------------------------------------

    /**
     * 获取系统配置缓存内容
     *
     * @param configCode 系统配置编码
     * @param tenantId   租户Id
     * @return 查询结果
     */
    public String getSystemConfigByConfigCode(String configCode, Long tenantId) {
        return this.systemConfigClientService.getSystemConfigByConfigCode(configCode, tenantId);
    }

    /**
     * 获取平台系统配置缓存内容
     *
     * @param configCode 系统配置编码
     * @return 查询结果
     */
    public String getSystemConfigByConfigCode(String configCode) {
        return this.systemConfigClientService.getSystemConfigByConfigCode(configCode);
    }

// ------------------------------------CommonTemplate-------------------------------------------------------------------

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
    public String renderContent(Long tenantId, String templateCode, String lang, Map<String, Object> args) {
        return this.commonTemplateClientService.renderContent(tenantId, templateCode, lang, args);
    }

    /**
     * 模板渲染，如果渲染过程中出现问题，会将错误信息封装到结果对象中
     *
     * @param tenantId     租户ID
     * @param templateCode 通用模板编码
     * @param lang         语言
     * @param args         渲染参数
     * @return 渲染结果对象
     */
    public RenderResult render(Long tenantId, String templateCode, String lang, Map<String, Object> args) {
        return this.commonTemplateClientService.render(tenantId, templateCode, lang, args);
    }
}
