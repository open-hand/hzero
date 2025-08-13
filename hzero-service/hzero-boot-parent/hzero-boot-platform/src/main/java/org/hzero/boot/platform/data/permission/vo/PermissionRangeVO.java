package org.hzero.boot.platform.data.permission.vo;

import java.util.List;

/**
 * <p>
 * 数据权限存储应用VO
 * </p>
 *
 * @author yunxiang.zhou01@hand-china.com 2018/08/13 20:34
 */
public class PermissionRangeVO {

    public PermissionRangeVO() {

    }

    public PermissionRangeVO(Integer customRuleFlag,
                             List<String> sqlList,
                             String dbPrefix,
                             List<PermissionRangeExclVO> rangeExclList) {
        this.customRuleFlag = customRuleFlag;
        this.sqlList = sqlList;
        this.dbPrefix = dbPrefix;
        this.rangeExclList = rangeExclList;
    }

    private Integer customRuleFlag;
    private List<String> sqlList;
    private String dbPrefix;
    private List<PermissionRangeExclVO> rangeExclList;

    public String getDbPrefix() {
        return dbPrefix;
    }

    public void setDbPrefix(String dbPrefix) {
        this.dbPrefix = dbPrefix;
    }

    public Integer getCustomRuleFlag() {
        return customRuleFlag;
    }

    public void setCustomRuleFlag(Integer customRuleFlag) {
        this.customRuleFlag = customRuleFlag;
    }

    public List<String> getSqlList() {
        return sqlList;
    }

    public void setSqlList(List<String> sqlList) {
        this.sqlList = sqlList;
    }

    public List<PermissionRangeExclVO> getRangeExclList() {
        return rangeExclList;
    }

    public PermissionRangeVO setRangeExclList(List<PermissionRangeExclVO> rangeExclList) {
        this.rangeExclList = rangeExclList;
        return this;
    }

    @Override
    public String toString() {
        return "PermissionRangeVO{" +
                "customRuleFlag=" + customRuleFlag +
                ", sqlList=" + sqlList +
                ", dbPrefix='" + dbPrefix + '\'' +
                ", rangeExclList=" + rangeExclList +
                '}';
    }
}
