package org.hzero.platform.domain.repository;

import io.choerodon.core.domain.Page;
import io.choerodon.mybatis.pagehelper.domain.PageRequest;
import org.hzero.mybatis.base.BaseRepository;
import org.hzero.platform.api.dto.CountryDTO;
import org.hzero.platform.domain.entity.Country;

import java.util.List;
import java.util.Set;

/**
 * <p>
 * 国家定义仓库接口
 * </p>
 *
 * @author qingsheng.chen 2018/6/21 星期四 14:39
 */
public interface CountryRepository extends BaseRepository<Country> {
    /**
     * 查询国家
     *
     * @param tenantId    租户ID
     * @param pageRequest 分页信息
     * @param condition   查询条件，国家地区或者
     * @param enabledFlag 筛选条件
     * @return 国家列表
     */
    Page<Country> selectByCondition(PageRequest pageRequest, Long tenantId, Integer enabledFlag, String condition);

    /**
     * 查询国家
     *
     * @param tenantId    租户ID
     * @param condition   查询条件，国家地区或者
     * @param enabledFlag 筛选条件
     * @return 国家列表
     */
    List<Country> selectByConditionAll(Long tenantId, Integer enabledFlag, String condition);

    /**
     * 国家ID
     *
     * @param ids PrimaryKeyList
     * @return 国家
     */
    List<Country> selectByPrimaryKeyList(List<Long> ids);

    /**
     * 批量查询国家
     *
     * @param countryIds 国家ID列表
     * @return List<CountryDTO>
     */
    List<CountryDTO> queryCountryByPrimaryKeys(Set<Long> countryIds);
}
