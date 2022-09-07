package org.hzero.message.app.service.impl;

import org.apache.commons.lang3.StringUtils;
import org.hzero.boot.platform.lov.adapter.LovAdapter;
import org.hzero.boot.platform.lov.dto.LovValueDTO;
import org.hzero.core.algorithm.tree.TreeBuilder;
import org.hzero.core.base.BaseConstants;
import org.hzero.core.redis.RedisHelper;
import org.hzero.core.util.Pair;
import org.hzero.message.api.dto.ReceiveConfigDTO;
import org.hzero.message.app.service.ReceiveConfigService;
import org.hzero.message.domain.entity.ReceiveConfig;
import org.hzero.message.domain.entity.TemplateServer;
import org.hzero.message.domain.repository.ReceiveConfigRepository;
import org.hzero.message.domain.repository.UserReceiveConfigRepository;
import org.hzero.message.infra.constant.HmsgConstant;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.Assert;
import org.springframework.util.CollectionUtils;

import java.util.*;
import java.util.stream.Collectors;

import io.choerodon.core.exception.CommonException;

/**
 * 接收配置应用服务默认实现
 *
 * @author shuangfei.zhu@hand-china.com 2018-11-19 20:42:53
 */
@Service
public class ReceiveConfigServiceImpl implements ReceiveConfigService {

    private final LovAdapter lovAdapter;
    private final RedisHelper redisHelper;
    private final ReceiveConfigRepository receiveConfigRepository;
    private final UserReceiveConfigRepository userReceiveConfigRepository;

    @Autowired
    public ReceiveConfigServiceImpl(LovAdapter lovAdapter,
                                    RedisHelper redisHelper,
                                    ReceiveConfigRepository receiveConfigRepository,
                                    UserReceiveConfigRepository userReceiveConfigRepository) {
        this.lovAdapter = lovAdapter;
        this.redisHelper = redisHelper;
        this.receiveConfigRepository = receiveConfigRepository;
        this.userReceiveConfigRepository = userReceiveConfigRepository;
    }

