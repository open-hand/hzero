package org.hzero.message.domain.repository;

import io.choerodon.core.domain.Page;
import io.choerodon.mybatis.pagehelper.domain.PageRequest;
import org.hzero.message.domain.entity.EmailServer;
import org.hzero.mybatis.base.BaseRepository;

/**
 * 邮箱服务资源库
 *
 * @author xianzhi.chen@hand-china.com 2018-07-27 11:30:58
 */
public interface EmailServerRepository extends BaseRepository<EmailServer> {

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
    Page<EmailServer> selectEmailServer(Long tenantId, String serverCode, String serverName, Integer enabledFlag, boolean includeSiteIfQueryByTenantId, PageRequest pageRequest);

    /**
     * 根据编码查询服务配置
     *
     * @param tenantId   租户id
     * @param serverCode 服务编码
     * @return 查询结果
     */
    EmailServer selectByCode(Long tenantId, String serverCode);
}
