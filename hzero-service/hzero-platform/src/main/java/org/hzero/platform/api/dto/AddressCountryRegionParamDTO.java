package org.hzero.platform.api.dto;

import io.choerodon.mybatis.domain.AuditDomain;

import java.util.Set;

/**
 * 国家地区参数
 *
 * @author jian.zhang02@hand-china.com 2018/09/14 14:54
 */
public class AddressCountryRegionParamDTO extends AuditDomain {
    private Set<Long> countryIds;
    private Set<Long> regionIds;

    public Set<Long> getCountryIds() {
        return countryIds;
    }

    public void setCountryIds(Set<Long> countryIds) {
        this.countryIds = countryIds;
    }

    public Set<Long> getRegionIds() {
        return regionIds;
    }

    public void setRegionIds(Set<Long> regionIds) {
        this.regionIds = regionIds;
    }

    @Override
    public String toString() {
        return "AddressCountryRegionParamDTO{" +
                "countryIds=" + countryIds +
                ", regionIds=" + regionIds +
                '}';
    }
}
