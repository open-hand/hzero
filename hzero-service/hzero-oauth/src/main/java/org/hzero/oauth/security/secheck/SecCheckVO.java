package org.hzero.oauth.security.secheck;

import java.io.Serializable;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

import org.hzero.oauth.security.constant.LoginField;

/**
 * 二次校验所需参数
 *
 * @author bergturing 2020/08/26 11:23
 */
public class SecCheckVO implements Serializable {
    private static final long serialVersionUID = -5093217090726859855L;

    /**
     * 二次校验的key
     */
    public static final String SEC_CHECK_KEY = "secCheckVoKey";
    public static final String PARAMETER_SEC_CHECK_TYPE = "secCheckType";

    public static final String FIELD_SUPPORT_TYPES = "supportTypes";
    public static final String FIELD_PHONE = "phone";
    public static final String FIELD_EMAIL = "email";

    /**
     * 支持的校验方式
     */
    private Set<String> supportTypes;

    /**
     * 手机号
     */
    private String phone;

    /**
     * 邮箱
     */
    private String email;

    public Set<String> getSupportTypes() {
        return supportTypes;
    }

    public void setSupportTypes(Set<String> supportTypes) {
        this.supportTypes = supportTypes;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public void addSupportTypes(String... supportTypes) {
        if (Objects.isNull(this.supportTypes)) {
            this.supportTypes = new HashSet<>();
        }

        this.supportTypes.addAll(Arrays.asList(supportTypes));
    }

    public String getAccount(String secCheckType) {
        if (this.supportTypes.contains(secCheckType)) {
            if (LoginField.PHONE.code().equals(secCheckType)) {
                return this.getPhone();
            } else if (LoginField.EMAIL.code().equals(secCheckType)) {
                return this.getEmail();
            }
        }
        return null;
    }

    @Override
    public String toString() {
        return "SecCheckVO{" +
                "supportTypes=" + supportTypes +
                ", phone='" + phone + '\'' +
                ", email='" + email + '\'' +
                '}';
    }
}
