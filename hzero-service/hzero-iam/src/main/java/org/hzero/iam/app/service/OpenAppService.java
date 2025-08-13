package org.hzero.iam.app.service;

import org.hzero.iam.domain.entity.OpenApp;


/**
 * 三方网站管理服务
 *
 * @author jiaxu.cui@hand-china.com 2018/9/29 14:08
 */
public interface OpenAppService {

    /**
     * 创建三方网站
     *
     * @param openApp 三方网站实体
     * @return OpenApp
     */
    OpenApp createOpenApp(OpenApp openApp);

    /**
     * 更新三方网站
     *
     * @param openApp 三方网站实体
     * @return OpenApp
     */
    OpenApp updateOpenApp(OpenApp openApp);

    /**
     * 启用三方网站
     *
     * @param openAppId 三方网站主键Id
     * @return OpenApp
     */
    OpenApp enableOpenApp(Long openAppId);

    /**
     * 禁用三方网站
     *
     * @param openAppId 三方网站主键Id
     * @return OpenApp
     */
    OpenApp disableOpenApp(Long openAppId);

    /**
     * 删除三方网站
     *
     * @param openAppId 三方网站主键Id
     */
    void deleteOpenApp(Long openAppId);

    /**
     * 刷新第三方登录方式缓存
     *
     */
    void saveOpenAppCache();

    /**
     * 初始化缓存OpenApp内容
     */
    void initCacheOpenApp();
}
