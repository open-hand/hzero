package org.hzero.iam.api.dto;

import java.util.List;

import org.hzero.iam.domain.entity.SecGrp;
import org.hzero.starter.keyencrypt.core.Encrypt;

/**
 * 安全组快速复制数据DTO
 *
 * @author xingxingwu.hand-china.com 2019/11/20 14:32
 */
public class SecGrpQuickCreateDTO {
    @Encrypt
    private List<Long> sourceSecGrpIds;
    private SecGrp secGrp;
    @Encrypt
    private Long roleId;

    public List<Long> getSourceSecGrpIds() {
        return sourceSecGrpIds;
    }

    public void setSourceSecGrpIds(List<Long> sourceSecGrpIds) {
        this.sourceSecGrpIds = sourceSecGrpIds;
    }

    public SecGrp getSecGrp() {
        return secGrp;
    }

    public void setSecGrp(SecGrp secGrp) {
        this.secGrp = secGrp;
    }

    public Long getRoleId() {
        return roleId;
    }

    public void setRoleId(Long roleId) {
        this.roleId = roleId;
    }
}
