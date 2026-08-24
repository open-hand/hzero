package org.hzero.message.app.service.impl;

import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.apache.commons.lang3.StringUtils;
import org.hzero.core.algorithm.tree.TreeBuilder;
import org.hzero.core.base.BaseConstants;
import org.hzero.core.redis.RedisHelper;
import org.hzero.message.api.dto.ReceiveConfigDTO;
import org.hzero.message.api.dto.UserReceiveConfigDTO;
import org.hzero.message.app.service.UserReceiveConfigService;
import org.hzero.message.domain.entity.UserReceiveConfig;
import org.hzero.message.domain.repository.ReceiveConfigRepository;
import org.hzero.message.domain.repository.UserReceiveConfigRepository;
import org.hzero.message.domain.vo.UserInfoVO;
import org.hzero.message.infra.constant.HmsgConstant;
import org.hzero.mybatis.helper.SecurityTokenHelper;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.Assert;
import org.springframework.util.CollectionUtils;

import io.choerodon.core.oauth.DetailsHelper;

/**
 * 用户接收配置应用服务默认实现
 *
 * @author shuangfei.zhu@hand-china.com 2018-11-19 20:42:53
 */
@Service
public class UserReceiveConfigServiceImpl implements UserReceiveConfigService {

    private final UserReceiveConfigRepository userReceiveConfigRepository;
    private final ReceiveConfigRepository receiveConfigRepository;
    private final RedisHelper redisHelper;

    @Autowired
    public UserReceiveConfigServiceImpl(UserReceiveConfigRepository userReceiveConfigRepository,
                                        ReceiveConfigRepository receiveConfigRepository,
                                        RedisHelper redisHelper) {
        this.userReceiveConfigRepository = userReceiveConfigRepository;
        this.receiveConfigRepository = receiveConfigRepository;
        this.redisHelper = redisHelper;
    }