    @Override
    public List<ReceiveConfigDTO> listConfig(Long organizationId) {
        List<ReceiveConfigDTO> configs = receiveConfigRepository.listConfig(organizationId);
        // 根据编码排序，防止乱序
        configs = configs.stream().sorted(Comparator.comparingInt(item -> item.getReceiveCode().hashCode())).collect(Collectors.toList());
        // 值集翻译
        List<LovValueDTO> list = lovAdapter.queryLovValue(HmsgConstant.MessageType.LOV, organizationId);
        Map<String, String> lov = new HashMap<>(16);
        list.forEach(dto -> lov.put(dto.getValue(), dto.getMeaning()));
        configs.forEach(config -> {
            StringBuilder meanings = new StringBuilder();
            String[] types = config.getDefaultReceiveType().split(BaseConstants.Symbol.COMMA);
            Arrays.stream(types).forEach(type -> meanings.append(BaseConstants.Symbol.COMMA).append(lov.get(type)));
            if (StringUtils.isNotBlank(meanings)) {
                config.setDefaultReceiveTypeMeaning(meanings.substring(1, meanings.length()));
            }
        });
        return TreeBuilder.buildTree(configs, HmsgConstant.ReceiveConfig.OVERALL_PARENT_CODE, ReceiveConfigDTO::getReceiveCode, ReceiveConfigDTO::getParentReceiveCode);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public List<ReceiveConfig> createAndUpdate(List<ReceiveConfig> receiveConfigList, Long tenantId) {
        List<ReceiveConfig> result = new ArrayList<>();
        receiveConfigList.forEach(receiveConfig -> {
            // 数据不可更新
            if (Objects.equals(receiveConfig.getEditableFlag(), BaseConstants.Flag.NO)) {
                return;
            }
            if (!Objects.equals(receiveConfig.getTenantId(), tenantId)) {
                receiveConfig.setReceiveId(null);
            }
            receiveConfig.setTenantId(tenantId);
            // 获取消息类型
            List<String> configType = Arrays.asList(receiveConfig.getDefaultReceiveType().split(BaseConstants.Symbol.COMMA));
            // 获取上一级配置
            List<ReceiveConfigDTO> parentList = receiveConfigRepository.listParentReceive(receiveConfig.getParentReceiveCode(), tenantId);
            ReceiveConfig parent = new ReceiveConfig();
            for (ReceiveConfigDTO parentConfig : parentList) {
                if (Objects.equals(parentConfig.getTenantId(), tenantId)) {
                    parent = parentConfig.getReceiveConfig();
                    break;
                } else {
                    parent = parentConfig.getReceiveConfig();
                }
            }
            // 父级接收配置
            String parentReceiveType = parent.getDefaultReceiveType();
            Assert.notNull(parentReceiveType, HmsgConstant.ErrorCode.INVALID_PARENT_NODE);
            // 校验是否多于父节点的消息类型
            List<String> parentType = Arrays.asList(parentReceiveType.split(BaseConstants.Symbol.COMMA));
            configType.forEach(type -> Assert.isTrue(parentType.contains(type), HmsgConstant.ErrorCode.RECEIVE_TYPE_MORE));

            if (receiveConfig.getReceiveId() == null) {
                // 新建
                receiveConfig.setLevelNumber(parent.getLevelNumber() + 1);
                result.add(this.createConfig(receiveConfig));
            } else {
                // 更新
                result.add(this.updateConfig(receiveConfig));
            }
        });
        return result;
    }

    private ReceiveConfig createConfig(ReceiveConfig receiveConfig) {
        receiveConfig.validateRepeat(receiveConfigRepository);
        receiveConfigRepository.insertSelective(receiveConfig);
        ReceiveConfig.refreshCache(redisHelper, receiveConfig);
        return receiveConfig;
    }

    private ReceiveConfig updateConfig(ReceiveConfig receiveConfig) {
        this.updateChildren(receiveConfig, receiveConfig.getDefaultReceiveType());
        receiveConfigRepository.updateOptional(receiveConfig,
                ReceiveConfig.FIELD_RECEIVE_NAME,
                ReceiveConfig.FIELD_DEFAULT_RECEIVE_TYPE);
        ReceiveConfig.refreshCache(redisHelper, receiveConfig);
        return receiveConfig;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void deleteConfig(ReceiveConfig receiveConfig) {
        // 查询租户和平台的配置
        List<ReceiveConfigDTO> receiveConfigList = receiveConfigRepository.listParentReceive(receiveConfig.getReceiveCode(), receiveConfig.getTenantId());
        boolean flag = false;
        // 存在平台配置，即可删除
        for (ReceiveConfigDTO dto : receiveConfigList) {
            if (Objects.equals(dto.getTenantId(), BaseConstants.DEFAULT_TENANT_ID)) {
                flag = true;
                break;
            }
        }
        if (flag) {
            receiveConfigRepository.deleteByPrimaryKey(receiveConfig.getReceiveId());
            // 删除用户接收配置的对应记录
            userReceiveConfigRepository.deleteByCode(receiveConfig.getReceiveCode());
            // 清除缓存
            ReceiveConfig.clearCache(redisHelper, receiveConfig.getTenantId(), receiveConfig.getReceiveCode());
        } else {
            throw new CommonException(HmsgConstant.ErrorCode.RECEIVE_CONFIG_NO_MORE);
        }
    }

    @Override
    public void initReceiveConfig(TemplateServer templateServer, Set<String> typeCodes) {
        if (CollectionUtils.isEmpty(typeCodes)) {
            return;
        }
        ReceiveConfig overallConfig = receiveConfigRepository.selectOne(new ReceiveConfig().setReceiveCode(HmsgConstant.ReceiveConfig.OVERALL).setTenantId(BaseConstants.DEFAULT_TENANT_ID));
        if (overallConfig == null) {
            throw new CommonException(HmsgConstant.ErrorCode.GLOBAL_CONFIG_NOT_EXIST);
        }
        ReceiveConfig categoryConfig = null;
        ReceiveConfig subcategoryConfig = null;
        if (StringUtils.isNotBlank(templateServer.getCategoryCode())) {
            Pair<ReceiveConfig, ReceiveConfig> pair = initParent(templateServer, overallConfig);
            categoryConfig = pair.getFirst();
            subcategoryConfig = pair.getSecond();
        }
        // 去除微信公众号类型、webHook类型
        typeCodes.remove(HmsgConstant.MessageType.WC_O);
        typeCodes.remove(HmsgConstant.MessageType.WEB_HOOK);
        String defaultReceiveType = StringUtils.join(typeCodes, BaseConstants.Symbol.COMMA);
        boolean create = false;
        // 创建或修改接收配置
        ReceiveConfig receiveConfig = receiveConfigRepository.selectOne(new ReceiveConfig().setTenantId(templateServer.getTenantId()).setReceiveCode(templateServer.getMessageCode()));
        if (receiveConfig == null) {
            create = true;
            receiveConfig = new ReceiveConfig()
                    .setTenantId(templateServer.getTenantId())
                    .setReceiveName(templateServer.getMessageName())
                    .setReceiveCode(templateServer.getMessageCode())
                    .setEditableFlag(BaseConstants.Flag.NO);
        }
        receiveConfig.setEditableFlag(BaseConstants.Flag.NO);
        if (subcategoryConfig != null) {
            receiveConfig.setParentReceiveCode(subcategoryConfig.getReceiveCode()).setLevelNumber(subcategoryConfig.getLevelNumber() + 1);
        } else if (categoryConfig != null) {
            receiveConfig.setParentReceiveCode(categoryConfig.getReceiveCode()).setLevelNumber(categoryConfig.getLevelNumber() + 1);
        } else {
            receiveConfig.setParentReceiveCode(overallConfig.getReceiveCode()).setLevelNumber(overallConfig.getLevelNumber() + 1);
        }
        receiveConfig.setDefaultReceiveType(defaultReceiveType);
        if (create) {
            receiveConfigRepository.insertSelective(receiveConfig);
        } else {
            receiveConfigRepository.updateOptional(receiveConfig,
                    ReceiveConfig.FIELD_PARENT_RECEIVE_CODE,
                    ReceiveConfig.FIELD_DEFAULT_RECEIVE_TYPE,
                    ReceiveConfig.FIELD_LEVEL_NUMBER,
                    ReceiveConfig.FIELD_EDITABLE_FLAG);
        }
        // 刷新缓存
        ReceiveConfig.refreshCache(redisHelper, receiveConfig);
    }

    /**
     * 初始化父级
     */
    private Pair<ReceiveConfig, ReceiveConfig> initParent(TemplateServer templateServer, ReceiveConfig overallConfig) {
        ReceiveConfig categoryConfig;
        ReceiveConfig subcategoryConfig = null;
        if (Objects.equals(templateServer.getCategoryCode(), templateServer.getSubcategoryCode())) {
            throw new CommonException(HmsgConstant.ErrorCode.CATEGORY_CODE_REPEAT);
        }
        categoryConfig = receiveConfigRepository.selectOne(new ReceiveConfig().setReceiveCode(templateServer.getCategoryCode()).setTenantId(templateServer.getTenantId()));
        if (categoryConfig == null) {
            categoryConfig = new ReceiveConfig()
                    .setReceiveCode(templateServer.getCategoryCode())
                    .setTenantId(templateServer.getTenantId())
                    .setReceiveName(templateServer.getCategoryMeaning())
                    .setParentReceiveCode(HmsgConstant.ReceiveConfig.OVERALL)
                    .setLevelNumber(BaseConstants.Digital.ONE)
                    .setDefaultReceiveType(overallConfig.getDefaultReceiveType())
                    .setEditableFlag(BaseConstants.Flag.NO);
            // 消息类别对应接收配置
            receiveConfigRepository.insertSelective(categoryConfig);
        }
        if (StringUtils.isNotBlank(templateServer.getSubcategoryCode())) {
            subcategoryConfig = receiveConfigRepository.selectOne(new ReceiveConfig().setReceiveCode(templateServer.getSubcategoryCode()).setTenantId(templateServer.getTenantId()));
            if (subcategoryConfig == null) {
                subcategoryConfig = new ReceiveConfig()
                        .setReceiveCode(templateServer.getSubcategoryCode())
                        .setTenantId(templateServer.getTenantId())
                        .setReceiveName(templateServer.getSubcategoryMeaning())
                        .setParentReceiveCode(categoryConfig.getReceiveCode())
                        .setLevelNumber(BaseConstants.Digital.TWO)
                        .setDefaultReceiveType(categoryConfig.getDefaultReceiveType())
                        .setEditableFlag(BaseConstants.Flag.NO);
                // 消息子类别对应接收配置
                receiveConfigRepository.insertSelective(subcategoryConfig);
            }
        }
        return Pair.of(categoryConfig, subcategoryConfig);
    }

    @Override
    public void updateReceiveConfig(String configCode, Long tenantId, List<String> typeList) {
        // 去除微信公众号类型、webHook
        typeList.remove(HmsgConstant.MessageType.WC_O);
        typeList.remove(HmsgConstant.MessageType.WEB_HOOK);
        ReceiveConfig receiveConfig = receiveConfigRepository.selectOne(new ReceiveConfig().setReceiveCode(configCode).setTenantId(tenantId));
        if (receiveConfig != null) {
            receiveConfigRepository.updateByPrimaryKey(receiveConfig.setDefaultReceiveType(StringUtils.join(typeList, BaseConstants.Symbol.COMMA)));
            // 刷新缓存
            ReceiveConfig.refreshCache(redisHelper, receiveConfig);
        }
    }

    @Override
    public void deleteReceiveConfig(String configCode, Long tenantId) {
        receiveConfigRepository.delete(new ReceiveConfig().setReceiveCode(configCode).setTenantId(tenantId));
        ReceiveConfig.clearCache(redisHelper, tenantId, configCode);
    }


    /**
     * 禁用子节点多于当前节点的接收类型
     *
     * @param receiveConfig      消息配置
     * @param defaultReceiveType 接收类型
     */
    private void updateChildren(ReceiveConfig receiveConfig, String defaultReceiveType) {
        List<String> configTypes = Arrays.asList(defaultReceiveType.split(BaseConstants.Symbol.COMMA));
        List<ReceiveConfig> children = receiveConfigRepository.select(new ReceiveConfig().setParentReceiveCode(receiveConfig.getReceiveCode()).setTenantId(receiveConfig.getTenantId()));
        if (!CollectionUtils.isEmpty(children)) {
            StringBuilder builder = new StringBuilder();
            children.forEach(child -> {
                String[] childTypes = child.getDefaultReceiveType().split(BaseConstants.Symbol.COMMA);
                builder.setLength(0);
                for (String childType : childTypes) {
                    if (configTypes.contains(childType)) {
                        builder.append(childType).append(BaseConstants.Symbol.COMMA);
                    }
                }
                // 若类型为空类型，报错
                Assert.hasText(String.valueOf(builder), HmsgConstant.ErrorCode.RECEIVE_TYPE_NULL);
                // 否则更新
                child.setDefaultReceiveType(builder.substring(0, builder.length() - 1));
                receiveConfigRepository.updateByPrimaryKey(child);
                this.updateChildren(child, child.getDefaultReceiveType());
            });
        }
    }
}
