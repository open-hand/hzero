package org.hzero.platform.infra.repository.impl;

import io.choerodon.core.domain.Page;
import io.choerodon.mybatis.pagehelper.PageHelper;
import io.choerodon.mybatis.pagehelper.domain.PageRequest;
import org.hzero.mybatis.base.impl.BaseRepositoryImpl;
import org.hzero.platform.api.dto.CountryDTO;
import org.hzero.platform.domain.entity.Country;
import org.hzero.platform.domain.repository.CountryRepository;
import org.hzero.platform.infra.mapper.CountryMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Set;

/**
 * <p>
 * 国家定义仓库接口实现类
 * </p>
 *
 * @author qingsheng.chen 2018/6/21 星期四 14:40
 */
@Component
public class CountryRepositoryImpl extends BaseRepositoryImpl<Country> implements CountryRepository {
    private CountryMapper countryMapper;

    @Autowired
    public CountryRepositoryImpl(CountryMapper countryMapper) {
        this.countryMapper = countryMapper;
    }


    @Override
    public Page<Country> selectByCondition(PageRequest pageRequest, Long tenantId, Integer enabledFlag, String condition) {
        return PageHelper.doPageAndSort(pageRequest, () -> countryMapper.listCountryByCondition(tenantId, condition, enabledFlag));
    }

    @Override
    public List<Country> selectByConditionAll(Long tenantId, Integer enabledFlag, String condition) {
        return countryMapper.listCountryByCondition(tenantId, condition, enabledFlag);
    }

    @Override
    public List<Country> selectByPrimaryKeyList(List<Long> ids) {
        return countryMapper.selectByPrimaryKeyList(ids);
    }

    @Override
    public List<CountryDTO> queryCountryByPrimaryKeys(Set<Long> countryIds) {
        return countryMapper.queryCountryByPrimaryKeys(countryIds);
    }
}
