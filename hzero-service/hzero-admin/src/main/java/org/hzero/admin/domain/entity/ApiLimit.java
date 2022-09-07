package org.hzero.admin.domain.entity;

import com.fasterxml.jackson.annotation.JsonInclude;
import io.choerodon.mybatis.annotation.ModifyAudit;
import io.choerodon.mybatis.annotation.VersionAudit;
import io.choerodon.mybatis.domain.AuditDomain;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import org.hzero.admin.api.dto.ApiLimitDTO;
import org.hzero.starter.keyencrypt.core.Encrypt;
import org.springframework.util.CollectionUtils;

import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;
import java.util.List;

/**
 * api限制
 * - 支持黑白名单
 * @author XCXCXCXCX
 * @version 1.2.0
 * @date 2019/12/9 2:17 下午
 */
@ApiModel("api限制")
@VersionAudit
@ModifyAudit
@Table(name = "hadm_api_limit")
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ApiLimit extends AuditDomain {

    public static final String ENCRYPT_KEY = "hadm_api_limit";

    public static final String FIELD_API_LIMIT_ID = "apiLimitId";
    public static final String FIELD_MONITOR_RULE_ID = "monitorRuleId";
    public static final String FIELD_LIMIT_MODE = "listMode";
    public static final String FIELD_VALUE_LIST = "valueList";
    public static final String FIELD_ENABLE_FLAG = "enabledFlag";
    public static final String FIELD_BLACKLIST_THRESHOLD = "blacklistThreshold";

    public ApiLimit() {
    }

    public ApiLimit(ApiLimitDTO dto) {
        this.apiLimitId = dto.getApiLimitId();
        this.monitorRuleId = dto.getMonitorRuleId();
        this.listMode = dto.getListMode();
        this.valueList = listToString(dto.getValueList());
        this.blacklistThreshold = dto.getBlacklistThreshold();
        this.enabledFlag = dto.getEnabledFlag();
    }

    private String listToString(List<String> valueList) {
        if (CollectionUtils.isEmpty(valueList)){
            return null;
        } else {
            StringBuilder builder = new StringBuilder();
            for (int i = 0; i < valueList.size(); i++){
                if (i != 0) {
                    builder.append(",");
                }
                builder.append(valueList.get(i));
            }
            return builder.toString();
        }
    }

    @Encrypt
    @ApiModelProperty("表ID，主键，供其他表做外键")
    @Id
    @GeneratedValue
    private Long apiLimitId;

    @Encrypt
    @ApiModelProperty(value = "api监控配置ID")
    @NotNull
    private Long monitorRuleId;

    @ApiModelProperty(value = "名单类型(eg.BLACK黑名单、WHITE白名单)")
    private String listMode;

    @ApiModelProperty(value = "ip名单列表")
    private String valueList;

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

    public ApiLimit setMonitorRuleId(Long monitorRuleId) {
        this.monitorRuleId = monitorRuleId;
        return this;
    }

    public String getListMode() {
        return listMode;
    }

    public void setListMode(String listMode) {
        this.listMode = listMode;
    }

    public String getValueList() {
        return valueList;
    }

    public void setValueList(String valueList) {
        this.valueList = valueList;
    }

    public Boolean getEnabledFlag() {
        return enabledFlag;
    }

    public ApiLimit setEnabledFlag(Boolean enabledFlag) {
        this.enabledFlag = enabledFlag;
        return this;
    }

    public Integer getBlacklistThreshold() {
        return blacklistThreshold;
    }

    public void setBlacklistThreshold(Integer blacklistThreshold) {
        this.blacklistThreshold = blacklistThreshold;
    }
}
