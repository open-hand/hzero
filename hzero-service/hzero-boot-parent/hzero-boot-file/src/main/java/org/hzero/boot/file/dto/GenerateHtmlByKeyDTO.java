package org.hzero.boot.file.dto;

import javax.validation.constraints.NotBlank;

/**
 * description
 *
 * @author shuangfei.zhu@hand-china.com 2019/06/26 17:44
 */
public class GenerateHtmlByKeyDTO {

    @NotBlank
    private String fileKey;
    private PermissionDTO permission;

    public String getFileKey() {
        return fileKey;
    }

    public GenerateHtmlByKeyDTO setFileKey(String fileKey) {
        this.fileKey = fileKey;
        return this;
    }

    public PermissionDTO getPermission() {
        return permission;
    }

    public GenerateHtmlByKeyDTO setPermission(PermissionDTO permission) {
        this.permission = permission;
        return this;
    }

    @Override
    public String toString() {
        return "GenerateHtmlDTO{" +
                ", fileKey='" + fileKey + '\'' +
                ", permission=" + permission +
                '}';
    }
}
