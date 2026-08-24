package org.hzero.message.app.service.impl;

import com.fasterxml.jackson.core.type.TypeReference;
import org.hzero.boot.message.entity.MessageSender;
import org.hzero.boot.message.entity.Receiver;
import org.hzero.core.base.BaseConstants;
import org.hzero.core.util.ResponseUtils;
import org.hzero.message.api.dto.UnitUserDTO;
import org.hzero.message.app.service.MessageReceiverService;
import org.hzero.message.domain.entity.ReceiverDetail;
import org.hzero.message.domain.entity.ReceiverType;
import org.hzero.message.domain.entity.ReceiverTypeLine;
import org.hzero.message.domain.entity.UserGroupAssign;
import org.hzero.message.domain.repository.ReceiverDetailRepository;
import org.hzero.message.domain.repository.ReceiverTypeRepository;
import org.hzero.message.infra.constant.HmsgConstant;
import org.hzero.message.infra.feign.IamRemoteService;
import org.hzero.message.infra.feign.UnitService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpMethod;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;
import org.springframework.web.client.RestTemplate;

import java.util.*;
import java.util.stream.Collectors;

/**
 * 消息接受方应用服务默认实现
 *
 * @author xianzhi.chen@hand-china.com 2018-07-27 11:30:58
 */
@Service
public class MessageReceiverServiceImpl implements MessageReceiverService {
    private static final String SPLIT = "/";
    private static final String TENANT_ID = "tenantId";
    private static final String ORGANIZATION_ID = "organizationId";
    private ReceiverTypeRepository receiverTypeRepository;
    private RestTemplate restTemplate;
    private IamRemoteService iamRemoteService;
    private UnitService unitService;
    private final ReceiverDetailRepository receiverDetailRepository;

    @Autowired
    public MessageReceiverServiceImpl(ReceiverTypeRepository receiverTypeRepository,
                                      IamRemoteService iamRemoteService,
                                      UnitService unitService,
                                      RestTemplate restTemplate,
                                      ReceiverDetailRepository receiverDetailRepository) {
        this.receiverTypeRepository = receiverTypeRepository;
        this.restTemplate = restTemplate;
        this.iamRemoteService = iamRemoteService;
        this.unitService = unitService;
        this.receiverDetailRepository = receiverDetailRepository;
    }

    @Override
    public List<Receiver> queryReceiver(long tenantId, String typeCode, Map<String, String> args) {
        // 获取消息接受人配置
        ReceiverType receiverType = queryReceiverType(tenantId, typeCode);
        // 获取消息接受人配置
        if (receiverType == null) {
            return Collections.emptyList();
        }
        List<ReceiverTypeLine> receiverUserGroupList = receiverTypeRepository.listReceiveTypeLine(receiverType.getReceiverTypeId());
        if (HmsgConstant.ReceiverTypeMode.USER_GROUP.equals(receiverType.getTypeModeCode())) {
            List<UserGroupAssign> userGroupList = covertReceiverTypeLineToUserGroup(receiverUserGroupList);
            return ResponseUtils.getResponse(iamRemoteService.listUserGroupAssignUsers(userGroupList), new TypeReference<Set<Receiver>>() {
            }).stream().distinct().collect(Collectors.toList());
        } else if (HmsgConstant.ReceiverTypeMode.UNIT.equals(receiverType.getTypeModeCode())) {
            List<UnitUserDTO> unitList = covertReceiverTypeLineToUnit(receiverUserGroupList);
            return ResponseUtils.getResponse(unitService.listUnitUsers(unitList), new TypeReference<Set<Receiver>>() {
            }).stream().distinct().collect(Collectors.toList());
        } else if (HmsgConstant.ReceiverTypeMode.EXT_USER.equals(receiverType.getTypeModeCode())) {
            List<ReceiverDetail> receiverDetails = receiverDetailRepository.select(new ReceiverDetail().setReceiverTypeId(receiverType.getReceiverTypeId()));
            List<Receiver> result = new ArrayList<>();
            receiverDetails.forEach(receiverDetail -> {
                Receiver receiver = new Receiver();
                if (HmsgConstant.ReceiverAccountType.PHONE.equals(receiverDetail.getAccountTypeCode())) {
                    receiver.setPhone(receiverDetail.getAccountNum());
                } else {
                    receiver.setEmail(receiverDetail.getAccountNum());
                }
                result.add(receiver);
            });
            return result;
        } else if (HmsgConstant.ReceiverTypeMode.USER.equals(receiverType.getTypeModeCode())) {
            List<Long> userIds = receiverUserGroupList.stream().map(ReceiverTypeLine::getReceiveTargetId).collect(Collectors.toList());
            return ResponseUtils.getResponse(iamRemoteService.listReceiverByUserIds(tenantId, userIds), new TypeReference<Set<Receiver>>() {
            }).stream().distinct().collect(Collectors.toList());
        } else {
            if (!args.containsKey(TENANT_ID)) {
                args.put(TENANT_ID, String.valueOf(tenantId));
            }
            if (!args.containsKey(ORGANIZATION_ID)) {
                args.put(ORGANIZATION_ID, String.valueOf(tenantId));
            }
            return ResponseUtils.getResponse(restTemplate.exchange(buildUrl(receiverType.getRouteName(), receiverType.getApiUrl(), args.keySet()), HttpMethod.GET, null, String.class, args),
                    new TypeReference<List<Receiver>>() {
                    });
        }
    }

