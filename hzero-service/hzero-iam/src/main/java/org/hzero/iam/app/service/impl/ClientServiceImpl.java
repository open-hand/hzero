package org.hzero.iam.app.service.impl;

import java.util.*;
import java.util.stream.Collectors;

import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.hzero.boot.platform.encrypt.EncryptClient;
import org.hzero.boot.platform.lov.adapter.LovAdapter;
import org.hzero.boot.platform.lov.annotation.ProcessLovValue;
import org.hzero.core.base.BaseConstants;
import org.hzero.iam.app.service.ClientService;
import org.hzero.iam.app.service.MemberRoleService;
import org.hzero.iam.domain.entity.Client;
import org.hzero.iam.domain.entity.MemberRole;
import org.hzero.iam.domain.repository.ClientRepository;
import org.hzero.iam.domain.repository.RoleRepository;
import org.hzero.iam.domain.vo.RoleVO;
import org.hzero.iam.infra.constant.HiamMemberType;
import org.hzero.mybatis.helper.SecurityTokenHelper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.Assert;

import io.choerodon.core.domain.Page;
import io.choerodon.core.exception.CommonException;
import io.choerodon.mybatis.pagehelper.domain.PageRequest;

/**
 * 客户端服务实现
 *
 * @author bojiangzhou 2019/08/02
 */
@Service
public class ClientServiceImpl implements ClientService {

    private ClientRepository clientRepository;
    private EncryptClient encryptClient;
    private RoleRepository roleRepository;
    private MemberRoleService memberRoleService;
    private LovAdapter lovAdapter;

    @Autowired
    public ClientServiceImpl(ClientRepository clientRepository, EncryptClient encryptClient, RoleRepository roleRepository,
            MemberRoleService memberRoleService, LovAdapter lovAdapter) {
        this.clientRepository = clientRepository;
        this.encryptClient = encryptClient;
        this.roleRepository = roleRepository;
        this.memberRoleService = memberRoleService;
        this.lovAdapter = lovAdapter;
    }


