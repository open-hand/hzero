package org.hzero.iam.app.service.impl;

import java.util.List;

import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.Assert;

import org.hzero.boot.oauth.domain.entity.BaseOpenApp;
import org.hzero.boot.oauth.domain.repository.BaseOpenAppRepository;
import org.hzero.core.base.BaseConstants;
import org.hzero.iam.app.service.OpenAppService;
import org.hzero.iam.domain.entity.OpenApp;
import org.hzero.iam.domain.repository.OpenAppRepository;
import org.hzero.mybatis.helper.DataSecurityHelper;

/**
 * 三方网站管理服务实现
 *
 * @author jiaxu.cui@hand-china.com 2018/9/29 14:09
 */
@Service
public class OpenAppServiceImpl implements OpenAppService {

    @Autowired
    private OpenAppRepository openAppRepository;
    @Autowired
    private BaseOpenAppRepository baseOpenAppRepository;


    @Override
    @Transactional(rollbackFor = Exception.class)
    public OpenApp createOpenApp(OpenApp openApp) {
        //校验数据
        openApp.validateAppCode(openAppRepository);
        // 开启数据加密
        DataSecurityHelper.open();
        //插入数据
        openAppRepository.insertSelective(openApp);
        //刷新缓存
        refreshCache(openApp);
        return openApp;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public OpenApp updateOpenApp(OpenApp openApp) {
        //校验应用编码
        openApp.validateAppCode(openAppRepository);
        OpenApp dbOpenApp = openAppRepository.selectByPrimaryKey(openApp.getOpenAppId());
        Assert.notNull(dbOpenApp, BaseConstants.ErrorCode.DATA_NOT_EXISTS);
        if (StringUtils.isBlank(openApp.getAppKey())) {
            // 未修改授权码（AppKey），更新其它字段
            openAppRepository.updateOptional(openApp, OpenApp.FIELD_ORDER_SEQ, OpenApp.FIELD_APP_IMAGE,
                    OpenApp.FIELD_APP_ID, OpenApp.FIELD_APP_NAME, OpenApp.FIELD_SUB_APP_ID, OpenApp.FIELD_SCOPE
            );
            // 将数据库中的 appKey 设置进去
            openApp.setAppKey(dbOpenApp.getAppKey());
        } else {
            // 修改了授权码，先加密再更新
            DataSecurityHelper.open();
            openAppRepository.updateOptional(openApp,
                    OpenApp.FIELD_ORDER_SEQ,
                    OpenApp.FIELD_APP_IMAGE,
                    OpenApp.FIELD_APP_ID,
                    OpenApp.FIELD_APP_NAME,
                    OpenApp.FIELD_APP_KEY,
                    OpenApp.FIELD_SUB_APP_ID,
                    OpenApp.FIELD_SCOPE
            );
        }
        if (BaseConstants.Flag.YES.equals(openApp.getEnabledFlag())) {
            // 非禁用数据需刷新缓存
            refreshCache(openApp);
        }
        return openApp;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public OpenApp enableOpenApp(Long openAppId) {
        OpenApp openApp = openAppRepository.selectByPrimaryKey(openAppId);
        openApp.setEnabledFlag(BaseConstants.Flag.YES);
        openAppRepository.updateOptional(openApp, OpenApp.FIELD_ENABLED_FLAG);
        //刷新缓存
        refreshCache(openApp);
        return openApp;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public OpenApp disableOpenApp(Long openAppId) {
        OpenApp openApp = openAppRepository.selectByPrimaryKey(openAppId);
        openApp.setEnabledFlag(BaseConstants.Flag.NO);
        openAppRepository.updateOptional(openApp, OpenApp.FIELD_ENABLED_FLAG);
        //删除缓存
        baseOpenAppRepository.removeOpenApp(openApp.getChannel(), openApp.getAppCode());
        return openApp;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void deleteOpenApp(Long openAppId) {
        OpenApp openApp = openAppRepository.selectByPrimaryKey(openAppId);
        //清除数据
        openAppRepository.deleteByPrimaryKey(openAppId);
        //删除缓存
        baseOpenAppRepository.removeOpenApp(openApp.getChannel(), openApp.getAppCode());
    }

    /**
     * 刷新第三方登录方式缓存
     */
    @Override
    public void saveOpenAppCache() {
        List<BaseOpenApp> userOpenAppList = openAppRepository.listLoginWayByEnabled();
        if (CollectionUtils.isNotEmpty(userOpenAppList)) {
            // 刷新缓存
            userOpenAppList.forEach(x -> baseOpenAppRepository.saveOpenApp(x));
        }
    }

    @Override
    public void initCacheOpenApp() {
        // init cache open app
        this.saveOpenAppCache();
    }

    /**
     * 刷新缓存
     */
    private void refreshCache(OpenApp openApp) {
        BaseOpenApp baseOpenApp = new BaseOpenApp();
        BeanUtils.copyProperties(openApp, baseOpenApp);
        baseOpenAppRepository.saveOpenApp(baseOpenApp);
    }
}
