package org.hzero.iam.domain.entity;

import javax.persistence.Column;
import javax.persistence.Id;
import javax.persistence.Table;

/**
 * <p>
 * 描述
 * </p>
 *
 * @author allen 2018/7/5
 */
@Table(name = "iam_menu_tl")
public class MenuTl {
    @Id
    Long id;
    String lang;
    String name;
    @Column(name = "h_tenant_id")
    Long tenantId;

    public MenuTl() {
    }

    public MenuTl(Long id, String lang, String name) {
        this.id = id;
        this.lang = lang;
        this.name = name;
    }

    public Long getTenantId() {
        return tenantId;
    }

    public MenuTl setTenantId(Long tenantId) {
        this.tenantId = tenantId;
        return this;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getLang() {
        return lang;
    }

    public void setLang(String lang) {
        this.lang = lang;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
