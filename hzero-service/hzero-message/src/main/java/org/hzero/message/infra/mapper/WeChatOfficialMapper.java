package org.hzero.message.infra.mapper;

import io.choerodon.mybatis.common.BaseMapper;
import org.apache.ibatis.annotations.Param;
import org.hzero.message.domain.entity.WechatOfficial;

import java.util.List;

/**
 * 微信公众号配置Mapper
 *
 * @author shuangfei.zhu@hand-china.com 2019-10-15 14:33:21
 */
public interface WeChatOfficialMapper extends BaseMapper<WechatOfficial> {


    /**
     * 查询列表
     *
     * @param tenantId                     租户ID
     * @param serverCode                   编码
     * @param serverName                   名称
     * @param authType                     授权类型
     * @param enabledFlag                  启用
     * @param includeSiteIfQueryByTenantId 按照租户ID检索的时候是否包含平台
     * @return 查询结果
     */
    List<WechatOfficial> listWeChatOfficial(@Param("tenantId") Long tenantId,
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
    WechatOfficial getOfficialById(@Param("tenantId") Long tenantId, @Param("serverId") Long serverId);

    /**
     * 根据编码查询服务配置
     *
     * @param tenantId   租户id
     * @param serverCode 服务编码
     * @return 查询结果
     */
    WechatOfficial selectByCode(@Param("tenantId") Long tenantId,
                                @Param("serverCode") String serverCode);
}
