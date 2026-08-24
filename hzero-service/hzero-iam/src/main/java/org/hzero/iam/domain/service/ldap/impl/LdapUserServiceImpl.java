package org.hzero.iam.domain.service.ldap.impl;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.hzero.common.UserSource;
import org.hzero.iam.api.dto.LdapConnectionDTO;
import org.hzero.iam.app.service.UserService;
import org.hzero.iam.domain.entity.Ldap;
import org.hzero.iam.domain.entity.LdapErrorUser;
import org.hzero.iam.domain.entity.User;
import org.hzero.iam.domain.repository.LdapRepository;
import org.hzero.iam.domain.service.ldap.LdapConnectService;
import org.hzero.iam.domain.service.ldap.LdapUserService;
import org.hzero.iam.infra.constant.Constants;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import io.choerodon.core.exception.CommonException;

/**
 *
 * @author bojiangzhou 2019/08/05
 */
@Service
public class LdapUserServiceImpl implements LdapUserService {

    private static final Logger LOGGER = LoggerFactory.getLogger(LdapUserServiceImpl.class);

    private final UserService userService;
    private final LdapConnectService ldapConnectService;
    private final LdapRepository ldapRepository;

    public LdapUserServiceImpl(UserService userService, LdapConnectService ldapConnectService,
                    LdapRepository ldapRepository) {
        this.userService = userService;
        this.ldapConnectService = ldapConnectService;
        this.ldapRepository = ldapRepository;
    }

    @Override
    public List<LdapErrorUser> batchCreateUsers(List<User> insertUsers) {
        List<LdapErrorUser> errorUsers = new ArrayList<>(insertUsers.size());
        insertUsers.forEach(user -> {
            try {
                // UserInfo
                user.setUserSource(UserSource.ADMIN_CREATE.code());
                user.setStartDateActive(user.getStartDateActive() == null ? LocalDate.now() : user.getStartDateActive());
                user.setCheckPasswordPolicy(false);

                userService.createUserInternal(user);
            } catch (Exception e) {
                LOGGER.warn("Ldap create user error. exception message is [{}]", e.getMessage());
                LdapErrorUser errorUser = new LdapErrorUser();
                errorUser.setUuid(user.getUuid())
                        .setLoginName(user.getLoginName())
                        .setEmail(user.getEmail())
                        .setRealName(user.getRealName())
                        .setPhone(user.getPhone())
                        .setCause(((CommonException) e).getCode());
                errorUsers.add(errorUser);
            }
        });

        return errorUsers;
    }

    @Override
    public List<LdapErrorUser> batchUpdateUsers(List<User> updateUsers) {
        List<LdapErrorUser> errorUsers = new ArrayList<>(updateUsers.size());
        updateUsers.forEach(user -> {
            try {
                user.setCheckPasswordPolicy(false);

                userService.updateUserInternal(user);
            } catch (Exception e) {
                LOGGER.warn("Ldap update user error. exception message is [{}]", e.getMessage());
                LdapErrorUser errorUser = new LdapErrorUser();
                errorUser.setUuid(user.getUuid())
                        .setLoginName(user.getLoginName())
                        .setEmail(user.getEmail())
                        .setRealName(user.getRealName())
                        .setPhone(user.getPhone());
                if (e instanceof CommonException) {
                    errorUser.setCause(((CommonException) e).getCode());
                } else {
                    errorUser.setCause("ldap update user error");
                }
                errorUsers.add(errorUser);
            }
        });

        return errorUsers;
    }

    @Override
    public Map<String, Object> validateLdapConnection(Ldap ldap) {
        Map<String, Object> map = ldapConnectService.testConnect(ldap);
        LdapConnectionDTO ldapConnectionDTO = (LdapConnectionDTO) map.get(LdapConnectServiceImpl.LDAP_CONNECTION_DTO);
        if (!ldapConnectionDTO.getCanConnectServer()) {
            throw new CommonException("error.ldap.connect");
        }
        if (!ldapConnectionDTO.getCanLogin()) {
            throw new CommonException("error.ldap.authenticate");
        }
        if (!ldapConnectionDTO.getMatchAttribute()) {
            throw new CommonException("error.ldap.attribute.match");
        }
        return map;
    }

    @Override
    public Ldap validateLdap(Long organizationId, Long ldapId) {
        Ldap ldap = ldapRepository.selectLdap(organizationId, ldapId);
        if (ldap == null) {
            throw new CommonException(Constants.ErrorCode.LDAP_NOT_EXIST_EXCEPTION);
        }
        ldap.connectValidate();
        return ldap;
    }
}
