package org.hzero.iam.infra.repository.impl;

import io.choerodon.core.domain.Page;
import io.choerodon.mybatis.pagehelper.PageHelper;
import io.choerodon.mybatis.pagehelper.domain.PageRequest;
import org.hzero.boot.oauth.domain.entity.BaseOpenApp;
import org.hzero.boot.platform.lov.annotation.ProcessLovValue;
import org.hzero.iam.domain.entity.OpenApp;
import org.hzero.iam.domain.repository.OpenAppRepository;
import org.hzero.iam.infra.mapper.OpenAppMapper;
import org.hzero.mybatis.base.impl.BaseRepositoryImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * 三方网站管理资源库实现
 *
 * @author jiaxu.cui@hand-china.com 2018/9/29 14:03
 */
@Component
public class OpenAppRepositoryImpl extends BaseRepositoryImpl<OpenApp> implements OpenAppRepository {

    @Autowired
    private OpenAppMapper openAppMapper;

    @Override
    public List<BaseOpenApp> listLoginWayByEnabled() {
        return openAppMapper.listLoginWayByEnabled();
    }

    @Override
    @ProcessLovValue
    public Page<OpenApp> pageOpenApp(PageRequest pageRequest, OpenApp openApp) {
        return PageHelper.doPageAndSort(pageRequest, () -> openAppMapper.selectOpenAppList(openApp));
    }

    @Override
    public OpenApp getOpenAppDetails(Long openAppId) {
        return openAppMapper.selectOpenAppDetails(openAppId);
    }
}
