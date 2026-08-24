package org.hzero.iam.app.service.validator;

import org.apache.commons.lang.StringUtils;
import org.hzero.boot.imported.app.service.ValidatorHandler;
import org.hzero.boot.imported.infra.validator.annotation.ImportValidator;
import org.hzero.boot.imported.infra.validator.annotation.ImportValidators;
import org.hzero.core.util.ValidUtils;
import org.hzero.iam.domain.entity.MemberRole;
import org.hzero.iam.domain.entity.User;
import org.hzero.iam.domain.entity.UserAuthImport;
import org.hzero.iam.domain.repository.UserRepository;
import org.hzero.iam.domain.service.UserRoleImportService;
import org.hzero.iam.infra.constant.Constants;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.TypeReference;


/**
 * @author jianbo.li@hand-china.com 2018/10/16
 */
@ImportValidators({
        @ImportValidator(templateCode = Constants.ImportTemplateCode.CREATE_TEMP),
        @ImportValidator(templateCode = Constants.ImportTemplateCode.ROLE_TEMP),
        @ImportValidator(templateCode = Constants.ImportTemplateCode.AUTH_TEMP)}
)
public class AccountImportValidator extends ValidatorHandler {
    private static final Logger logger = LoggerFactory.getLogger(AccountImportValidator.class);
    @Autowired
    private javax.validation.Validator validator;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private UserRoleImportService userRoleImportService;

    @Override
    public boolean validate(String data) {
        Object object = JSON.parseObject(data, new TypeReference<Object>() {
        });
        try {
            ValidUtils.valid(validator, object);
            if (object instanceof MemberRole) {
                MemberRole memberRole = (MemberRole) object;
                userRoleImportService.checkAndInitMemberRole(memberRole);
                return userRoleImportService.memberRoleNotExists(memberRole);
            } else if (object instanceof User) {
                User user = (User) object;
                userRoleImportService.assignRole(user);
                String userType = StringUtils.defaultIfBlank(user.getUserType(), User.DEFAULT_USER_TYPE);
                return userRepository.existsUser(user.getLoginName(), user.getEmail(), user.getPhone(), userType);
            } else if (object instanceof UserAuthImport) {
                UserAuthImport userAuthImport = (UserAuthImport) object;
                userRoleImportService.checkAndInitUserAuthority(userAuthImport);
            }
        } catch (Exception e) {
            logger.warn("校验不成功： {}", e.getMessage(), e);
            return false;
        }
        return true;
    }
}
