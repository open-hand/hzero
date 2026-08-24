package org.hzero.platform.app.service;


import io.choerodon.core.domain.Page;
import io.choerodon.mybatis.pagehelper.domain.PageRequest;
import org.hzero.platform.api.dto.CountryDTO;
import org.hzero.platform.domain.entity.Country;

import java.util.List;
import java.util.Set;

/**
 * <p>
 * 国家定义接口
 * </p>
 *
 * @author qingsheng.chen 2018/6/21 星期四 14:19
 */
public interface CountryService {
    /**
     * 查询所有国家
     *
     * @param tenantId    租户ID
     * @param condition   国家定义查询参数，国家编码或者国家名称模糊查询
     * @param enabledFlag 筛选条件
     * @param pageRequest 分页排序
     * @return 所有查询的国家
     */
    Page<Country> pageCountryByCondition(Long tenantId, String condition, Integer enabledFlag, PageRequest pageRequest);

    /**
     * 查询所有国家
     *
     * @param tenantId    租户ID
     * @param condition   国家定义查询参数，国家编码或者国家名称模糊查询
     * @param enabledFlag 筛选条件
     * @return 所有查询的国家
     */
    List<Country> listCountryByCondition(Long tenantId, String condition, Integer enabledFlag);

    /**
     * 根据国家ID查询国家
     *
     * @param countryId 国家ID
     * @return 国家
     */
    Country queryCountry(long countryId);

    /**
     * 根据国家ID批量查询国家
     *
     * @param countryIdList 国家ID
     * @return 国家
     */
    List<Country> listCountry(List<Long> countryIdList);

    /**
     * 创建国家定义
     *
     * @param country 国家信息
     * @return 创建完成之后的国家
     */
    Country createCountry(Country country);

    /**
     * 更新国家定义
     *
     * @param country 国家信息
     * @return 更新完成之后的国家
     */
    Country updateCountry(Country country);

    /**
     * 批量禁用国家定义
     *
     * @param countryList 国家定义信息列表
     * @return 禁用后国家定义信息列表
     */
    List<Country> batchDisableCountry(List<Country> countryList);

    /**
     * 批量查询国家
     *
     * @param countryIds 国家ID
     * @return List<CountryDTO>
     */
    List<CountryDTO> queryCountryByPrimaryKeys(Set<Long> countryIds);
}
