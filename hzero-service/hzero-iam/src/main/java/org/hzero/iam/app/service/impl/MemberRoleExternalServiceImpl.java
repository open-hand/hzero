package org.hzero.iam.app.service.impl;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.hzero.core.message.MessageAccessor;
import org.hzero.iam.api.dto.AutoProcessResultDTO;
import org.hzero.iam.api.dto.TenantAdminRoleAndDataPrivAutoAssignmentDTO;
import org.hzero.iam.api.eventhandler.CompanyConstants;
import org.hzero.iam.app.service.AutoAssignTenantAdminRoleAndDataPrivService;
import org.hzero.iam.app.service.MemberRoleExternalService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import io.choerodon.core.exception.CommonException;

/**
 *
 * @author mingwei.liu@hand-china.com 2018/8/11
 */
@Service
public class MemberRoleExternalServiceImpl implements MemberRoleExternalService {

    private static final Logger LOGGER = LoggerFactory.getLogger(MemberRoleExternalServiceImpl.class);

    @Autowired
    private AutoAssignTenantAdminRoleAndDataPrivService autoAssignTenantAdminRoleAndDataPrivService;

    @Override
    public List<AutoProcessResultDTO> batchAutoAssignTenantAdminRoleAndDataPriv(List<TenantAdminRoleAndDataPrivAutoAssignmentDTO> tenantAdminRoleAndDataPrivAutoAssignmentDTOList) {
        if (CollectionUtils.isEmpty(tenantAdminRoleAndDataPrivAutoAssignmentDTOList)) {
            return Collections.emptyList();
        }

        // 模拟登陆, 以平台管理员身份进行分配
        //UserUtils.login(userRepository, Constants.SITE_ADMIN);

        // 结果集
        List<AutoProcessResultDTO> autoProcessResultDTOList = new ArrayList<>();

        // 循环处理
        tenantAdminRoleAndDataPrivAutoAssignmentDTOList.forEach(item -> {
            // 初始化处理结果并添加至结果集
            AutoProcessResultDTO autoProcessResultDTO = new AutoProcessResultDTO(item.getCompanyNum(),
                    item.getSourceKey(), item.getSourceCode());
            autoProcessResultDTOList.add(autoProcessResultDTO);

            // 自动分配角色及数据权限, 并且封装结果
            try {
                // 单个处理方法 注意: 此处存在事务处理
                autoAssignTenantAdminRoleAndDataPrivService.autoAssignTenantAdminRoleAndDataPriv(item);
            } catch(Exception ex) {
                LOGGER.error("autoAssignTenantAdminRoleAndDataPriv auto assign role and data priv with error {}", ex.getMessage());
                // 处理失败消息
                autoProcessResultDTO.setProcessStatus(CompanyConstants.CompanyApprovalProcessStatus.ERROR);
                if (ex instanceof CommonException) {
                    autoProcessResultDTO.setProcessMessage(MessageAccessor.getMessage(ex.getMessage(), ((CommonException) ex).getParameters()).desc());
                } else {
                    autoProcessResultDTO.setProcessMessage(ex.getMessage());
                }
            }
        });

        return autoProcessResultDTOList;
    }
}
