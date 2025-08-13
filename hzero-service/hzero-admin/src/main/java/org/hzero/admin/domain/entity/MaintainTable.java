package org.hzero.admin.domain.entity;

import com.fasterxml.jackson.annotation.JsonInclude;
import io.choerodon.mybatis.annotation.ModifyAudit;
import io.choerodon.mybatis.annotation.VersionAudit;
import io.choerodon.mybatis.domain.AuditDomain;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import org.hibernate.validator.constraints.Length;
import org.hzero.starter.keyencrypt.core.Encrypt;

import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;

/**
 * @author XCXCXCXCX
 * @date 2020/6/1 11:16 上午
 */
@ApiModel("在线运维表")
@VersionAudit
@ModifyAudit
@Table(name = "hadm_maintain_table")
@JsonInclude(JsonInclude.Include.NON_NULL)
public class MaintainTable extends AuditDomain {

    public static final String ENCRYPT_KEY = "hadm_maintain_table";

    @Encrypt
    @Id
    @GeneratedValue
    @ApiModelProperty("主键ID")
    private Long maintainTableId;

    @Encrypt
    @ApiModelProperty("在线运维ID，关联hadm_maintain_table.maintain_id")
    @NotNull
    private Long maintainId;

    @ApiModelProperty("服务编码，与注册中心上的服务名对应")
    @NotNull
    private String serviceCode;

    @ApiModelProperty("运维表名")
    @Length(max = 60)
    @NotEmpty
    private String tableName;

    @ApiModelProperty("运维模式，包括READ、WRITE")
    @NotEmpty
    private String maintainMode;


    // ==== 关联查询字段

    public String getServiceCode() {
        return serviceCode;
    }

    public void setServiceCode(String serviceCode) {
        this.serviceCode = serviceCode;
    }

    public Long getMaintainTableId() {
        return maintainTableId;
    }

    public void setMaintainTableId(Long maintainTableId) {
        this.maintainTableId = maintainTableId;
    }

    public Long getMaintainId() {
        return maintainId;
    }

    public void setMaintainId(Long maintainId) {
        this.maintainId = maintainId;
    }

    public String getTableName() {
        return tableName;
    }

    public void setTableName(String tableName) {
        this.tableName = tableName;
    }

    public String getMaintainMode() {
        return maintainMode;
    }

    public void setMaintainMode(String maintainMode) {
        if (maintainMode == null) {
            return;
        }
        MaintainMode.valueOf(maintainMode);
        this.maintainMode = maintainMode;
    }

    public enum MaintainMode {
        /**
         * 读模式，读模式下不允许写
         */
        READ,
        /**
         * 写模式，写模式下不允许读写
         */
        WRITE
    }

}
