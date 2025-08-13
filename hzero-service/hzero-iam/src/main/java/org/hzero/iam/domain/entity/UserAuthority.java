package org.hzero.iam.domain.entity;

import java.util.Date;
import java.util.List;

import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.Transient;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

import org.apache.commons.lang3.StringUtils;
import org.hzero.core.base.BaseConstants;
import org.hzero.iam.domain.repository.UserAuthorityRepository;
import org.hzero.iam.infra.constant.Constants;
import org.hzero.starter.keyencrypt.core.Encrypt;

import com.fasterxml.jackson.annotation.JsonIgnore;

import io.choerodon.mybatis.annotation.ModifyAudit;
import io.choerodon.mybatis.annotation.VersionAudit;
import io.choerodon.mybatis.domain.AuditDomain;

/**
 * 用户权限管理表
 *
 * @author liang.jin@hand-china.com 2018-07-31 15:45:42
 */
@VersionAudit
@ModifyAudit
@Table(name = "hiam_user_authority")
public class UserAuthority extends AuditDomain {

    public static final String ENCRYPT_KEY = "hiam_user_authority";

    public static final String USER_AUTHORITY_NOT_NULL = "error.userAuthority.info.not.null";
    public static final String USER_ID_REQUIRED = "error.userAuthority.userId.required";
    public static final String TENANT_ID_REQUIRED = "error.userAuthority.tenantId.required";
    public static final String AUTHORITY_TYPE_CODE_REQUIRED = "error.userAuthority.authorityTypeCode.required";

    public static final String FIELD_AUTHORITY_ID = "authorityId";
    public static final String FIELD_USER_ID = "userId";
    public static final String FIELD_TENANT_ID = "tenantId";
    public static final String FIELD_AUTHORITY_TYPE_CODE = "authorityTypeCode";
    public static final String FIELD_INCLUDE_ALL_FLAG = "includeAllFlag";
    public static final String FIELD_DATASOURCE = "dataSource";

    //
    // 业务方法(按public protected private顺序排列)
    // ------------------------------------------------------------------------------

    public UserAuthority() {
    }

    public UserAuthority(Long userId, Long tenantId, String authorityTypeCode) {
        this.userId = userId;
        this.tenantId = tenantId;
        this.authorityTypeCode = authorityTypeCode;
    }

    /**
     * 实体类初始化方法
     *
     * @param userId
     * @param tenantId
     * @param authorityTypeCode
     * @param includeAllFlag
     * @return
     */
    public UserAuthority init(Long userId, Long tenantId, String authorityTypeCode, Integer includeAllFlag) {
        UserAuthority userAuthority = new UserAuthority();
        userAuthority.setUserId(userId);
        userAuthority.setTenantId(tenantId);
        userAuthority.setAuthorityTypeCode(authorityTypeCode);
        userAuthority.setIncludeAllFlag(includeAllFlag);
        return userAuthority;
    }


    /**
     * 根据 租户ID、用户ID、权限类型代码、查询头表ID没查到则新增后返回主键
     *
     * @param tenantId
     * @param userId
     * @param authorityTypeCode
     * @return
     */
    public Long getAuthorityIdByTenantIdAndUserId(Long tenantId, Long userId, String authorityTypeCode, UserAuthorityRepository userAuthorityRepository) {
        UserAuthority userAuthority = new UserAuthority();
        userAuthority.setTenantId(tenantId);
        userAuthority.setUserId(userId);
        userAuthority.setAuthorityTypeCode(authorityTypeCode);
        List<UserAuthority> result = userAuthorityRepository.select(userAuthority);
        if (result.size() > 0) {
            return result.get(0).getAuthorityId();
        } else {
            userAuthority.setIncludeAllFlag(BaseConstants.Flag.NO);
            userAuthority.setDataSource(Constants.SecGrpAssign.DEFAULT_DATA_SOURCE);
            userAuthorityRepository.insert(userAuthority);
            return userAuthority.getAuthorityId();
        }
    }


