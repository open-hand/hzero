package org.hzero.iam.domain.entity;

import java.time.LocalDate;
import java.util.Map;
import java.util.StringJoiner;
import javax.persistence.*;
import javax.validation.constraints.NotNull;

import com.fasterxml.jackson.annotation.JsonIgnore;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import io.choerodon.mybatis.annotation.ModifyAudit;
import io.choerodon.mybatis.annotation.VersionAudit;
import io.choerodon.mybatis.domain.AuditDomain;

import org.hzero.iam.infra.constant.HiamResourceLevel;
import org.hzero.starter.keyencrypt.core.Encrypt;


@ModifyAudit
@VersionAudit
@Table(name = "iam_member_role")
@ApiModel("成员角色")
public class MemberRole extends AuditDomain {

    public static final String ENCRYPT_KEY = "iam_member_role";

    public static final Logger LOGGER = LoggerFactory.getLogger(MemberRole.class);
    public static final String FIELD_ROLE_ID = "roleId";
    public static final String FIELD_MEMBER_TYPE = "memberType";
    public static final String FIELD_MEMBER_ID = "memberId";
    public static final String FIELD_ASSIGN_LEVEL = "assignLevel";
    public static final String FIELD_ASSIGN_LEVEL_VALUE = "assignLevelValue";
    public static final String FIELD_START_DATE_ACTIVE = "startDateActive";
    public static final String FIELD_END_DATE_ACTIVE = "endDateActive";

    //
    // getter/setter
    // ------------------------------------------------------------------------------
    @Id
    @GeneratedValue
    @Encrypt
    private Long id;
    @ApiModelProperty("角色ID，传入角色ID或角色编码")
    @Encrypt
    private Long roleId;
    @ApiModelProperty("成员ID")
    @Encrypt
    private Long memberId;
    @ApiModelProperty("成员类型，用户-user/客户端-client")
    private String memberType;
    private Long sourceId;
    private String sourceType;
    /**
     * 子账户导入-角色导入所需字段
     * 废弃 2019-11-26
     */
    @Column(name = "h_assign_level")
    @ApiModelProperty("分配层级，租户层-organization/组织层-org")
    private String assignLevel;
    @ApiModelProperty("分配层级值，租户层-角色所属租户ID/组织层-组织ID")
    @Column(name = "h_assign_level_value")
    private Long assignLevelValue;
    @ApiModelProperty("有效期起")
    private LocalDate startDateActive;
    @ApiModelProperty("有效期止")
    private LocalDate endDateActive;

    /**
     * 子账户导入-角色导入所需字段
     * 废弃 since 2019-11-26
     */
    @ApiModelProperty("角色编码")
    @Transient
    private String roleCode;
    /**
     * 子账户导入所需地段
     * ADD 2019-12-10
     */
    @ApiModelProperty("角色路径")
    @Transient
    private String levelPath;
    @ApiModelProperty("角色名称")
    @Transient
    private String roleName;
    /**
     * 子账户导入-角色导入所需字段
     */
    @ApiModelProperty("用户名称")
    @Transient
    @NotNull
    private String loginName;
    /**
     * 子账户导入-角色导入所需字段
     */
    @ApiModelProperty("真实名称")
    @Transient
    private String realName;

    @Transient
    @JsonIgnore
    private Role role;
    @Transient
    @JsonIgnore
    private User user;
    @Transient
    @JsonIgnore
    private Client client;

    /**
     * 额外的参数信息
     */
    @Transient
    @JsonIgnore
    private Map<String, Object> additionalParams;

    public MemberRole() {

    }

    public MemberRole(Long roleId, Long memberId, String memberType, Long sourceId, String sourceType) {
        this.roleId = roleId;
        this.memberId = memberId;
        this.memberType = memberType;
        this.sourceId = sourceId;
        this.sourceType = sourceType;
        // 固定
        this.assignLevel = HiamResourceLevel.ORGANIZATION.value();
        this.assignLevelValue = sourceId;
    }

    public MemberRole(Long roleId, Long memberId, String memberType) {
        this.roleId = roleId;
        this.memberId = memberId;
        this.memberType = memberType;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getRoleId() {
        return roleId;
    }

    public void setRoleId(Long roleId) {
        this.roleId = roleId;
    }

    public Long getMemberId() {
        return memberId;
    }

    public void setMemberId(Long memberId) {
        this.memberId = memberId;
    }

    public String getMemberType() {
        return memberType;
    }

    public void setMemberType(String memberType) {
        this.memberType = memberType;
    }

    public Long getSourceId() {
        return sourceId;
    }

    public void setSourceId(Long sourceId) {
        this.sourceId = sourceId;
    }

    public String getSourceType() {
        return sourceType;
    }

    public void setSourceType(String sourceType) {
        this.sourceType = sourceType;
    }

    /**
     * @return 角色分配层级
     */
    public String getAssignLevel() {
        return assignLevel;
    }

    public void setAssignLevel(String assignLevel) {
        this.assignLevel = assignLevel;
    }

    /**
     * @return 角色分配层级值
     */
    public Long getAssignLevelValue() {
        return assignLevelValue;
    }

    public void setAssignLevelValue(Long assignLevelValue) {
        this.assignLevelValue = assignLevelValue;
    }

    public String getRoleCode() {
        return roleCode;
    }

    public MemberRole setRoleCode(String roleCode) {
        this.roleCode = roleCode;
        return this;
    }

    public String getLevelPath() {
        return levelPath;
    }

    public MemberRole setLevelPath(String levelPath) {
        this.levelPath = levelPath;
        return this;
    }

    public String getRoleName() {
        return roleName;
    }

    public void setRoleName(String roleName) {
        this.roleName = roleName;
    }

    public String getLoginName() {
        return loginName;
    }

    public void setLoginName(String loginName) {
        this.loginName = loginName;
    }

    public String getRealName() {
        return realName;
    }

    public void setRealName(String realName) {
        this.realName = realName;
    }

    public Role getRole() {
        return role;
    }

    public void setRole(Role role) {
        this.role = role;
    }

    public LocalDate getStartDateActive() {
        return startDateActive;
    }

    public void setStartDateActive(LocalDate startDateActive) {
        this.startDateActive = startDateActive;
    }

    public LocalDate getEndDateActive() {
        return endDateActive;
    }

    public void setEndDateActive(LocalDate endDateActive) {
        this.endDateActive = endDateActive;
    }

    public Map<String, Object> getAdditionalParams() {
        return additionalParams;
    }

    public void setAdditionalParams(Map<String, Object> additionalParams) {
        this.additionalParams = additionalParams;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public Client getClient() {
        return client;
    }

    public void setClient(Client client) {
        this.client = client;
    }

    @Override
    public String toString() {
        return new StringJoiner(", ", MemberRole.class.getSimpleName() + "[", "]")
                .add("id=" + id)
                .add("roleId=" + roleId)
                .add("levelPath=" + levelPath)
                .add("roleCode=" + roleCode)
                .add("memberId=" + memberId)
                .add("memberType='" + memberType + "'")
                .add("sourceId=" + sourceId)
                .add("sourceType='" + sourceType + "'")
                .add("assignLevel='" + assignLevel + "'")
                .add("assignLevelValue=" + assignLevelValue)
                .add("startDateActive=" + startDateActive)
                .add("endDateActive=" + endDateActive)
                .add("additionalParams=" + additionalParams)
                .toString();
    }
}
