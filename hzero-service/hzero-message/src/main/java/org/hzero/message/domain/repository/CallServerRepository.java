package org.hzero.message.domain.repository;

import org.hzero.message.domain.entity.CallServer;
import org.hzero.mybatis.base.BaseRepository;

import io.choerodon.core.domain.Page;
import io.choerodon.mybatis.pagehelper.domain.PageRequest;

/**
 * 语音消息服务资源库
 *
 * @author shuangfei.zhu@hand-china.com 2020-02-26 15:59:05
 */
public interface CallServerRepository extends BaseRepository<CallServer> {

    /**
     * 查询语音服务配置
     *
     * @param pageRequest                  分页
     * @param tenantId                     租户Id
     * @param enabledFlag                  启用标识
     * @param serverTypeCode               服务类型编码
     * @param serverCode                   服务编码
     * @param serverName                   服务名称
     * @param includeSiteIfQueryByTenantId 按照租户ID检索的时候是否包含平台
     * @return 语音配置
     */
    Page<CallServer> pageCallServer(PageRequest pageRequest, Long tenantId, Integer enabledFlag, String serverTypeCode, String serverCode, String serverName, boolean includeSiteIfQueryByTenantId);

    /**
     * 查询语音服务配置明细
     *
     * @param tenantId 租户Id
     * @param serverId 服务Id
     * @return 语音配置明细
     */
    CallServer detailCallServer(Long tenantId, Long serverId);

    /**
     * 根据编码查询服务配置
     *
     * @param tenantId   租户id
     * @param serverCode 服务编码
     * @return 查询结果
     */
    CallServer selectByCode(Long tenantId, String serverCode);
}
