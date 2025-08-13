package org.hzero.iam.domain.entity;

import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.Transient;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

import com.fasterxml.jackson.annotation.JsonInclude;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import org.hibernate.validator.constraints.Length;
import org.hibernate.validator.constraints.Range;

import io.choerodon.mybatis.annotation.ModifyAudit;
import io.choerodon.mybatis.annotation.MultiLanguage;
import io.choerodon.mybatis.annotation.MultiLanguageField;
import io.choerodon.mybatis.annotation.VersionAudit;
import io.choerodon.mybatis.domain.AuditDomain;

import org.hzero.boot.platform.lov.annotation.LovValue;
import org.hzero.starter.keyencrypt.core.Encrypt;

/**
 * 标签管理实体类
 *
 * @author xiaoyu.zhao@hand-china.com 2020-02-25 14:22:16
 */
@ApiModel("标签管理")
@VersionAudit
@ModifyAudit
@MultiLanguage
@JsonInclude(value = JsonInclude.Include.NON_NULL)
@Table(name = "iam_label")
public class Label extends AuditDomain {

    public static final String ENCRYPT_KEY = "iam_label";

    public static final String FIELD_ID = "id";
    public static final String FIELD_NAME = "name";
    public static final String FIELD_TYPE = "type";
    public static final String FIELD_FD_LEVEL = "fdLevel";
    public static final String FIELD_DESCRIPTION = "description";
    public static final String FIELD_ENABLED_FLAG = "enabledFlag";
    public static final String FIELD_TAG = "tag";
    public static final String FIELD_INHERIT_FLAG = "inheritFlag";
    public static final String FIELD_PRESET_FLAG = "presetFlag";
    public static final String FIELD_VISIBLE_FLAG = "visibleFlag";

    @ApiModelProperty("")
    @Id
    @GeneratedValue
    @Encrypt
    private Long id;
    @ApiModelProperty(value = "名称")
    @NotBlank(groups = {Insert.class, Update.class})
    @Length(max = 64, groups = {Insert.class, Update.class})
    private String name;
    //
    // 数据库字段
    // ------------------------------------------------------------------------------
    @ApiModelProperty(value = "类型")
    @NotBlank(groups = {Insert.class, Update.class})
    @LovValue(lovCode = "HIAM.TAG_TYPE", meaningField = "typeMeaning")
    private String type;
    @ApiModelProperty(value = "层级")
    @LovValue(lovCode = "HPFM.DATA_TENANT_LEVEL", meaningField = "levelMeaning", groups = {Insert.class, Update.class})
    private String fdLevel;
    @ApiModelProperty(value = "描述")
    @Length(max = 128, groups = {Insert.class, Update.class})
    @MultiLanguageField
    private String description;
    @ApiModelProperty(value = "是否启用")
    @Range(max = 1, groups = {Insert.class, Update.class})
    private Integer enabledFlag;
    @ApiModelProperty(value = "标记")
    @Length(max = 240, groups = {Insert.class, Update.class})
    @LovValue(lovCode = "HIAM.API_TAG_TYPE", meaningField = "tagMeaning")
    private String tag;
    @ApiModelProperty(value = "是否可继承")
    @NotNull
    @Range(max = 1)
    private Integer inheritFlag;
    @ApiModelProperty(value = "是否内置标签，内置标签页面不可更新")
    @NotNull
    @Range(max = 1)
    private Integer presetFlag;
    @ApiModelProperty(value = "是否页面可见")
    @NotNull
    @Range(max = 1)
    private Integer visibleFlag;
    @Transient
    private String typeMeaning;
    @Transient
    private String levelMeaning;

    //
    // 非数据库字段
    // ------------------------------------------------------------------------------
    @Transient
    private String tagMeaning;

    public String getTagMeaning() {
        return tagMeaning;
    }

    public void setTagMeaning(String tagMeaning) {
        this.tagMeaning = tagMeaning;
    }

    //
    // getter/setter
    // ------------------------------------------------------------------------------

    public String getTag() {
        return tag;
    }

    public void setTag(String tag) {
        this.tag = tag;
    }

    public Integer getEnabledFlag() {
        return enabledFlag;
    }

    public void setEnabledFlag(Integer enabledFlag) {
        this.enabledFlag = enabledFlag;
    }

    /**
     * @return
     */
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
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
     * @return 类型
     */
    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    /**
     * @return 层级
     */
    public String getFdLevel() {
        return fdLevel;
    }

    public void setFdLevel(String fdLevel) {
        this.fdLevel = fdLevel;
    }

    /**
     * @return 描述
     */
    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Integer getInheritFlag() {
        return inheritFlag;
    }

    public void setInheritFlag(Integer inheritFlag) {
        this.inheritFlag = inheritFlag;
    }

    public Integer getPresetFlag() {
        return presetFlag;
    }

    public void setPresetFlag(Integer presetFlag) {
        this.presetFlag = presetFlag;
    }

    public Integer getVisibleFlag() {
        return visibleFlag;
    }

    public void setVisibleFlag(Integer visibleFlag) {
        this.visibleFlag = visibleFlag;
    }

    public String getTypeMeaning() {
        return typeMeaning;
    }

    public void setTypeMeaning(String typeMeaning) {
        this.typeMeaning = typeMeaning;
    }

    public String getLevelMeaning() {
        return levelMeaning;
    }

    public void setLevelMeaning(String levelMeaning) {
        this.levelMeaning = levelMeaning;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }

        Label label = (Label) o;

        return id != null ? id.equals(label.id) : label.id == null;
    }

    @Override
    public int hashCode() {
        return id != null ? id.hashCode() : 0;
    }

    //
    // 业务方法(按public protected private顺序排列)
    // ------------------------------------------------------------------------------
    public interface Insert {
    }

    public interface Update {
    }

    @Override
    public String toString() {
        return "Label{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", type='" + type + '\'' +
                '}';
    }
}
