package org.hzero.platform.domain.entity;

import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.Transient;
import javax.validation.constraints.NotNull;

import com.fasterxml.jackson.annotation.JsonInclude;
import org.hibernate.validator.constraints.Length;

import javax.validation.constraints.NotBlank;
import io.choerodon.mybatis.domain.AuditDomain;
import io.choerodon.mybatis.annotation.ModifyAudit;
import io.choerodon.mybatis.annotation.VersionAudit;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import org.hzero.starter.keyencrypt.core.Encrypt;

/**
 * 服务器集群设置表
 *
 * @author minghui.qiu@hand-china.com 2019-07-02 14:33:33
 */
@ApiModel("服务器集群设置表")
@VersionAudit
@ModifyAudit
@Table(name = "hpfm_server_cluster")
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ServerCluster extends AuditDomain {

    public static final String FIELD_CLUSTER_ID = "clusterId";
    public static final String FIELD_CLUSTER_CODE = "clusterCode";
    public static final String FIELD_CLUSTER_NAME = "clusterName";
    public static final String FIELD_CLUSTER_DESCRIPTION = "clusterDescription";
    public static final String FIELD_ENABLED_FLAG = "enabledFlag";
    public static final String FIELD_TENANT_ID = "tenantId";


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
    private Long clusterId;
    @ApiModelProperty(value = "代码")
    @NotBlank
    @Length(max = 30)
    private String clusterCode;
    @ApiModelProperty(value = "名称")
    @NotBlank
    @Length(max = 80)
    private String clusterName;
    @ApiModelProperty(value = "说明")  
    @Length(max = 240)
    private String clusterDescription;
    @ApiModelProperty(value = "是否启用 1:启用 0：不启用")
    @NotNull
    private Integer enabledFlag;
    @ApiModelProperty(value = "租户ID")
    @NotNull
    private Long tenantId;

	//
    // 非数据库字段
    // ------------------------------------------------------------------------------
    @Transient
    @ApiModelProperty("租户名称")
    private String tenantName;
    //
    // getter/setter
    // ------------------------------------------------------------------------------

    /**
     * @return 
     */
	public Long getClusterId() {
		return clusterId;
	}

	public void setClusterId(Long clusterId) {
		this.clusterId = clusterId;
	}
    /**
     * @return 代码
     */
	public String getClusterCode() {
		return clusterCode;
	}

	public void setClusterCode(String clusterCode) {
		this.clusterCode = clusterCode;
	}
    /**
     * @return 名称
     */
	public String getClusterName() {
		return clusterName;
	}

	public void setClusterName(String clusterName) {
		this.clusterName = clusterName;
	}
    /**
     * @return 说明
     */
	public String getClusterDescription() {
		return clusterDescription;
	}

	public void setClusterDescription(String clusterDescription) {
		this.clusterDescription = clusterDescription;
	}
    /**
     * @return 是否启用 1:启用 0：不启用
     */
	public Integer getEnabledFlag() {
		return enabledFlag;
	}

	public void setEnabledFlag(Integer enabledFlag) {
		this.enabledFlag = enabledFlag;
	}
    /**
     * @return 租户ID
     */
	public Long getTenantId() {
		return tenantId;
	}

	public void setTenantId(Long tenantId) {
		this.tenantId = tenantId;
	}

    public String getTenantName() {
        return tenantName;
    }

    public void setTenantName(String tenantName) {
        this.tenantName = tenantName;
    }
}
