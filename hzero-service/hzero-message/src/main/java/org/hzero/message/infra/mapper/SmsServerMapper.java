package org.hzero.message.infra.mapper;

import io.choerodon.mybatis.common.BaseMapper;
import org.apache.ibatis.annotations.Param;
import org.hzero.message.domain.entity.SmsServer;

import java.util.List;

/**
 * 短信服务Mapper
 *
 * @author xianzhi.chen@hand-china.com 2018-07-27 11:30:58
 */
public interface SmsServerMapper extends BaseMapper<SmsServer> {
    /**
     * 查询短信服务配置列表
     *
     * @param tenantId                     租户ID
     * @param serverCode                   服务代码
     * @param serverName                   服务名称
     * @param serverTypeCode               服务类型
     * @param enabledFlag                  启用标记
     * @param includeSiteIfQueryByTenantId 按照租户ID检索的时候是否包含平台
     * @return 短信服务配置列表
     */
    List<SmsServer> selectSmsServer(@Param("tenantId") Long tenantId,
                                    @Param("serverCode") String serverCode,
                                    @Param("serverName") String serverName,
                                    @Param("serverTypeCode") String serverTypeCode,
                                    @Param("enabledFlag") Integer enabledFlag,
                                    @Param("includeSiteIfQueryByTenantId") boolean includeSiteIfQueryByTenantId);

    /**
     * 根据编码查询服务配置
     *
     * @param tenantId   租户id
     * @param serverCode 服务编码
     * @return 查询结果
     */
    SmsServer selectByCode(@Param("tenantId") Long tenantId,
                           @Param("serverCode") String serverCode);
}
