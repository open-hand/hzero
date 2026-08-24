package org.hzero.admin.api.dto;

import io.swagger.annotations.ApiModelProperty;
import org.hzero.admin.domain.entity.ApiLimit;
import org.hzero.starter.keyencrypt.core.Encrypt;
import org.springframework.util.StringUtils;

import javax.validation.constraints.NotNull;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * @author XCXCXCXCX
 * @version 1.2.0
 * @date 2019/12/26 11:14 上午
 */
public class ApiLimitDTO {

    public ApiLimitDTO() {
    }

    public ApiLimitDTO(ApiLimit apiLimit) {
        if (apiLimit == null) {
            return;
        }
        this.apiLimitId = apiLimit.getApiLimitId();
        this.monitorRuleId = apiLimit.getMonitorRuleId();
        this.listMode = apiLimit.getListMode();
        this.blacklistThreshold = apiLimit.getBlacklistThreshold();
        this.valueList = stringToList(apiLimit.getValueList());
        this.enabledFlag = apiLimit.getEnabledFlag();
    }

    private List<String> stringToList(String valueList) {
        List<String> returnVal = new ArrayList<>();
        if (!StringUtils.isEmpty(valueList)) {
            String[] values = valueList.split(",");
            Collections.addAll(returnVal, values);
        }
        return returnVal;
    }

    @Encrypt
    @ApiModelProperty(value = "主键ID")
    private Long apiLimitId;

    @Encrypt
    @ApiModelProperty(value = "api监控配置ID")
    @NotNull
    private Long monitorRuleId;

    @ApiModelProperty(value = "名单类型(eg.BLACK黑名单、WHITE白名单)")
    private String listMode;

    @ApiModelProperty(value = "ip名单列表")
    private List<String> valueList;

    @ApiModelProperty(value = "是否启用")
    private Boolean enabledFlag;

    @ApiModelProperty(value = "黑名单阈值(请求量超过该值,自动进入黑名单)")
    private Integer blacklistThreshold;

    public Long getApiLimitId() {
        return apiLimitId;
    }

    public void setApiLimitId(Long apiLimitId) {
        this.apiLimitId = apiLimitId;
    }

    public Long getMonitorRuleId() {
        return monitorRuleId;
    }

    public void setMonitorRuleId(Long monitorRuleId) {
        this.monitorRuleId = monitorRuleId;
    }

    public String getListMode() {
        return listMode;
    }

    public void setListMode(String listMode) {
        this.listMode = listMode;
    }

    public List<String> getValueList() {
        return valueList;
    }

    public void setValueList(List<String> valueList) {
        this.valueList = valueList;
    }

    public Boolean getEnabledFlag() {
        return enabledFlag;
    }

    public void setEnabledFlag(Boolean enabledFlag) {
        this.enabledFlag = enabledFlag;
    }

    public Integer getBlacklistThreshold() {
        return blacklistThreshold;
    }

    public void setBlacklistThreshold(Integer blacklistThreshold) {
        this.blacklistThreshold = blacklistThreshold;
    }
}
