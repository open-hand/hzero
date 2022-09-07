package org.hzero.admin.domain.entity;

import com.fasterxml.jackson.annotation.JsonInclude;
import io.choerodon.core.exception.CommonException;
import io.choerodon.mybatis.annotation.ModifyAudit;
import io.choerodon.mybatis.annotation.MultiLanguage;
import io.choerodon.mybatis.annotation.MultiLanguageField;
import io.choerodon.mybatis.annotation.VersionAudit;
import io.choerodon.mybatis.domain.AuditDomain;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import org.hibernate.validator.constraints.Length;
import org.hzero.admin.domain.repository.ServiceRouteRepository;
import org.hzero.core.util.Regexs;
import org.hzero.mybatis.domian.Condition;
import org.hzero.mybatis.util.Sqls;
import org.hzero.starter.keyencrypt.core.Encrypt;

import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.Transient;
import javax.validation.constraints.Pattern;
import java.util.Date;

/**
 * 应用服务
 *
 * @author zhiying.dong@hand-china.com 2018-11-14 11:04:42
 */
@ApiModel("应用服务")
@VersionAudit
@ModifyAudit
@MultiLanguage
@JsonInclude(JsonInclude.Include.NON_NULL)
@Table(name = "hadm_service")
public class HService extends AuditDomain {

    public static final String ENCRYPT_KEY = "hadm_service";

    public static final String FIELD_SERVICE_ID = "serviceId";
    public static final String FIELD_SERVICE_CODE = "serviceCode";
    public static final String FIELD_SERVICE_NAME = "serviceName";
    public static final String FIELD_SERVICE_LOGO = "serviceLogo";
    public static final String FIELD_VERSION_NUMBER = "versionNumber";

    /**
     * 校验是否存在服务路由引用该服务
     */
    public void checkServiceRef(ServiceRouteRepository routeRepository) {
        int count = routeRepository.selectCountByCondition(Condition.builder(ServiceRoute.class)
                .andWhere(Sqls.custom()
                        .andEqualTo(ServiceRoute.FIELD_SERVICE_ID, serviceId)
                ).build());
        if (count != 0) {
            throw new CommonException("hadm.error.has_route_rely");
        }
    }

    @Encrypt
    @Id
    @GeneratedValue
    @ApiModelProperty("服务ID")
    private Long serviceId;
    @ApiModelProperty("服务编码")
    @Length(max = 60)
    @Pattern(regexp = Regexs.CODE)
    private String serviceCode;
    @ApiModelProperty("服务名称")
    @MultiLanguageField
    @Length(max = 90)
    private String serviceName;
    @ApiModelProperty("服务图标")
    private String serviceLogo;

    @Transient
    @ApiModelProperty("服务版本")
    private String versionNumber;
    @Transient
    @ApiModelProperty("发布时间")
    private Date releaseDate;
    @Transient
    @ApiModelProperty("更新时间")
    private Date updateDate;

    //
    // getter/setter
    // ------------------------------------------------------------------------------

    /**
     * @return 表ID，主键
     */
    public Long getServiceId() {
        return serviceId;
    }

    public void setServiceId(Long serviceId) {
        this.serviceId = serviceId;
    }

    /**
     * @return 服务编码
     */
    public String getServiceCode() {
        return serviceCode;
    }

    public HService setServiceCode(String serviceCode) {
        this.serviceCode = serviceCode;
        return this;
    }

    /**
     * @return 服务名称
     */
    public String getServiceName() {
        return serviceName;
    }

    public void setServiceName(String serviceName) {
        this.serviceName = serviceName;
    }

    /**
     * @return 服务图标
     */
    public String getServiceLogo() {
        return serviceLogo;
    }

    public void setServiceLogo(String serviceLogo) {
        this.serviceLogo = serviceLogo;
    }

    public String getVersionNumber() {
        return versionNumber;
    }

    public HService setVersionNumber(String versionNumber) {
        this.versionNumber = versionNumber;
        return this;
    }

    public Date getReleaseDate() {
        return releaseDate;
    }

    public HService setReleaseDate(Date releaseDate) {
        this.releaseDate = releaseDate;
        return this;
    }

    public Date getUpdateDate() {
        return updateDate;
    }

    public HService setUpdateDate(Date updateDate) {
        this.updateDate = updateDate;
        return this;
    }
}
