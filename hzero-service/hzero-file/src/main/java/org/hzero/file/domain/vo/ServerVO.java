package org.hzero.file.domain.vo;

/**
 * description
 *
 * @author shuangfei.zhu@hand-china.com 2019/07/05 13:35
 */
public class ServerVO {

    private Long serverId;
    private String serverCode;
    private String serverName;
    private String protocolCode;
    private String ip;
    private Integer port;
    private String loginUser;
    private String loginEncPwd;
    private Integer enabledFlag;
    private Long tenantId;
    private Long clusterId;

    private String rootDir;

    public Long getServerId() {
        return serverId;
    }

    public ServerVO setServerId(Long serverId) {
        this.serverId = serverId;
        return this;
    }

    public String getServerCode() {
        return serverCode;
    }

    public ServerVO setServerCode(String serverCode) {
        this.serverCode = serverCode;
        return this;
    }

    public String getServerName() {
        return serverName;
    }

    public ServerVO setServerName(String serverName) {
        this.serverName = serverName;
        return this;
    }

    public String getProtocolCode() {
        return protocolCode;
    }

    public ServerVO setProtocolCode(String protocolCode) {
        this.protocolCode = protocolCode;
        return this;
    }

    public String getIp() {
        return ip;
    }

    public ServerVO setIp(String ip) {
        this.ip = ip;
        return this;
    }

    public Integer getPort() {
        return port;
    }

    public ServerVO setPort(Integer port) {
        this.port = port;
        return this;
    }

    public String getLoginUser() {
        return loginUser;
    }

    public ServerVO setLoginUser(String loginUser) {
        this.loginUser = loginUser;
        return this;
    }

    public String getLoginEncPwd() {
        return loginEncPwd;
    }

    public ServerVO setLoginEncPwd(String loginEncPwd) {
        this.loginEncPwd = loginEncPwd;
        return this;
    }

    public Integer getEnabledFlag() {
        return enabledFlag;
    }

    public ServerVO setEnabledFlag(Integer enabledFlag) {
        this.enabledFlag = enabledFlag;
        return this;
    }

    public Long getTenantId() {
        return tenantId;
    }

    public ServerVO setTenantId(Long tenantId) {
        this.tenantId = tenantId;
        return this;
    }

    public String getRootDir() {
        return rootDir;
    }

    public ServerVO setRootDir(String rootDir) {
        this.rootDir = rootDir;
        return this;
    }

    public Long getClusterId() {
        return clusterId;
    }

    public ServerVO setClusterId(Long clusterId) {
        this.clusterId = clusterId;
        return this;
    }

    @Override
    public String toString() {
        return "ServerVO{" +
                "serverId=" + serverId +
                ", serverCode='" + serverCode + '\'' +
                ", serverName='" + serverName + '\'' +
                ", protocolCode='" + protocolCode + '\'' +
                ", ip='" + ip + '\'' +
                ", port=" + port +
                ", loginUser='" + loginUser + '\'' +
                ", loginEncPwd='" + loginEncPwd + '\'' +
                ", enabledFlag=" + enabledFlag +
                ", tenantId=" + tenantId +
                ", clusterId=" + clusterId +
                ", rootDir='" + rootDir + '\'' +
                '}';
    }
}
