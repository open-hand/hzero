package org.hzero.iam.domain.entity;

import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.Transient;
import javax.validation.constraints.NotNull;

import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.hzero.iam.api.dto.SecGrpDclDimDTO;
import org.hzero.iam.api.dto.SecGrpDclDimLineDTO;
import org.hzero.iam.infra.constant.Constants;
import org.hzero.starter.keyencrypt.core.Encrypt;
import org.springframework.beans.BeanUtils;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;

import io.choerodon.mybatis.annotation.ModifyAudit;
import io.choerodon.mybatis.annotation.VersionAudit;
import io.choerodon.mybatis.domain.AuditDomain;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

/**
 * 安全组数据权限维度
 *
 * @author xingxing.wu@hand-china.com 2019-10-20 10:20:22
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
@ApiModel("安全组数据权限维度")
@VersionAudit
@ModifyAudit
@Table(name = "hiam_sec_grp_dcl_dim")
public class SecGrpDclDim extends AuditDomain {
    public static final String ENCRYPT_KEY = "hiam_sec_grp_dcl_dim";
    public static final String FIELD_SEC_GRP_DCL_DIM_ID = "secGrpDclDimId";
    public static final String FIELD_SEC_GRP_ID = "secGrpId";
    public static final String FIELD_TENANT_ID = "tenantId";
    public static final String FIELD_AUTH_DOC_TYPE_ID = "authDocTypeId";
    public static final String FIELD_AUTH_SCOPE_CODE = "authScopeCode";
    public static final String FIELD_ASSIGN_TYPE_CODE = "assignTypeCode";

    public SecGrpDclDim() {

    }

    public SecGrpDclDim(Long secGrpId) {
        this.secGrpId = secGrpId;
    }

    public SecGrpDclDim(Long secGrpId, Long authDocTypeId, String authScopeCode) {
        this.secGrpId = secGrpId;
        this.authDocTypeId = authDocTypeId;
        this.authScopeCode = authScopeCode;
    }

    @ApiModelProperty(value = "权限分配类型，Code：HAIM.SEC_GRP.ASSIGN_TYPE_CODE ([SELF/自己创建]、[PARENT/父类分配]、[SELF_PARENT/自己创建之后，父类也创建])")
    private String assignTypeCode;

    public SecGrpDclDim(Long tenantId, Long secGrpId, Long authDocTypeId, String authScopeCode, String assignTypeCode) {
        this.secGrpId = secGrpId;
        this.tenantId = tenantId;
        this.authDocTypeId = authDocTypeId;
        this.authScopeCode = authScopeCode;
        this.assignTypeCode = assignTypeCode;
    }

    /**
     * 构建安全组数据权限对象
     *
     * @param dto 数据权限维度DTO
     * @return 安全组数据权限对象
     */
    public static SecGrpDclDim buildDim(SecGrpDclDimDTO dto) {
        SecGrpDclDim dim = new SecGrpDclDim();
        dim.setSecGrpDclDimId(dto.getSecGrpDclDimId());
        dim.setAuthDocTypeId(dto.getDocTypeId());
        dim.setSecGrpId(dto.getSecGrpId());
        dim.setTenantId(dto.getTenantId());
        dim.setAuthScopeCode(dto.getAuthScopeCode());

        List<SecGrpDclDimLineDTO> secGrpDclDimLineList = dto.getSecGrpDclDimLineList();
        List<SecGrpDclDimLine> lineList = Collections.emptyList();
        if (CollectionUtils.isNotEmpty(secGrpDclDimLineList)) {
            lineList = secGrpDclDimLineList.stream()
                    // 只需相同维度的行
                    .filter(line -> StringUtils.equals(dto.getAuthScopeCode(), line.getDimensionType()) && 1 == line.getDeleteEnableFlag())
                    .map(line -> SecGrpDclDimLine.buildDimLine(dim,
                            line.getAuthTypeCode(),
                            line.getSecGrpDclDimLineId(),
                            line.getSecGrpDclDimLineCheckedFlag(),
                            Constants.SecGrpAssignTypeCode.SELF
                    ))
                    .collect(Collectors.toList());
        }
        dim.setDimLineList(lineList);

        return dim;
    }

    /**
     * 根据已有的Dim构建新的Dim
     *
     * @param secGrpId       安全组ID
     * @param secGrpDclDim   已有的Dim
     * @param assignTypeCode 分配类型码
     * @return 新的Dim
     */
    public static SecGrpDclDim buildDim(Long secGrpId, SecGrpDclDim secGrpDclDim, String assignTypeCode) {
        // 新建Dim
        SecGrpDclDim newDim = new SecGrpDclDim();
        // 拷贝属性
        BeanUtils.copyProperties(secGrpDclDim, newDim);

        // 设置安全组ID
        newDim.setSecGrpId(secGrpId);
        // 设置分配类型码
        newDim.setAssignTypeCode(assignTypeCode);
        // 设置dim的ID为空
        newDim.setSecGrpDclDimId(null);

        // 返回新建的Dim
        return newDim;
    }

    //
    // 数据库字段
    // ------------------------------------------------------------------------------


    @ApiModelProperty("表ID，主键，供其他表做外键")
    @Id
    @GeneratedValue
    @Encrypt
    private Long secGrpDclDimId;
    @ApiModelProperty(value = "安全组ID", required = true)
    @NotNull
    @Encrypt
    private Long secGrpId;
    @ApiModelProperty(value = "租户", required = true)
    @NotNull
    private Long tenantId;
    @ApiModelProperty(value = "单据类型ID，HIAM_DOC_TYPE.DOC_TYPE_ID", required = true)
    @NotNull
    @Encrypt
    private Long authDocTypeId;
    @ApiModelProperty(value = "权限限制范围，HIAM.AUTHORITY_SCOPE_TYPE_CODE")
    private String authScopeCode;

    /**
     * 安全组数据权限维度头可操作性
     *
     * @return true 可操作    false 不可操作
     */
    @JsonIgnore
    public boolean operability() {
        return Constants.SecGrpAssignTypeCode.SELF.equals(this.assignTypeCode);
    }

    //
    // 非数据库字段
    // ------------------------------------------------------------------------------
    @Transient
    private String docTypeName;
    @Transient
    @JsonIgnore
    private List<SecGrpDclDimLine> dimLineList;


    //
    // getter/setter
    // ------------------------------------------------------------------------------

    /**
     * @return 表ID，主键，供其他表做外键
     */
    public Long getSecGrpDclDimId() {
        return secGrpDclDimId;
    }

    public void setSecGrpDclDimId(Long secGrpDclDimId) {
        this.secGrpDclDimId = secGrpDclDimId;
    }

    /**
     * @return 安全组ID
     */
    public Long getSecGrpId() {
        return secGrpId;
    }

    public void setSecGrpId(Long secGrpId) {
        this.secGrpId = secGrpId;
    }

    /**
     * @return 租户
     */
    public Long getTenantId() {
        return tenantId;
    }

    public void setTenantId(Long tenantId) {
        this.tenantId = tenantId;
    }

    /**
     * @return 单据类型ID，HIAM_DOC_TYPE.DOC_TYPE_ID
     */
    public Long getAuthDocTypeId() {
        return authDocTypeId;
    }

    public void setAuthDocTypeId(Long authDocTypeId) {
        this.authDocTypeId = authDocTypeId;
    }

    /**
     * @return 权限限制范围，HIAM.AUTHORITY_SCOPE_TYPE_CODE
     */
    public String getAuthScopeCode() {
        return authScopeCode;
    }

    public void setAuthScopeCode(String authScopeCode) {
        this.authScopeCode = authScopeCode;
    }

    public String getAssignTypeCode() {
        return assignTypeCode;
    }

    public void setAssignTypeCode(String assignTypeCode) {
        this.assignTypeCode = assignTypeCode;
    }

    public String getDocTypeName() {
        return docTypeName;
    }

    public void setDocTypeName(String docTypeName) {
        this.docTypeName = docTypeName;
    }

    public List<SecGrpDclDimLine> getDimLineList() {
        return Optional.ofNullable(dimLineList).orElse(Collections.emptyList());
    }

    public void setDimLineList(List<SecGrpDclDimLine> dimLineList) {
        this.dimLineList = dimLineList;
    }
}
