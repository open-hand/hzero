package org.hzero.file.domain.entity;

import java.util.Date;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.Transient;
import javax.validation.constraints.NotNull;

import com.fasterxml.jackson.annotation.JsonInclude;
import org.hibernate.validator.constraints.Length;
import org.hzero.boot.platform.lov.annotation.LovValue;
import org.hzero.starter.keyencrypt.core.Encrypt;

import io.choerodon.mybatis.annotation.ModifyAudit;
import io.choerodon.mybatis.annotation.VersionAudit;
import io.choerodon.mybatis.domain.AuditDomain;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

/**
 * 文件编辑日志
 *
 * @author shuangfei.zhu@hand-china.com 2019-06-28 11:36:32
 */
@ApiModel("文件编辑日志")
@VersionAudit
@ModifyAudit
@Table(name = "hfle_file_edit_log")
@JsonInclude(JsonInclude.Include.NON_NULL)
public class FileEditLog extends AuditDomain {

    public static final String FIELD_LOG_ID = "logId";
    public static final String FIELD_FILE_ID = "fileId";
    public static final String FIELD_USER_ID = "userId";
    public static final String FIELD_EDIT_TYPE = "editType";
    public static final String FIELD_CHANGE_LOG = "changeLog";
    public static final String FIELD_CHANGE_DATE = "changeDate";

    //
    // 业务方法(按public protected private顺序排列)
    // ------------------------------------------------------------------------------

    //
    // 数据库字段
    // ------------------------------------------------------------------------------


    @ApiModelProperty("表ID，主键，供其他表做外键")
    @Id
    @GeneratedValue
    @Encrypt
    private Long logId;
    @ApiModelProperty(value = "文件Id，hfle_file.file_id", required = true)
    @NotNull
    @Encrypt
    private Long fileId;
    @ApiModelProperty(value = "用户Id，iam_user.id", required = true)
    @NotNull
    private Long userId;
    @ApiModelProperty(value = "编辑类型，值集：HFLE.FILE.EDIT_TYPE")
    @LovValue("HFLE.FILE.EDIT_TYPE")
    @Length(max = 30)
    private String editType;
    @ApiModelProperty(value = "变更记录")
    private String changeLog;
    @ApiModelProperty(value = "变更时间", required = true)
    @NotNull
    private Date changeDate;
    @NotNull
    @ApiModelProperty(value = "租户ID")
    private Long tenantId;

    //
    // 非数据库字段
    // ------------------------------------------------------------------------------

    @Transient
    private String fileName;
    @Transient
    private String tenantName;
    @Transient
    private String realName;
    @Transient
    private String editTypeMeaning;

    //
    // getter/setter
    // ------------------------------------------------------------------------------


    public Long getLogId() {
        return logId;
    }

    public FileEditLog setLogId(Long logId) {
        this.logId = logId;
        return this;
    }

    public Long getFileId() {
        return fileId;
    }

    public FileEditLog setFileId(Long fileId) {
        this.fileId = fileId;
        return this;
    }

    public Long getUserId() {
        return userId;
    }

    public FileEditLog setUserId(Long userId) {
        this.userId = userId;
        return this;
    }

    public String getEditType() {
        return editType;
    }

    public FileEditLog setEditType(String editType) {
        this.editType = editType;
        return this;
    }

    public String getChangeLog() {
        return changeLog;
    }

    public FileEditLog setChangeLog(String changeLog) {
        this.changeLog = changeLog;
        return this;
    }

    public Date getChangeDate() {
        return changeDate;
    }

    public FileEditLog setChangeDate(Date changeDate) {
        this.changeDate = changeDate;
        return this;
    }

    public String getFileName() {
        return fileName;
    }

    public FileEditLog setFileName(String fileName) {
        this.fileName = fileName;
        return this;
    }

    public Long getTenantId() {
        return tenantId;
    }

    public FileEditLog setTenantId(Long tenantId) {
        this.tenantId = tenantId;
        return this;
    }

    public String getTenantName() {
        return tenantName;
    }

    public FileEditLog setTenantName(String tenantName) {
        this.tenantName = tenantName;
        return this;
    }

    public String getRealName() {
        return realName;
    }

    public FileEditLog setRealName(String realName) {
        this.realName = realName;
        return this;
    }

    public String getEditTypeMeaning() {
        return editTypeMeaning;
    }

    public FileEditLog setEditTypeMeaning(String editTypeMeaning) {
        this.editTypeMeaning = editTypeMeaning;
        return this;
    }
}
