package org.hzero.iam.domain.entity;

import java.util.List;
import java.util.Objects;

import javax.persistence.*;

import org.apache.commons.lang3.StringUtils;
import org.hibernate.validator.constraints.Length;
import org.hzero.boot.platform.lov.annotation.LovValue;
import org.hzero.starter.keyencrypt.core.Encrypt;

import com.fasterxml.jackson.annotation.JsonInclude;

import io.choerodon.mybatis.annotation.ModifyAudit;
import io.choerodon.mybatis.annotation.MultiLanguage;
import io.choerodon.mybatis.annotation.MultiLanguageField;
import io.choerodon.mybatis.annotation.VersionAudit;
import io.choerodon.mybatis.domain.AuditDomain;

/**
 * @author wuguokai
 */
@ModifyAudit
@VersionAudit
@Table(name = "iam_permission")
@JsonInclude(JsonInclude.Include.NON_NULL)
@MultiLanguage
public class Permission extends AuditDomain {

    public static final String ENCRYPT_KEY = "iam_permission";

    public static final String FIELD_ID = "id";
    public static final String FIELD_CODE = "code";
    public static final String FIELD_SERVICE_NAME = "serviceName";
    public static final String FIELD_METHOD = "method";
    public static final String FIELD_PATH = "path";
    public static final String FIELD_DESCRIPTION = "description";
    public static final String FIELD_LEVEL = "level";
    public static final String PERMISSION_KEY = "gateway:permissions:";
    public static final String PERMISSION_TAG = "tag";

    /**
     * 标签数据类型
     */
    public static final String LABEL_DATA_TYPE = "API";
    @Id
    @GeneratedValue
    @Encrypt
    private Long id;
    private String code;
    private String path;
    private String method;
    @Column(name = "fd_level")
    @LovValue(value = "HIAM.PERMISSION_LEVEL", meaningField = "levelMeaning")
    private String level;
    @MultiLanguageField
    @Length(max = 1024)
    private String description;
    private String action;
    @Column(name = "fd_resource")
    private String resource;
    private Boolean publicAccess;
    private Boolean loginAccess;
    private Boolean signAccess;
    @Column(name = "is_within")
    private Boolean within;
    private String serviceName;
    private String tag;
    @Transient
    private Long permissionSetId;
    @Transient
    private String condition;
    @Transient
    private Long tenantId;
    @Transient
    private Integer fieldCount;
    @Transient
    private String[] tags;
    @Transient
    private String levelMeaning;
    /**
     * 权限标签
     */
    @Transient
    private List<Label> labels;

    /**
     * 缓存key: hgwh:permissions:serviceName:method
     */
    public static String generateKey(String serviceName, String method) {
        return String.format(PERMISSION_KEY + "%s:%s", serviceName.toLowerCase(), method.toLowerCase());
    }

    public String[] getTags() {
        return tags;
    }

    public Permission setTags(String[] tags) {
        this.tags = tags;
        return this;
    }

    public Long getId() {
        return id;
    }

    public Permission setId(Long id) {
        this.id = id;
        return this;
    }

    public String getCode() {
        return code;
    }

    public Permission setCode(String code) {
        this.code = code;
        return this;
    }

    public String getPath() {
        return path;
    }

    public Permission setPath(String path) {
        this.path = path;
        return this;
    }

    public String getMethod() {
        return method;
    }

    public Permission setMethod(String method) {
        this.method = method;
        return this;
    }

    public String getLevel() {
        return level;
    }

    public Permission setLevel(String level) {
        this.level = level;
        return this;
    }

    public String getDescription() {
        return description;
    }

    public Permission setDescription(String description) {
        this.description = description;
        return this;
    }

    public String getAction() {
        return action;
    }

    public Permission setAction(String action) {
        this.action = action;
        return this;
    }

    public String getResource() {
        return resource;
    }

    public Permission setResource(String resource) {
        this.resource = resource;
        return this;
    }

    public Boolean getPublicAccess() {
        return publicAccess;
    }

    public Permission setPublicAccess(Boolean publicAccess) {
        this.publicAccess = publicAccess;
        return this;
    }

    public Boolean getLoginAccess() {
        return loginAccess;
    }

