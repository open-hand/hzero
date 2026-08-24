package org.hzero.platform.domain.entity;

import java.util.Date;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.Size;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import io.swagger.annotations.ApiModelProperty;
import org.hzero.starter.keyencrypt.core.Encrypt;

import io.choerodon.mybatis.annotation.MultiLanguage;
import io.choerodon.mybatis.annotation.MultiLanguageField;
import io.choerodon.mybatis.domain.AuditDomain;

/**
 * @author superlee
 */
@MultiLanguage
@Table(name = "fd_language")
@JsonInclude(JsonInclude.Include.NON_NULL)
public class Language extends AuditDomain {
    public static final String FIELD_NAME = "name";
    public static final String FIELD_DESCRIPTION = "description";


    @Id
    @GeneratedValue
    @ApiModelProperty(value = "语言ID")
    @Encrypt
    private Long id;

    @NotBlank
    @Size(max = 32, min = 1)
    @ApiModelProperty(value = "编码/必填")
    private String code;

    @NotEmpty
    @Size(max = 32, min = 1)
    @ApiModelProperty(value = "名称/必填")
    private String name;

    @Size(max = 128)
    @MultiLanguageField
    @ApiModelProperty(value = "描述/非必填")
    private String description;

    public Long getId() {
        return id;
    }

    public Language setId(Long id) {
        this.id = id;
        return this;
    }

    public String getCode() {
        return code;
    }

    public Language setCode(String code) {
        this.code = code;
        return this;
    }

    public String getName() {
        return name;
    }

    public Language setName(String name) {
        this.name = name;
        return this;
    }

    public String getDescription() {
        return description;
    }

    public Language setDescription(String description) {
        this.description = description;
        return this;
    }

    @JsonIgnore
    @Override
    public Date getCreationDate() {
        return super.getCreationDate();
    }

    @JsonIgnore
    @Override
    public Long getCreatedBy() {
        return super.getCreatedBy();
    }

    @JsonIgnore
    @Override
    public Date getLastUpdateDate() {
        return super.getLastUpdateDate();
    }

    @JsonIgnore
    @Override
    public Long getLastUpdatedBy() {
        return super.getLastUpdatedBy();
    }
}
