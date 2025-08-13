package org.hzero.iam.api.dto;

import io.swagger.annotations.ApiModelProperty;
import org.apache.commons.lang3.StringUtils;
import org.hzero.core.helper.LanguageHelper;

import java.util.Objects;
import java.util.Set;

/**
 * 菜单树型结构查询DTO
 *
 * @author bo.he02@hand-china.com 2020/05/18 15:37
 */
public class MenuTreeQueryDTO {
    @ApiModelProperty(name = "roleId", value = "角色ID")
    private Long roleId;
    @ApiModelProperty(name = "lang", value = "语言")
    private String lang;
    @ApiModelProperty(name = "labels", value = "标签")
    private Set<String> labels;
    @ApiModelProperty(name = "unionLabel", value = "是否按照标签并集查询(即只要存在一个标签即可), 默认否(false)")
    private Boolean unionLabel;

    /**
     * 初始默认值
     *
     * @return 当前查询条件对象
     */
    public MenuTreeQueryDTO defaults() {
        this.lang = StringUtils.defaultIfBlank(this.lang, LanguageHelper.language());

        if (Objects.isNull(this.unionLabel)) {
            this.unionLabel = Boolean.FALSE;
        }

        return this;
    }

    public Long getRoleId() {
        return roleId;
    }

    public void setRoleId(Long roleId) {
        this.roleId = roleId;
    }

    public String getLang() {
        return lang;
    }

    public void setLang(String lang) {
        this.lang = lang;
    }

    public Set<String> getLabels() {
        return labels;
    }

    public void setLabels(Set<String> labels) {
        this.labels = labels;
    }

    public Boolean getUnionLabel() {
        return unionLabel;
    }

    public void setUnionLabel(Boolean unionLabel) {
        this.unionLabel = unionLabel;
    }

    @Override
    public String toString() {
        return "MenuTreeQueryDTO{" +
                "roleId=" + roleId +
                ", lang='" + lang + '\'' +
                ", labels=" + labels +
                ", unionLabel=" + unionLabel +
                '}';
    }
}
