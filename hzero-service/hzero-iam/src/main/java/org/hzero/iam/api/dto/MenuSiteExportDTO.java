package org.hzero.iam.api.dto;

import org.hzero.boot.platform.lov.annotation.LovValue;
import org.hzero.export.annotation.ExcelColumn;
import org.hzero.export.annotation.ExcelSheet;

import com.fasterxml.jackson.annotation.JsonInclude;

/**
 * 菜单数据导出类
 *
 * @author xiaoyu.zhao@hand-china.com 2020/03/31 14:20
 */
@ExcelSheet(zh = "菜单数据", en = "Menu Data")
@JsonInclude(JsonInclude.Include.NON_NULL)
public class MenuSiteExportDTO {

    @ExcelColumn(zh = "菜单名称", en = "menuName")
    private String name;
    @ExcelColumn(zh = "上级目录", en = "parentName")
    private String parentName;
    @ExcelColumn(zh = "上级目录编码", en = "parentCode")
    private String parentCode;
    @ExcelColumn(zh = "快速索引", en = "quickIndex")
    private String quickIndex;
    @ExcelColumn(zh = "图标", en = "icon")
    private String icon;
    @ExcelColumn(zh = "菜单编码", en = "menuCode")
    private String code;
    @ExcelColumn(zh = "类型", en = "type")
    private String typeMeaning;
    @LovValue(lovCode = "HIAM.MENU_TYPE", meaningField = "typeMeaning")
    private String type;
    @ExcelColumn(zh = "序号", en = "sort")
    private Integer sort;
    @ExcelColumn(zh = "描述", en = "description")
    private String description;
    @ExcelColumn(zh = "状态", en = "enabledFlag")
    private String enabledFlagMeaning;
    @LovValue(lovCode = "HPFM.ENABLED_FLAG", meaningField = "enabledFlagMeaning")
    private Integer enabledFlag;
    @LovValue(lovCode = "HIAM.RESOURCE_LEVEL", meaningField = "levelMeaning")
    private String level;
    @ExcelColumn(zh = "层级", en = "levelMeaning")
    private String levelMeaning;
    @ExcelColumn(zh = "菜单层级路径", en = "levelPath")
    private String levelPath;
    @ExcelColumn(zh = "菜单路由", en = "route")
    private String route;
    @ExcelColumn(zh = "是否客制化菜单", en = "customFlag")
    private String customFlagMeaning;
    @LovValue(lovCode = "HPFM.ENABLED_FLAG", meaningField = "customFlagMeaning")
    private Integer customFlag;
    @ExcelColumn(zh = "是否虚拟菜单", en = "menuCode")
    private String virtualFlagMeaning;
    @LovValue(lovCode = "HPFM.ENABLED_FLAG", meaningField = "virtualFlagMeaning")
    private Integer virtualFlag;
    @ExcelColumn(zh = "是否默认菜单", en = "isDefault")
    private String isDefaultMeaning;
    @LovValue(lovCode = "HPFM.ENABLED_FLAG", meaningField = "isDefaultMeaning")
    private Integer isDefault;
    @ExcelColumn(zh = "权限组件类型", en = "permissionType")
    private String permissionType;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getParentName() {
        return parentName;
    }

    public void setParentName(String parentName) {
        this.parentName = parentName;
    }

    public String getParentCode() {
        return parentCode;
    }

    public void setParentCode(String parentCode) {
        this.parentCode = parentCode;
    }

    public String getQuickIndex() {
        return quickIndex;
    }

    public void setQuickIndex(String quickIndex) {
        this.quickIndex = quickIndex;
    }

    public String getIcon() {
        return icon;
    }

    public void setIcon(String icon) {
        this.icon = icon;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public Integer getSort() {
        return sort;
    }

    public void setSort(Integer sort) {
        this.sort = sort;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Integer getEnabledFlag() {
        return enabledFlag;
    }

    public void setEnabledFlag(Integer enabledFlag) {
        this.enabledFlag = enabledFlag;
    }

    public String getLevel() {
        return level;
    }

    public void setLevel(String level) {
        this.level = level;
    }

    public String getLevelMeaning() {
        return levelMeaning;
    }

    public void setLevelMeaning(String levelMeaning) {
        this.levelMeaning = levelMeaning;
    }

    public String getLevelPath() {
        return levelPath;
    }

    public void setLevelPath(String levelPath) {
        this.levelPath = levelPath;
    }

    public String getRoute() {
        return route;
    }

    public void setRoute(String route) {
        this.route = route;
    }

    public Integer getCustomFlag() {
        return customFlag;
    }

    public void setCustomFlag(Integer customFlag) {
        this.customFlag = customFlag;
    }

    public Integer getVirtualFlag() {
        return virtualFlag;
    }

    public void setVirtualFlag(Integer virtualFlag) {
        this.virtualFlag = virtualFlag;
    }

    public Integer getIsDefault() {
        return isDefault;
    }

    public void setIsDefault(Integer isDefault) {
        this.isDefault = isDefault;
    }

    public String getPermissionType() {
        return permissionType;
    }

    public void setPermissionType(String permissionType) {
        this.permissionType = permissionType;
    }

    public String getTypeMeaning() {
        return typeMeaning;
    }

    public void setTypeMeaning(String typeMeaning) {
        this.typeMeaning = typeMeaning;
    }

    public String getEnabledFlagMeaning() {
        return enabledFlagMeaning;
    }

    public void setEnabledFlagMeaning(String enabledFlagMeaning) {
        this.enabledFlagMeaning = enabledFlagMeaning;
    }

    public String getCustomFlagMeaning() {
        return customFlagMeaning;
    }

    public void setCustomFlagMeaning(String customFlagMeaning) {
        this.customFlagMeaning = customFlagMeaning;
    }

    public String getVirtualFlagMeaning() {
        return virtualFlagMeaning;
    }

    public void setVirtualFlagMeaning(String virtualFlagMeaning) {
        this.virtualFlagMeaning = virtualFlagMeaning;
    }

    public String getIsDefaultMeaning() {
        return isDefaultMeaning;
    }

    public void setIsDefaultMeaning(String isDefaultMeaning) {
        this.isDefaultMeaning = isDefaultMeaning;
    }
}
