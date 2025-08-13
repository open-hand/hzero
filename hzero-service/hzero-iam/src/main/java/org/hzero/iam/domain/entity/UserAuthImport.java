package org.hzero.iam.domain.entity;

import io.swagger.annotations.ApiModelProperty;
import org.hzero.iam.domain.service.authdata.vo.AuthDataVo;

import javax.validation.constraints.NotNull;
import java.util.List;

/**
 * @author jianbo.li@hand-china.com 2018/10/16
 */
public class UserAuthImport {
    //
    // 业务方法(按public protected private顺序排列)
    // ------------------------------------------------------------------------------


    // 打平后区分标识
    //=================================================================
    @ApiModelProperty("唯一标识")
    private String id;
    @ApiModelProperty("类型标识")
    private String typeCode;

    //数据库字段
    //=================================================================
    @ApiModelProperty("权限Id")
    private Long authorityId;
    @ApiModelProperty("用户id")
    private Long userId;
    @ApiModelProperty("用户名,excel中头参数")
    private String userName;
    @ApiModelProperty("租户Id")
    private Long tenantId;
    @ApiModelProperty("租户编码,excel中头参数")
    @NotNull
    private String tenantNum;
    @ApiModelProperty("权限类型代码，excel中头参数")
    @NotNull
    private String authorityTypeCode;
    @ApiModelProperty("是否包括全部，excel中头参数")
    private Integer includeAllFlag;
    @ApiModelProperty("是否勾选下级组织")
    private Integer includeChildFlag;
    @ApiModelProperty("行Id")
    private Long authorityLineId;
    @ApiModelProperty("数据id")
    private Long dataId;
    @ApiModelProperty("数据编码,excel中行参数")
    @NotNull
    private String dataCode;
    @ApiModelProperty("数据名称,excel中行参数")
    private String dataName;
    @ApiModelProperty("名称")
    private String realName;
    @ApiModelProperty("自定义查询参数列表")
    private String queryParamList;
    @ApiModelProperty("扩展参数，格式自定义")
    private String extendsParam;

    /**
     * 临时数据变量：权限数据
     */
    private List<AuthDataVo> authDataVos;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public Long getTenantId() {
        return tenantId;
    }

    public void setTenantId(Long tenantId) {
        this.tenantId = tenantId;
    }

    public String getAuthorityTypeCode() {
        return authorityTypeCode;
    }

    public void setAuthorityTypeCode(String authorityTypeCode) {
        this.authorityTypeCode = authorityTypeCode;
    }

    public Integer getIncludeAllFlag() {
        return includeAllFlag;
    }

    public void setIncludeAllFlag(Integer includeAllFlag) {
        this.includeAllFlag = includeAllFlag;
    }

    public Integer getIncludeChildFlag() {
        return includeChildFlag;
    }

    public void setIncludeChildFlag(Integer includeChildFlag) {
        this.includeChildFlag = includeChildFlag;
    }

    public Long getAuthorityLineId() {
        return authorityLineId;
    }

    public void setAuthorityLineId(Long authorityLineId) {
        this.authorityLineId = authorityLineId;
    }

    public Long getDataId() {
        return dataId;
    }

    public void setDataId(Long dataId) {
        this.dataId = dataId;
    }

    public String getDataCode() {
        return dataCode;
    }

    public void setDataCode(String dataCode) {
        this.dataCode = dataCode;
    }

    public String getDataName() {
        return dataName;
    }

    public void setDataName(String dataName) {
        this.dataName = dataName;
    }

    public Long getAuthorityId() {
        return authorityId;
    }

    public void setAuthorityId(Long authorityId) {
        this.authorityId = authorityId;
    }

    public String getTypeCode() {
        return typeCode;
    }

    public void setTypeCode(String typeCode) {
        this.typeCode = typeCode;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getTenantNum() {
        return tenantNum;
    }

    public void setTenantNum(String tenantNum) {
        this.tenantNum = tenantNum;
    }

    public String getRealName() {
        return realName;
    }

    public void setRealName(String realName) {
        this.realName = realName;
    }

    public String getQueryParamList() {
        return queryParamList;
    }

    public void setQueryParamList(String queryParamList) {
        this.queryParamList = queryParamList;
    }

    public String getExtendsParam() {
        return extendsParam;
    }

    public void setExtendsParam(String extendsParam) {
        this.extendsParam = extendsParam;
    }

    public List<AuthDataVo> getAuthDataVos() {
        return authDataVos;
    }

    public void setAuthDataVos(List<AuthDataVo> authDataVos) {
        this.authDataVos = authDataVos;
    }
}
