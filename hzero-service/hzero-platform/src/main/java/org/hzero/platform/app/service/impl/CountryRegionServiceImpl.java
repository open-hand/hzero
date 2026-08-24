package org.hzero.platform.app.service.impl;

import org.hzero.core.util.Pair;
import org.hzero.platform.api.dto.AddressCountryRegionDTO;
import org.hzero.platform.api.dto.AddressCountryRegionParamDTO;
import org.hzero.platform.api.dto.CountryRegionDTO;
import org.hzero.platform.app.service.CountryRegionService;
import org.hzero.platform.app.service.CountryService;
import org.hzero.platform.app.service.RegionService;
import org.hzero.platform.domain.entity.Country;
import org.hzero.platform.domain.entity.Region;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * <p>
 * 国家地区组合查询API接口实现类
 * </p>
 *
 * @author qingsheng.chen 2018/7/3 星期二 10:36
 */
@Service
public class CountryRegionServiceImpl implements CountryRegionService {
    private static final String DELIM = "|";
    private CountryService countryService;
    private RegionService regionService;

    @Autowired
    public CountryRegionServiceImpl(CountryService countryService, RegionService regionService) {
        this.countryService = countryService;
        this.regionService = regionService;
    }

    @Override
    public CountryRegionDTO queryCountryRegion(Long countryId, Long regionId) {
        Country country = countryService.queryCountry(countryId);
        if (country == null) {
            return null;
        }
        return buildPathName(
                new CountryRegionDTO().setCountryId(countryId)
                        .setCountryCode(country.getCountryCode())
                        .setCountryName(country.getCountryName()),
                regionId,
                regionId != null
                        ? regionService.listRegionWithPossibleParent(countryId, regionId)
                        .stream()
                        .collect(Collectors.toMap(Region::getRegionId, Function.identity()))
                        : Collections.emptyMap());
    }

    @Override
    public List<CountryRegionDTO> listCountryRegion(Set<Pair<Long, Long>> countryRegionIdList) {
        List<CountryRegionDTO> countryRegionList = new ArrayList<>();
        if (CollectionUtils.isEmpty(countryRegionIdList)) {
            return countryRegionList;
        }
        Map<Long, Country> countryMap = countryService.listCountry(countryRegionIdList
                .stream()
                .map(Pair::getFirst)
                .collect(Collectors.toList()))
                .stream()
                .collect(Collectors.toMap(Country::getCountryId, Function.identity()));
        for (Pair<Long, Long> countryRegionId : countryRegionIdList) {
            if (countryMap.containsKey(countryRegionId.getFirst())) {
                Country country = countryMap.get(countryRegionId.getFirst());
                countryRegionList.add(buildPathName(
                        new CountryRegionDTO().setCountryId(countryRegionId.getFirst())
                                .setCountryCode(country.getCountryCode())
                                .setCountryName(country.getCountryName()),
                        countryRegionId.getSecond(),
                        countryRegionId.getSecond() != null
                                ? regionService.listRegionWithPossibleParent(countryRegionId.getFirst(), countryRegionId.getSecond())
                                .stream()
                                .collect(Collectors.toMap(Region::getRegionId, Function.identity()))
                                : Collections.emptyMap()));
            }
        }
        return countryRegionList;
    }

    @Override
    public AddressCountryRegionDTO queryAddressCountryRegion(AddressCountryRegionParamDTO addressCountryRegionParam) {
        AddressCountryRegionDTO addressCountryRegion = new AddressCountryRegionDTO();
        if (!CollectionUtils.isEmpty(addressCountryRegionParam.getCountryIds())) {
            addressCountryRegion.setCountrys(countryService.queryCountryByPrimaryKeys(addressCountryRegionParam.getCountryIds()));
        }
        if (!CollectionUtils.isEmpty(addressCountryRegionParam.getRegionIds())) {
            addressCountryRegion.setRegions(regionService.queryRegionByPrimaryKeys(addressCountryRegionParam.getRegionIds()));
        }
        return addressCountryRegion;
    }

    private CountryRegionDTO buildPathName(CountryRegionDTO countryRegion, Long regionId, Map<Long, Region> regionMap) {
        List<String> path = new ArrayList<>();
        LinkedList<Long> regionIds = new LinkedList<>();
        while (regionMap.containsKey(regionId)) {
            Region region = regionMap.get(regionId);
            path.add(0, region.getRegionName());
            regionIds.add(0, region.getRegionId());
            if (region.getParentRegionId() == null) {
                break;
            }
            regionId = region.getParentRegionId();
        }
        return countryRegion.setRegionPathName(StringUtils.collectionToDelimitedString(path, DELIM)).setRegionIds(regionIds);
    }
}
