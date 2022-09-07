package org.hzero.platform.infra.mapper;

import io.choerodon.mybatis.common.BaseMapper;
import org.apache.ibatis.annotations.Param;
import org.hzero.platform.api.dto.CountryDTO;
import org.hzero.platform.domain.entity.Country;

import java.util.List;
import java.util.Set;

/**
 * <p>
 * 国家定义数据库操作映射
 * </p>
 *
 * @author qingsheng.chen 2018/6/21 星期四 15:25
 */
public interface CountryMapper extends BaseMapper<Country> {
    /**
     * 查询国家
     *
     * @param tenantId    租户ID
     * @param condition   查询条件，国家地区或者
     * @param enabledFlag 筛选条件
     * @return 国家列表
     */
    List<Country> listCountryByCondition(@Param("tenantId") Long tenantId, @Param("condition") String condition, @Param("enabledFlag") Integer enabledFlag);

    /**
     * 国家ID
     *
     * @param ids PrimaryKeyList
     * @return 国家
     */
    List<Country> selectByPrimaryKeyList(@Param("ids") List<Long> ids);

    /**
     * 批量查询国家
     *
     * @param countryIds 国家ID列表
     * @return List<CountryDTO>
     */
    List<CountryDTO> queryCountryByPrimaryKeys(@Param("countryIds") Set<Long> countryIds);
}
