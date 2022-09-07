package org.hzero.platform.api.dto;

import com.fasterxml.jackson.annotation.JsonInclude;

import io.choerodon.mybatis.domain.AuditDomain;

import java.util.List;

/**
 * 国家地区
 *
 * @author jian.zhang02@hand-china.com  2018/09/14 14:43
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
public class AddressCountryRegionDTO extends AuditDomain {
    private List<CountryDTO> countrys;
    private List<RegionDTO> regions;

    public List<CountryDTO> getCountrys() {
        return countrys;
    }

    public void setCountrys(List<CountryDTO> countrys) {
        this.countrys = countrys;
    }

    public List<RegionDTO> getRegions() {
        return regions;
    }

    public void setRegions(List<RegionDTO> regions) {
        this.regions = regions;
    }

    @Override
    public String toString() {
        return "AddressCountryRegionDTO{" +
                "countrys=" + countrys +
                ", regions=" + regions +
                '}';
    }
}
