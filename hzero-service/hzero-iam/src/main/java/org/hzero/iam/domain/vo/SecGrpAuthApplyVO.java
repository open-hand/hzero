package org.hzero.iam.domain.vo;

import org.hzero.starter.keyencrypt.core.Encrypt;

/**
 * 安全组权限应用VO
 *
 * @author allen.liu
 * @date 2019/11/20
 */
public class SecGrpAuthApplyVO {
    @Encrypt
    private Long roleId;
    @Encrypt
    private Long secGrpId;
    private String authorityType;
    @Encrypt
    private Long authorityPermissionId;

    private SecGrpAuthApplyVO(Builder builder) {
        setRoleId(builder.roleId);
        setSecGrpId(builder.secGrpId);
        setAuthorityType(builder.authorityType);
        setAuthorityPermissionId(builder.authorityPermissionId);
    }

    public static Builder newBuilder() {
        return new Builder();
    }

    public static Builder newBuilder(SecGrpAuthApplyVO copy) {
        Builder builder = new Builder();
        builder.roleId = copy.getRoleId();
        builder.secGrpId = copy.getSecGrpId();
        builder.authorityType = copy.getAuthorityType();
        builder.authorityPermissionId = copy.getAuthorityPermissionId();
        return builder;
    }

    public Long getRoleId() {
        return roleId;
    }

    public SecGrpAuthApplyVO setRoleId(Long roleId) {
        this.roleId = roleId;
        return this;
    }

    public Long getSecGrpId() {
        return secGrpId;
    }

    public SecGrpAuthApplyVO setSecGrpId(Long secGrpId) {
        this.secGrpId = secGrpId;
        return this;
    }

    public String getAuthorityType() {
        return authorityType;
    }

    public SecGrpAuthApplyVO setAuthorityType(String authorityType) {
        this.authorityType = authorityType;
        return this;
    }

    public Long getAuthorityPermissionId() {
        return authorityPermissionId;
    }

    public SecGrpAuthApplyVO setAuthorityPermissionId(Long authorityPermissionId) {
        this.authorityPermissionId = authorityPermissionId;
        return this;
    }


    public static final class Builder {
        private Long roleId;
        private Long secGrpId;
        private String authorityType;
        private Long authorityPermissionId;

        private Builder() {
        }

        public Builder roleId(Long roleId) {
            this.roleId = roleId;
            return this;
        }

        public Builder secGrpId(Long secGrpId) {
            this.secGrpId = secGrpId;
            return this;
        }

        public Builder authorityType(String authorityType) {
            this.authorityType = authorityType;
            return this;
        }

        public Builder authorityPermissionId(Long authorityPermissionId) {
            this.authorityPermissionId = authorityPermissionId;
            return this;
        }

        public SecGrpAuthApplyVO build() {
            return new SecGrpAuthApplyVO(this);
        }
    }
}
