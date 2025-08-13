package org.hzero.admin.domain.entity;

import io.choerodon.mybatis.annotation.ModifyAudit;
import io.choerodon.mybatis.annotation.VersionAudit;
import io.choerodon.mybatis.domain.AuditDomain;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import org.hzero.starter.keyencrypt.core.Encrypt;

import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.util.Date;

/**
 * 服务版本
 *
 * @author shuangfei.zhu@hand-china.com 2019-08-23 14:35:21
 */
@ApiModel("服务版本")
@VersionAudit
@ModifyAudit
@Table(name = "hadm_service_version")
public class ServiceVersion extends AuditDomain {

    private static final String ENCRYPT_KEY = "hadm_service_version";

    public static final String FIELD_SERVICE_VERSION_ID = "serviceVersionId";
    public static final String FIELD_SERVICE_ID = "serviceId";
    public static final String FIELD_VERSION_NUMBER = "versionNumber";
    public static final String FIELD_META_VERSION = "metaVersion";
    public static final String FIELD_RELEASE_DATE = "releaseDate";
    public static final String FIELD_UPDATE_LOG = "updateLog";

    //
    // 业务方法(按public protected private顺序排列)
    // ------------------------------------------------------------------------------

    //
    // 数据库字段
    // ------------------------------------------------------------------------------


    @Encrypt
    @ApiModelProperty(value = "服务版本ID")
    @Id
    @GeneratedValue
    private Long serviceVersionId;
    @Encrypt
    @ApiModelProperty(value = "服务ID：hsgp_service.service_id", required = true)
    @NotNull
    private Long serviceId;
    @ApiModelProperty(value = "版本号", required = true)
    @NotBlank
    private String versionNumber;
    @ApiModelProperty(value = "标记版本", required = true)
    @NotBlank
    private String metaVersion;
    @ApiModelProperty(value = "发布时间", required = true)
    @NotNull
    private Date releaseDate;
    @ApiModelProperty(value = "更新日志")
    private String updateLog;

    //
    // 非数据库字段
    // ------------------------------------------------------------------------------

    //
    // getter/setter
    // ------------------------------------------------------------------------------


    public Long getServiceVersionId() {
        return serviceVersionId;
    }

    public ServiceVersion setServiceVersionId(Long serviceVersionId) {
        this.serviceVersionId = serviceVersionId;
        return this;
    }

    public Long getServiceId() {
        return serviceId;
    }

    public ServiceVersion setServiceId(Long serviceId) {
        this.serviceId = serviceId;
        return this;
    }

    public String getVersionNumber() {
        return versionNumber;
    }

    public ServiceVersion setVersionNumber(String versionNumber) {
        this.versionNumber = versionNumber;
        return this;
    }

    public String getMetaVersion() {
        return metaVersion;
    }

    public ServiceVersion setMetaVersion(String metaVersion) {
        this.metaVersion = metaVersion;
        return this;
    }

    public Date getReleaseDate() {
        return releaseDate;
    }

    public ServiceVersion setReleaseDate(Date releaseDate) {
        this.releaseDate = releaseDate;
        return this;
    }

    public String getUpdateLog() {
        return updateLog;
    }

    public ServiceVersion setUpdateLog(String updateLog) {
        this.updateLog = updateLog;
        return this;
    }

}
