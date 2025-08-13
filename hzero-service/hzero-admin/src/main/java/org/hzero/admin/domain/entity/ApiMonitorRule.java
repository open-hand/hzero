package org.hzero.admin.domain.entity;

import com.fasterxml.jackson.annotation.JsonInclude;
import io.choerodon.mybatis.annotation.ModifyAudit;
import io.choerodon.mybatis.annotation.VersionAudit;
import io.choerodon.mybatis.domain.AuditDomain;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import org.hzero.starter.keyencrypt.core.Encrypt;

import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.validation.constraints.NotEmpty;

/**
 *
 * api监控配置
 * 仅统计部分url的请求信息，无需记录全部请求
 *  - 支持通配符匹配
 *
 * @author XCXCXCXCX
 * @version 1.2.0
 * @date 2019/12/9 2:28 下午
 */
@ApiModel("api监控配置")
@VersionAudit
@ModifyAudit
@Table(name = "hadm_api_monitor_rule")
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ApiMonitorRule extends AuditDomain {

    public static final String ENCRYPT_KEY = "hadm_api_monitor_rule";

    public static final String FIELD_API_MONITOR_RULE_ID = "monitorRuleId";
    public static final String FIELD_URL_PATTERN = "urlPattern";

    @Encrypt
    @ApiModelProperty("表ID，主键，供其他表做外键")
    @Id
    @GeneratedValue
    private Long monitorRuleId;

    @ApiModelProperty(value = "匹配规则")
    @NotEmpty
    private String urlPattern;

    @ApiModelProperty(value = "时间窗口大小(单位秒)")
    private Integer timeWindowSize;

    public Long getMonitorRuleId() {
        return monitorRuleId;
    }

    public ApiMonitorRule setMonitorRuleId(Long monitorRuleId) {
        this.monitorRuleId = monitorRuleId;
        return this;
    }

    public String getUrlPattern() {
        return urlPattern;
    }

    public ApiMonitorRule setUrlPattern(String urlPattern) {
        this.urlPattern = urlPattern;
        return this;
    }

    public Integer getTimeWindowSize() {
        return timeWindowSize;
    }

    public void setTimeWindowSize(Integer timeWindowSize) {
        this.timeWindowSize = timeWindowSize;
    }
}
