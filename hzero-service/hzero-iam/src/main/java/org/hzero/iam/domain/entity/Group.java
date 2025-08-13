package org.hzero.iam.domain.entity;

import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

import org.hzero.mybatis.common.query.Where;
import org.hzero.starter.keyencrypt.core.Encrypt;

import com.fasterxml.jackson.annotation.JsonInclude;

import io.choerodon.mybatis.annotation.ModifyAudit;
import io.choerodon.mybatis.annotation.VersionAudit;
import io.choerodon.mybatis.domain.AuditDomain;

/**
 * 集团信息
 *
 * @author gaokuo.dai@hand-china.com 2018-07-04 19:49:15
 */
@VersionAudit
@ModifyAudit
@Table(name = "hpfm_group")
@JsonInclude(JsonInclude.Include.NON_NULL)
public class Group extends AuditDomain {

    public static final String ENCRYPT_KEY = "hpfm_group";

    public static final String FIELD_GROUP_ID = "groupId";
    public static final String FIELD_GROUP_NUM = "groupNum";
    public static final String FIELD_GROUP_NAME = "groupName";
    public static final String FIELD_TENANT_ID = "tenantId";
    public static final String FIELD_UNIT_ID = "unitId";
    public static final String FIELD_SOURCE_KEY = "sourceKey";
    public static final String FIELD_SOURCE_CODE = "sourceCode";
    public static final String FIELD_ENABLED_FLAG = "enabledFlag";

    //
    // 业务方法(按public protected private顺序排列)
    // ------------------------------------------------------------------------------

    //
    // 数据库字段
    // ------------------------------------------------------------------------------

    @Id
    @GeneratedValue
    @Encrypt
    private Long groupId;
    @Size(max = 30)
    private String groupNum;
    @NotEmpty
    @Size(max = 150)
    private String groupName;
    @NotNull
    @Where
    private Long tenantId;
    @Encrypt
    private Long unitId;
    private String sourceKey;
    @NotEmpty
    private String sourceCode;
    @NotNull
    private Integer enabledFlag;

    //
    // 非数据库字段
    // ------------------------------------------------------------------------------

    //
    // getter/setter
    // ------------------------------------------------------------------------------

    /**
     * @return 表ID，主键，供其他表做外键
     */
    public Long getGroupId() {
        return groupId;
    }

    public void setGroupId(Long groupId) {
        this.groupId = groupId;
    }

    /**
     * @return 集团编码，hpfm_hr_unit.unit_code
     */
    public String getGroupNum() {
        return groupNum;
    }

    public void setGroupNum(String groupNum) {
        this.groupNum = groupNum;
    }

    /**
     * @return 集团名称
     */
    public String getGroupName() {
        return groupName;
    }

    public void setGroupName(String groupName) {
        this.groupName = groupName;
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

    /**
     * @return 组织id
     */
    public Long getUnitId() {
        return unitId;
    }

    public void setUnitId(Long unitId) {
        this.unitId = unitId;
    }

    /**
     * @return 源数据key
     */
    public String getSourceKey() {
        return sourceKey;
    }

    public void setSourceKey(String sourceKey) {
        this.sourceKey = sourceKey;
    }

    /**
     * @return 来源, 值集：HPFM.DATA_SOURCE
     */
    public String getSourceCode() {
        return sourceCode;
    }

    public void setSourceCode(String sourceCode) {
        this.sourceCode = sourceCode;
    }

    /**
     * @return 启用标识
     */
    public Integer getEnabledFlag() {
        return enabledFlag;
    }

    public void setEnabledFlag(Integer enabledFlag) {
        this.enabledFlag = enabledFlag;
    }

    @Override
    public String toString() {
        return "Group{" +
                "groupId=" + groupId +
                ", groupNum='" + groupNum + '\'' +
                ", groupName='" + groupName + '\'' +
                ", tenantId=" + tenantId +
                ", unitId=" + unitId +
                ", sourceKey='" + sourceKey + '\'' +
                ", sourceCode='" + sourceCode + '\'' +
                ", enabledFlag=" + enabledFlag +
                '}';
    }
}
