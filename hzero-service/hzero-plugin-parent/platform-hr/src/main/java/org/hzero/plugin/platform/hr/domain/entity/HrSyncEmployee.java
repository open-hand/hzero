package org.hzero.plugin.platform.hr.domain.entity;

import java.util.List;

import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.Transient;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

import com.fasterxml.jackson.annotation.JsonInclude;

import io.choerodon.mybatis.annotation.ModifyAudit;
import io.choerodon.mybatis.annotation.VersionAudit;
import io.choerodon.mybatis.domain.AuditDomain;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

/**
 * HR员工数据同步
 *
 * @author zifeng.ding@hand-china.com 2019-12-25 09:35:06
 */
@ApiModel("HR员工数据同步")
@VersionAudit
@ModifyAudit
@JsonInclude(value = JsonInclude.Include.NON_NULL)
@Table(name = "hpfm_hr_sync_employee")
public class HrSyncEmployee extends AuditDomain {

    public static final String FIELD_SYNC_EMPLOYEE_ID = "syncEmployeeId";
    public static final String FIELD_SYNC_TYPE_CODE = "syncTypeCode";
    public static final String FIELD_USERID = "userid";
    public static final String FIELD_NAME = "name";
    public static final String FIELD_MOBILE = "mobile";
    public static final String FIELD_POSITION = "position";
    public static final String FIELD_GENDER = "gender";
    public static final String FIELD_EMAIL = "email";
    public static final String FIELD_TENANT_ID = "tenantId";

    //
    // 业务方法(按public protected private顺序排列)
    // ------------------------------------------------------------------------------

    //
    // 数据库字段
    // ------------------------------------------------------------------------------


    @ApiModelProperty("表ID，主键，供其他表做外键")
    @Id
    @GeneratedValue
    private Long syncEmployeeId;
    @ApiModelProperty(value = "同步类型，值集HPFM.HR_SYNC_TYPE DD:钉钉 WX:微信", required = true)
    @NotBlank
    private String syncTypeCode;
    @ApiModelProperty(value = "外部用户ID", required = true)
    @NotBlank
    private String userid;
    @ApiModelProperty(value = "内部员工编码", required = true)
    @NotBlank
    private String employeeNum;
    @ApiModelProperty(value = "名称", required = true)
    @NotBlank
    private String name;
    @ApiModelProperty(value = "电话")
    private String mobile;
    @ApiModelProperty(value = "职位")
    private String position;
    @ApiModelProperty(value = "性别")
    private Integer gender;
    @ApiModelProperty(value = "邮箱")
    private String email;
    @ApiModelProperty(value = "租户ID,hpfm_tenant.tenant_id", required = true)
    @NotNull
    private Long tenantId;

    //
    // 非数据库字段
    // ------------------------------------------------------------------------------
    /**
     * 员工所在部门
     */
    @Transient
    private Long departmentId;
    /**
     * 员工所属的所有部门
     */
    @Transient
    private List<Long> departmentIds;

    /**
     * 主岗标志
     */
    @Transient
    private Integer primaryPositionFlag;

    /**
     * 对象的同步操作，新增：create、更新：update、删除：delete
     */
    @Transient
    private String syncType;

    /**
     * 增量同步时是否已经把平台部门id映射成第三方部门id标志，与departmentIds一一对应
     */
    @Transient
    private List<Boolean> isDepartIdsMap;

    //
    // getter/setter
    // ------------------------------------------------------------------------------

    /**
     * @return 表ID，主键，供其他表做外键
     */
    public Long getSyncEmployeeId() {
        return syncEmployeeId;
    }

    public HrSyncEmployee setSyncEmployeeId(Long syncEmployeeId) {
        this.syncEmployeeId = syncEmployeeId;
        return this;
    }

    /**
     * @return 同步类型，值集HPFM.HR_SYNC_TYPE DD:钉钉 WX:微信
     */
    public String getSyncTypeCode() {
        return syncTypeCode;
    }

    public HrSyncEmployee setSyncTypeCode(String syncTypeCode) {
        this.syncTypeCode = syncTypeCode;
        return this;
    }

    /**
     * @return 用户ID
     */
    public String getUserid() {
        return userid;
    }

    public HrSyncEmployee setUserid(String userid) {
        this.userid = userid;
        return this;
    }

    /**
     * @return 名称
     */
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    /**
     * @return 电话
     */
    public String getMobile() {
        return mobile;
    }

    public void setMobile(String mobile) {
        this.mobile = mobile;
    }

    /**
     * @return 职位
     */
    public String getPosition() {
        return position;
    }

    public void setPosition(String position) {
        this.position = position;
    }

    /**
     * @return 性别
     */
    public Integer getGender() {
        return gender;
    }

    public HrSyncEmployee setGender(Integer gender) {
        this.gender = gender;
        return this;
    }

    /**
     * @return 邮箱
     */
    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    /**
     * @return 租户ID, hpfm_tenant.tenant_id
     */
    public Long getTenantId() {
        return tenantId;
    }

    public HrSyncEmployee setTenantId(Long tenantId) {
        this.tenantId = tenantId;
        return this;
    }

    public Long getDepartmentId() {
        return departmentId;
    }

    public void setDepartmentId(Long departmentId) {
        this.departmentId = departmentId;
    }

    public List<Long> getDepartmentIds() {
        return departmentIds;
    }

    public HrSyncEmployee setDepartmentIds(List<Long> departmentIds) {
        this.departmentIds = departmentIds;
        return this;
    }

    public String getSyncType() {
        return syncType;
    }

    public HrSyncEmployee setSyncType(String syncType) {
        this.syncType = syncType;
        return this;
    }

    public Integer getPrimaryPositionFlag() {
        return primaryPositionFlag;
    }

    public HrSyncEmployee setPrimaryPositionFlag(Integer primaryPositionFlag) {
        this.primaryPositionFlag = primaryPositionFlag;
        return this;
    }

    public List<Boolean> getIsDepartIdsMap() {
        return isDepartIdsMap;
    }

    public HrSyncEmployee setIsDepartIdsMap(List<Boolean> isDepartIdsMap) {
        this.isDepartIdsMap = isDepartIdsMap;
        return this;
    }

    public String getEmployeeNum() {
        return employeeNum;
    }

    public HrSyncEmployee setEmployeeNum(String employeeNum) {
        this.employeeNum = employeeNum;
        return this;
    }
}
