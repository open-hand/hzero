package org.hzero.iam.domain.entity;

import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.Transient;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;

import io.choerodon.mybatis.annotation.MultiLanguage;
import io.choerodon.mybatis.annotation.MultiLanguageField;
import org.hibernate.validator.constraints.Range;
import org.hzero.core.base.BaseConstants;
import org.hzero.core.util.Regexs;
import org.hzero.iam.domain.repository.UserGroupAssignRepository;
import org.hzero.iam.domain.repository.UserGroupRepository;
import org.hzero.mybatis.domian.Condition;
import org.hzero.mybatis.util.Sqls;
import org.hzero.starter.keyencrypt.core.Encrypt;

import com.fasterxml.jackson.annotation.JsonInclude;

import io.choerodon.core.exception.CommonException;
import io.choerodon.mybatis.annotation.ModifyAudit;
import io.choerodon.mybatis.annotation.VersionAudit;
import io.choerodon.mybatis.domain.AuditDomain;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

/**
 * 用户组
 *
 * @author xiaoyu.zhao@hand-china.com 2019-01-14 13:44:56
 */
@ApiModel("用户组")
@VersionAudit
@ModifyAudit
@MultiLanguage
@Table(name = "hiam_user_group")
@JsonInclude(JsonInclude.Include.NON_NULL)
public class UserGroup extends AuditDomain {
    public static final String ENCRYPT_KEY = "hiam_user_group";
    public static final String FIELD_USER_GROUP_ID = "userGroupId";
    public static final String FIELD_GROUP_CODE = "groupCode";
    public static final String FIELD_GROUP_NAME = "groupName";
    public static final String FIELD_REMARK = "remark";
    public static final String FIELD_ENABLED_FLAG = "enabledFlag";
    public static final String FIELD_TENANT_ID = "tenantId";

    //
    // 业务方法(按public protected private顺序排列)
    // ------------------------------------------------------------------------------

    /**
     * 校验当前租户查询条件下是否存在相同的用户组编码
     */
    public void validate(UserGroupRepository userGroupRepository) {
        int count = userGroupRepository.selectCountByCondition(Condition.builder(UserGroup.class)
                        .andWhere(Sqls.custom().andEqualTo(UserGroup.FIELD_GROUP_CODE, groupCode)
                                        .andEqualTo(UserGroup.FIELD_TENANT_ID, tenantId))
                        .build());
        if (count != BaseConstants.Digital.ZERO) {
            throw new CommonException("error.user-group.exist");
        }
    }

    /**
     * 校验当前用户组是否已经进行分配
     */
    public void checkUserGroupAssign(UserGroupAssignRepository userGroupAssignRepository) {
        int count = userGroupAssignRepository.selectCountByCondition(Condition.builder(UserGroupAssign.class)
                        .andWhere(Sqls.custom().andEqualTo(UserGroupAssign.FIELD_TENANT_ID, tenantId)
                                        .andEqualTo(UserGroupAssign.FIELD_USER_GROUP_ID, userGroupId)).build());
        if (count != BaseConstants.Digital.ZERO) {
            throw new CommonException("error.user-group.assigned");
        }
    }
    //
    // 数据库字段
    // ------------------------------------------------------------------------------


    @ApiModelProperty("表ID，主键，供其他表做外键")
    @Id
    @GeneratedValue
    @Encrypt
    private Long userGroupId;
    @ApiModelProperty(value = "用户组代码")
    @NotBlank
    @Size(max = 30)
    @Pattern(regexp = Regexs.CODE)
    private String groupCode;
    @ApiModelProperty(value = "用户组名称")
    @NotBlank
    @Size(max = 40)
    @MultiLanguageField
    private String groupName;
    @ApiModelProperty(value = "备注说明")
    private String remark;
    @ApiModelProperty(value = "是否启用。1启用，0未启用")
    @Range(max = 1)
    @NotNull
    private Integer enabledFlag;
    @ApiModelProperty(value = "租户ID，hpfm_tenant.tenant_id")
    @NotNull
    @MultiLanguageField
    private Long tenantId;

    //
    // 非数据库字段
    // ------------------------------------------------------------------------------

    @Transient
    @ApiModelProperty(value = "租户名称")
    private String tenantName;
    @Transient
    @Encrypt
    private Long userId;
    //
    // getter/setter
    // ------------------------------------------------------------------------------

    public Long getUserId() {
        return userId;
    }

    public UserGroup setUserId(Long userId) {
        this.userId = userId;
        return this;
    }

    /**
     * @return 表ID，主键，供其他表做外键
     */
    public Long getUserGroupId() {
        return userGroupId;
    }

    public void setUserGroupId(Long userGroupId) {
        this.userGroupId = userGroupId;
    }

    /**
     * @return 用户组代码
     */
    public String getGroupCode() {
        return groupCode;
    }

    public void setGroupCode(String groupCode) {
        this.groupCode = groupCode;
    }

    /**
     * @return 用户组名称
     */
    public String getGroupName() {
        return groupName;
    }

    public void setGroupName(String groupName) {
        this.groupName = groupName;
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
     * @return 租户ID，hpfm_tenant.tenant_id
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
