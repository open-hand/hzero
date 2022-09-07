package org.hzero.platform.api.dto;

import java.util.Date;

import com.fasterxml.jackson.annotation.JsonInclude;
import org.hzero.core.base.BaseConstants;
import org.hzero.export.annotation.ExcelColumn;
import org.hzero.export.annotation.ExcelSheet;

/**
 * description
 *
 * @author xingxing.wu@hand-china.com 2019/01/17 16:21
 */
@ExcelSheet(zh = "登录信息", en = "audit Login")
@JsonInclude(JsonInclude.Include.NON_EMPTY)
public class AuditLoginExportForSite {
    @ExcelColumn(zh = "审计类型", en = "auditTypeMeaning")
    private String auditTypeMeaning;
    @ExcelColumn(zh = "账号", en = "loginName")
    private String loginName;
    @ExcelColumn(zh = "名称", en = "userName")
    private String userName;
    @ExcelColumn(zh = "手机号", en = "phone")
    private String phone;
    @ExcelColumn(zh = "所属租户", en = "tenantName")
    private String tenantName;
    @ExcelColumn(zh = "登录时间", en = "loginDate", pattern = BaseConstants.Pattern.DATETIME)
    private Date loginDate;
    @ExcelColumn(zh = "登录地址", en = "loginIp")
    private String loginIp;
    private String loginPlatform;
    @ExcelColumn(zh = "登录设备", en = "loginDevice")
    private String loginDevice;
    @ExcelColumn(zh = "登录信息", en = "loginMessage")
    private String loginMessage;

    public String getLoginName() {
        return loginName;
    }

    public void setLoginName(String loginName) {
        this.loginName = loginName;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getTenantName() {
        return tenantName;
    }

    public void setTenantName(String tenantName) {
        this.tenantName = tenantName;
    }

    public Date getLoginDate() {
        return loginDate;
    }

    public void setLoginDate(Date loginDate) {
        this.loginDate = loginDate;
    }

    public String getLoginIp() {
        return loginIp;
    }

    public void setLoginIp(String loginIp) {
        this.loginIp = loginIp;
    }

    public String getLoginPlatform() {
        return loginPlatform;
    }

    public void setLoginPlatform(String loginPlatform) {
        this.loginPlatform = loginPlatform;
    }

    public String getLoginDevice() {
        return loginDevice;
    }

    public void setLoginDevice(String loginDevice) {
        this.loginDevice = loginDevice;
    }

    public String getAuditTypeMeaning() {
        return auditTypeMeaning;
    }

    public void setAuditTypeMeaning(String auditTypeMeaning) {
        this.auditTypeMeaning = auditTypeMeaning;
    }

    public String getLoginMessage() {
        return loginMessage;
    }

    public void setLoginMessage(String loginMessage) {
        this.loginMessage = loginMessage;
    }
}
