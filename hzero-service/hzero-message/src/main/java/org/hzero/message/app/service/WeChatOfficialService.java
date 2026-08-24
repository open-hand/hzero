package org.hzero.message.app.service;

import org.hzero.message.domain.entity.WechatOfficial;

/**
 * 微信公众号配置应用服务
 *
 * @author shuangfei.zhu@hand-china.com 2019-10-15 14:33:21
 */
public interface WeChatOfficialService {

    /**
     * 新建
     *
     * @param official 公众号配置
     * @return 新建的对象
     */
    WechatOfficial addOfficial(WechatOfficial official);

    /**
     * 更新
     *
     * @param official 公众号配置
     * @return 更新的对象
     */
    WechatOfficial updateOfficial(WechatOfficial official);

    /**
     * 删除
     *
     * @param official 公众号配置
     */
    void deleteOfficial(WechatOfficial official);

    /**
     * 获取解密后的配置信息
     *
     * @param tenantId   租户
     * @param serverCode 配置编码
     * @return 配置信息
     */
    WechatOfficial getOfficial(Long tenantId, String serverCode);

    /**
     * 获取解密后的配置信息
     *
     * @param tenantId   租户
     * @param serverCode 配置编码
     * @return token
     */
    String getToken(Long tenantId, String serverCode);
}
