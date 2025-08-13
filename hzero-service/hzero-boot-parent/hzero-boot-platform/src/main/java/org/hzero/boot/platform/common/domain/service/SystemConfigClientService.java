package org.hzero.boot.platform.common.domain.service;

/**
 * 系统配置服务接口
 *
 * @author bergturing 2020/08/18 15:12
 */
public interface SystemConfigClientService {
    /**
     * 获取系统配置缓存内容
     *
     * @param configCode 系统配置编码
     * @param tenantId   租户Id
     * @return 查询结果
     */
    String getSystemConfigByConfigCode(String configCode, Long tenantId);

    /**
     * 获取平台系统配置缓存内容
     *
     * @param configCode 系统配置编码
     * @return 查询结果
     */
    String getSystemConfigByConfigCode(String configCode);
}
