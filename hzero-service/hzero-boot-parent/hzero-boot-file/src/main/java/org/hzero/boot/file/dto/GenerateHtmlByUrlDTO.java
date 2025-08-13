package org.hzero.boot.file.dto;

import javax.validation.constraints.NotBlank;

/**
 * description
 *
 * @author shuangfei.zhu@hand-china.com 2019/06/27 11:33
 */
public class GenerateHtmlByUrlDTO {

    @NotBlank
    private String bucketName;
    private String storageCode;
    @NotBlank
    private String url;
    private PermissionDTO permission;

    public String getBucketName() {
        return bucketName;
    }

    public GenerateHtmlByUrlDTO setBucketName(String bucketName) {
        this.bucketName = bucketName;
        return this;
    }

    public String getStorageCode() {
        return storageCode;
    }

    public GenerateHtmlByUrlDTO setStorageCode(String storageCode) {
        this.storageCode = storageCode;
        return this;
    }

    public String getUrl() {
        return url;
    }

    public GenerateHtmlByUrlDTO setUrl(String url) {
        this.url = url;
        return this;
    }

    public PermissionDTO getPermission() {
        return permission;
    }

    public GenerateHtmlByUrlDTO setPermission(PermissionDTO permission) {
        this.permission = permission;
        return this;
    }

    @Override
    public String toString() {
        return "GenerateHtmlByUrlDTO{" +
                "bucketName='" + bucketName + '\'' +
                ", storageCode='" + storageCode + '\'' +
                ", url='" + url + '\'' +
                ", permission=" + permission +
                '}';
    }
}
