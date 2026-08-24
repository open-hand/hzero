package org.hzero.message.infra.mapper;

import io.choerodon.mybatis.common.BaseMapper;
import org.apache.ibatis.annotations.Param;
import org.hzero.message.domain.entity.WeChatEnterprise;

import java.util.List;

/**
 * 企业微信配置Mapper
 *
 * @author fanghan.liu@hand-china.com 2019-10-15 14:31:46
 */
public interface WeChatEnterpriseMapper extends BaseMapper<WeChatEnterprise> {

    /**
     * 查询企业微信配置
     *
     * @param tenantId                     租户id
     * @param serverCode                   配置编码
     * @param serverName                   配置名称
     * @param authType                     授权类型
     * @param enabledFlag                  启用标识
     * @param includeSiteIfQueryByTenantId 按照租户ID检索的时候是否包含平台
     * @return 查询结果
     */
    List<WeChatEnterprise> listWeChatEnterprise(@Param("tenantId") Long tenantId,
                                                @Param("serverCode") String serverCode,
                                                @Param("serverName") String serverName,
                                                @Param("authType") String authType,
                                                @Param("enabledFlag") Integer enabledFlag,
                                                @Param("includeSiteIfQueryByTenantId") boolean includeSiteIfQueryByTenantId);

    /**
     * 主键查询企业微信配置
     *
     * @param tenantId 租户ID
     * @param serverId id
     * @return 查询结果
     */
    WeChatEnterprise getWeChatEnterpriseById(@Param("tenantId") Long tenantId, @Param("serverId") Long serverId);

    /**
     * 根据编码查询服务配置
     *
     * @param tenantId   租户id
     * @param serverCode 服务编码
     * @return 查询结果
     */
    WeChatEnterprise selectByCode(@Param("tenantId") Long tenantId,
                                  @Param("serverCode") String serverCode);
}
