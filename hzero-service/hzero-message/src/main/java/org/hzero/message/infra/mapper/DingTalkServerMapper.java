package org.hzero.message.infra.mapper;

import io.choerodon.mybatis.common.BaseMapper;
import org.apache.ibatis.annotations.Param;
import org.hzero.message.domain.entity.DingTalkServer;

import java.util.List;

/**
 * 钉钉配置Mapper
 *
 * @author zifeng.ding@hand-china.com 2019-11-13 14:36:25
 */
public interface DingTalkServerMapper extends BaseMapper<DingTalkServer> {

    /**
     * 查询列表
     *
     * @param tenantId                     租户ID
     * @param serverCode                   配置编码
     * @param serverName                   配置名称
     * @param authType                     授权类型
     * @param enabledFlag                  启用标识
     * @param includeSiteIfQueryByTenantId 按照租户ID检索的时候是否包含平台
     * @return 列表
     */
    List<DingTalkServer> listDingTalkServer(@Param("tenantId") Long tenantId,
                                            @Param("serverCode") String serverCode,
                                            @Param("serverName") String serverName,
                                            @Param("authType") String authType,
                                            @Param("enabledFlag") Integer enabledFlag,
                                            @Param("includeSiteIfQueryByTenantId") boolean includeSiteIfQueryByTenantId);

    /**
     * 查询详情
     *
     * @param tenantId 租户ID
     * @param serverId 主键
     * @return 查询结果
     */
    DingTalkServer getDingTalkServerById(@Param("tenantId") Long tenantId, @Param("serverId") Long serverId);

    /**
     * 根据编码查询服务配置
     *
     * @param tenantId   租户id
     * @param serverCode 服务编码
     * @return 查询结果
     */
    DingTalkServer selectByCode(@Param("tenantId") Long tenantId,
                                @Param("serverCode") String serverCode);
}
