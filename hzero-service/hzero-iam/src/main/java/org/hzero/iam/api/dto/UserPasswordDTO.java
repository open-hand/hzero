package org.hzero.iam.api.dto;

import io.swagger.annotations.ApiModelProperty;
import org.hzero.iam.domain.vo.MobileCaptchaVerifyVO;

import javax.validation.constraints.NotEmpty;
import java.util.Objects;

/**
 * @author superlee
 * @since 2018/4/12
 */
public class UserPasswordDTO extends MobileCaptchaVerifyVO {

    @ApiModelProperty(value = "新密码/必填")
    @NotEmpty
    private String password;

    @ApiModelProperty(value = "原始密码/必填")
    @NotEmpty
    private String originalPassword;
    @ApiModelProperty(value = "组织Id/必填")
    private Long organizationId;

    @ApiModelProperty(value = "密码是否加密")
    private Boolean passwordIsEncrypt;

    public Long getOrganizationId() {
        return organizationId;
    }

    public void setOrganizationId(Long organizationId) {
        this.organizationId = organizationId;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getOriginalPassword() {
        return originalPassword;
    }

    public void setOriginalPassword(String originalPassword) {
        this.originalPassword = originalPassword;
    }

    public boolean isPasswordIsEncrypt() {
        return Boolean.TRUE.equals(this.passwordIsEncrypt);
    }

    public void setPasswordIsEncrypt(Boolean passwordIsEncrypt) {
        this.passwordIsEncrypt = passwordIsEncrypt;
    }

    /**
     * 初始化部分数据
     */
    public void init() {
        if (Objects.isNull(this.passwordIsEncrypt)) {
            // 默认密码加密
            this.passwordIsEncrypt = Boolean.TRUE;
        }
    }

    @Override
    public String toString() {
        return "UserPasswordDTO{" +
                "password='" + password + '\'' +
                ", originalPassword='" + originalPassword + '\'' +
                ", organizationId=" + organizationId +
                ", passwordIsEncrypt=" + passwordIsEncrypt +
                "} " + super.toString();
    }
}
