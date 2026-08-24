package org.hzero.platform.domain.entity;

import java.util.Date;

import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.Transient;
import javax.validation.constraints.NotNull;

import org.hibernate.validator.constraints.NotBlank;
import org.hzero.platform.domain.repository.CertificateRepository;
import org.hzero.starter.keyencrypt.core.Encrypt;

import com.fasterxml.jackson.annotation.JsonInclude;

import io.choerodon.core.exception.CommonException;
import io.choerodon.mybatis.annotation.ModifyAudit;
import io.choerodon.mybatis.annotation.VersionAudit;
import io.choerodon.mybatis.domain.AuditDomain;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

/**
 * CA证书配置
 *
 * @author xingxing.wu@hand-china.com 2019-09-29 10:38:09
 */
@ApiModel("CA证书配置")
@VersionAudit
@ModifyAudit
@Table(name = "hpfm_certificate")
@JsonInclude(JsonInclude.Include.NON_NULL)
public class Certificate extends AuditDomain {

    public static final String FIELD_CERTIFICATE_ID = "certificateId";
    public static final String FIELD_TENANT_ID = "tenantId";
    public static final String FIELD_DOMAIN_NAME = "domainName";
    public static final String FIELD_ISSUER_DOMAIN_NAME = "issuerDomainName";
    public static final String FIELD_START_DATE = "startDate";
    public static final String FIELD_END_DATE = "endDate";
    public static final String FIELD_DATA = "data";


    //
    // 业务方法(按public protected private顺序排列)
    // ------------------------------------------------------------------------------

    //
    // 数据库字段
    // ------------------------------------------------------------------------------
	@ApiModelProperty("表ID，主键")
	@Id
	@GeneratedValue
	@Encrypt
	private Long certificateId;
    @ApiModelProperty(value = "租户ID", required = true)
    @NotNull
    private Long tenantId;
    @ApiModelProperty(value = "域名", required = true)
    @NotBlank
    private String domainName;
    @ApiModelProperty(value = "颁发者")
    private String issuerDomainName;
    @ApiModelProperty(value = "有效期从")
    private Date startDate;
    @ApiModelProperty(value = "有效期至")
    private Date endDate;
    @ApiModelProperty(value = "证书数据")
    private byte[] data;
    @Transient
    private String tenantName;

    //
    // 非数据库字段
    // ------------------------------------------------------------------------------

    /**
     * 唯一性校验
     *
     * @param certificateRepository 资源对象
     */
    public void checkUnique(CertificateRepository certificateRepository) {
        Certificate query = new Certificate();
        query.setDomainName(this.domainName);
        query.setTenantId(this.tenantId);
        if (certificateRepository.selectCount(query) > 0) {
            throw new CommonException("hpfm.error.certificate.exists");
        }
    }

    //
    // getter/setter
    // ------------------------------------------------------------------------------

    /**
     * @return 表ID，主键
     */
    public Long getCertificateId() {
        return certificateId;
    }

    public void setCertificateId(Long certificateId) {
        this.certificateId = certificateId;
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
     * @return 域名
     */
    public String getDomainName() {
        return domainName;
    }

    public void setDomainName(String domainName) {
        this.domainName = domainName;
    }

    /**
     * @return 颁发者
     */
    public String getIssuerDomainName() {
        return issuerDomainName;
    }

    public void setIssuerDomainName(String issuerDomainName) {
        this.issuerDomainName = issuerDomainName;
    }

    /**
     * @return 有效期从
     */
    public Date getStartDate() {
        return startDate;
    }

    public void setStartDate(Date startDate) {
        this.startDate = startDate;
    }

    /**
     * @return 有效期至
     */
    public Date getEndDate() {
        return endDate;
    }

    public void setEndDate(Date endDate) {
        this.endDate = endDate;
    }

    /**
     * @return 证书数据
     */
    public byte[] getData() {
        return data;
    }

    public void setData(byte[] data) {
        this.data = data;
    }

    public String getTenantName() {
        return tenantName;
    }

    public void setTenantName(String tenantName) {
        this.tenantName = tenantName;
    }
}