    @Override
    public List<UserReceiveConfigDTO> listUserConfig(Long userId, Long organizationId) {
        // 租户的接收列表
        List<ReceiveConfigDTO> configs = receiveConfigRepository.listConfig(organizationId);
        // 用户已存在的接受配置
        List<UserReceiveConfigDTO> userConfigs = userReceiveConfigRepository.listUserConfig(userId, organizationId);
        // 用户接收配置编码
        List<String> list = userConfigs.stream().map(UserReceiveConfigDTO::getReceiveCode).collect(Collectors.toList());
        // 替换默认配置
        for (ReceiveConfigDTO config : configs) {
            if (!list.contains(config.getReceiveCode())) {
                UserReceiveConfigDTO userReceiveConfig = new UserReceiveConfigDTO();
                BeanUtils.copyProperties(config, userReceiveConfig);
                userConfigs.add(userReceiveConfig);
            }
        }
        userConfigs.forEach(userConfig -> {
            String[] types = StringUtils.isBlank(userConfig.getReceiveType()) ? null : userConfig.getReceiveType().split(BaseConstants.Symbol.COMMA);
            String[] defaultTypes = StringUtils.isBlank(userConfig.getDefaultReceiveType()) ? null : userConfig.getDefaultReceiveType().split(BaseConstants.Symbol.COMMA);
            userConfig.setReceiveTypeList(types == null ? null : Arrays.asList(types)).setDefaultReceiveTypeList(defaultTypes == null ? null : Arrays.asList(defaultTypes));
        });
        // 根据编码排序，防止乱序
        userConfigs = userConfigs.stream().sorted(Comparator.comparingInt(item -> item.getReceiveCode().hashCode())).collect(Collectors.toList());
        return TreeBuilder.buildTree(userConfigs, HmsgConstant.ReceiveConfig.OVERALL_PARENT_CODE, UserReceiveConfigDTO::getReceiveCode, UserReceiveConfigDTO::getParentReceiveCode);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public List<UserReceiveConfigDTO> createAndUpdate(Long organizationId, List<UserReceiveConfig> userReceiveConfigList) {
        // 获取用户Id
        Assert.notNull(DetailsHelper.getUserDetails(), HmsgConstant.ErrorCode.USER_DETAIL_NOT_FOUND);
        Assert.notNull(DetailsHelper.getUserDetails().getUserId(), HmsgConstant.ErrorCode.USER_DETAIL_NOT_FOUND);
        Long userId = DetailsHelper.getUserDetails().getUserId();
        // 查询原用户接收配置
        List<UserReceiveConfig> oldConfigList = userReceiveConfigRepository.select(new UserReceiveConfig().setUserId(userId));
        userReceiveConfigList.forEach(userReceiveConfig -> {
            userReceiveConfig.setUserId(userId);
            userReceiveConfig.setTenantId(organizationId);
            if (userReceiveConfig.getUserReceiveId() == null) {
                userReceiveConfig.validateRepeat(userReceiveConfigRepository);
                userReceiveConfigRepository.insertSelective(userReceiveConfig);
            } else {
                // 更新
                SecurityTokenHelper.validToken(userReceiveConfig);
                userReceiveConfigRepository.updateOptional(userReceiveConfig,
                        UserReceiveConfig.FIELD_RECEIVE_TYPE);
            }
        });
        UserInfoVO userInfo = userReceiveConfigRepository.getUserInfo(userId, organizationId);
        if (userInfo == null) {
            return this.listUserConfig(userId, organizationId);
        }
        if (!CollectionUtils.isEmpty(oldConfigList)) {
            // 对比数据库原有数据，把用户删掉的消息类型移除黑名单缓存, 用户新增的数据加入黑名单
            Map<String, List<String>> oldMap = oldConfigList.stream().collect(Collectors.toMap(UserReceiveConfig::getReceiveCode, item -> Arrays.asList(item.getReceiveType().split(BaseConstants.Symbol.COMMA)), (k1, k2) -> k2));
            Map<String, List<String>> newMap = userReceiveConfigList.stream().collect(Collectors.toMap(UserReceiveConfig::getReceiveCode, item -> Arrays.asList(item.getReceiveType().split(BaseConstants.Symbol.COMMA)), (k1, k2) -> k2));
            // 正向比较，获取更新和新增
            for (Map.Entry<String, List<String>> entrySet : newMap.entrySet()) {
                if (oldMap.containsKey(entrySet.getKey())) {
                    // 需要更新，删除原数据
                    UserReceiveConfig.deleteCache(redisHelper, oldMap.get(entrySet.getKey()), userInfo, entrySet.getKey());
                }
                // 新增
                UserReceiveConfig.refreshCache(redisHelper, entrySet.getValue(), userInfo, entrySet.getKey());
            }
            // 反向比较， 获取删除
            for (Map.Entry<String, List<String>> entrySet : oldMap.entrySet()) {
                if (newMap.containsKey(entrySet.getKey())) {
                    continue;
                }
                // 删除
                UserReceiveConfig.deleteCache(redisHelper, entrySet.getValue(), userInfo, entrySet.getKey());
            }
        } else {
            // 全部是新增数据
            for (UserReceiveConfig item : userReceiveConfigList) {
                List<String> msgTypeList = Arrays.asList(item.getReceiveType().split(BaseConstants.Symbol.COMMA));
                UserReceiveConfig.refreshCache(redisHelper, msgTypeList, userInfo, item.getReceiveCode());
            }
        }
        return this.listUserConfig(userId, organizationId);
    }

    @Override
    public void refreshTenantUserConfig(Long tenantId) {
        List<UserReceiveConfig> userReceiveConfigList = userReceiveConfigRepository.select(new UserReceiveConfig().setTenantId(tenantId));
        Map<Long, List<UserReceiveConfig>> userIdConfigMap = userReceiveConfigList.stream().collect(Collectors.groupingBy(UserReceiveConfig::getUserId));
        userIdConfigMap.forEach((userId, configList) -> {
            UserInfoVO userInfo = userReceiveConfigRepository.getUserInfo(userId, tenantId);
            Map<String, List<String>> codeTypeMap = configList.stream().collect(Collectors.toMap(UserReceiveConfig::getReceiveCode,
                            item -> Arrays.asList(item.getReceiveType().split(BaseConstants.Symbol.COMMA)), (k1, k2) -> k2));
            for (Map.Entry<String, List<String>> entrySet : codeTypeMap.entrySet()) {
                // 删除原数据
                UserReceiveConfig.deleteCache(redisHelper, entrySet.getValue(), userInfo, entrySet.getKey());
                // 新增
                UserReceiveConfig.refreshCache(redisHelper, entrySet.getValue(), userInfo, entrySet.getKey());
            }
        });
    }

    @Override
    public void refreshAllUserConfig() {
        List<Long> tenantIdList = userReceiveConfigRepository.listAllTenantId();
        if (CollectionUtils.isEmpty(tenantIdList)) {
            return;
        }
        tenantIdList.forEach(this::refreshTenantUserConfig);
    }
}