    @Override
    public MessageSender queryReceiver(MessageSender messageSender) {
        // 如果接收人列表为空 & 接收组编码不为空 & 租户ID不为空 > 通过编码获取接收人配置，获取接收人
        if (CollectionUtils.isEmpty(messageSender.getReceiverAddressList())
                && StringUtils.hasText(messageSender.getReceiverTypeCode()) && messageSender.getTenantId() != null) {
            messageSender.setReceiverAddressList(queryReceiver(messageSender.getTenantId(),
                    messageSender.getReceiverTypeCode(), messageSender.getArgs()));
        }
        return messageSender;
    }

    @Override
    public List<Receiver> queryOpenReceiver(long tenantId, String typeCode, String thirdPlatformType, Map<String, String> args) {
        // 获取消息接收人配置
        ReceiverType receiverType = queryReceiverType(tenantId, typeCode);
        // 获取消息接收人配置
        if (receiverType == null) {
            return Collections.emptyList();
        }
        List<ReceiverTypeLine> receiverUserGroupList = receiverTypeRepository.listReceiveTypeLine(receiverType.getReceiverTypeId());
        if (HmsgConstant.ReceiverTypeMode.USER_GROUP.equals(receiverType.getTypeModeCode())) {
            List<UserGroupAssign> userGroupList = covertReceiverTypeLineToUserGroup(receiverUserGroupList);
            return ResponseUtils.getResponse(iamRemoteService.listOpenUserGroupAssignUsers(userGroupList, thirdPlatformType), new TypeReference<Set<Receiver>>() {
            }).stream().distinct().collect(Collectors.toList());
        } else if (HmsgConstant.ReceiverTypeMode.UNIT.equals(receiverType.getTypeModeCode())) {
            List<UnitUserDTO> unitList = covertReceiverTypeLineToUnit(receiverUserGroupList);
            return ResponseUtils.getResponse(unitService.listOpenUnitUsers(unitList, thirdPlatformType), new TypeReference<Set<Receiver>>() {
            }).stream().distinct().collect(Collectors.toList());
        } else if (HmsgConstant.ReceiverTypeMode.USER.equals(receiverType.getTypeModeCode())) {
            List<Long> userIds = receiverUserGroupList.stream().map(ReceiverTypeLine::getReceiveTargetId).collect(Collectors.toList());
            return ResponseUtils.getResponse(iamRemoteService.listOpenReceiverByUserIds(tenantId, userIds, thirdPlatformType), new TypeReference<Set<Receiver>>() {
            }).stream().distinct().collect(Collectors.toList());
        } else {
            if (!args.containsKey(TENANT_ID)) {
                args.put(TENANT_ID, String.valueOf(tenantId));
            }
            if (!args.containsKey(ORGANIZATION_ID)) {
                args.put(ORGANIZATION_ID, String.valueOf(tenantId));
            }
            return ResponseUtils.getResponse(restTemplate.exchange(buildUrl(receiverType.getRouteName(), receiverType.getApiUrl(), args.keySet()), HttpMethod.GET, null, String.class, args),
                    new TypeReference<List<Receiver>>() {
                    });
        }
    }


    private ReceiverType queryReceiverType(Long tenantId, String typeCode) {
        // 获取消息接收人配置
        ReceiverType receiverType = receiverTypeRepository.selectOne(new ReceiverType().setTenantId(tenantId).setTypeCode(typeCode).setEnabledFlag(BaseConstants.Flag.YES));
        // 如果租户级未找到，在平台级找
        if (receiverType == null && !BaseConstants.DEFAULT_TENANT_ID.equals(tenantId)) {
            receiverType = receiverTypeRepository.selectOne(new ReceiverType().setTenantId(BaseConstants.DEFAULT_TENANT_ID)
                    .setTypeCode(typeCode).setEnabledFlag(BaseConstants.Flag.YES));
        }
        return receiverType;
    }

    /**
     * 消息接收配置行转用户组分配DTO
     *
     * @param receiverTypes 消息接收配置行
     */
    private List<UserGroupAssign> covertReceiverTypeLineToUserGroup(List<ReceiverTypeLine> receiverTypes) {
        List<UserGroupAssign> userGroupList = new ArrayList<>();
        for (ReceiverTypeLine receiverUserGroup : receiverTypes) {
            UserGroupAssign dto = new UserGroupAssign();
            dto.setUserGroupId(receiverUserGroup.getReceiveTargetId());
            dto.setTenantId(receiverUserGroup.getReceiveTargetTenantId());
            userGroupList.add(dto);
        }
        return userGroupList;
    }

    /**
     * 消息接收配置行转组织DTO
     *
     * @param receiverTypes 接收者类型
     */
    private List<UnitUserDTO> covertReceiverTypeLineToUnit(List<ReceiverTypeLine> receiverTypes) {
        List<UnitUserDTO> unitList = new ArrayList<>();
        for (ReceiverTypeLine receiverUserGroup : receiverTypes) {
            UnitUserDTO dto = new UnitUserDTO();
            dto.setUnitId(receiverUserGroup.getReceiveTargetId());
            dto.setTenantId(receiverUserGroup.getReceiveTargetTenantId());
            unitList.add(dto);
        }
        return unitList;
    }

    /**
     * 拼接URL
     *
     * @param route 目标路由
     * @param api   请求路径
     * @return URL
     */
    private String buildUrl(String route, String api, Set<String> argNames) {
        return "http://" + route + (api.startsWith(SPLIT) ? "" : SPLIT) + api + (api.contains("?") ? "&" : "?")
                + argNames.stream().filter(item -> !api.contains("{" + item + "}"))
                .map(item -> item + "={" + item + "}").collect(Collectors.joining("&"));
    }

}
