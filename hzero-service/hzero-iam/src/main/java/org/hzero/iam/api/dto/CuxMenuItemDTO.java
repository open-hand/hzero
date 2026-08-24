package org.hzero.iam.api.dto;

import java.util.List;

/**
 * <p>
 * 描述
 * </p>
 *
 * @author allen 2018/7/4
 */
public class CuxMenuItemDTO {
    private String name;
    private String icon;
    private Integer sort;
    private String route;
    private List<String> permission;
    private List<CuxMenuItemDTO> site;
    private List<CuxMenuItemDTO> organization;
    private List<CuxMenuItemDTO> project;
    private List<CuxMenuItemDTO> user;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getIcon() {
        return icon;
    }

    public void setIcon(String icon) {
        this.icon = icon;
    }

    public Integer getSort() {
        return sort;
    }

    public void setSort(Integer sort) {
        this.sort = sort;
    }

    public String getRoute() {
        return route;
    }

    public void setRoute(String route) {
        this.route = route;
    }

    public List<String> getPermission() {
        return permission;
    }

    public void setPermission(List<String> permission) {
        this.permission = permission;
    }

    public List<CuxMenuItemDTO> getSite() {
        return site;
    }

    public void setSite(List<CuxMenuItemDTO> site) {
        this.site = site;
    }

    public List<CuxMenuItemDTO> getOrganization() {
        return organization;
    }

    public void setOrganization(List<CuxMenuItemDTO> organization) {
        this.organization = organization;
    }

    public List<CuxMenuItemDTO> getProject() {
        return project;
    }

    public void setProject(List<CuxMenuItemDTO> project) {
        this.project = project;
    }

    public List<CuxMenuItemDTO> getUser() {
        return user;
    }

    public void setUser(List<CuxMenuItemDTO> user) {
        this.user = user;
    }
}
