package org.hzero.message.infra.repository.impl;

import org.hzero.message.api.dto.UserReceiveConfigDTO;
import org.hzero.message.domain.entity.UserReceiveConfig;
import org.hzero.message.domain.repository.UserReceiveConfigRepository;
import org.hzero.message.domain.vo.UserInfoVO;
import org.hzero.message.infra.mapper.UserReceiveConfigMapper;
import org.hzero.mybatis.base.impl.BaseRepositoryImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * 用户接收配置 资源库实现
 *
 * @author shuangfei.zhu@hand-china.com 2018-11-19 20:42:53
 */
@Component
public class UserReceiveConfigRepositoryImpl extends BaseRepositoryImpl<UserReceiveConfig> implements UserReceiveConfigRepository {

    private final UserReceiveConfigMapper userReceiveConfigMapper;

    @Autowired
    public UserReceiveConfigRepositoryImpl(UserReceiveConfigMapper userReceiveConfigMapper) {
        this.userReceiveConfigMapper = userReceiveConfigMapper;
    }

    @Override
    public List<UserReceiveConfigDTO> listUserConfig(Long userId, Long tenantId) {
        List<UserReceiveConfigDTO> list = userReceiveConfigMapper.listConfigByUserId(userId, tenantId);
        List<UserReceiveConfigDTO> result = new ArrayList<>();
        List<String> codeList = new ArrayList<>();
        for (UserReceiveConfigDTO item : list) {
            String receiveCode = item.getReceiveCode();
            Long configTenantId = item.getConfigTenantId();
            if (codeList.contains(receiveCode)) {
                if (Objects.equals(configTenantId, tenantId)) {
                    result.set(codeList.indexOf(receiveCode), item);
                }
            } else {
                codeList.add(receiveCode);
                result.add(item);
            }
        }
        return result;
    }

    @Override
    public UserReceiveConfig selectParent(Long userId, String receiveCode) {
        return userReceiveConfigMapper.selectParent(userId, receiveCode);
    }

    @Override
    public List<UserReceiveConfig> selectChildren(Long userId, String receiveCode) {
        return userReceiveConfigMapper.selectChildren(userId, receiveCode);
    }

    @Override
    public void deleteByCode(String receiveCode) {
        userReceiveConfigMapper.deleteByCode(receiveCode);
    }

    @Override
    public UserInfoVO getUserInfo(Long userId, Long tenantId) {
        List<UserInfoVO> userInfoList = userReceiveConfigMapper.getUserInfo(userId, tenantId);
        if (CollectionUtils.isEmpty(userInfoList)) {
            return null;
        }
        return userInfoList.get(0);
    }

    @Override
    public List<Long> listAllTenantId() {
        return userReceiveConfigMapper.listAllTenantId();
    }
}
