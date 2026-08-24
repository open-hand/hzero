package org.hzero.message.infra.mapper;

import java.util.List;

import org.apache.ibatis.annotations.Param;
import org.hzero.message.domain.entity.CallServer;

import io.choerodon.mybatis.common.BaseMapper;

/**
 * 语音消息服务Mapper
 *
 * @author shuangfei.zhu@hand-china.com 2020-02-26 15:59:05
 */
public interface CallServerMapper extends BaseMapper<CallServer> {

    /**
     * 查询语音服务配置
     *
     * @param tenantId                     租户Id
     * @param enabledFlag                  启用标识
     * @param serverTypeCode               服务类型编码
     * @param serverCode                   服务编码
     * @param serverName                   服务名称
     * @param includeSiteIfQueryByTenantId 按照租户ID检索的时候是否包含平台
     * @return 语音配置
     */
    List<CallServer> listCallServer(@Param("tenantId") Long tenantId,
                                    @Param("enabledFlag") Integer enabledFlag,
                                    @Param("serverTypeCode") String serverTypeCode,
                                    @Param("serverCode") String serverCode,
                                    @Param("serverName") String serverName,
                                    @Param("includeSiteIfQueryByTenantId") boolean includeSiteIfQueryByTenantId);

    /**
     * 查询语音服务配置明细
     *
     * @param tenantId 租户Id
     * @param serverId 服务Id
     * @return 语音配置明细
     */
    CallServer detailCallServer(@Param("tenantId") Long tenantId,
                                @Param("serverId") Long serverId);

    /**
     * 根据编码查询服务配置
     *
     * @param tenantId   租户id
     * @param serverCode 服务编码
     * @return 查询结果
     */
    CallServer selectByCode(@Param("tenantId") Long tenantId,
                            @Param("serverCode") String serverCode);

}
