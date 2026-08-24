package org.hzero.message.app.service.impl;


import org.apache.commons.collections4.CollectionUtils;
import org.hzero.core.base.AopProxy;
import org.hzero.core.base.BaseConstants;
import org.hzero.message.app.service.ReceiveConfigService;
import org.hzero.message.app.service.TemplateServerService;
import org.hzero.message.domain.entity.*;
import org.hzero.message.domain.repository.*;
import org.hzero.message.infra.constant.HmsgConstant;
import org.hzero.mybatis.helper.SecurityTokenHelper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.Assert;

import java.util.*;
import java.util.stream.Collectors;

import io.choerodon.core.domain.Page;
import io.choerodon.core.exception.CommonException;
import io.choerodon.mybatis.pagehelper.domain.PageRequest;


/**
 * 消息模板账户应用服务默认实现
 *
 * @author zhiying.dong@hand-china.com 2018-09-07 11:09:29
 */
@Service
public class TemplateServerServiceImpl implements TemplateServerService, AopProxy<TemplateServerServiceImpl> {

    private final TemplateServerRepository templateServerRepository;
    private final TemplateServerLineRepository templateServerLineRepository;
    private final MessageTemplateRepository messageTemplateRepository;
    private final EmailServerRepository emailServerRepository;
    private final SmsServerRepository smsServerRepository;
    private final CallServerRepository callServerRepository;
    private final WeChatOfficialRepository weChatOfficialRepository;
    private final WeChatEnterpriseRepository weChatEnterpriseRepository;
    private final DingTalkServerRepository dingTalkServerRepository;
    private final ReceiveConfigService receiveConfigService;

    @Autowired
    public TemplateServerServiceImpl(TemplateServerRepository templateServerRepository,
                                     TemplateServerLineRepository templateServerLineRepository,
                                     MessageTemplateRepository messageTemplateRepository,
                                     EmailServerRepository emailServerRepository,
                                     SmsServerRepository smsServerRepository,
                                     CallServerRepository callServerRepository,
                                     WeChatOfficialRepository weChatOfficialRepository,
                                     WeChatEnterpriseRepository weChatEnterpriseRepository,
                                     DingTalkServerRepository dingTalkServerRepository,
                                     ReceiveConfigService receiveConfigService) {
        this.templateServerRepository = templateServerRepository;
        this.templateServerLineRepository = templateServerLineRepository;
        this.messageTemplateRepository = messageTemplateRepository;
        this.emailServerRepository = emailServerRepository;
        this.smsServerRepository = smsServerRepository;
        this.callServerRepository = callServerRepository;
        this.weChatOfficialRepository = weChatOfficialRepository;
        this.weChatEnterpriseRepository = weChatEnterpriseRepository;
        this.dingTalkServerRepository = dingTalkServerRepository;
        this.receiveConfigService = receiveConfigService;
    }

    @Override
    public Page<TemplateServer> pageTemplateServer(Long tenantId, String messageCode, String messageName, boolean includeSiteIfQueryByTenantId, PageRequest pageRequest) {
        return templateServerRepository.pageTemplateServer(tenantId, messageCode, messageName, includeSiteIfQueryByTenantId, pageRequest);
    }

    @Override
    public List<TemplateServerLine> listTemplateServerLine(long tempServerId, long tenantId) {
        List<TemplateServerLine> templateServerLineList = templateServerLineRepository.listTemplateServerLine(tempServerId, tenantId);
        List<TemplateServerLine> result = new ArrayList<>();
        for (TemplateServerLine line : templateServerLineList) {
            // 处理关联的模板
            MessageTemplate messageTemplate = messageTemplateRepository.selectByCode(tenantId, line.getTemplateCode());
            if (messageTemplate == null && !Objects.equals(tenantId, BaseConstants.DEFAULT_TENANT_ID)) {
                messageTemplate = messageTemplateRepository.selectByCode(BaseConstants.DEFAULT_TENANT_ID, line.getTemplateCode());
            }
            if (messageTemplate != null) {
                line.setTemplateName(messageTemplate.getTemplateName());
                // 处理关联的账户
                line.setServerName(getServerName(line.getTypeCode(), tenantId, line.getServerCode()));
                result.add(line);
            }
        }
        return result;
    }

