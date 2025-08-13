package org.hzero.message.domain.repository;

import io.choerodon.core.domain.Page;
import io.choerodon.mybatis.pagehelper.domain.PageRequest;
import org.hzero.message.domain.entity.SmsServer;
import org.hzero.mybatis.base.BaseRepository;

/**
 * 短信服务资源库
 *
 * @author xianzhi.chen@hand-china.com 2018-07-27 11:30:58
 */
public interface SmsServerRepository extends BaseRepository<SmsServer> {

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
    Page<SmsServer> selectSmsServer(Long tenantId, String serverCode, String serverName, String serverTypeCode, Integer enabledFlag, boolean includeSiteIfQueryByTenantId, PageRequest pageRequest);

    /**
     * 根据编码查询服务配置
     *
     * @param tenantId   租户id
     * @param serverCode 服务编码
     * @return 查询结果
     */
    SmsServer selectByCode(Long tenantId, String serverCode);
}
