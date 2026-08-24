package org.hzero.platform.domain.entity;

import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.Transient;
import javax.validation.constraints.NotNull;

import com.fasterxml.jackson.annotation.JsonInclude;
import org.hzero.platform.domain.repository.DataGroupLineRepository;
import org.hzero.platform.domain.repository.LovRepository;
import org.hzero.platform.infra.constant.HpfmMsgCodeConstants;

import io.choerodon.core.exception.CommonException;
import io.choerodon.mybatis.annotation.ModifyAudit;
import io.choerodon.mybatis.annotation.VersionAudit;
import io.choerodon.mybatis.domain.AuditDomain;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import org.hzero.starter.keyencrypt.core.Encrypt;

/**
 * 数据组行定义
 *
 * @author jianbo.li@hand-china.com 2019-07-17 15:48:48
 */
@ApiModel("数据组行定义")
@VersionAudit
@ModifyAudit
@Table(name = "hpfm_data_group_line")
@JsonInclude(JsonInclude.Include.NON_NULL)
public class DataGroupLine extends AuditDomain {

    public static final String FIELD_GROUP_LINE_ID = "groupLineId";
    public static final String FIELD_GROUP_ID = "groupId";
    public static final String FIELD_TENANT_ID = "tenantId";
    public static final String FIELD_LINE_VALUE_ID = "lineValueId";


    //
    // 业务方法(按public protected private顺序排列)
    // ------------------------------------------------------------------------------


    /**
     * 校验唯一性索引
     *
     * @param dataGroupLineRepository
     * @param lovRepository
     */
    public void validUniqueIndex(DataGroupLineRepository dataGroupLineRepository, LovRepository lovRepository) {
        DataGroupLine uniqueIndex = new DataGroupLine();
        uniqueIndex.setGroupId(groupId);
        uniqueIndex.setLineValueId(lineValueId);
        if (dataGroupLineRepository.selectOne(uniqueIndex) != null) {
            throw new CommonException(HpfmMsgCodeConstants.ERROR_GROUP_DATA_EXISTS, lovRepository.selectByPrimaryKey(lineValueId).getLovCode());
        }
    }

    //
    // 数据库字段
    // ------------------------------------------------------------------------------


    @ApiModelProperty("表ID，主键，供其他表做外键")
    @Id
    @GeneratedValue
    @Encrypt
    private Long groupLineId;
    @ApiModelProperty(value = "数据组ID")
    @NotNull
    @Encrypt
    private Long groupId;
    @ApiModelProperty(value = "租户ID,hpfm_tenant.tenant_id")
    @NotNull
    private Long tenantId;
    @ApiModelProperty(value = "行值ID，取自值集配置ID")
    @NotNull
    @Encrypt
    private Long lineValueId;

    //
    // 非数据库字段
    // ------------------------------------------------------------------------------

    @Transient
    private String tenantName;
    @Transient
    private String dimensionCode;
    @Transient
    private String dimensionName;
    //
    // getter/setter
    // ------------------------------------------------------------------------------

    /**
     * @return 表ID，主键，供其他表做外键
     */
    public Long getGroupLineId() {
        return groupLineId;
    }

    public void setGroupLineId(Long groupLineId) {
        this.groupLineId = groupLineId;
    }

    /**
     * @return 数据组ID
     */
    public Long getGroupId() {
        return groupId;
    }

    public void setGroupId(Long groupId) {
        this.groupId = groupId;
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
     * @return 行值ID，取自值集配置ID
     */
    public Long getLineValueId() {
        return lineValueId;
    }

    public void setLineValueId(Long lineValueId) {
        this.lineValueId = lineValueId;
    }

    public String getTenantName() {
        return tenantName;
    }

    public void setTenantName(String tenantName) {
        this.tenantName = tenantName;
    }

    public String getDimensionCode() {
        return dimensionCode;
    }

    public void setDimensionCode(String dimensionCode) {
        this.dimensionCode = dimensionCode;
    }

    public String getDimensionName() {
        return dimensionName;
    }

    public void setDimensionName(String dimensionName) {
        this.dimensionName = dimensionName;
    }
}
