package org.hzero.boot.platform.common.domain.service;

import org.hzero.boot.platform.common.vo.TemplateConfigVO;

import java.util.Set;

/**
 * 内容模板服务接口
 *
 * @author bergturing 2020/08/18 15:05
 */
public interface ContentTemplateClientService {
    /**
     * 查询缓存获取某域名下指定模版编码的模板配置值
     */
    Set<TemplateConfigVO> getTemplateConfigValues(String domainUrl, String sourceType, String templateCode,
                                                  String configCode);

    /**
     * 查询缓存获取域名管理下指定模版编码的模板配置值
     */
    Set<TemplateConfigVO> getTemplateConfigValues(String domainUrl, String templateCode, String configCode);

    /**
     * 查询缓存获取某域名下指定模版编码的单一模板配置值
     */
    TemplateConfigVO getOneTemplateConfigValue(String domainUrl, String sourceType, String templateCode,
                                               String configCode);

    /**
     * 查询缓存获取某域名下指定模版编码的单一模板配置值
     */
    TemplateConfigVO getOneTemplateConfigValue(String domainUrl, String templateCode, String configCode);

    /**
     * 获取默认模板缓存值
     */
    Set<TemplateConfigVO> getDefaultTplConfigValues(String domainUrl, String sourceType, String configCode);

    /**
     * 获取默认模板缓存值
     */
    Set<TemplateConfigVO> getDefaultTplConfigValues(String domainUrl, String configCode);

    /**
     * 获取默认模板单一缓存值
     */
    TemplateConfigVO getOneDefaultTplConfigValue(String domainUrl, String sourceType, String configCode);

    /**
     * 获取默认模板单一缓存值
     */
    TemplateConfigVO getOneDefaultTplConfigValue(String domainUrl, String configCode);
}