    private String getServerName(String typeCode, Long tenantId, String serverCode) {
        String serverName = null;
        switch (typeCode) {
            case HmsgConstant.MessageType.EMAIL:
                EmailServer emailServer = emailServerRepository.selectByCode(tenantId, serverCode);
                if (emailServer == null && !Objects.equals(tenantId, BaseConstants.DEFAULT_TENANT_ID)) {
                    emailServer = emailServerRepository.selectByCode(BaseConstants.DEFAULT_TENANT_ID, serverCode);
                }
                if (emailServer != null) {
                    serverName = emailServer.getServerName();
                }
                break;
            case HmsgConstant.MessageType.SMS:
                SmsServer smsServer = smsServerRepository.selectByCode(tenantId, serverCode);
                if (smsServer == null && !Objects.equals(tenantId, BaseConstants.DEFAULT_TENANT_ID)) {
                    smsServer = smsServerRepository.selectByCode(BaseConstants.DEFAULT_TENANT_ID, serverCode);
                }
                if (smsServer != null) {
                    serverName = smsServer.getServerName();
                }
                break;
            case HmsgConstant.MessageType.CALL:
                CallServer callServer = callServerRepository.selectByCode(tenantId, serverCode);
                if (callServer == null && !Objects.equals(tenantId, BaseConstants.DEFAULT_TENANT_ID)) {
                    callServer = callServerRepository.selectByCode(BaseConstants.DEFAULT_TENANT_ID, serverCode);
                }
                if (callServer != null) {
                    serverName = callServer.getServerName();
                }
                break;
            case HmsgConstant.MessageType.WC_O:
                WechatOfficial wechatOfficial = weChatOfficialRepository.selectByCode(tenantId, serverCode);
                if (wechatOfficial == null && !Objects.equals(tenantId, BaseConstants.DEFAULT_TENANT_ID)) {
                    wechatOfficial = weChatOfficialRepository.selectByCode(BaseConstants.DEFAULT_TENANT_ID, serverCode);
                }
                if (wechatOfficial != null) {
                    serverName = wechatOfficial.getServerName();
                }
                break;
            case HmsgConstant.MessageType.WC_E:
                WeChatEnterprise weChatEnterprise = weChatEnterpriseRepository.selectByCode(tenantId, serverCode);
                if (weChatEnterprise == null && !Objects.equals(tenantId, BaseConstants.DEFAULT_TENANT_ID)) {
                    weChatEnterprise = weChatEnterpriseRepository.selectByCode(BaseConstants.DEFAULT_TENANT_ID,
                            serverCode);
                }
                if (weChatEnterprise != null) {
                    serverName = weChatEnterprise.getServerName();
                }
                break;
            case HmsgConstant.MessageType.DT:
                DingTalkServer dingTalkServer = dingTalkServerRepository.selectByCode(tenantId, serverCode);
                if (dingTalkServer == null && !Objects.equals(tenantId, BaseConstants.DEFAULT_TENANT_ID)) {
                    dingTalkServer = dingTalkServerRepository.selectByCode(BaseConstants.DEFAULT_TENANT_ID, serverCode);
                }
                if (dingTalkServer != null) {
                    serverName = dingTalkServer.getServerName();
                }
                break;
            case HmsgConstant.MessageType.WEB:
            default:
                break;
        }
        return serverName;
    }

    @Override
    public TemplateServer getTemplateServer(long tenantId, String messageCode) {
        TemplateServer templateServer = templateServerRepository.selectOne(new TemplateServer().setTenantId(tenantId).setMessageCode(messageCode));
        if (templateServer == null) {
            templateServer = templateServerRepository.selectOne(new TemplateServer().setTenantId(BaseConstants.DEFAULT_TENANT_ID).setMessageCode(messageCode));
        }
        return templateServer;
    }

