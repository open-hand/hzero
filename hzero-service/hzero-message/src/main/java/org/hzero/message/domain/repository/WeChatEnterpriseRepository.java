package org.hzero.message.domain.repository;

import io.choerodon.core.domain.Page;
import io.choerodon.mybatis.pagehelper.domain.PageRequest;
import org.hzero.message.domain.entity.WeChatEnterprise;
import org.hzero.mybatis.base.BaseRepository;

/**
 * 企业微信配置资源库
 *
 * @author fanghan.liu@hand-china.com 2019-10-15 14:31:46
 */
public interface WeChatEnterpriseRepository extends BaseRepository<WeChatEnterprise> {

    /**
     * 分页查询企业微信配置
     *
     * @param pageRequest                  分页
     * @param organizationId               租户id
     * @param serverCode                   配置编码
     * @param serverName                   配置名称
     * @param authType                     授权类型
     * @param enabledFlag                  启用标识
     * @param includeSiteIfQueryByTenantId 按照租户ID检索的时候是否包含平台
     * @return 查询结果
     */
    Page<WeChatEnterprise> pageWeChatEnterprise(PageRequest pageRequest, Long organizationId, String serverCode, String serverName, String authType, Integer enabledFlag, boolean includeSiteIfQueryByTenantId);

    /**
     * 主键查询企业微信配置
     *
     * @param tenantId 租户ID
     * @param serverId 企业微信id
     * @return 查询结果
     */
    WeChatEnterprise getWeChatEnterpriseById(Long tenantId, Long serverId);

    /**
     * 根据编码查询服务配置
     *
     * @param tenantId   租户id
     * @param serverCode 服务编码
     * @return 查询结果
     */
    WeChatEnterprise selectByCode(Long tenantId, String serverCode);
}
