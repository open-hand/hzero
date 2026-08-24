package org.hzero.platform.domain.vo;

/**
 * 平台卡片VO
 *
 * @author xiaoyu.zhao@hand-china.com 2019/01/23 14:53
 */
public class DashboardCardVO {

    private String code;
    private String name;
    private String catalogType;
    private String level;
    private Integer w;
    private Integer h;

    public DashboardCardVO(String code, String name, String catalogType, String level, Integer w, Integer h) {
        this.code = code;
        this.name = name;
        this.catalogType = catalogType;
        this.level = level;
        this.w = w;
        this.h = h;
    }

    public DashboardCardVO() {
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getCatalogType() {
        return catalogType;
    }

    public void setCatalogType(String catalogType) {
        this.catalogType = catalogType;
    }

    public String getLevel() {
        return level;
    }

    public void setLevel(String level) {
        this.level = level;
    }

    public Integer getW() {
        return w;
    }

    public void setW(Integer w) {
        this.w = w;
    }

    public Integer getH() {
        return h;
    }

    public void setH(Integer h) {
        this.h = h;
    }

    @Override public String toString() {
        return "DashboardCardVO{" + "code='" + code + '\'' + ", name='" + name + '\'' + ", catalogType='" + catalogType
                + '\'' + ", level='" + level + '\'' + ", w=" + w + ", h=" + h + '}';
    }
}
