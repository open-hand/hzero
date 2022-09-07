package org.hzero.platform.domain.entity;

import java.util.Arrays;

import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.Transient;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;

import org.hibernate.validator.constraints.Length;
import org.hzero.boot.platform.lov.annotation.LovValue;
import org.hzero.core.util.Regexs;
import org.hzero.mybatis.annotation.Unique;
import org.hzero.starter.keyencrypt.core.Encrypt;

import com.fasterxml.jackson.annotation.JsonInclude;

import io.choerodon.mybatis.annotation.ModifyAudit;
import io.choerodon.mybatis.annotation.MultiLanguage;
import io.choerodon.mybatis.annotation.MultiLanguageField;
import io.choerodon.mybatis.annotation.VersionAudit;
import io.choerodon.mybatis.domain.AuditDomain;

import io.swagger.annotations.ApiModel;

/**
 * 数据层级配置
 *
 * @author qingsheng.chen@hand-china.com 2019-08-14 09:12:07
 */
@ApiModel("数据层级配置")
@VersionAudit
@ModifyAudit
@MultiLanguage
@Table(name = "hpfm_data_hierarchy")
@JsonInclude(JsonInclude.Include.NON_NULL)
public class DataHierarchy extends AuditDomain {

    public static final String FIELD_DATA_HIERARCHY_ID = "dataHierarchyId";
    public static final String FIELD_DATA_HIERARCHY_CODE = "dataHierarchyCode";
    public static final String FIELD_DATA_HIERARCHY_NAME = "dataHierarchyName";
    public static final String FIELD_PARENT_ID = "parentId";
    public static final String FIELD_TENANT_ID = "tenantId";
    public static final String FIELD_VALUE_SOURCE_ID = "valueSourceId";
    public static final String FIELD_ENABLED_FLAG = "enabledFlag";
    public static final String FIELD_DISPLAY_STYLE = "displayStyle";
    public static final String FIELD_LEVEL_PATH = "levelPath";
    public static final String FIELD_ORDER_SEQ = "orderSeq";

    private static final String[] UPDATABLE_FIELD = new String[]{FIELD_DATA_HIERARCHY_NAME, FIELD_VALUE_SOURCE_ID, FIELD_ENABLED_FLAG, FIELD_ORDER_SEQ, FIELD_DISPLAY_STYLE};


    //
    // 业务方法(按public protected private顺序排列)
    // ------------------------------------------------------------------------------

    //
    // 数据库字段
    // ------------------------------------------------------------------------------

    @Id
    @GeneratedValue
    @Encrypt
    private Long dataHierarchyId;
    @NotBlank
    @Pattern(regexp = Regexs.CODE_UPPER)
    @Length(max = 30)
    @Unique
    private String dataHierarchyCode;
    @NotBlank
    @Length(max = 60)
    @MultiLanguageField
    private String dataHierarchyName;
    @Encrypt
    private Long parentId;
    @NotNull
    @Unique
    @MultiLanguageField
    private Long tenantId;
    @NotNull
    @Encrypt
    private Long valueSourceId;
    @NotNull
    private Integer enabledFlag;
    @NotBlank
    @LovValue(lovCode = "HPFM.DATA_HIERARCHY.DISPLAY_STYLE")
    private String displayStyle;
    private String levelPath;
    @NotNull
    private Long orderSeq;

    //
    // 非数据库字段
    // ------------------------------------------------------------------------------
    @Transient
    private String displayStyleMeaning;
    //
    // getter/setter
    // ------------------------------------------------------------------------------
    public static String[] getUpdatableField() {
        return Arrays.copyOf(UPDATABLE_FIELD, UPDATABLE_FIELD.length);
    }


    /**
     * @return 表ID，主键，供其他表做外键
     */
    public Long getDataHierarchyId() {
        return dataHierarchyId;
    }

    public DataHierarchy setDataHierarchyId(Long dataHierarchyId) {
        this.dataHierarchyId = dataHierarchyId;
        return this;
    }

    /**
     * @return 数据层级编码
     */
    public String getDataHierarchyCode() {
        return dataHierarchyCode;
    }

    public DataHierarchy setDataHierarchyCode(String dataHierarchyCode) {
        this.dataHierarchyCode = dataHierarchyCode;
        return this;
    }

    /**
     * @return 数据层级名称
     */
    public String getDataHierarchyName() {
        return dataHierarchyName;
    }

    public DataHierarchy setDataHierarchyName(String dataHierarchyName) {
        this.dataHierarchyName = dataHierarchyName;
        return this;
    }

    /**
     * @return 上级数据层级ID，hpfm_data_hierarchy.data_hierarchy_id
     */
    public Long getParentId() {
        return parentId;
    }

    public DataHierarchy setParentId(Long parentId) {
        this.parentId = parentId;
        return this;
    }

    /**
     * @return 租户ID，hpfm_tenant.tenant_id
     */
    public Long getTenantId() {
        return tenantId;
    }

    public DataHierarchy setTenantId(Long tenantId) {
        this.tenantId = tenantId;
        return this;
    }

    /**
     * @return 值来源
     */
    public Long getValueSourceId() {
        return valueSourceId;
    }

    public DataHierarchy setValueSourceId(Long valueSourceId) {
        this.valueSourceId = valueSourceId;
        return this;
    }

    /**
     * @return 是否启用。1启用，0未启用
     */
    public Integer getEnabledFlag() {
        return enabledFlag;
    }

    public DataHierarchy setEnabledFlag(Integer enabledFlag) {
        this.enabledFlag = enabledFlag;
        return this;
    }

    public String getDisplayStyle() {
        return displayStyle;
    }

    public DataHierarchy setDisplayStyle(String displayStyle) {
        this.displayStyle = displayStyle;
        return this;
    }

    /**
     * @return 层级路径
     */
    public String getLevelPath() {
        return levelPath;
    }

    public DataHierarchy setLevelPath(String levelPath) {
        this.levelPath = levelPath;
        return this;
    }

    /**
     * @return 排序号
     */
    public Long getOrderSeq() {
        return orderSeq;
    }

    public DataHierarchy setOrderSeq(Long orderSeq) {
        this.orderSeq = orderSeq;
        return this;
    }

    public String getDisplayStyleMeaning() {
        return displayStyleMeaning;
    }

    public DataHierarchy setDisplayStyleMeaning(String displayStyleMeaning) {
        this.displayStyleMeaning = displayStyleMeaning;
        return this;
    }
}
