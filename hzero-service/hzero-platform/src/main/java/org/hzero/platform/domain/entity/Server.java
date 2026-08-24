package org.hzero.platform.domain.entity;

import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.Transient;
import javax.validation.constraints.NotNull;

import com.fasterxml.jackson.annotation.JsonInclude;
import org.hibernate.validator.constraints.Length;
import org.hzero.boot.platform.lov.annotation.LovValue;
import org.hzero.mybatis.annotation.DataSecurity;

import javax.validation.constraints.NotBlank;
import io.choerodon.mybatis.domain.AuditDomain;
import io.choerodon.mybatis.annotation.ModifyAudit;
import io.choerodon.mybatis.annotation.VersionAudit;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import org.hzero.starter.keyencrypt.core.Encrypt;

/**
 * 文件服务器
 *
 * @author minghui.qiu@hand-china.com 2019-07-02 14:33:33
 */
@ApiModel("文件服务器")
@VersionAudit
@ModifyAudit
@Table(name = "hpfm_server")
@JsonInclude(JsonInclude.Include.NON_NULL)
public class Server extends AuditDomain {

    public static final String FIELD_SERVER_ID = "serverId";
    public static final String FIELD_SERVER_CODE = "serverCode";
    public static final String FIELD_SERVER_NAME = "serverName";
    public static final String FIELD_SERVER_DESCRIPTION = "serverDescription";
    public static final String FIELD_PROTOCOL_CODE = "protocolCode";
    public static final String FIELD_IP = "ip";
    public static final String FIELD_PORT = "port";
    public static final String FIELD_LOGIN_USER = "loginUser";
    public static final String FIELD_LOGIN_ENC_PWD = "loginEncPwd";
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
    private Long serverId;
    @ApiModelProperty(value = "服务器代码")
    @NotBlank
    @Length(max = 30)
    private String serverCode;
    @ApiModelProperty(value = "服务器名称")
    @NotBlank
    @Length(max = 80)
    private String serverName;
    @ApiModelProperty(value = "说明")   
    @Length(max = 240)
    private String serverDescription;
    @ApiModelProperty(value = "协议FTP、SFTP(快码：HPFM.PROTOCOL_TYPE)")
    @NotBlank
    @LovValue(lovCode = "HPFM.PROTOCOL_TYPE")
    private String protocolCode;
    @ApiModelProperty(value = "服务器IP")
    @NotBlank
    @Length(max = 30)
    private String ip;
    @ApiModelProperty(value = "服务器端口")
    @NotNull
    private int port;
    @ApiModelProperty(value = "登录用户")
    @NotBlank
    @Length(max = 20)
    private String loginUser;
    @ApiModelProperty(value = "加密密码")
    @NotBlank
    @Length(max = 240)
    private String loginEncPwd;
    @ApiModelProperty(value = "是否启用 1:启用 0：不启用")
    @NotNull
    private Integer enabledFlag;
    @ApiModelProperty(value = "租户ID")
    @NotNull
    private Long tenantId;

	//
    // 非数据库字段
    // ------------------------------------------------------------------------------
//    @Transient
//    private String loginPassword;
    @Transient
    @ApiModelProperty(value = "服务器集群id")
    @Encrypt
    private Long clusterId;
    @Transient
    private String clusterCode;
    @Transient
    private String clusterName;
//    @Transient
//    private String key ;
//    @Transient
//    private String path ;
    @Transient
    private String protocolMeaning ;
    @Transient
    @ApiModelProperty("租户名称")
    private String tenantName;
    //
    // getter/setter
    // ------------------------------------------------------------------------------

    /**
     * @return 
     */
	public Long getServerId() {
		return serverId;
	}

	public void setServerId(Long serverId) {
		this.serverId = serverId;
	}
    /**
     * @return 服务器代码
     */
	public String getServerCode() {
		return serverCode;
	}

	public Server setServerCode(String serverCode) {
		this.serverCode = serverCode;
		return this;
	}
    /**
     * @return 服务器名称
     */
	public String getServerName() {
		return serverName;
	}

	public Server setServerName(String serverName) {
		this.serverName = serverName;
		return this;
	}
    /**
     * @return 说明
     */
	public String getServerDescription() {
		return serverDescription;
	}

	public void setServerDescription(String serverDescription) {
		this.serverDescription = serverDescription;
	}

    /**
     * @return 服务器IP
     */
	public String getIp() {
		return ip;
	}

	public void setIp(String ip) {
		this.ip = ip;
	}
    /**
     * @return 服务器端口
     */
	public int getPort() {
		return port;
	}

	public void setPort(int port) {
		this.port = port;
	}
    /**
     * @return 登录用户
     */
	public String getLoginUser() {
		return loginUser;
	}

	public void setLoginUser(String loginUser) {
		this.loginUser = loginUser;
	}
    /**
     * @return 加密密码
     */
	public String getLoginEncPwd() {
		return loginEncPwd;
	}

	public void setLoginEncPwd(String loginEncPwd) {
		this.loginEncPwd = loginEncPwd;
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

	public Server setTenantId(Long tenantId) {
		this.tenantId = tenantId;
		return this;
	}

//    public String getLoginPassword() {
//        return loginPassword;
//    }
//
//    public void setLoginPassword(String loginPassword) {
//        this.loginPassword = loginPassword;
//    }

    public Long getClusterId() {
        return clusterId;
    }

    public void setClusterId(Long clusterId) {
        this.clusterId = clusterId;
    }

    public String getProtocolCode() {
        return protocolCode;
    }

    public void setProtocolCode(String protocolCode) {
        this.protocolCode = protocolCode;
    }

    public String getProtocolMeaning() {
        return protocolMeaning;
    }

    public void setProtocolMeaning(String protocolMeaning) {
        this.protocolMeaning = protocolMeaning;
    }

    public String getClusterCode() {
        return clusterCode;
    }

    public void setClusterCode(String clusterCode) {
        this.clusterCode = clusterCode;
    }

    public String getClusterName() {
        return clusterName;
    }

    public void setClusterName(String clusterName) {
        this.clusterName = clusterName;
    }

    public String getTenantName() {
        return tenantName;
    }

    public void setTenantName(String tenantName) {
        this.tenantName = tenantName;
    }

//    public String getKey() {
//        return key;
//    }
//
//    public void setKey(String key) {
//        this.key = key;
//    }
//
//    public String getPath() {
//        return path;
//    }
//
//    public void setPath(String path) {
//        this.path = path;
//    }

}
