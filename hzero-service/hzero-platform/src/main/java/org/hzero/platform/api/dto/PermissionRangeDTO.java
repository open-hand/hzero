package org.hzero.platform.api.dto;

import org.hzero.mybatis.domian.SecurityToken;
import org.hzero.platform.domain.entity.PermissionRange;

import com.fasterxml.jackson.annotation.JsonInclude;
import org.hzero.platform.domain.entity.PermissionRangeExcl;
import org.hzero.platform.domain.entity.PermissionRule;
import org.hzero.starter.keyencrypt.core.Encrypt;
import org.springframework.util.StringUtils;

import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * <p>
 * 屏蔽范围DTO
 * </p>
 *
 * @author yunxiang.zhou01@hand-china.com 2018/07/24 14:49
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
public class PermissionRangeDTO extends PermissionRange {

    @Encrypt
    private Long ruleId;
    private String tenantName;

    @Override
    public String toString() {
        return "PermissionRangeDTO{" +
                ", ruleId=" + ruleId +
                ", tenantName='" + tenantName + '\'' +
                '}';
    }

    @Override
    public Class<? extends SecurityToken> associateEntityClass() {
        return (Class<? extends SecurityToken>) this.getClass().getSuperclass();
    }

    public Long getRuleId() {
        return ruleId;
    }

    public void setRuleId(Long ruleId) {
        this.ruleId = ruleId;
    }

    public String getTenantName() {
        return tenantName;
    }

    public void setTenantName(String tenantName) {
        this.tenantName = tenantName;
    }
}
