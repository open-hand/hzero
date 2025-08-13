package org.hzero.platform.domain.entity;

import io.choerodon.mybatis.annotation.ModifyAudit;
import io.choerodon.mybatis.annotation.VersionAudit;
import io.choerodon.mybatis.domain.AuditDomain;

import com.fasterxml.jackson.annotation.JsonInclude;
import org.hzero.boot.platform.lov.annotation.LovValue;

import javax.persistence.Column;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.Transient;
import java.util.Date;

/**
 * @author XCXCXCXCX
 * @date 2019/8/5
 * @project hzero-starter-parent
 */
@VersionAudit
@ModifyAudit
@Table(name = "hpfm_export_task")
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ExportTask extends AuditDomain {

    public static final String FIELD_TASK_ID = "taskId";
    public static final String FIELD_TASK_CODE = "taskCode";
    public static final String FIELD_TASK_NAME = "taskName";
    public static final String FIELD_SERVICE_NAME = "serviceName";
    public static final String FIELD_TENANT_ID = "tenantId";
    public static final String FIELD_STATE = "state";
    public static final String FIELD_DOWNLOAD_URL = "downloadUrl";
    public static final String FIELD_END_DATE_TIME = "endDateTime";

    @Id
    @GeneratedValue
    private Long taskId;
    @Column(name = "task_code")
    private String taskCode;
    @Column(name = "task_name")
    private String taskName;
    @Column(name = "host_name")
    private String hostName;
    @Column(name = "service_name")
    private String serviceName;
    @Column(name = "tenant_id")
    private Long tenantId;
    @LovValue(value = "HIAM.TENANT.USER", meaningField = "loginName")
    @Column(name = "user_id")
    private Long userId;
    @Column(name = "state")
    private String state;
    @Column(name = "error_info")
    private String errorInfo;
    @Column(name = "download_url")
    private String downloadUrl;
    @Column(name = "end_date_time")
    private Date endDateTime;

    @Transient
    private String loginName;

    public Long getTaskId() {
        return taskId;
    }

    public void setTaskId(Long taskId) {
        this.taskId = taskId;
    }

    public String getTaskCode() {
        return taskCode;
    }

    public void setTaskCode(String taskCode) {
        this.taskCode = taskCode;
    }

    public String getTaskName() {
        return taskName;
    }

    public void setTaskName(String taskName) {
        this.taskName = taskName;
    }

    public String getDownloadUrl() {
        return downloadUrl;
    }

    public void setDownloadUrl(String downloadUrl) {
        this.downloadUrl = downloadUrl;
    }

    public String getServiceName() {
        return serviceName;
    }

    public void setServiceName(String serviceName) {
        this.serviceName = serviceName;
    }

    public String getHostName() {
        return hostName;
    }

    public void setHostName(String hostName) {
        this.hostName = hostName;
    }

    public String getState() {
        return state;
    }

    public void setState(String state) {
        this.state = state;
    }

    public String getErrorInfo() {
        return errorInfo;
    }

    public void setErrorInfo(String errorInfo) {
        this.errorInfo = errorInfo;
    }

    public Long getTenantId() {
        return tenantId;
    }

    public void setTenantId(Long tenantId) {
        this.tenantId = tenantId;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public Date getEndDateTime() {
        return endDateTime;
    }

    public void setEndDateTime(Date endDateTime) {
        this.endDateTime = endDateTime;
    }

    public String getLoginName() {
        return loginName;
    }

    public void setLoginName(String loginName) {
        this.loginName = loginName;
    }
}