    @Override
    public TemplateServer getTemplateServer(Long tenantId, long tempServerId) {
        TemplateServer templateServer = templateServerRepository.getTemplateServer(tenantId, tempServerId);
        if (templateServer != null) {
            if (tenantId == null) {
                tenantId = templateServer.getTenantId();
            }
            templateServer.setServerList(listTemplateServerLine(tempServerId, tenantId));
        }
        return templateServer;
    }

    @Override
    public TemplateServer copyTemplateServer(Long tenantId, Long tempServerId) {
        Assert.isTrue(!Objects.equals(tenantId, BaseConstants.DEFAULT_TENANT_ID), BaseConstants.ErrorCode.DATA_INVALID);
        TemplateServer templateServer = templateServerRepository.selectByPrimaryKey(tempServerId);
        if (templateServer == null || !Objects.equals(templateServer.getTenantId(), BaseConstants.DEFAULT_TENANT_ID)) {
            throw new CommonException(BaseConstants.ErrorCode.DATA_NOT_EXISTS);
        }
        // 查询行信息
        List<TemplateServerLine> list = listTemplateServerLine(tempServerId, tenantId);
        list.forEach(item -> item.setTempServerId(null).setTempServerLineId(null));
        return templateServer.setServerList(list).setTempServerId(null);
    }

    @Override
    public TemplateServerLine getTemplateServerLine(long tenantId, String typeCode, String messageCode) {
        Assert.isTrue(HmsgConstant.MessageType.EMAIL.equals(typeCode) || HmsgConstant.MessageType.SMS.equals(typeCode), BaseConstants.ErrorCode.DATA_INVALID);
        return templateServerLineRepository.getTemplateServerLine(tenantId, messageCode, typeCode);
    }