    @Override
    @Transactional(rollbackFor = Exception.class)
    public Client create(Client client) {
        createCheck(client);
        // 密码解密
        client.setSecret(encryptClient.decrypt(client.getSecret()));
        clientRepository.insertSelective(client);
        clientRepository.cacheClient(clientRepository.selectByPrimaryKey(client.getId()));
        return client;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void update(Client client) {
        // 密码解密
        client.setSecret(encryptClient.decrypt(client.getSecret()));
        clientRepository.updateOptional(client,
                Client.FIELD_RESOURCE_IDS,
                Client.FIELD_SECRET,
                Client.FIELD_SCOPE,
                Client.FIELD_AUTHORIZED_GRANT_TYPES,
                Client.FIELD_WEB_SERVER_REDIRECT_URI,
                Client.FIELD_ACCESS_TOKEN_VALIDITY,
                Client.FIELD_REFRESH_TOKEN_VALIDITY,
                Client.FIELD_ADDITIONAL_INFORMATION,
                Client.FIELD_AUTO_APPROVE,
                Client.FIELD_ENABLED_FLAG,
                Client.FIELD_PWD_REPLAY_FLAG,
                Client.FIELD_TIME_ZONE,
                Client.FIELD_API_ENCRYPT_FLAG,
                Client.FIELD_API_REPLAY_FLAG
        );
        clientRepository.cacheClient(clientRepository.selectByPrimaryKey(client.getId()));
        if (CollectionUtils.isEmpty(client.getMemberRoleList())) {
            return;
        }
        client.getMemberRoleList().forEach(item -> {
            item.setMemberId(client.getId());
            item.setMemberType(HiamMemberType.CLIENT.toString().toLowerCase());
        });
        memberRoleService.batchAssignMemberRole(client.getMemberRoleList());
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void delete(Client client) {
        clientRepository.deleteByPrimaryKey(client.getId());
        clientRepository.removeCacheClient(client.getName());
    }

    @Override
    public void createCheck(Client client) {
        Client param = new Client();
        param.setName(client.getName());
        if (clientRepository.selectCount(param) > 0) {
            throw new CommonException("hiam.warn.client.clientNameExists");
        }
    }

    @Override
    public void enable(Client client) {
        SecurityTokenHelper.validToken(client);

        Client query = clientRepository.selectByPrimaryKey(client.getId());
        if (query == null) {
            throw new CommonException(BaseConstants.ErrorCode.DATA_NOT_EXISTS);
        }

        query.setEnabledFlag(BaseConstants.Flag.YES);

        clientRepository.updateOptional(query, Client.FIELD_ENABLED_FLAG);

        clientRepository.cacheClient(query);
    }

    @Override
    public void disable(Client client) {
        SecurityTokenHelper.validToken(client);

        Client query = clientRepository.selectByPrimaryKey(client.getId());
        if (query == null) {
            throw new CommonException(BaseConstants.ErrorCode.DATA_NOT_EXISTS);
        }

        query.setEnabledFlag(BaseConstants.Flag.NO);

        clientRepository.updateOptional(query, Client.FIELD_ENABLED_FLAG);

        clientRepository.cacheClient(query);
    }

    @Override
    public Page<RoleVO> listClientAccessRoles(Long organizationId, Long clientId, PageRequest pageRequest) {
        Client client = clientRepository.detailClient(organizationId, clientId);
        if (client == null || StringUtils.isEmpty(client.getAccessRoles())) {
            return new Page<>();
        }
        List<Long> accessRoleIdList = new ArrayList<>();
        String[] roleIds = StringUtils.split(client.getAccessRoles(), BaseConstants.Symbol.COMMA);
        for (String roleId : roleIds) {
            accessRoleIdList.add(Long.parseLong(roleId));
        }
        return roleRepository.selectByRoleIds(accessRoleIdList, pageRequest);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public List<MemberRole> createAccessRoles(Long organizationId, List<MemberRole> memberRoleList, Long clientId) {
        if (CollectionUtils.isEmpty(memberRoleList)) {
            return Collections.emptyList();
        }
        Client client = clientRepository.detailClient(organizationId, clientId);
        if (client == null) {
            throw new CommonException("error.client.not.exist");
        }

        Set<String> accessRoles;
        // 新增的角色id
        List<Long> addRoleIds = memberRoleList.stream().map(MemberRole::getRoleId).distinct().collect(Collectors.toList());
        // 已分配可访问角色
        String assignedRoles = client.getAccessRoles();
        if (StringUtils.isEmpty(assignedRoles)) {
            accessRoles = new HashSet<>();
        } else {
            String[] assignedRolesArr = StringUtils.split(assignedRoles, BaseConstants.Symbol.COMMA);
            accessRoles = new HashSet<>(Arrays.asList(assignedRolesArr));
        }
        for (Long roleId : addRoleIds) {
            accessRoles.add(roleId.toString());
        }
        client.setAccessRoles(StringUtils.join(accessRoles, BaseConstants.Symbol.COMMA));
        clientRepository.updateOptional(client, Client.FIELD_ACCESS_ROLES);
        clientRepository.cacheClient(clientRepository.selectByPrimaryKey(client.getId()));
        return memberRoleList;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void deleteAccessRoles(Long organizationId, List<MemberRole> memberRoleList, Long clientId) {
        Client client = clientRepository.detailClient(organizationId, clientId);
        if (client == null) {
            throw new CommonException("error.client.not.exist");
        }
        String accessRoles = client.getAccessRoles();
        if (StringUtils.isEmpty(accessRoles)) {
            throw new CommonException("error.client.accessRoles.not.exist");
        }
        // 删除的角色id
        List<Long> deleteRoles = memberRoleList.stream().map(MemberRole::getRoleId).collect(Collectors.toList());
        // 已分配角色ID
        String[] assignedRolesArr = StringUtils.split(accessRoles, BaseConstants.Symbol.COMMA);
        List<String> result = new ArrayList<>(Arrays.asList(assignedRolesArr));
        for (Long roleId : deleteRoles) {
            result.remove(roleId.toString());
        }
        client.setAccessRoles(StringUtils.join(result, BaseConstants.Symbol.COMMA));
        clientRepository.updateOptional(client, Client.FIELD_ACCESS_ROLES);
        clientRepository.cacheClient(clientRepository.selectByPrimaryKey(client.getId()));
    }


    @Override
    @ProcessLovValue
    public Client detailClient(Long tenantId, Long clientId) {
        return clientRepository.detailClient(tenantId, clientId);
    }

    @Override
    public Page<Client> pageClient(Long organizationId, String name, Integer enabledFlag, PageRequest pageRequest) {
        Page<Client> clients = clientRepository.pageClient(organizationId, name, enabledFlag, pageRequest);
        clients.getContent().forEach(this::convertAuthorizedGrantTypesToMeaning);
        return clients;
    }

    /**
     * 转换授权类型编码为名称
     *
     * @param client 客户端对象
     */
    private void convertAuthorizedGrantTypesToMeaning(Client client) {
        Assert.notNull(client.getAuthorizedGrantTypes(), BaseConstants.ErrorCode.NOT_NULL);
        String[] split = StringUtils.split(client.getAuthorizedGrantTypes(), BaseConstants.Symbol.COMMA);
        String authGrantTypeMeanings = "";
        for (String authGrantType : split) {
            String meaning =
                    lovAdapter.queryLovMeaning(Client.AUTH_GRANT_TYPE_LOV, client.getOrganizationId(), authGrantType);
            if (StringUtils.isNotBlank(authGrantTypeMeanings)) {
                authGrantTypeMeanings = StringUtils.join(authGrantTypeMeanings, BaseConstants.Symbol.COMMA, meaning);
            } else {
                authGrantTypeMeanings = meaning;
            }
        }
        client.setAuthGrantTypeMeanings(authGrantTypeMeanings);
    }
}

