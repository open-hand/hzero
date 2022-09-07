package org.hzero.message.domain.repository;

import io.choerodon.core.domain.Page;
import io.choerodon.mybatis.pagehelper.domain.PageRequest;
import org.hzero.message.domain.entity.WechatOfficial;
import org.hzero.mybatis.base.BaseRepository;

/**
 * 微信公众号配置资源库
 *
 * @author shuangfei.zhu@hand-china.com 2019-10-15 14:33:21
 */
public interface WeChatOfficialRepository extends BaseRepository<WechatOfficial> {

    /**
     * 分页查询
     *
     * @param pageRequest                  分页
     * @param tenantId                     租户ID
     * @param serverCode                   编码
     * @param serverName                   名称
     * @param authType                     授权类型
     * @param includeSiteIfQueryByTenantId 按照租户ID检索的时候是否包含平台
     * @param enabledFlag                  启用
     * @return 查询结果
     */
    Page<WechatOfficial> pageWeChatOfficial(PageRequest pageRequest, Long tenantId, String serverCode, String serverName, String authType, Integer enabledFlag, boolean includeSiteIfQueryByTenantId);

    /**
     * 查询详情
     *
     * @param tenantId 租户ID
     * @param serverId 主键
     * @return 查询结果
     */
    WechatOfficial getOfficialById(Long tenantId, Long serverId);

    /**
     * 根据编码查询服务配置
     *
     * @param tenantId   租户id
     * @param serverCode 服务编码
     * @return 查询结果
     */
    WechatOfficial selectByCode(Long tenantId, String serverCode);
}
