package org.hzero.message.api.dto;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import org.hzero.core.algorithm.tree.Child;
import org.hzero.message.domain.entity.UserReceiveConfig;
import org.hzero.mybatis.domian.SecurityToken;
import org.hzero.starter.keyencrypt.core.Encrypt;

import java.util.List;

/**
 * 用户接收配置DTO
 *
 * @author shuangfei.zhu@hand-china.com 2018/11/22 13:48
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
public class UserReceiveConfigDTO extends Child<UserReceiveConfigDTO> implements SecurityToken {

    @Encrypt
    private Long userReceiveId;
    private String receiveCode;
    @JsonIgnore
    private String receiveType;
    private List<String> receiveTypeList;
    @JsonIgnore
    private String defaultReceiveType;
    private List<String> defaultReceiveTypeList;
    private Long tenantId;
    private Long configTenantId;
    private Long objectVersionNumber;
    private String _token;

    /**
     * 辅助构建树形结构的字段
     */
    private Long receiveId;
    private String parentReceiveCode;

    private String receiveName;
    private Integer levelNumber;

    public Long getUserReceiveId() {
        return userReceiveId;
    }

    public UserReceiveConfigDTO setUserReceiveId(Long userReceiveId) {
        this.userReceiveId = userReceiveId;
        return this;
    }

    public String getReceiveCode() {
        return receiveCode;
    }

    public UserReceiveConfigDTO setReceiveCode(String receiveCode) {
        this.receiveCode = receiveCode;
        return this;
    }

    public String getReceiveType() {
        return receiveType;
    }

    public UserReceiveConfigDTO setReceiveType(String receiveType) {
        this.receiveType = receiveType;
        return this;
    }

    public String getDefaultReceiveType() {
        return defaultReceiveType;
    }

    public UserReceiveConfigDTO setDefaultReceiveType(String defaultReceiveType) {
        this.defaultReceiveType = defaultReceiveType;
        return this;
    }

    public Long getObjectVersionNumber() {
        return objectVersionNumber;
    }

    public UserReceiveConfigDTO setObjectVersionNumber(Long objectVersionNumber) {
        this.objectVersionNumber = objectVersionNumber;
        return this;
    }

    public Long getReceiveId() {
        return receiveId;
    }

    public UserReceiveConfigDTO setReceiveId(Long receiveId) {
        this.receiveId = receiveId;
        return this;
    }

    public String getParentReceiveCode() {
        return parentReceiveCode;
    }

    public UserReceiveConfigDTO setParentReceiveCode(String parentReceiveCode) {
        this.parentReceiveCode = parentReceiveCode;
        return this;
    }

    public String getReceiveName() {
        return receiveName;
    }

    public UserReceiveConfigDTO setReceiveName(String receiveName) {
        this.receiveName = receiveName;
        return this;
    }

    public Integer getLevelNumber() {
        return levelNumber;
    }

    public UserReceiveConfigDTO setLevelNumber(Integer levelNumber) {
        this.levelNumber = levelNumber;
        return this;
    }

    @Override
    public String toString() {
        return "UserReceiveConfigDTO{" +
                "userReceiveId=" + userReceiveId +
                ", receiveCode='" + receiveCode + '\'' +
                ", receiveType='" + receiveType + '\'' +
                ", receiveTypeList=" + receiveTypeList +
                ", defaultReceiveType='" + defaultReceiveType + '\'' +
                ", defaultReceiveTypeList=" + defaultReceiveTypeList +
                ", tenantId=" + tenantId +
                ", configTenantId=" + configTenantId +
                ", objectVersionNumber=" + objectVersionNumber +
                ", _token='" + _token + '\'' +
                ", receiveId=" + receiveId +
                ", parentReceiveCode='" + parentReceiveCode + '\'' +
                ", receiveName='" + receiveName + '\'' +
                ", levelNumber=" + levelNumber +
                '}';
    }

    @Override
    public String get_token() {
        return this._token;
    }

    @Override
    public void set_token(String tokenValue) {
        this._token = tokenValue;
    }

    @Override
    public Class<? extends SecurityToken> associateEntityClass() {
        return UserReceiveConfig.class;
    }

    public UserReceiveConfigDTO setReceiveTypeList(List<String> receiveTypeList) {
        this.receiveTypeList = receiveTypeList;
        return this;
    }

    public UserReceiveConfigDTO setDefaultReceiveTypeList(List<String> defaultReceiveTypeList) {
        this.defaultReceiveTypeList = defaultReceiveTypeList;
        return this;
    }

    public Long getTenantId() {
        return tenantId;
    }

    public UserReceiveConfigDTO setTenantId(Long tenantId) {
        this.tenantId = tenantId;
        return this;
    }

    public Long getConfigTenantId() {
        return configTenantId;
    }

    public UserReceiveConfigDTO setConfigTenantId(Long configTenantId) {
        this.configTenantId = configTenantId;
        return this;
    }

    public List<String> getReceiveTypeList() {
        return receiveTypeList;
    }

    public List<String> getDefaultReceiveTypeList() {
        return defaultReceiveTypeList;
    }
}
