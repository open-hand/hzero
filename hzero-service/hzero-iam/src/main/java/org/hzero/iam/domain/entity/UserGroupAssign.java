package org.hzero.iam.domain.entity;

import java.util.List;

import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.Transient;
import javax.validation.constraints.NotNull;

import org.hibernate.validator.constraints.Range;
import org.hzero.core.base.BaseConstants;
import org.hzero.iam.domain.repository.UserGroupAssignRepository;
import org.hzero.mybatis.domian.Condition;
import org.hzero.mybatis.util.Sqls;
import org.hzero.starter.keyencrypt.core.Encrypt;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;

import io.choerodon.core.exception.CommonException;
import io.choerodon.mybatis.annotation.ModifyAudit;
import io.choerodon.mybatis.annotation.VersionAudit;
import io.choerodon.mybatis.domain.AuditDomain;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

/**
 * 用户组分配
 *
 * @author xiaoyu.zhao@hand-china.com 2019-01-15 16:06:11
 */
@ApiModel("用户组分配")
@VersionAudit
@ModifyAudit
@Table(name = "hiam_user_group_assign")
@JsonInclude(JsonInclude.Include.NON_NULL)
public class UserGroupAssign extends AuditDomain {
    public static final String ENCRYPT_KEY = "hiam_user_group_assign";
    public static final String FIELD_ASSIGN_ID = "assignId";
    public static final String FIELD_USER_ID = "userId";
    public static final String FIELD_USER_GROUP_ID = "userGroupId";
    public static final String FIELD_DEFAULT_FLAG = "defaultFlag";
    public static final String FIELD_TENANT_ID = "tenantId";

    //
    // 业务方法(按public protected private顺序排列)
    // ------------------------------------------------------------------------------

    /**
     * 校验数据是否重复
     *
     * @param userGroupAssignRepository 用户组分配资源库
     */
    public void validate(UserGroupAssignRepository userGroupAssignRepository) {
        int count = userGroupAssignRepository.selectCountByCondition(Condition.builder(UserGroupAssign.class)
                        .andWhere(Sqls.custom()
                                        .andEqualTo(UserGroupAssign.FIELD_TENANT_ID, tenantId)
                                        .andEqualTo(UserGroupAssign.FIELD_USER_ID, userId)
                                        .andEqualTo(UserGroupAssign.FIELD_USER_GROUP_ID, userGroupId)
                                        .andNotEqualTo(UserGroupAssign.FIELD_ASSIGN_ID, assignId, true)
                        )
                        .build());
        if (count != BaseConstants.Digital.ZERO) {
            throw new CommonException("error.user-group-assign.exist");
        }
    }
    //
    // 数据库字段
    // ------------------------------------------------------------------------------

    @ApiModelProperty("表ID，主键，供其他表做外键")
    @Id
    @GeneratedValue
    @Encrypt
    private Long assignId;
    @ApiModelProperty(value = "用户ID，iam_user.user_id")
    @Encrypt
    private Long userId;
    @ApiModelProperty(value = "hiam_user_group.user_group_id")
    @Encrypt
    private Long userGroupId;
    @ApiModelProperty(value = "是否默认 1:默认 0:不默认")
    @NotNull
    @Range(max = 1)
    private Integer defaultFlag;
    @ApiModelProperty(value = "租户ID，hpfm_tenant.tenant_id")
    @NotNull
    private Long tenantId;

    //
    // 非数据库字段
    // ------------------------------------------------------------------------------

    @Transient
    @ApiModelProperty(value = "租户名称")
    private String tenantName;
    @Transient
    @ApiModelProperty(value = "用户组编码")
    private String groupCode;
    @Transient
    @ApiModelProperty(value = "用户账号")
    private String loginName;
    @Transient
    @ApiModelProperty(value = "用户真实姓名")
    private String realName;
    @Transient
    @ApiModelProperty(value = "用户状态")
    @JsonIgnore
    private Boolean enabled;
    @ApiModelProperty(value = "用户组名称")
    @Transient
    private String groupName;
    @ApiModelProperty(value = "用户组状态")
    @Transient
    private Integer enabledFlag;
    @Transient
    @ApiModelProperty(value = "查询排除Id")
    private List<Long> excludeIds;


    //
    // getter/setter
    // ------------------------------------------------------------------------------



    public String getRealName() {
        return realName;
    }

    public void setRealName(String realName) {
        this.realName = realName;
    }

    public Boolean getEnabled() {
        return enabled;
    }

    public void setEnabled(Boolean enabled) {
        this.enabled = enabled;
    }

    public Integer getEnabledFlag() {
        return enabledFlag;
    }

    public void setEnabledFlag(Integer enabledFlag) {
        this.enabledFlag = enabledFlag;
    }

    /**
     * @return 表ID，主键，供其他表做外键
     */
    public Long getAssignId() {
        return assignId;
    }

    public void setAssignId(Long assignId) {
        this.assignId = assignId;
    }

    /**
     * @return 用户ID，iam_user.user_id
     */
    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    /**
     * @return hiam_user_group.user_group_id
     */
    public Long getUserGroupId() {
        return userGroupId;
    }

    public void setUserGroupId(Long userGroupId) {
        this.userGroupId = userGroupId;
    }

    /**
     * @return 是否默认 1:默认 0:不默认
     */
    public Integer getDefaultFlag() {
        defaultFlag = defaultFlag == null ? BaseConstants.Flag.NO : defaultFlag;
        return defaultFlag;
    }

    public void setDefaultFlag(Integer defaultFlag) {
        this.defaultFlag = defaultFlag == null ? BaseConstants.Flag.NO : defaultFlag;
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

    public String getGroupCode() {
        return groupCode;
    }

    public void setGroupCode(String groupCode) {
        this.groupCode = groupCode;
    }

    public String getLoginName() {
        return loginName;
    }

    public void setLoginName(String loginName) {
        this.loginName = loginName;
    }

    public String getGroupName() {
        return groupName;
    }

    public void setGroupName(String groupName) {
        this.groupName = groupName;
    }

    public List<Long> getExcludeIds() {
        return excludeIds;
    }

    public void setExcludeIds(List<Long> excludeIds) {
        this.excludeIds = excludeIds;
    }


}
