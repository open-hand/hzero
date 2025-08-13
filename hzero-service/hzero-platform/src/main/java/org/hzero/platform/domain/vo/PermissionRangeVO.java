package org.hzero.platform.domain.vo;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonInclude;
import org.hzero.platform.domain.entity.PermissionRangeExcl;

/**
 * <p>
 * 数据权限范围VO
 * </p>
 *
 * @author yunxiang.zhou01@hand-china.com 2018/08/13 20:34
 */
@JsonInclude(value = JsonInclude.Include.NON_NULL)
public class PermissionRangeVO {

    public PermissionRangeVO() {

    }

    public PermissionRangeVO(Integer customRuleFlag, List<String> sqlList) {
        this.customRuleFlag = customRuleFlag;
        this.sqlList = sqlList;
    }

    public PermissionRangeVO(Integer customRuleFlag, List<String> sqlList, List<PermissionRangeExcl> rangeExclList) {
        this.customRuleFlag = customRuleFlag;
        this.sqlList = sqlList;
        this.rangeExclList = rangeExclList;
    }

    public PermissionRangeVO(Integer customRuleFlag, List<String> sqlList, String dbPrefix) {
        this.customRuleFlag = customRuleFlag;
        this.sqlList = sqlList;
        this.dbPrefix = dbPrefix;
    }

    public PermissionRangeVO(Integer customRuleFlag, List<String> sqlList, String dbPrefix, List<PermissionRangeExcl> rangeExclList) {
        this.customRuleFlag = customRuleFlag;
        this.sqlList = sqlList;
        this.dbPrefix = dbPrefix;
        this.rangeExclList = rangeExclList;
    }

    private Integer customRuleFlag;
    private List<String> sqlList;
    private String dbPrefix;
    private List<PermissionRangeExcl> rangeExclList;

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

    public List<PermissionRangeExcl> getRangeExclList() {
        return rangeExclList;
    }

    public PermissionRangeVO setRangeExclList(List<PermissionRangeExcl> rangeExclList) {
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
