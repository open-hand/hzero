package org.hzero.message.api.dto;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import org.hzero.core.algorithm.tree.Child;
import org.hzero.message.domain.entity.ReceiveConfig;
import org.hzero.mybatis.domian.SecurityToken;
import org.hzero.starter.keyencrypt.core.Encrypt;
import org.springframework.beans.BeanUtils;

import io.choerodon.mybatis.annotation.MultiLanguage;
import io.choerodon.mybatis.annotation.MultiLanguageField;

/**
 * 接收配置DTO
 *
 * @author shuangfei.zhu@hand-china.com 2018/11/21 20:45
 */
@MultiLanguage
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ReceiveConfigDTO extends Child<ReceiveConfigDTO> implements SecurityToken {

    @Encrypt
    private Long receiveId;
    private String receiveCode;
    @MultiLanguageField
    private String receiveName;
    private String defaultReceiveType;
    private String parentReceiveCode;
    private Integer levelNumber;
    private String defaultReceiveTypeMeaning;
    private Long objectVersionNumber;
    private String _token;
    private Long tenantId;
    private Integer editableFlag;

    @JsonIgnore
    public ReceiveConfig getReceiveConfig(){
        ReceiveConfig receiveConfig = new ReceiveConfig();
        BeanUtils.copyProperties(this,receiveConfig);
        return receiveConfig;
    }

    public Long getReceiveId() {
        return receiveId;
    }

    public ReceiveConfigDTO setReceiveId(Long receiveId) {
        this.receiveId = receiveId;
        return this;
    }

    public String getReceiveCode() {
        return receiveCode;
    }

    public ReceiveConfigDTO setReceiveCode(String receiveCode) {
        this.receiveCode = receiveCode;
        return this;
    }

    public String getReceiveName() {
        return receiveName;
    }

    public ReceiveConfigDTO setReceiveName(String receiveName) {
        this.receiveName = receiveName;
        return this;
    }

    public String getDefaultReceiveType() {
        return defaultReceiveType;
    }

    public ReceiveConfigDTO setDefaultReceiveType(String defaultReceiveType) {
        this.defaultReceiveType = defaultReceiveType;
        return this;
    }

    public String getParentReceiveCode() {
        return parentReceiveCode;
    }

    public ReceiveConfigDTO setParentReceiveCode(String parentReceiveCode) {
        this.parentReceiveCode = parentReceiveCode;
        return this;
    }

    public Integer getLevelNumber() {
        return levelNumber;
    }

    public ReceiveConfigDTO setLevelNumber(Integer levelNumber) {
        this.levelNumber = levelNumber;
        return this;
    }

    public String getDefaultReceiveTypeMeaning() {
        return defaultReceiveTypeMeaning;
    }

    public ReceiveConfigDTO setDefaultReceiveTypeMeaning(String defaultReceiveTypeMeaning) {
        this.defaultReceiveTypeMeaning = defaultReceiveTypeMeaning;
        return this;
    }

    public Long getObjectVersionNumber() {
        return objectVersionNumber;
    }

    public ReceiveConfigDTO setObjectVersionNumber(Long objectVersionNumber) {
        this.objectVersionNumber = objectVersionNumber;
        return this;
    }

    public Long getTenantId() {
        return tenantId;
    }

    public ReceiveConfigDTO setTenantId(Long tenantId) {
        this.tenantId = tenantId;
        return this;
    }

    public Integer getEditableFlag() {
        return editableFlag;
    }

    public ReceiveConfigDTO setEditableFlag(Integer editableFlag) {
        this.editableFlag = editableFlag;
        return this;
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
        return ReceiveConfig.class;
    }
}
