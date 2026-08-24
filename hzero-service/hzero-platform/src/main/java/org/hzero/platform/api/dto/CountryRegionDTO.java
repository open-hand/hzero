package org.hzero.platform.api.dto;


import io.choerodon.mybatis.domain.AuditDomain;

import java.util.LinkedList;

/**
 * <p>
 * 国家地区组合DTO
 * </p>
 *
 * @author qingsheng.chen 2018/6/21 星期四 14:11
 */
public class CountryRegionDTO extends AuditDomain {
    private Long countryId;
    private String countryCode;
    private String countryName;

    private LinkedList<Long> regionIds;
    private String regionCode;
    private String regionPathName;

    public Long getCountryId() {
        return countryId;
    }

    public CountryRegionDTO setCountryId(Long countryId) {
        this.countryId = countryId;
        return this;
    }

    public String getCountryCode() {
        return countryCode;
    }

    public CountryRegionDTO setCountryCode(String countryCode) {
        this.countryCode = countryCode;
        return this;
    }

    public String getCountryName() {
        return countryName;
    }

    public CountryRegionDTO setCountryName(String countryName) {
        this.countryName = countryName;
        return this;
    }

    public LinkedList<Long> getRegionIds() {
        return regionIds;
    }

    public CountryRegionDTO setRegionIds(LinkedList<Long> regionIds) {
        this.regionIds = regionIds;
        return this;
    }

    public String getRegionCode() {
        return regionCode;
    }

    public CountryRegionDTO setRegionCode(String regionCode) {
        this.regionCode = regionCode;
        return this;
    }

    public String getRegionPathName() {
        return regionPathName;
    }

    public CountryRegionDTO setRegionPathName(String regionPathName) {
        this.regionPathName = regionPathName;
        return this;
    }

    @Override
    public String toString() {
        return "CountryRegionDTO{" +
                "countryId=" + countryId +
                ", countryCode='" + countryCode + '\'' +
                ", countryName='" + countryName + '\'' +
                ", regionIds=" + regionIds +
                ", regionCode='" + regionCode + '\'' +
                ", regionPathName='" + regionPathName + '\'' +
                '}';
    }
}