    //
    // 数据库字段
    // ------------------------------------------------------------------------------


    @Id
    @GeneratedValue
    @Encrypt
    private Long authorityId;
    @NotNull
    private Long userId;
    @NotNull
    private Long tenantId;
    @NotBlank
    private String authorityTypeCode;
    @NotNull
    private Integer includeAllFlag;

    private String dataSource;

    //
    // 非数据库字段
    // ------------------------------------------------------------------------------
    @Transient
    private String loginName;
    @Transient
    private String realName;
    @Transient
    private String tenantName;


    //
    // getter/setter
    // ------------------------------------------------------------------------------


    public String getDataSource() {
        return dataSource;
    }

    public void setDataSource(String dataSource) {
        this.dataSource = dataSource;
    }

    /**
     * @return 表ID，主键，供其他表做外键
     */
    public Long getAuthorityId() {
        return authorityId;
    }

    public void setAuthorityId(Long authorityId) {
        this.authorityId = authorityId;
    }

    /**
     * @return 用户ID，HIAM.HIAM_USER
     */
    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    /**
     * @return 租户ID，HPFM.HPFM_TENANT
     */
    public Long getTenantId() {
        return tenantId;
    }

    public void setTenantId(Long tenantId) {
        this.tenantId = tenantId;
    }

    /**
     * @return 权限类型代码，HIAM.USER_AUTHORITY_TYPE_CODE
     */
    public String getAuthorityTypeCode() {
        return authorityTypeCode;
    }

    public void setAuthorityTypeCode(String authorityTypeCode) {
        this.authorityTypeCode = authorityTypeCode;
    }

    /**
     * @return 是否包含所有标识
     */
    public Integer getIncludeAllFlag() {
        return includeAllFlag;
    }

    public void setIncludeAllFlag(Integer includeAllFlag) {
        this.includeAllFlag = includeAllFlag;
    }

    @Override
    @JsonIgnore
    public Date getCreationDate() {
        return super.getCreationDate();
    }

    @Override
    @JsonIgnore
    public Long getCreatedBy() {
        return super.getCreatedBy();
    }

    @Override
    @JsonIgnore
    public Date getLastUpdateDate() {
        return super.getLastUpdateDate();
    }

    @Override
    @JsonIgnore
    public Long getLastUpdatedBy() {
        return super.getLastUpdatedBy();
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

    public String getTenantName() {
        return tenantName;
    }

    public void setTenantName(String tenantName) {
        this.tenantName = tenantName;
    }


    /**
     * 包含默认数据权限
     *
     * @return true: 包含
     */
    public boolean containDefaultDataSource() {
        return StringUtils.contains(this.dataSource, Constants.SecGrpAssign.DEFAULT_DATA_SOURCE);
    }

    /**
     * 包含安全组的数据权限
     *
     * @return true:包含
     */
    public boolean containSecGrpDataSource() {
        return StringUtils.contains(this.dataSource, Constants.SecGrpAssign.SEC_GRP_DATA_SOURCE);
    }

    /**
     * 等于安全组的数据权限
     *
     * @return true:相等
     */
    public boolean equalSecGrpDataSource() {
        return StringUtils.equals(this.dataSource, Constants.SecGrpAssign.SEC_GRP_DATA_SOURCE);
    }


    /**
     * 等于默认的数据权限
     *
     * @return true:相等
     */
    public boolean equalDefaultDataSource() {
        return StringUtils.equals(this.dataSource, Constants.SecGrpAssign.DEFAULT_DATA_SOURCE);
    }


    /**
     * 等于默认和安全组的数据权限
     *
     * @return true:相等
     */
    public boolean equalDefaultSecGrpDataSource() {
        return StringUtils.equals(this.dataSource, Constants.SecGrpAssign.DEFAULT_SEC_GRP_DATA_SOURCE);
    }
}
