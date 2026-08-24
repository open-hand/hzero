package org.hzero.platform.domain.vo;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import org.hzero.boot.platform.lov.annotation.LovValue;
import org.hzero.platform.domain.entity.DashboardCard;
import org.hzero.starter.keyencrypt.core.Encrypt;

import java.util.Objects;

/**
 * 工作台VO
 *
 * @author zhiying.dong@hand-china.com 2018/09/26 14:33
 * @author xiaoyu.zhao@hand-china.com
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
public class DashboardLayoutVO {
    private String code;
    private Integer w;
    private Integer h;
    private Integer x;
    private Integer y;
    @LovValue(lovCode = "HPFM.DASHBOARD_CARD.TYPE", meaningField = "catalogMeaning")
    private String catalogType;
    private String catalogMeaning;
    private String name;
    private String logo;
    /**
     * 初始化卡片标识，若该标识为true则工作台卡片不可删除
     */
    private Integer initFlag;
    /**
     * 标记是否是默认的，默认状态的话前端需要设置该卡片不可取消
     */
    private Integer defaultDisplayFlag;

    /**
     * 卡片参数
     */
    private String cardParams;

    /**
     * 卡片Id
     */
    @Encrypt
    private Long cardId;

    /**
     * 工作台查询卡片设置信息时过滤数据使用
     */
    @JsonIgnore
    private Long roleId;
    @JsonIgnore
    private String level;

    public DashboardLayoutVO(String code, Integer w, Integer h, Integer x, Integer y, Integer defaultDisplayFlag,
            String catalogType, String name, Integer initFlag, String cardParams, Long cardId) {
        this.code = code;
        this.w = w;
        this.h = h;
        this.x = x;
        this.y = y;
        this.defaultDisplayFlag = defaultDisplayFlag;
        this.catalogType = catalogType;
        this.name = name;
        this.initFlag = initFlag;
        this.cardParams = cardParams;
        this.cardId = cardId;
    }
    public DashboardLayoutVO(String code, Integer w, Integer h, Integer x, Integer y, String logo) {
        this.code = code;
        this.w = w;
        this.h = h;
        this.x = x;
        this.y = y;
        this.logo = logo;
    }

    public DashboardLayoutVO() {
    }

    public Long getCardId() {
        return cardId;
    }

    public void setCardId(Long cardId) {
        this.cardId = cardId;
    }

    public Integer getInitFlag() {
        return initFlag;
    }

    public void setInitFlag(Integer initFlag) {
        this.initFlag = initFlag;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
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

    public Integer getX() {
        return x;
    }

    public void setX(Integer x) {
        this.x = x;
    }

    public Integer getY() {
        return y;
    }

    public void setY(Integer y) {
        this.y = y;
    }

    public Integer getDefaultDisplayFlag() {
        return defaultDisplayFlag;
    }

    public void setDefaultDisplayFlag(Integer defaultDisplayFlag) {
        this.defaultDisplayFlag = defaultDisplayFlag;
    }

    public String getCatalogType() {
        return catalogType;
    }

    public void setCatalogType(String catalogType) {
        this.catalogType = catalogType;
    }

    public String getCatalogMeaning() {
        return catalogMeaning;
    }

    public void setCatalogMeaning(String catalogMeaning) {
        this.catalogMeaning = catalogMeaning;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getLogo() {
        return logo;
    }

    public void setLogo(String logo) {
        this.logo = logo;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o)
            return true;
        if (!(o instanceof DashboardLayoutVO))
            return false;
        DashboardLayoutVO layoutVO = (DashboardLayoutVO) o;
        return Objects.equals(getCode(), layoutVO.getCode()) && Objects.equals(getW(), layoutVO.getW()) && Objects
                .equals(getH(), layoutVO.getH()) && Objects.equals(getCatalogType(), layoutVO.getCatalogType())
                && Objects.equals(getName(), layoutVO.getName()) && Objects.equals(getLogo(), layoutVO.getLogo())
                && Objects.equals(getCardParams(), layoutVO.getCardParams());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getCode(), getW(), getH(), getCatalogType(), getName(), getLogo(), getCardParams());
    }

    public Long getRoleId() {
        return roleId;
    }

    public void setRoleId(Long roleId) {
        this.roleId = roleId;
    }

    public String getLevel() {
        return level;
    }

    public void setLevel(String level) {
        this.level = level;
    }

    public String getCardParams() {
        return cardParams;
    }

    public void setCardParams(String cardParams) {
        this.cardParams = cardParams;
    }
}
