package org.hzero.iam.domain.service.user.impl;

import java.util.List;
import java.util.Objects;
import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import io.choerodon.core.exception.CommonException;
import io.choerodon.core.oauth.DetailsHelper;

import org.hzero.core.exception.NotLoginException;
import org.hzero.core.util.TokenUtils;
import org.hzero.iam.app.service.TenantAccessService;
import org.hzero.iam.domain.entity.MemberRole;
import org.hzero.iam.domain.entity.Role;
import org.hzero.iam.domain.repository.MemberRoleRepository;
import org.hzero.iam.domain.repository.RoleRepository;
import org.hzero.iam.domain.service.RootUserService;
import org.hzero.iam.domain.service.user.UserDetailsService;
import org.hzero.iam.infra.constant.HiamMemberType;
import org.hzero.iam.infra.constant.HiamResourceLevel;
import org.hzero.iam.infra.feign.UserDetailsClient;
import org.hzero.mybatis.domian.Condition;
import org.hzero.mybatis.util.Sqls;

/**
 * <p>
 * 当前用户角色管理接口实现类
 * </p>
 *
 * @author qingsheng.chen 2018/8/31 星期五 15:36
 */
public class DefaultUserDetailsService implements UserDetailsService {
    private static final Logger logger = LoggerFactory.getLogger(DefaultUserDetailsService.class);

    @Autowired
    private UserDetailsClient userDetailsClient;
    @Autowired
    private TenantAccessService tenantAccessService;
    @Autowired
    private MemberRoleRepository memberRoleRepository;
    @Autowired
    private RoleRepository roleRepository;

    @Override
    public Long readUserRole() {
        return DetailsHelper.getUserDetails().getRoleId();
    }

    @Override
    public Long readUserTenant() {
        return DetailsHelper.getUserDetails().getTenantId();
    }

    @Override
    public void storeUserRole(Long roleId) {
        if (roleId == null) {
            return;
        }

        MemberRole memberRole = memberRoleRepository.selectByCondition(Condition.builder(MemberRole.class)
                .andWhere(Sqls.custom()
                        .andEqualTo(MemberRole.FIELD_ROLE_ID, roleId)
                        .andEqualTo(MemberRole.FIELD_MEMBER_TYPE, HiamMemberType.USER.value())
                        .andEqualTo(MemberRole.FIELD_MEMBER_ID, Optional.of(DetailsHelper.getUserDetails()).orElseThrow(NotLoginException::new).getUserId()))
                .build()).stream().findFirst().orElse(null);

        // Root 用户不需分配角色也可以使用
        if (Objects.isNull(memberRole) && RootUserService.isRootUser()) {
            Role role = roleRepository.selectRoleSimpleById(roleId);
            memberRole = new MemberRole();
            memberRole.setRoleId(roleId);
            memberRole.setMemberType(HiamMemberType.USER.value());
            memberRole.setAssignLevel(HiamResourceLevel.ORGANIZATION.value());
            memberRole.setAssignLevelValue(role.getTenantId());
        }

        if (Objects.isNull(memberRole)) {
            throw new CommonException("error.change.role.not-exists");
        }

        ResponseEntity responseEntity = userDetailsClient.storeUserRole(TokenUtils.getToken(), memberRole.getRoleId(), memberRole.getAssignLevel(), memberRole.getAssignLevelValue());
        if (!responseEntity.getStatusCode().is2xxSuccessful()) {
            logger.error("Store role failed = {}, {}", responseEntity.getStatusCode(), responseEntity.getBody());
            throw new CommonException("error.change.role");
        }
    }

    @Override
    public void storeUserTenant(Long tenantId) {
        if (tenantId == null) {
            return;
        }
        ResponseEntity responseEntity = userDetailsClient.storeUserTenant(TokenUtils.getToken(), tenantId);
        if (!responseEntity.getStatusCode().is2xxSuccessful()) {
            logger.error("Store tenant failed = {}, {}", responseEntity.getStatusCode(), responseEntity.getBody());
            throw new CommonException("error.change.tenant");
        } else {
            tenantAccessService.storeUserTenant(DetailsHelper.getUserDetails().getUserId(), tenantId);
        }
    }

    @Override
    public void storeUserLanguage(String language) {
        if (!StringUtils.hasText(language)) {
            return;
        }
        ResponseEntity responseEntity = userDetailsClient.storeUserLanguage(TokenUtils.getToken(), language);
        if (!responseEntity.getStatusCode().is2xxSuccessful()) {
            logger.error("Store language failed = {}, {}", responseEntity.getStatusCode(), responseEntity.getBody());
            throw new CommonException("error.change.language");
        }
    }

    @Override
    public void storeUserTimeZone(String timeZone) {
        if (!StringUtils.hasText(timeZone)) {
            return;
        }
        ResponseEntity responseEntity = userDetailsClient.storeUserTimeZone(TokenUtils.getToken(), timeZone);
        if (!responseEntity.getStatusCode().is2xxSuccessful()) {
            logger.error("Store timeZone failed = {}, {}", responseEntity.getStatusCode(), responseEntity.getBody());
            throw new CommonException("error.change.time-zone");
        }
    }

    @Override
    public void refresh(List<String> loginNameList) {
        if (CollectionUtils.isEmpty(loginNameList)) {
            return;
        }
        userDetailsClient.refresh(TokenUtils.getToken(), loginNameList);
    }
}
