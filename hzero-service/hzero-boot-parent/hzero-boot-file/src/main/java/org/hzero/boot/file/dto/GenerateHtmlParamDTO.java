package org.hzero.boot.file.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import io.swagger.annotations.ApiModelProperty;

/**
 * 生成HTML的参数VO
 *
 * @author xianzhi.chen@hand-china.com 2019年4月29日下午4:44:35
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
public class GenerateHtmlParamDTO {

    @ApiModelProperty("文档信息")
    private DocumentDTO document;
    @ApiModelProperty("权限信息")
    private PermissionDTO permissions;
    @ApiModelProperty("人员信息")
    private EditorDTO editor;
    @ApiModelProperty("logo信息")
    private Customization customization;
    @ApiModelProperty("额外字段")
    private String extra;

    public DocumentDTO getDocument() {
        return document;
    }

    public GenerateHtmlParamDTO setDocument(DocumentDTO document) {
        this.document = document;
        return this;
    }

    public PermissionDTO getPermissions() {
        return permissions;
    }

    public GenerateHtmlParamDTO setPermissions(PermissionDTO permissions) {
        this.permissions = permissions;
        return this;
    }

    public EditorDTO getEditor() {
        return editor;
    }

    public GenerateHtmlParamDTO setEditor(EditorDTO editor) {
        this.editor = editor;
        return this;
    }

    public Customization getCustomization() {
        return customization;
    }

    public GenerateHtmlParamDTO setCustomization(Customization customization) {
        this.customization = customization;
        return this;
    }

    public String getExtra() {
        return extra;
    }

    public GenerateHtmlParamDTO setExtra(String extra) {
        this.extra = extra;
        return this;
    }

    @Override
    public String toString() {
        return "GenerateHtmlParamDTO{" +
                "document=" + document +
                ", permissions=" + permissions +
                ", editor=" + editor +
                ", customization=" + customization +
                ", extra='" + extra + '\'' +
                '}';
    }
}
