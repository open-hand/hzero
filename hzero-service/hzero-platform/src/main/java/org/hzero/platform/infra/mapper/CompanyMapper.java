package org.hzero.platform.infra.mapper;

import java.util.List;

import org.apache.ibatis.annotations.Param;
import org.hzero.platform.domain.entity.Company;
import io.choerodon.mybatis.common.BaseMapper;

/**
 * 公司信息Mapper
 *
 * @author gaokuo.dai@hand-china.com 2018-07-04 19:49:15
 */
public interface CompanyMapper extends BaseMapper<Company> {
    
    /**
     * 查询当前用户所属公司
     *
     * @param tenantId
     * @param userId
     * @param companyNum
     * @param companyName
     * @param enabledFlag
     * @return List<Company>
     */
    List<Company> selectCompaniesByUser(@Param("tenantId") Long tenantId, @Param("userId") Long userId, @Param("companyNum") String companyNum, @Param("companyName") String companyName, @Param("enabledFlag") Integer enabledFlag);
    
    /**
     * 查询重复的编码数量
     * 
     * @param company
     * @return
     */
    int selectRepeatCount(Company company);

}
