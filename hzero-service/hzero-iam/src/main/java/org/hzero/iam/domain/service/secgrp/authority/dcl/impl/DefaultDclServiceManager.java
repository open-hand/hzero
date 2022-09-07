package org.hzero.iam.domain.service.secgrp.authority.dcl.impl;

import io.choerodon.mybatis.pagehelper.domain.PageRequest;
import org.hzero.iam.api.dto.SecGrpDclDTO;
import org.hzero.iam.api.dto.SecGrpDclQueryDTO;
import org.hzero.iam.domain.entity.SecGrp;
import org.hzero.iam.domain.entity.SecGrpDcl;
import org.hzero.iam.domain.entity.SecGrpDclLine;
import org.hzero.iam.domain.service.secgrp.authority.dcl.AbstractDclServiceManager;
import org.springframework.stereotype.Component;

import javax.annotation.Nonnull;
import java.util.List;

/**
 * 安全组数据权限服务管理器的默认实现
 *
 * @author bo.he02@hand-china.com 2020/04/03 10:45
 */
@Component
public class DefaultDclServiceManager extends AbstractDclServiceManager {
    @Override
    public SecGrpDclDTO querySecGrpDclAuthority(@Nonnull String authorityTypeCode,
                                                @Nonnull SecGrpDclQueryDTO queryDTO,
                                                @Nonnull PageRequest pageRequest) {
        // 返回处理结果
        return this.findSupportAndProcessR(authorityTypeCode,
                dclService -> dclService.querySecGrpDclAuthority(queryDTO, pageRequest));
    }

    @Override
    public SecGrpDclDTO querySecGrpDclAssignableAuthority(@Nonnull String authorityTypeCode,
                                                          @Nonnull SecGrpDclQueryDTO queryDTO,
                                                          @Nonnull PageRequest pageRequest) {
        // 返回处理结果
        return this.findSupportAndProcessR(authorityTypeCode,
                dclService -> dclService.querySecGrpDclAssignableAuthority(queryDTO, pageRequest));
    }

    @Override
    public SecGrpDclDTO querySecGrpDclAssignedAuthority(@Nonnull String authorityTypeCode,
                                                        @Nonnull SecGrpDclQueryDTO queryDTO,
                                                        @Nonnull PageRequest pageRequest) {
        // 返回处理结果
        return this.findSupportAndProcessR(authorityTypeCode,
                dclService -> dclService.querySecGrpDclAssignedAuthority(queryDTO, pageRequest));
    }

    @Override
    public SecGrpDclDTO queryRoleSecGrpDclAuthority(@Nonnull String authorityTypeCode,
                                                    @Nonnull Long roleId,
                                                    @Nonnull SecGrpDclQueryDTO queryDTO,
                                                    @Nonnull PageRequest pageRequest) {
        // 返回处理结果
        return this.findSupportAndProcessR(authorityTypeCode,
                dclService -> dclService.queryRoleSecGrpDclAuthority(roleId, queryDTO, pageRequest));
    }

    @Override
    public void addSecGrpDclAuthority(@Nonnull String authorityTypeCode,
                                      @Nonnull SecGrp secGrp,
                                      @Nonnull SecGrpDcl dcl,
                                      @Nonnull List<SecGrpDclLine> dclLines) {
        // 处理数据
        this.findSupportAndProcess(authorityTypeCode,
                dclService -> dclService.addSecGrpDclAuthority(secGrp, dcl, dclLines));
    }

    @Override
    public void removeSecGrpDclAuthority(@Nonnull String authorityTypeCode,
                                         @Nonnull SecGrp secGrp,
                                         @Nonnull SecGrpDcl dcl,
                                         @Nonnull List<SecGrpDclLine> dclLines) {
        // 处理数据
        this.findSupportAndProcess(authorityTypeCode,
                dclService -> dclService.removeSecGrpDclAuthority(secGrp, dcl, dclLines));
    }

    @Override
    public void enableSecGrpDclAuthority(@Nonnull String authorityTypeCode,
                                         @Nonnull SecGrp secGrp,
                                         @Nonnull SecGrpDcl dcl) {
        // 处理数据
        this.findSupportAndProcess(authorityTypeCode,
                dclService -> dclService.enableSecGrpDclAuthority(secGrp, dcl));
    }

    @Override
    public void disabledSecGrpDclAuthority(@Nonnull String authorityTypeCode,
                                           @Nonnull SecGrp secGrp,
                                           @Nonnull SecGrpDcl dcl) {
        // 处理数据
        this.findSupportAndProcess(authorityTypeCode,
                dclService -> dclService.disabledSecGrpDclAuthority(secGrp, dcl));
    }

    @Override
    public void saveSecGrpDclAuthority(@Nonnull String authorityTypeCode,
                                       @Nonnull Long secGrpId,
                                       @Nonnull List<SecGrpDclLine> dclLines) {
        // 处理数据
        this.findSupportAndProcess(authorityTypeCode,
                dclService -> dclService.saveSecGrpDclAuthority(secGrpId, dclLines));
    }

    @Override
    public void deleteSecGrpDclAuthority(@Nonnull String authorityTypeCode,
                                         @Nonnull Long secGrpId,
                                         @Nonnull List<SecGrpDclLine> dclLines) {
        // 处理数据
        this.findSupportAndProcess(authorityTypeCode,
                dclService -> dclService.deleteSecGrpDclAuthority(secGrpId, dclLines));
    }
}
