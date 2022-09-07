package org.hzero.oauth.domain.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import io.choerodon.mybatis.annotation.ModifyAudit;
import io.choerodon.mybatis.annotation.MultiLanguage;
import io.choerodon.mybatis.annotation.MultiLanguageField;
import io.choerodon.mybatis.annotation.VersionAudit;
import io.choerodon.mybatis.domain.AuditDomain;
import io.swagger.annotations.ApiModel;

import javax.persistence.Id;
import javax.persistence.Table;
import java.util.Date;

/**
 * @author qingsheng.chen@hand-china.com
 */
@ApiModel("系统语言")
@MultiLanguage
@Table(name = "fd_language")
public class Language {
    @Id
    private Long id;
    private String code;
    private String name;
    @MultiLanguageField
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

}
