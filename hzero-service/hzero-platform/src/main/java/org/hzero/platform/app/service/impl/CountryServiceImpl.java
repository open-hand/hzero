package org.hzero.platform.app.service.impl;

import io.choerodon.core.domain.Page;
import io.choerodon.core.exception.CommonException;
import io.choerodon.mybatis.pagehelper.domain.PageRequest;
import io.choerodon.mybatis.service.BaseServiceImpl;
import org.hzero.core.base.BaseConstants;
import org.hzero.platform.api.dto.CountryDTO;
import org.hzero.platform.app.service.CountryService;
import org.hzero.platform.domain.entity.Country;
import org.hzero.platform.domain.repository.CountryRepository;
import org.hzero.platform.infra.constant.HpfmMsgCodeConstants;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Set;

/**
 * <p>
 * 国家编码接口实现类
 * </p>
 *
 * @author qingsheng.chen 2018/6/21 星期四 14:31
 */
@Service
public class CountryServiceImpl extends BaseServiceImpl<Country> implements CountryService {
    private CountryRepository countryRepository;

    @Autowired
    public CountryServiceImpl(CountryRepository countryRepository) {
        this.countryRepository = countryRepository;
    }

    @Override
    public Page<Country> pageCountryByCondition(Long tenantId, String condition, Integer enabledFlag, PageRequest pageRequest) {
        return countryRepository.selectByCondition(pageRequest, tenantId, enabledFlag, condition);
    }

    @Override
    public List<Country> listCountryByCondition(Long tenantId, String condition, Integer enabledFlag) {
        return countryRepository.selectByConditionAll(tenantId, enabledFlag, condition);
    }

    @Override
    public Country queryCountry(long countryId) {
        return countryRepository.selectByPrimaryKey(countryId);
    }

    @Override
    public List<Country> listCountry(List<Long> countryIdList) {
        return countryRepository.selectByPrimaryKeyList(countryIdList);
    }

    @Override
    public Country createCountry(Country country) {
        validateCountryCodeRepeat(country);
        validateCountryNameRepeat(country);
        countryRepository.insertSelective(country);
        return country;
    }

    @Override
    public Country updateCountry(Country country) {
        Country exists = countryRepository.selectByPrimaryKey(country.getCountryId());
        if (exists == null) {
            throw new CommonException(BaseConstants.ErrorCode.DATA_NOT_EXISTS);
        }
        country.setCountryCode(exists.getCountryCode());
        countryRepository.updateOptional(country, Country.FIELD_COUNTRY_NAME,
                Country.FIELD_ABBREVIATION, Country.FIELD_QUICK_INDEX,
                Country.FIELD_ENABLE_FLAG);
        return country;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public List<Country> batchDisableCountry(List<Country> countryList) {
        for (Country country : countryList) {
            country.setCountryCode(null).setCountryName(null).setEnabledFlag(BaseConstants.Flag.NO);
        }
        return countryRepository.batchUpdateByPrimaryKeySelective(countryList);
    }

    @Override
    public List<CountryDTO> queryCountryByPrimaryKeys(Set<Long> countryIds) {
        return countryRepository.queryCountryByPrimaryKeys(countryIds);
    }

    private void validateCountryCodeRepeat(Country country) {
        // 验证国家Code是否重复
        List<Country> countryList = countryRepository.select(new Country().setCountryCode(country.getCountryCode()).setTenantId(country.getTenantId()));
        if (!countryList.isEmpty()) {
            throw new CommonException(BaseConstants.ErrorCode.ERROR_CODE_REPEAT);
        }
    }

    private void validateCountryNameRepeat(Country country) {
        // 验证国家名称是否重复
        List<Country> countryList = countryRepository.select(new Country().setCountryName(country.getCountryName()).setTenantId(country.getTenantId()));
        if (!countryList.isEmpty()) {
            throw new CommonException(HpfmMsgCodeConstants.ERROR_MULTIPLE_NAME);
        }
    }
}
