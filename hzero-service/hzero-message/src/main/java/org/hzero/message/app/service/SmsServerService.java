package org.hzero.message.app.service;

import io.choerodon.core.domain.Page;
import io.choerodon.mybatis.pagehelper.domain.PageRequest;
import org.hzero.message.domain.entity.SmsServer;

/**
 * 短信服务应用服务
 *
 * @author xianzhi.chen@hand-china.com 2018-07-27 11:30:58
 */
public interface SmsServerService {

    /**
     * 分页查询短信服务配置列表
     *
     * @param tenantId                     租户ID
     * @param serverCode                   服务代码
     * @param serverName                   服务名称
     * @param serverTypeCode               服务类型
     * @param enabledFlag                  启用标记
     * @param includeSiteIfQueryByTenantId 按照租户ID检索的时候是否包含平台
     * @param pageRequest                  分页
     * @return 分页短信服务配置列表
     */
    Page<SmsServer> listSmsServer(Long tenantId, String serverCode, String serverName, String serverTypeCode, Integer enabledFlag, boolean includeSiteIfQueryByTenantId, PageRequest pageRequest);

    /**
     * 查询短信服务配置
     *
     * @param tenantId 租户ID
     * @param serverId 短信服务ID
     * @return 短信服务配置
     */
    SmsServer getSmsServer(Long tenantId, long serverId);

    /**
     * 查询短信服务配置
     *
     * @param tenantId   租户ID
     * @param serverCode 服务代码
     * @return 短信服务配置
     */
    SmsServer getSmsServer(long tenantId, String serverCode);

    /**
     * 新增短信服务配置
     *
     * @param smsServer 短信服务配置
     * @return 短信服务配置
     */
    SmsServer createSmsServer(SmsServer smsServer);

    /**
     * 更新短信服务配置
     *
     * @param smsServer 短信服务配置
     * @return 短信服务配置
     */
    SmsServer updateSmsServer(SmsServer smsServer);

    /**
     * 删除短信配置
     *
     * @param smsServer 短信配置
     */
    void deleteSmsServer(SmsServer smsServer);
}
