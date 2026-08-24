package org.hzero.boot.imported.domain.entity;

import java.util.Date;

import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.Transient;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

import org.hibernate.validator.constraints.Length;
import org.hzero.boot.imported.infra.constant.HimpBootConstants;
import org.hzero.boot.platform.lov.annotation.LovValue;

import com.fasterxml.jackson.annotation.JsonInclude;

import io.choerodon.mybatis.annotation.ModifyAudit;
import io.choerodon.mybatis.annotation.VersionAudit;
import io.choerodon.mybatis.domain.AuditDomain;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import org.hzero.common.HZeroCacheKey;
import org.hzero.core.cache.CacheValue;
import org.hzero.core.cache.Cacheable;
import org.hzero.starter.keyencrypt.core.Encrypt;

/**
 * @author shuangfei.zhu@hand-china.com 2018-12-19 14:57:57
 */
@ApiModel("导入状态")
@VersionAudit
@ModifyAudit
@Table(name = "himp_import")
@JsonInclude(JsonInclude.Include.NON_NULL)
public class Import extends AuditDomain implements Cacheable {

    public static final String FIELD_IMPORT_ID = "importId";
    public static final String FIELD_BATCH = "batch";
    public static final String FIELD_STATUS = "status";
    public static final String FIELD_DATA_COUNT = "dataCount";

    //
    // 业务方法(按public protected private顺序排列)
    // ------------------------------------------------------------------------------

    //
    // 数据库字段
    // ------------------------------------------------------------------------------

    @ApiModelProperty("主键 表ID")
    @Id
    @GeneratedValue
    @Encrypt
    private Long importId;
    @ApiModelProperty(value = "批次", required = true)
    @NotBlank
    @Length(max = 120)
    private String batch;
    @ApiModelProperty(value = "当前状态", required = true)
    @NotBlank
    @Length(max = 30)
    @LovValue(lovCode = HimpBootConstants.ImportStatus.CODE)
    private String status;
    @ApiModelProperty(value = "数据数量", required = true)
    @NotNull
    private Integer dataCount;
    @ApiModelProperty(value = "自定义参数")
    private String param;
    @ApiModelProperty(value = "租户ID")
    private Long tenantId;
    @ApiModelProperty(value = "模板编码")
    private String templateCode;

    //
    // 非数据库字段
    // ------------------------------------------------------------------------------

    @Transient
    private String statusMeaning;
    @ApiModelProperty(value = "进度")
    @Transient
    private String progress;
    @ApiModelProperty(value = "数据总量")
    @Transient
    private Integer count;
    @ApiModelProperty(value = "就绪数量")
    @Transient
    private Integer ready;
    @Transient
    @ApiModelProperty("创建人姓名")
    @CacheValue(key = HZeroCacheKey.USER, primaryKey = "createdBy", searchKey = "realName",
            structure = CacheValue.DataStructure.MAP_OBJECT)
    private String createdUserName;

    //
    // getter/setter
    // ------------------------------------------------------------------------------

    /**
     * @return 主键 表ID
     */
    public Long getImportId() {
        return importId;
    }

    public Import setImportId(Long importId) {
        this.importId = importId;
        return this;
    }

    /**
     * @return 批次
     */
    public String getBatch() {
        return batch;
    }

    public Import setBatch(String batch) {
        this.batch = batch;
        return this;
    }

    /**
     * @return 当前状态
     */
    public String getStatus() {
        return status;
    }

    public Import setStatus(String status) {
        this.status = status;
        return this;
    }

    /**
     * @return 数据数量
     */
    public Integer getDataCount() {
        return dataCount;
    }

    public Import setDataCount(Integer dataCount) {
        this.dataCount = dataCount;
        return this;
    }

    public String getStatusMeaning() {
        return statusMeaning;
    }

    public Import setStatusMeaning(String statusMeaning) {
        this.statusMeaning = statusMeaning;
        return this;
    }

    public String getParam() {
        return param;
    }

    public Import setParam(String param) {
        this.param = param;
        return this;
    }

    public Long getTenantId() {
        return tenantId;
    }

    public Import setTenantId(Long tenantId) {
        this.tenantId = tenantId;
        return this;
    }

    public String getTemplateCode() {
        return templateCode;
    }

    public Import setTemplateCode(String templateCode) {
        this.templateCode = templateCode;
        return this;
    }

    public String getProgress() {
        return progress;
    }

    public Import setProgress(String progress) {
        this.progress = progress;
        return this;
    }

    public Integer getCount() {
        return count;
    }

    public Import setCount(Integer count) {
        this.count = count;
        return this;
    }

    public Integer getReady() {
        return ready;
    }

    public Import setReady(Integer ready) {
        this.ready = ready;
        return this;
    }

    public String getCreatedUserName() {
        return createdUserName;
    }

    public void setCreatedUserName(String createdUserName) {
        this.createdUserName = createdUserName;
    }

    @Override
    public Date getCreationDate() {
        return super.getCreationDate();
    }

    @Override
    public Long getCreatedBy() {
        return super.getCreatedBy();
    }

    @Override
    public Date getLastUpdateDate() {
        return super.getLastUpdateDate();
    }

    @Override
    public Long getLastUpdatedBy() {
        return super.getLastUpdatedBy();
    }
}
