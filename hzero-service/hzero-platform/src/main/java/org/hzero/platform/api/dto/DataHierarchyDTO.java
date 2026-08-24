package org.hzero.platform.api.dto;

import org.hzero.boot.platform.lov.annotation.LovValue;
import org.hzero.core.algorithm.tree.Child;
import org.hzero.mybatis.domian.SecurityToken;
import org.hzero.platform.domain.entity.DataHierarchy;
import org.hzero.starter.keyencrypt.core.Encrypt;

/**
 * @author qingsheng.chen@hand-china.com
 */
public class DataHierarchyDTO extends Child<DataHierarchyDTO> implements SecurityToken {
    @Encrypt
    private Long dataHierarchyId;
    private String dataHierarchyCode;
    private String dataHierarchyName;
    private Object dataHierarchyValue;
    private Object dataHierarchyMeaning;
    @Encrypt
    private Long parentId;
    private String parentName;
    private Long tenantId;
    private String tenantName;
    @Encrypt
    private String valueSourceId;
    private String valueSourceName;
    private String valueSourceCode;
    @LovValue(lovCode = "HPFM.DATA_HIERARCHY.DISPLAY_STYLE")
    private String displayStyle;
    private String displayStyleMeaning;
    private Integer enabledFlag;
    private Long orderSeq;
    private String levelPath;
    private Long objectVersionNumber;
    private String _token;

    public Long getDataHierarchyId() {
        return dataHierarchyId;
    }

    public DataHierarchyDTO setDataHierarchyId(Long dataHierarchyId) {
        this.dataHierarchyId = dataHierarchyId;
        return this;
    }

    public String getDataHierarchyCode() {
        return dataHierarchyCode;
    }

    public DataHierarchyDTO setDataHierarchyCode(String dataHierarchyCode) {
        this.dataHierarchyCode = dataHierarchyCode;
        return this;
    }

    public Object getDataHierarchyValue() {
        return dataHierarchyValue;
    }

    public DataHierarchyDTO setDataHierarchyValue(Object dataHierarchyValue) {
        this.dataHierarchyValue = dataHierarchyValue;
        return this;
    }

    public Object getDataHierarchyMeaning() {
        return dataHierarchyMeaning;
    }

    public DataHierarchyDTO setDataHierarchyMeaning(Object dataHierarchyMeaning) {
        this.dataHierarchyMeaning = dataHierarchyMeaning;
        return this;
    }

    public String getDataHierarchyName() {
        return dataHierarchyName;
    }

    public DataHierarchyDTO setDataHierarchyName(String dataHierarchyName) {
        this.dataHierarchyName = dataHierarchyName;
        return this;
    }

    public Long getParentId() {
        return parentId;
    }

    public DataHierarchyDTO setParentId(Long parentId) {
        this.parentId = parentId;
        return this;
    }

    public String getParentName() {
        return parentName;
    }

    public DataHierarchyDTO setParentName(String parentName) {
        this.parentName = parentName;
        return this;
    }

    public Long getTenantId() {
        return tenantId;
    }

    public DataHierarchyDTO setTenantId(Long tenantId) {
        this.tenantId = tenantId;
        return this;
    }

    public String getTenantName() {
        return tenantName;
    }

    public DataHierarchyDTO setTenantName(String tenantName) {
        this.tenantName = tenantName;
        return this;
    }

    public String getValueSourceId() {
        return valueSourceId;
    }

    public DataHierarchyDTO setValueSourceId(String valueSourceId) {
        this.valueSourceId = valueSourceId;
        return this;
    }

    public String getValueSourceName() {
        return valueSourceName;
    }

    public DataHierarchyDTO setValueSourceName(String valueSourceName) {
        this.valueSourceName = valueSourceName;
        return this;
    }

    public String getValueSourceCode() {
        return valueSourceCode;
    }

    public DataHierarchyDTO setValueSourceCode(String valueSourceCode) {
        this.valueSourceCode = valueSourceCode;
        return this;
    }

    public String getDisplayStyle() {
        return displayStyle;
    }

    public void setDisplayStyle(String displayStyle) {
        this.displayStyle = displayStyle;
    }

    public String getDisplayStyleMeaning() {
        return displayStyleMeaning;
    }

    public DataHierarchyDTO setDisplayStyleMeaning(String displayStyleMeaning) {
        this.displayStyleMeaning = displayStyleMeaning;
        return this;
    }

    public Integer getEnabledFlag() {
        return enabledFlag;
    }

    public DataHierarchyDTO setEnabledFlag(Integer enabledFlag) {
        this.enabledFlag = enabledFlag;
        return this;
    }

    public Long getOrderSeq() {
        return orderSeq;
    }

    public DataHierarchyDTO setOrderSeq(Long orderSeq) {
        this.orderSeq = orderSeq;
        return this;
    }

    public String getLevelPath() {
        return levelPath;
    }

    public DataHierarchyDTO setLevelPath(String levelPath) {
        this.levelPath = levelPath;
        return this;
    }

    public Long getObjectVersionNumber() {
        return objectVersionNumber;
    }

    public DataHierarchyDTO setObjectVersionNumber(Long objectVersionNumber) {
        this.objectVersionNumber = objectVersionNumber;
        return this;
    }

    @Override
    public String get_token() {
        return _token;
    }

    @Override
    public void set_token(String tokenValue) {
        this._token = tokenValue;
    }

    @Override
    public Class<? extends SecurityToken> associateEntityClass() {
        return DataHierarchy.class;
    }
}
