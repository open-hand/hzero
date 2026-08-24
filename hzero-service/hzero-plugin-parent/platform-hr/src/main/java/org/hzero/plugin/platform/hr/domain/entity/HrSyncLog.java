package org.hzero.plugin.platform.hr.domain.entity;

import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.Transient;
import javax.validation.constraints.NotNull;

import org.hzero.boot.platform.lov.annotation.LovValue;
import org.hzero.plugin.platform.hr.domain.repository.HrSyncLogRepository;
import org.hzero.starter.keyencrypt.core.Encrypt;

import com.fasterxml.jackson.annotation.JsonInclude;

import io.choerodon.mybatis.annotation.ModifyAudit;
import io.choerodon.mybatis.annotation.VersionAudit;
import io.choerodon.mybatis.domain.AuditDomain;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

/**
 * hr基础数据同步外部系统日志
 *
 * @author minghui.qiu@hand-china.com 2019-10-14 21:20:14
 */
@ApiModel("hr基础数据同步外部系统日志")
@VersionAudit
@ModifyAudit
@Table(name = "hpfm_hr_sync_log")
@JsonInclude(JsonInclude.Include.NON_NULL)
public class HrSyncLog extends AuditDomain {

    public static final String FIELD_SYNC_LOG_ID = "syncLogId";
    public static final String FIELD_SYNC_ID = "syncId";
    public static final String FIELD_DEPT_STATUS_CODE = "deptStatusCode";
    public static final String FIELD_EMP_STATUS_CODE = "empStatusCode";
    public static final String FIELD_LOG_CONTENT = "logContent";
    public static final String FIELD_TENANT_ID = "tenantId";
    public static final String FIELD_SYNC_DIRECTION = "syncDirection";

    //
    // 业务方法(按public protected private顺序排列)
    // ------------------------------------------------------------------------------

    //
    // 数据库字段
    // ------------------------------------------------------------------------------

    @ApiModelProperty("")
    @Id
    @GeneratedValue
    @Encrypt
    private Long syncLogId;
    @ApiModelProperty(value = "基础数据同步配置hpfm_hr_sync.sync_id", required = true)
    @NotNull
    @Encrypt
    private Long syncId;
    @ApiModelProperty(value = "部门同步状态", required = true)
    @LovValue(lovCode = "HPFM.HR_SYNC_STATUS")
    @NotNull
    private Long deptStatusCode;
    @ApiModelProperty(value = "员工同步状态", required = true)
    @LovValue(lovCode = "HPFM.HR_SYNC_STATUS")
    @NotNull
    private Long empStatusCode;
    @ApiModelProperty(value = "同步日志")
    private String logContent;
    @ApiModelProperty(value = "租户id", required = true)
    @NotNull
    private Long tenantId;
    @ApiModelProperty(value = "同步方向", required = true)
    @LovValue(lovCode = "HPFM.HR_SYNC.DIRECTION")
    @NotNull
    private String syncDirection;

    //
    // method
    // -----------------------------------------------------------------------------

    public void setSyncFailed(HrSyncLogRepository hrSyncLogRepository, String message){
        hrSyncLogRepository.updateLog(this.setDeptStatusCode(-1L).setEmpStatusCode(-1L).setLogContent("Get Token Failed!"));
    }

    //
    // 非数据库字段
    // -----------------------------------------------------------------------------
    @Transient
    private String deptStatusMeaning;
    @Transient
    private String empStatusMeaning;
    @Transient
    @ApiModelProperty(value = "同步类型 值集HPFM.HR_SYNC_TYPE")
    @LovValue(lovCode = "HPFM.HR_SYNC_TYPE")
    private String syncTypeCode;
    @Transient
    private String syncTypeMeaning;
    @Transient
    private String syncDirectionMeaning;
    //
    // getter/setter
    // ------------------------------------------------------------------------------

    /**
     * @return 
     */
    public Long getSyncLogId() {
        return syncLogId;
    }

    public HrSyncLog setSyncLogId(Long syncLogId) {
        this.syncLogId = syncLogId;
        return this;
    }

    /**
     * @return 基础数据同步配置hpfm_hr_sync.sync_id
     */
    public Long getSyncId() {
        return syncId;
    }

    public HrSyncLog setSyncId(Long syncId) {
        this.syncId = syncId;
        return this;
    }

    /**
     * @return 部门同步状态
     */
    public Long getDeptStatusCode() {
        return deptStatusCode;
    }

    public HrSyncLog setDeptStatusCode(Long deptStatusCode) {
        this.deptStatusCode = deptStatusCode;
        return this;
    }

    /**
     * @return 员工同步状态
     */
    public Long getEmpStatusCode() {
        return empStatusCode;
    }

    public HrSyncLog setEmpStatusCode(Long empStatusCode) {
        this.empStatusCode = empStatusCode;
        return this;
    }

    /**
     * @return 同步日志
     */
    public String getLogContent() {
        return logContent;
    }

    public HrSyncLog setLogContent(String logContent) {
        this.logContent = logContent;
        return this;
    }

    /**
     * @return 租户id
     */
    public Long getTenantId() {
        return tenantId;
    }

    public HrSyncLog setTenantId(Long tenantId) {
        this.tenantId = tenantId;
        return this;
    }

    public String getDeptStatusMeaning() {
        return deptStatusMeaning;
    }

    public void setDeptStatusMeaning(String deptStatusMeaning) {
        this.deptStatusMeaning = deptStatusMeaning;
    }

    public String getEmpStatusMeaning() {
        return empStatusMeaning;
    }

    public void setEmpStatusMeaning(String empStatusMeaning) {
        this.empStatusMeaning = empStatusMeaning;
    }

    public String getSyncTypeMeaning() {
        return syncTypeMeaning;
    }

    public void setSyncTypeMeaning(String syncTypeMeaning) {
        this.syncTypeMeaning = syncTypeMeaning;
    }

    public String getSyncTypeCode() {
        return syncTypeCode;
    }

    public void setSyncTypeCode(String syncTypeCode) {
        this.syncTypeCode = syncTypeCode;
    }

    public String getSyncDirection() {
        return syncDirection;
    }

    public HrSyncLog setSyncDirection(String syncDirection) {
        this.syncDirection = syncDirection;
        return this;
    }

    public String getSyncDirectionMeaning() {
        return syncDirectionMeaning;
    }

    public HrSyncLog setSyncDirectionMeaning(String syncDirectionMeaning) {
        this.syncDirectionMeaning = syncDirectionMeaning;
        return this;
    }
}
