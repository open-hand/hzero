package org.hzero.platform.app.service;

import org.hzero.core.util.Pair;
import org.hzero.platform.api.dto.AddressCountryRegionDTO;
import org.hzero.platform.api.dto.AddressCountryRegionParamDTO;
import org.hzero.platform.api.dto.CountryRegionDTO;

import java.util.List;
import java.util.Set;

/**
 * <p>
 * 国家地区组合查询API接口
 * </p>
 *
 * @author qingsheng.chen 2018/7/3 星期二 10:35
 */
public interface CountryRegionService {
    /**
     * 在远程服务中查询国家地区
     *
     * @param countryId 国家ID
     * @param regionId  地区ID
     * @return 国家地区
     */
    CountryRegionDTO queryCountryRegion(Long countryId, Long regionId);

    /**
     * 在远程服务中查询国家地区
     *
     * @param countryRegionIdList 国家地区ID
     * @return 国家地区
     */
    List<CountryRegionDTO> listCountryRegion(Set<Pair<Long, Long>> countryRegionIdList);

    /**
     * 查询公司国家、地区
     *
     * @param addressCountryRegionParam 地区国家查询参数
     * @return AddressCountryRegionDTO
     */
    AddressCountryRegionDTO queryAddressCountryRegion(AddressCountryRegionParamDTO addressCountryRegionParam);
}