    @Override
    public List<TemplateServerLine> listTemplateServerLine(long tenantId, String messageCode) {
        TemplateServer param = new TemplateServer().setTenantId(tenantId).setMessageCode(messageCode).setEnabledFlag(BaseConstants.Flag.YES);
        TemplateServer templateServer = templateServerRepository.selectOne(param);
        if (templateServer == null && !Objects.equals(tenantId, BaseConstants.DEFAULT_TENANT_ID)) {
            templateServerRepository.selectOne(param.setTenantId(BaseConstants.DEFAULT_TENANT_ID));
        }
        if (templateServer == null) {
            return new ArrayList<>();
        }
        return listTemplateServerLine(templateServer.getTempServerId(), tenantId);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void createTemplateServer(TemplateServer templateServer) {
        templateServer.validRepeat(templateServerRepository);
        templateServerRepository.insertSelective(templateServer);
        Set<String> typeCodes = new HashSet<>();
        if (CollectionUtils.isNotEmpty(templateServer.getServerList())) {
            templateServer.getServerList().forEach(server -> {
                // 校验同一消息模板在同一消息类型下只能关联一个消息服务
                server.setTempServerId(templateServer.getTempServerId()).validTemplateServer(templateServerLineRepository);
                server.setTenantId(templateServer.getTenantId());
                typeCodes.add(server.getTypeCode());
                templateServerLineRepository.insertSelective(server);
            });
        }
        // 如果配置了接收，则创建接收配置
        if (Objects.equals(templateServer.getReceiveConfigFlag(), BaseConstants.Flag.YES)) {
            // 编码取消息类别编码.消息子类别编码.消息模板账户编码
            receiveConfigService.initReceiveConfig(templateServer, typeCodes);
        }
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void updateTemplateServer(TemplateServer templateServer) {
        TemplateServer old = templateServerRepository.selectByPrimaryKey(templateServer.getTempServerId());
        templateServerRepository.updateOptional(templateServer,
                TemplateServer.FIELD_ENABLED_FLAG,
                TemplateServer.FIELD_MESSAGE_NAME,
                TemplateServer.FIELD_CATEGORY_CODE,
                TemplateServer.FIELD_SUBCATEGORY_CODE,
                TemplateServer.FIELD_RECEIVE_CONFIG_FLAG);
        Set<String> typeCodes = new HashSet<>();
        if (CollectionUtils.isNotEmpty(templateServer.getServerList())) {
            templateServer.getServerList().forEach(server -> {
                typeCodes.add(server.getTypeCode());
                if (server.getTempServerLineId() != null) {
                    templateServerLineRepository.updateOptional(server,
                            TemplateServerLine.FIELD_REMARK,
                            TemplateServerLine.FIELD_SERVER_CODE,
                            TemplateServerLine.FIELD_TEMPLATE_CODE,
                            TemplateServerLine.FIELD_TRY_TIMES,
                            TemplateServerLine.FIELD_ENABLED_FLAG);
                } else {
                    // 校验同一消息模板在同一消息类型下只能关联一个消息服务
                    server.setTempServerId(templateServer.getTempServerId()).validTemplateServer(templateServerLineRepository);
                    server.setTenantId(templateServer.getTenantId());
                    templateServerLineRepository.insertSelective(server);
                }
            });
        }

        if (Objects.equals(old.getReceiveConfigFlag(), templateServer.getReceiveConfigFlag()) && Objects.equals(templateServer.getReceiveConfigFlag(), BaseConstants.Flag.YES)) {
            if (!Objects.equals(old.getCategoryCode(), templateServer.getCategoryCode()) || !Objects.equals(old.getSubcategoryCode(), templateServer.getSubcategoryCode())) {
                receiveConfigService.deleteReceiveConfig(templateServer.getMessageCode(), templateServer.getTenantId());
                receiveConfigService.initReceiveConfig(templateServer, typeCodes);
            } else {
                // 更新接收配置
                List<String> typeList = templateServer.getServerList().stream().filter(item -> Objects.equals(item.getEnabledFlag(), BaseConstants.Flag.YES)).map(TemplateServerLine::getTypeCode).collect(Collectors.toList());
                receiveConfigService.updateReceiveConfig(templateServer.getMessageCode(), templateServer.getTenantId(), typeList);
            }
            return;
        }
        if (Objects.equals(templateServer.getReceiveConfigFlag(), BaseConstants.Flag.YES)) {
            // 自定义配置变更为开启
            receiveConfigService.initReceiveConfig(templateServer, typeCodes);
        } else {
            // 自定义配置变更为关闭
            receiveConfigService.deleteReceiveConfig(templateServer.getMessageCode(), templateServer.getTenantId());
        }
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void deleteTemplateServer(TemplateServer templateServer) {
        templateServerLineRepository.select(TemplateServerLine.FIELD_TEMP_SERVER_ID,
                templateServer.getTempServerId()).forEach(item ->
                templateServerLineRepository.deleteByPrimaryKey(item.getTempServerLineId())
        );
        templateServerRepository.deleteByPrimaryKey(templateServer.getTempServerId());
        // 删除对应接收配置
        receiveConfigService.deleteReceiveConfig(templateServer.getMessageCode(), templateServer.getTenantId());
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void deleteTemplateServerLine(long tempServerLineId, TemplateServerLine templateServerLine) {
        SecurityTokenHelper.validToken(templateServerLine.setTempServerLineId(tempServerLineId));
        // 删除行，需要删除对应接收配置下的接收类型
        templateServerLineRepository.deleteByPrimaryKey(tempServerLineId);
        // 处理对应的消息接收配置
        TemplateServer templateServer = templateServerRepository.selectByPrimaryKey(templateServerLine.getTempServerId());
        if (templateServer == null) {
            return;
        }
        List<TemplateServerLine> lineList = templateServerLineRepository.select(new TemplateServerLine().setTempServerId(templateServerLine.getTempServerId()));
        if (CollectionUtils.isEmpty(lineList)) {
            return;
        }
        List<String> typeList = lineList.stream().map(TemplateServerLine::getTypeCode).collect(Collectors.toList());
        receiveConfigService.updateReceiveConfig(templateServer.getMessageCode(), templateServer.getTenantId(), typeList);
    }
}