package org.hzero.message.infra.mapper;

import io.choerodon.mybatis.common.BaseMapper;
import org.apache.ibatis.annotations.Param;
import org.hzero.message.domain.entity.EmailServer;

import java.util.List;

/**
 * 邮箱服务Mapper
 *
 * @author xianzhi.chen@hand-china.com 2018-07-27 11:30:58
 */
public interface EmailServerMapper extends BaseMapper<EmailServer> {
    /**
     * 查询邮箱服务器信息
     *
     * @param tenantId                     租户ID
     * @param serverCode                   服务器编码
     * @param serverName                   服务器名称
     * @param enabledFlag                  启用标记
     * @param includeSiteIfQueryByTenantId 按照租户ID检索的时候是否包含平台
     * @return 邮箱服务器信息列表
     */
    List<EmailServer> selectEmailServer(@Param("tenantId") Long tenantId,
                                        @Param("serverCode") String serverCode,
                                        @Param("serverName") String serverName,
                                        @Param("enabledFlag") Integer enabledFlag,
                                        @Param("includeSiteIfQueryByTenantId") boolean includeSiteIfQueryByTenantId);

    /**
     * 根据编码查询服务配置
     *
     * @param tenantId   租户id
     * @param serverCode 服务编码
     * @return 查询结果
     */
    EmailServer selectByCode(@Param("tenantId") Long tenantId,
                             @Param("serverCode") String serverCode);
}
