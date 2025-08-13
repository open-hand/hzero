package org.hzero.boot.oauth.domain.repository;

import java.util.List;

import org.hzero.boot.oauth.domain.entity.BaseOpenApp;
import org.hzero.mybatis.base.BaseRepository;

/**
 * 三方网站管理资源库
 *
 * @author xiaoyu.zhao@hand-china.com 2019/08/28 9:03
 */
public interface BaseOpenAppRepository extends BaseRepository<BaseOpenApp> {

    /**
     * 保存三方登录方式缓存
     *
     * @param baseOpenApp 缓存参数
     */
    void saveOpenApp(BaseOpenApp baseOpenApp);

    /**
     * 删除三方登录方式缓存
     *
     * @param channel 登录渠道
     * @param appCode 应用编码
     */
    void removeOpenApp(String channel, String appCode);

    /**
     * 通过渠道和应用编码获取三方登录配置
     *
     * @param channel 渠道
     * @param appCode 应用编码
     * @return 三方登录配置
     */
    BaseOpenApp getOpenApp(String channel, String appCode);

    /**
     * 通过渠道获取三方登录配置
     *
     * @param channel 渠道
     * @return 三方登录配置
     */
    List<BaseOpenApp> getOpenApps(String channel);

}
