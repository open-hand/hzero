package org.hzero.report.domain.entity;

import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

import com.fasterxml.jackson.annotation.JsonInclude;
import org.hibernate.validator.constraints.Length;

import io.choerodon.mybatis.annotation.ModifyAudit;
import io.choerodon.mybatis.annotation.VersionAudit;
import io.choerodon.mybatis.domain.AuditDomain;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

/**
 * ureport报表文件
 *
 * @author shuangfei.zhu@hand-china.com 2020-08-03 15:55:31
 */
@ApiModel("ureport报表文件")
@VersionAudit
@ModifyAudit
@JsonInclude(value = JsonInclude.Include.NON_NULL)
@Table(name = "hrpt_ureport_file")
public class UreportFile extends AuditDomain {

    public static final String FIELD_FILE_ID = "fileId";
    public static final String FIELD_FILE_NAME = "fileName";
    public static final String FIELD_FILE_URL = "fileUrl";
    public static final String FIELD_TENANT_ID = "tenantId";

    //
    // 业务方法(按public protected private顺序排列)
    // ------------------------------------------------------------------------------

    //
    // 数据库字段
    // ------------------------------------------------------------------------------

    @ApiModelProperty("表ID，主键，供其他表做外键")
    @Id
    @GeneratedValue
    private Long fileId;
    @ApiModelProperty(value = "文件名", required = true)
    @NotBlank
    @Length(max = 180)
    private String fileName;
    @ApiModelProperty(value = "文件url", required = true)
    @NotBlank
    @Length(max = 480)
    private String fileUrl;
    @ApiModelProperty(value = "租户ID,hpfm_tenant.tenant_id", required = true)
    @NotNull
    private Long tenantId;

    //
    // 非数据库字段
    // ------------------------------------------------------------------------------

    //
    // getter/setter
    // ------------------------------------------------------------------------------

    /**
     * @return 表ID，主键，供其他表做外键
     */
    public Long getFileId() {
        return fileId;
    }

    public UreportFile setFileId(Long fileId) {
        this.fileId = fileId;
        return this;
    }

    /**
     * @return 文件名
     */
    public String getFileName() {
        return fileName;
    }

    public UreportFile setFileName(String fileName) {
        this.fileName = fileName;
        return this;
    }

    /**
     * @return 文件url
     */
    public String getFileUrl() {
        return fileUrl;
    }

    public UreportFile setFileUrl(String fileUrl) {
        this.fileUrl = fileUrl;
        return this;
    }

    /**
     * @return 租户ID, hpfm_tenant.tenant_id
     */
    public Long getTenantId() {
        return tenantId;
    }

    public UreportFile setTenantId(Long tenantId) {
        this.tenantId = tenantId;
        return this;
    }
}