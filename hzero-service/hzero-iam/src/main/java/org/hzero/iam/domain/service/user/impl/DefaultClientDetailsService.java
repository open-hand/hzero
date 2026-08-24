package org.hzero.iam.domain.service.user.impl;

import io.choerodon.core.exception.CommonException;
import io.choerodon.core.oauth.DetailsHelper;
import org.hzero.core.base.BaseConstants;
import org.hzero.core.exception.NotLoginException;
import org.hzero.core.util.TokenUtils;
import org.hzero.iam.domain.entity.MemberRole;
import org.hzero.iam.domain.repository.MemberRoleRepository;
import org.hzero.iam.domain.service.user.ClientDetailsService;
import org.hzero.iam.infra.feign.ClientDetailsClient;
import org.hzero.mybatis.domian.Condition;
import org.hzero.mybatis.util.Sqls;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.util.CollectionUtils;

import java.util.List;
import java.util.Optional;

/**
 * <p>
 * 当前客户端角色管理接口实现类
 * </p>
 *
 * @author qingsheng.chen 2018/8/31 星期五 15:36
 */
public class DefaultClientDetailsService implements ClientDetailsService {
    private static final Logger logger = LoggerFactory.getLogger(DefaultClientDetailsService.class);

    @Autowired
    private ClientDetailsClient clientDetailsClient;
    @Autowired
    private MemberRoleRepository memberRoleRepository;

    @Override
    public Long readClientRole() {
        return DetailsHelper.getUserDetails().getRoleId();
    }

    @Override
    public Long readClientTenant() {
        return DetailsHelper.getUserDetails().getTenantId();
    }

    @Override
    public void storeClientRole(Long roleId) {
        if (roleId == null) {
            return;
        }
        List<MemberRole> memberRoleList = memberRoleRepository.selectByCondition(Condition.builder(MemberRole.class)
                .andWhere(Sqls.custom()
                        .andEqualTo(MemberRole.FIELD_ROLE_ID, roleId)
                        .andEqualTo(MemberRole.FIELD_MEMBER_TYPE, "client")
                        .andEqualTo(MemberRole.FIELD_MEMBER_ID, Optional.of(DetailsHelper.getUserDetails()).orElseThrow(NotLoginException::new).getClientId()))
                .build());
        if (CollectionUtils.isEmpty(memberRoleList)) {
            throw new CommonException("error.change.role.not-exists");
        }
        if (memberRoleList.size() > BaseConstants.Digital.ONE) {
            logger.error("Found a role assigned to the same Client multiple times. roleId = {}", roleId);
        }
        MemberRole memberRole = memberRoleList.get(0);
        ResponseEntity responseEntity = clientDetailsClient.storeClientRole(TokenUtils.getToken(), memberRole.getRoleId(), memberRole.getAssignLevel(), memberRole.getAssignLevelValue());
        if (!responseEntity.getStatusCode().is2xxSuccessful()) {
            logger.error("Store role failed = {}, {}", responseEntity.getStatusCode(), responseEntity.getBody());
            throw new CommonException("error.change.role");
        }
    }

    @Override
    public void storeClientTenant(Long tenantId) {
        if (tenantId == null) {
            return;
        }
        ResponseEntity responseEntity = clientDetailsClient.storeClientTenant(TokenUtils.getToken(), tenantId);
        if (!responseEntity.getStatusCode().is2xxSuccessful()) {
            logger.error("Store tenant failed = {}, {}", responseEntity.getStatusCode(), responseEntity.getBody());
            throw new CommonException("error.change.tenant");
        }
    }
}
