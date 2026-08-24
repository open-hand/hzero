package org.hzero.platform.domain.entity;

import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;

import javax.validation.constraints.NotBlank;
import io.choerodon.mybatis.domain.AuditDomain;
import io.choerodon.mybatis.annotation.ModifyAudit;
import io.choerodon.mybatis.annotation.VersionAudit;

import com.fasterxml.jackson.annotation.JsonInclude;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import org.hzero.starter.keyencrypt.core.Encrypt;

/**
 * 文件服务器的集群分配
 *
 * @author minghui.qiu@hand-china.com 2019-07-02 14:33:33
 */
@ApiModel("文件服务器的集群分配")
@VersionAudit
@ModifyAudit
@Table(name = "hpfm_server_assign")
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ServerAssign extends AuditDomain {

    public static final String FIELD_ASSIGN_ID = "assignId";
    public static final String FIELD_CLUSTER_ID = "clusterId";
    public static final String FIELD_SERVER_ID = "serverId";
    public static final String FIELD_ASSIGN_DESCRIPTION = "assignDescription";
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
    private Long assignId;
    @ApiModelProperty(value = "集群ID",required = true)
    @NotNull
	@Encrypt
    private Long clusterId;
    @ApiModelProperty(value = "服务器ID",required = true)
    @NotNull
	@Encrypt
    private Long serverId;
    @ApiModelProperty(value = "分配描述")
    private String assignDescription;
    @ApiModelProperty(value = "租户ID",required = true)
    @NotNull
    private Long tenantId;

	//
    // 非数据库字段
    // ------------------------------------------------------------------------------

    //
    // getter/setter
    // ------------------------------------------------------------------------------

    /**
     * @return 
     */
	public Long getAssignId() {
		return assignId;
	}

	public void setAssignId(Long assignId) {
		this.assignId = assignId;
	}
    /**
     * @return 集群ID
     */
	public Long getClusterId() {
		return clusterId;
	}

	public void setClusterId(Long clusterId) {
		this.clusterId = clusterId;
	}
    /**
     * @return 服务器ID
     */
	public Long getServerId() {
		return serverId;
	}

	public void setServerId(Long serverId) {
		this.serverId = serverId;
	}
    /**
     * @return 分配描述
     */
	public String getAssignDescription() {
		return assignDescription;
	}

	public void setAssignDescription(String assignDescription) {
		this.assignDescription = assignDescription;
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

}
