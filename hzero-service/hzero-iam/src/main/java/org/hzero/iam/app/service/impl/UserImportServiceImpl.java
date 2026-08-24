package org.hzero.iam.app.service.impl;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Stream;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import io.choerodon.core.exception.CommonException;
import io.choerodon.core.oauth.CustomUserDetails;

import org.hzero.boot.imported.app.service.IDoImportService;
import org.hzero.boot.imported.infra.validator.annotation.ImportService;
import org.hzero.core.base.BaseConstants;
import org.hzero.iam.app.service.UserService;
import org.hzero.iam.domain.entity.MemberRole;
import org.hzero.iam.domain.entity.User;
import org.hzero.iam.infra.common.utils.UserUtils;
import org.hzero.iam.infra.constant.Constants;
import org.hzero.iam.infra.constant.HiamResourceLevel;


/**
 * @author jianbo.li@hand-china.com 2018/10/16
 */
@ImportService(templateCode = Constants.ImportTemplateCode.CREATE_TEMP)
public class UserImportServiceImpl implements IDoImportService {
    private static final Logger LOGGER = LoggerFactory.getLogger(UserImportServiceImpl.class);

    @Autowired
    private ObjectMapper objectMapper;
    @Autowired
    private UserService userService;

    @Override
    public Boolean doImport(String data) {
        LOGGER.debug("import user data : {}", data);

        //转实体
        User user;
        try {
            user = objectMapper.readValue(data, User.class);
        } catch (IOException e) {
            throw new CommonException(BaseConstants.ErrorCode.DATA_INVALID);
        }


        //创建账户的两次密码输入，导入简化为一次输入
        user.setAnotherPassword(user.getPassword());

        //启用账户
        user.setEnabled(true);

        // 构建 MemberRole
        user.setMemberRoleList(buildMemberRole(user));

        if (user.getOrganizationId() == null) {
            CustomUserDetails self = UserUtils.getUserDetails();
            user.setOrganizationId(self.getTenantId());
        }

        // 是否对密码加密
        user.setPasswordEncrypt(false);

        // 创建用户
        userService.importCreateUser(user);

        return true;
    }

    private List<MemberRole> buildMemberRole(User user) {
        List<MemberRole> memberRoles = new ArrayList<>(8);
        if (StringUtils.isNotBlank(user.getRoleLevelPath())) {
            Stream.of(StringUtils.split(user.getRoleLevelPath(), BaseConstants.Symbol.COMMA))
                    .map(String::trim).forEach(path -> {
                MemberRole mr = new MemberRole();
                mr.setLevelPath(path);
                memberRoles.add(mr);
            });
        } else if (StringUtils.isNotBlank(user.getRoleCode())) {
            Stream.of(StringUtils.split(user.getRoleCode(), BaseConstants.Symbol.COMMA))
                    .map(String::trim).forEach(code -> {
                MemberRole mr = new MemberRole();
                mr.setRoleCode(code);
                mr.setAssignLevel(HiamResourceLevel.ORGANIZATION.value());
                memberRoles.add(mr);
            });
        }

        return memberRoles;
    }

}
