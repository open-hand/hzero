package org.hzero.iam.infra.repository.impl;


import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import io.choerodon.core.exception.CommonException;

import org.hzero.iam.domain.entity.UserConfig;
import org.hzero.iam.domain.repository.UserConfigRepository;
import org.hzero.iam.infra.mapper.UserConfigMapper;
import org.hzero.mybatis.base.impl.BaseRepositoryImpl;

/**
 * 用户默认配置 资源库实现
 *
 * @author zhiying.dong@hand-china.com 2018-09-14 10:46:53
 */
@Component
public class UserConfigRepositoryImpl extends BaseRepositoryImpl<UserConfig> implements UserConfigRepository {
    private final UserConfigMapper userConfigMapper;

    @Autowired
    public UserConfigRepositoryImpl(UserConfigMapper userConfigMapper) {
        this.userConfigMapper = userConfigMapper;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void createUserConfig(UserConfig config) {
        UserConfig params = new UserConfig(config.getUserId(), config.getTenantId());
        if (selectCount(params) > 0) {
            throw new CommonException("error_user_exist");
        }
        insertSelective(config);
    }

    @Override
    public List<UserConfig> selectByUserId(Long userId) {
        return userConfigMapper.selectByUserId(userId);
    }

    @Override
    public UserConfig queryUserConfig(Long userId, Long tenantId) {
        return userConfigMapper.queryUserConfig(userId, tenantId);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void updateUserConfig(UserConfig config) {
        UserConfig params = new UserConfig(config.getUserId(), config.getTenantId());
        UserConfig userConfig = selectOne(params);
        config.setUserConfigId(userConfig.getUserConfigId());
        config.setObjectVersionNumber(userConfig.getObjectVersionNumber());
        updateByPrimaryKeySelective(config);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public UserConfig createOrUpdate(UserConfig config) {
        UserConfig params = new UserConfig(config.getUserId(), config.getTenantId());
        UserConfig exist = selectOne(params);
        if (exist == null) {
            insertSelective(config);
            return config;
        } else {
            exist.setDefaultRoleId(config.getDefaultRoleId());
            if (config.getDefaultCompanyId() != null) {
                exist.setDefaultCompanyId(config.getDefaultCompanyId());
            }
            updateByPrimaryKey(exist);
            return exist;
        }
    }
}