    public Permission setLoginAccess(Boolean loginAccess) {
        this.loginAccess = loginAccess;
        return this;
    }

    public Boolean getSignAccess() {
        return signAccess;
    }

    public Permission setSignAccess(Boolean signAccess) {
        this.signAccess = signAccess;
        return this;
    }

    public Boolean getWithin() {
        return within;
    }

    public Permission setWithin(Boolean within) {
        this.within = within;
        return this;
    }

    public String getServiceName() {
        return serviceName;
    }

    public Permission setServiceName(String serviceName) {
        this.serviceName = serviceName;
        return this;
    }

    public String getTag() {
        return tag;
    }

    public Permission setTag(String tag) {
        this.tag = tag;
        return this;
    }

    public Long getPermissionSetId() {
        return permissionSetId;
    }

    public Permission setPermissionSetId(Long permissionSetId) {
        this.permissionSetId = permissionSetId;
        return this;
    }

    public String getCondition() {
        return condition;
    }

    public Permission setCondition(String condition) {
        this.condition = condition;
        return this;
    }

    public Long getTenantId() {
        return tenantId;
    }

    public Permission setTenantId(Long tenantId) {
        this.tenantId = tenantId;
        return this;
    }

    public Integer getFieldCount() {
        return fieldCount;
    }

    public void setFieldCount(Integer fieldCount) {
        this.fieldCount = fieldCount;
    }

    public String getLevelMeaning() {
        return levelMeaning;
    }

    public void setLevelMeaning(String levelMeaning) {
        this.levelMeaning = levelMeaning;
    }

    public List<Label> getLabels() {
        return labels;
    }

    public void setLabels(List<Label> labels) {
        this.labels = labels;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }

        Permission that = (Permission) o;

        if (!code.equals(that.code)) {
            return false;
        }
        if (!path.equals(that.path)) {
            return false;
        }
        if (!method.equals(that.method)) {
            return false;
        }
        if (!level.equals(that.level)) {
            return false;
        }
        if (!StringUtils.equals(this.description, that.description)) {
            return false;
        }
        if (!action.equals(that.action)) {
            return false;
        }
        if (!resource.equals(that.resource)) {
            return false;
        }
        if (!publicAccess.equals(that.publicAccess)) {
            return false;
        }
        if (!loginAccess.equals(that.loginAccess)) {
            return false;
        }
        if (!signAccess.equals(that.signAccess)) {
            return false;
        }
        if (!within.equals(that.within)) {
            return false;
        }
        if (!serviceName.equals(that.serviceName)) {
            return false;
        }
        if (!StringUtils.equals(tag, that.tag)) {
            return false;
        }
        return this.getObjectVersionNumber() != null ? Objects.equals(this.getObjectVersionNumber(),
                that.getObjectVersionNumber()) : that.getObjectVersionNumber() == null;
    }

    @Override
    public int hashCode() {
        int result = code.hashCode();
        result = 31 * result + path.hashCode();
        result = 31 * result + method.hashCode();
        result = 31 * result + level.hashCode();
        result = 31 * result + (description != null ? description.hashCode() : 0);
        result = 31 * result + action.hashCode();
        result = 31 * result + resource.hashCode();
        result = 31 * result + publicAccess.hashCode();
        result = 31 * result + loginAccess.hashCode();
        result = 31 * result + signAccess.hashCode();
        result = 31 * result + within.hashCode();
        result = 31 * result + serviceName.hashCode();
        result = 31 * result + (this.getObjectVersionNumber() != null ? this.getObjectVersionNumber().hashCode() : 0);
        return result;
    }

    @Override
    public String toString() {
        return "Permission{" +
                "id=" + id +
                ", code='" + code + '\'' +
                ", path='" + path + '\'' +
                ", method='" + method + '\'' +
                ", level='" + level + '\'' +
                ", description='" + description + '\'' +
                ", action='" + action + '\'' +
                ", resource='" + resource + '\'' +
                ", publicAccess=" + publicAccess +
                ", loginAccess=" + loginAccess +
                ", signAccess=" + signAccess +
                ", within=" + within +
                ", serviceName='" + serviceName + '\'' +
                '}';
    }

}
