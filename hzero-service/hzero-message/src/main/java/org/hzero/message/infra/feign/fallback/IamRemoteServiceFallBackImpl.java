package org.hzero.message.infra.feign.fallback;

import org.hzero.message.domain.entity.UserGroupAssign;
import org.hzero.message.infra.feign.IamRemoteService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * iam
 *
 * @author minghui.qiu@hand-china.com 2019/6/14 11:15
 */
@Component
public class IamRemoteServiceFallBackImpl implements IamRemoteService {

    private static final Logger logger = LoggerFactory.getLogger(IamRemoteServiceFallBackImpl.class);

    @Override
    public ResponseEntity<String> listUserGroupAssignUsers(List<UserGroupAssign> userGroupAssigns) {
        logger.error("Error to list user-group-assign users, params[userGroupAssigns = {}]", userGroupAssigns);
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
    }

    @Override
    public ResponseEntity<String> listOpenUserGroupAssignUsers(List<UserGroupAssign> userGroupAssigns, String messageType) {
        logger.error("Error to list user-group-assign users, params[userGroupAssigns = {}] messageType {} ", userGroupAssigns, messageType);
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
    }

    @Override
    public ResponseEntity<String> pageUser(Long organizationId, Integer page, Integer size) {
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
    }

    @Override
    public ResponseEntity<String> listRoleMembers(Long organizationId, Long roleId, Integer page, Integer size) {
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
    }

    @Override
    public ResponseEntity<String> listReceiverByUserIds(Long organizationId, List<Long> userIds) {
        logger.error("Error to list user by userIdList, userIdList= {}", userIds);
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
    }

    @Override
    public ResponseEntity<String> listOpenReceiverByUserIds(Long organizationId, List<Long> userIds, String thirdPlatformType) {
        logger.error("Error to list open-user by userIdList userIdList= {}", userIds);
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
    }
}
