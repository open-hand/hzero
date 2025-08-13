package org.hzero.iam.domain.vo;

import org.hzero.starter.keyencrypt.core.Encrypt;

/**
 * Lov
 *
 * @author bojiangzhou 2019/01/29
 */
public class Lov {
    @Encrypt
    private Long lovId;
    private String lovCode;
    private String lovTypeCode;
    private String lovName;
    private Long tenantId;
    private String routeName;
    private String customUrl;

    public Long getLovId() {
        return lovId;
    }

    public Lov setLovId(Long lovId) {
        this.lovId = lovId;
        return this;
    }

    public String getLovCode() {
        return lovCode;
    }

    public Lov setLovCode(String lovCode) {
        this.lovCode = lovCode;
        return this;
    }

    public String getLovTypeCode() {
        return lovTypeCode;
    }

    public Lov setLovTypeCode(String lovTypeCode) {
        this.lovTypeCode = lovTypeCode;
        return this;
    }

    public String getLovName() {
        return lovName;
    }

    public Lov setLovName(String lovName) {
        this.lovName = lovName;
        return this;
    }

    public Long getTenantId() {
        return tenantId;
    }

    public Lov setTenantId(Long tenantId) {
        this.tenantId = tenantId;
        return this;
    }

    public String getRouteName() {
        return routeName;
    }

    public Lov setRouteName(String routeName) {
        this.routeName = routeName;
        return this;
    }

    public String getCustomUrl() {
        return customUrl;
    }

    public Lov setCustomUrl(String customUrl) {
        this.customUrl = customUrl;
        return this;
    }
}
