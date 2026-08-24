package org.hzero.message.app.service;

import io.choerodon.core.domain.Page;
import io.choerodon.mybatis.pagehelper.domain.PageRequest;
import org.hzero.message.domain.entity.EmailProperty;
import org.hzero.message.domain.entity.EmailServer;

import java.util.List;

/**
 * 邮箱服务应用服务
 *
 * @author xianzhi.chen@hand-china.com 2018-07-27 11:30:58
 */
public interface EmailServerService {

    /**
     * 分页查询邮箱服务器信息
     *
     * @param tenantId                     租户ID
     * @param serverCode                   服务器编码
     * @param serverName                   服务器名称
     * @param enabledFlag                  启用标记
     * @param includeSiteIfQueryByTenantId 按照租户ID检索的时候是否包含平台
     * @param pageRequest                  分页
     * @return 邮箱服务器信息列表
     */
    Page<EmailServer> listEmailServer(Long tenantId, String serverCode, String serverName, Integer enabledFlag, boolean includeSiteIfQueryByTenantId, PageRequest pageRequest);

    /**
     * 查询邮箱服务器信息明细
     *
     * @param tenantId 租户ID
     * @param serverId 邮箱服务器信息ID
     * @return 邮箱服务器信息明细
     */
    EmailServer getEmailServer(Long tenantId, long serverId);

    /**
     * 查询邮箱服务器信息明细
     *
     * @param tenantId   租户ID
     * @param serverCode 服务器编码
     * @return 邮箱服务器信息明细
     */
    EmailServer getEmailServer(long tenantId, String serverCode);

    /**
     * 查询邮箱配置列表
     *
     * @param tenantId 租户ID
     * @param serverId 邮箱服务器信息ID
     * @return 邮箱配置列表
     */
    List<EmailProperty> listEmailProperty(Long tenantId, long serverId);

    /**
     * 创建邮箱信息
     *
     * @param emailServer 邮箱服务器信息
     * @return 邮箱服务器信息
     */
    EmailServer createEmailServer(EmailServer emailServer);

    /**
     * 更新邮箱信息
     *
     * @param emailServer 邮箱服务器信息
     * @return 邮箱服务器信息
     */
    EmailServer updateEmailServer(EmailServer emailServer);

    /**
     * 根据编码查询邮箱账户
     *
     * @param serverCode 有项目账户编码
     * @param tenantId   租户Id
     * @return 邮箱服务器信息
     */
    List<EmailServer> listEmailServersByCode(String serverCode, Long tenantId);

    /**
     * 删除邮箱信息
     *
     * @param emailServer 邮箱服务器信息
     */
    void deleteEmailServer(EmailServer emailServer);
}
