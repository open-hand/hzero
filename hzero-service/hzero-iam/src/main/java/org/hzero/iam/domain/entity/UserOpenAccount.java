package org.hzero.iam.domain.entity;

import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.Transient;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

import com.fasterxml.jackson.annotation.JsonInclude;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

import io.choerodon.mybatis.annotation.ModifyAudit;
import io.choerodon.mybatis.annotation.VersionAudit;
import io.choerodon.mybatis.domain.AuditDomain;

import org.hzero.starter.keyencrypt.core.Encrypt;

/**
 * 用户第三方账号
 *
 * @author jiaxu.cui@hand-china.com 2018/9/29 16:05
 */
@ApiModel("用户第三方账号")
@VersionAudit
@ModifyAudit
@JsonInclude(JsonInclude.Include.NON_NULL)
@Table(name = "hiam_user_open_account")
public class UserOpenAccount extends AuditDomain {
    public static final String ENCRYPT_KEY = "hiam_user_open_account";

    public interface Insert{}


    @ApiModelProperty("表主键，Id")
    @Id
    @GeneratedValue
    @Encrypt
    private Long openAccountId;
    @ApiModelProperty("用户名d")
    @NotNull
    private String username;
    @ApiModelProperty("三方网站OpenId")
    @NotBlank
    private String openId;
    @ApiModelProperty("三方网站账户名称,不存在则存储openId")
    @NotBlank
    private String openName;
    @ApiModelProperty("三方网站应用编码")
    @NotNull
    private String openAppCode;
    @NotNull(groups = Insert.class)
    private Long tenantId;

    @Transient
    private String appCode;
    @Transient
    private String appName;
    @Transient
    private String appImage;
    @Transient
    private String channel;

    public Long getTenantId() {
        return tenantId;
    }

    public UserOpenAccount setTenantId(Long tenantId) {
        this.tenantId = tenantId;
        return this;
    }

    public Long getOpenAccountId() {
        return openAccountId;
    }

    public void setOpenAccountId(Long openAccountId) {
        this.openAccountId = openAccountId;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getOpenId() {
        return openId;
    }

    public void setOpenId(String openId) {
        this.openId = openId;
    }

    public String getOpenName() {
        return openName;
    }

    public void setOpenName(String openName) {
        this.openName = openName;
    }

    public String getOpenAppCode() {
        return openAppCode;
    }

    public void setOpenAppCode(String openAppCode) {
        this.openAppCode = openAppCode;
    }

    public String getAppCode() {
        return appCode;
    }

    public void setAppCode(String appCode) {
        this.appCode = appCode;
    }

    public String getAppName() {
        return appName;
    }

    public void setAppName(String appName) {
        this.appName = appName;
    }

    public String getAppImage() {
        return appImage;
    }

    public void setAppImage(String appImage) {
        this.appImage = appImage;
    }

    public String getChannel() {
        return channel;
    }

    public void setChannel(String channel) {
        this.channel = channel;
    }
}
