package org.hzero.platform.domain.entity;

import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.Transient;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;

import com.fasterxml.jackson.annotation.JsonInclude;
import org.hzero.platform.domain.repository.DataGroupRepository;
import org.hzero.platform.infra.constant.HpfmMsgCodeConstants;

import io.choerodon.core.exception.CommonException;
import io.choerodon.mybatis.annotation.ModifyAudit;
import io.choerodon.mybatis.annotation.VersionAudit;
import io.choerodon.mybatis.domain.AuditDomain;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import org.hzero.starter.keyencrypt.core.Encrypt;

/**
 * 数据组定义
 *
 * @author jianbo.li@hand-china.com 2019-07-17 15:48:48
 */
@ApiModel("数据组定义")
@VersionAudit
@ModifyAudit
@Table(name = "hpfm_data_group")
@JsonInclude(JsonInclude.Include.NON_NULL)
public class DataGroup extends AuditDomain {

    public static final String FIELD_GROUP_ID = "groupId";
    public static final String FIELD_GROUP_CODE = "groupCode";
    public static final String FIELD_GROUP_NAME = "groupName";
    public static final String FIELD_TENANT_ID = "tenantId";
    public static final String FIELD_ENABLED_FLAG = "enabledFlag";
    public static final String FIELD_REMARK = "remark";


    //
    // 业务方法(按public protected private顺序排列)
    // ------------------------------------------------------------------------------


    /**
     * 校验唯一性索引
     *
     * @param dataGroupRepository
     */
    public void validUniqueIndex(DataGroupRepository dataGroupRepository) {
        DataGroup uniqueIndex = new DataGroup();
        uniqueIndex.setGroupCode(groupCode);
        uniqueIndex.setTenantId(tenantId);
        if (dataGroupRepository.selectOne(uniqueIndex) != null) {
            throw new CommonException(HpfmMsgCodeConstants.ERROR_GROUP_DATA_EXISTS,
                    groupCode);
        }
    }

    //
    // 数据库字段
    // ------------------------------------------------------------------------------


    @ApiModelProperty("表ID，主键，供其他表做外键")
    @Id
    @GeneratedValue
    @Encrypt
    private Long groupId;
    @ApiModelProperty(value = "数据组代码")
    @NotBlank
    @Pattern(regexp = "^[A-Z0-9][A-Z0-9\\./\\-_]*$")
    private String groupCode;
    @ApiModelProperty(value = "数据组名称")
    private String groupName;
    @ApiModelProperty(value = "租户ID,hpfm_tenant.tenant_id")
    @NotNull
    private Long tenantId;
    @ApiModelProperty(value = "是否启用。1启用，0未启用")
    @NotNull
    private Integer enabledFlag;
    @ApiModelProperty(value = "备注说明")
    private String remark;

    //
    // 非数据库字段
    // ------------------------------------------------------------------------------

    @Transient
    private String tenantName;
    /**
     * 排除查询的id
     */
    @Transient
    private Long[] excludeIds;

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
     * @return 数据组代码
     */
    public String getGroupCode() {
        return groupCode;
    }

    public void setGroupCode(String groupCode) {
        this.groupCode = groupCode;
    }

    /**
     * @return 数据组名称
     */
    public String getGroupName() {
        return groupName;
    }

    public void setGroupName(String groupName) {
        this.groupName = groupName;
    }

    /**
     * @return 租户ID, hpfm_tenant.tenant_id
     */
    public Long getTenantId() {
        return tenantId;
    }

    public void setTenantId(Long tenantId) {
        this.tenantId = tenantId;
    }

    /**
     * @return 是否启用。1启用，0未启用
     */
    public Integer getEnabledFlag() {
        return enabledFlag;
    }

    public void setEnabledFlag(Integer enabledFlag) {
        this.enabledFlag = enabledFlag;
    }

    /**
     * @return 备注说明
     */
    public String getRemark() {
        return remark;
    }

    public void setRemark(String remark) {
        this.remark = remark;
    }

    public String getTenantName() {
        return tenantName;
    }

    public void setTenantName(String tenantName) {
        this.tenantName = tenantName;
    }

    public Long[] getExcludeIds() {
        return excludeIds;
    }

    public void setExcludeIds(Long[] excludeIds) {
        this.excludeIds = excludeIds;
    }
}
