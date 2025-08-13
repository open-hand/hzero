package org.hzero.iam.app.service.impl;

import java.io.IOException;
import java.util.Collections;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;

import io.choerodon.core.exception.CommonException;

import org.hzero.boot.imported.app.service.IDoImportService;
import org.hzero.boot.imported.infra.validator.annotation.ImportService;
import org.hzero.core.base.BaseConstants;
import org.hzero.iam.app.service.MemberRoleService;
import org.hzero.iam.domain.entity.MemberRole;
import org.hzero.iam.domain.service.UserRoleImportService;
import org.hzero.iam.infra.constant.Constants;

/**
 * @author jianbo.li@hand-china.com 2018/10/16
 */
@ImportService(templateCode = Constants.ImportTemplateCode.ROLE_TEMP)
public class RoleImportServiceImpl implements IDoImportService {
    @Autowired
    private MemberRoleService memberRoleService;
    @Autowired
    private ObjectMapper objectMapper;
    @Autowired
    private UserRoleImportService userRoleImportService;

    @Override
    public Boolean doImport(String data) {

        //转实体
        MemberRole memberRole;
        try {
            memberRole = objectMapper.readValue(data, MemberRole.class);
        } catch (IOException e) {
            throw new CommonException(BaseConstants.ErrorCode.DATA_INVALID);
        }

        //参数校验以及转换
        userRoleImportService.checkAndInitMemberRole(memberRole);

        //创建角色
        memberRoleService.batchAssignMemberRole(Collections.singletonList(memberRole));

        return true;
    }
}
