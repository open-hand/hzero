package org.hzero.message.domain.repository;

import io.choerodon.core.domain.Page;
import io.choerodon.mybatis.pagehelper.domain.PageRequest;
import org.hzero.message.domain.entity.DingTalkServer;
import org.hzero.mybatis.base.BaseRepository;

/**
 * 钉钉配置资源库
 *
 * @author zifeng.ding@hand-china.com 2019-11-13 14:36:25
 */
public interface DingTalkServerRepository extends BaseRepository<DingTalkServer> {

    /**
     * 分页查询
     *
     * @param pageRequest                  分页信息
     * @param tenantId                     租户ID
     * @param serverCode                   配值编码
     * @param serverName                   配置名称
     * @param authType                     授权类型
     * @param enabledFlag                  启用标识
     * @param includeSiteIfQueryByTenantId 按照租户ID检索的时候是否包含平台
     * @return 查询结果
     */
    Page<DingTalkServer> pageDingTalkServer(PageRequest pageRequest, Long tenantId, String serverCode, String serverName, String authType, Integer enabledFlag, boolean includeSiteIfQueryByTenantId);

    /**
     * 查询详情
     *
     * @param tenantId 租户ID
     * @param serverId 主键ID
     * @return 查询结果
     */
    DingTalkServer getDingTalkServerById(Long tenantId, Long serverId);

    /**
     * 根据配置编码查询配置编码
     *
     * @param tenantId   租户ID
     * @param serverCode 配置编码
     * @return 查询结果
     */
    DingTalkServer selectByCode(Long tenantId, String serverCode);
}
