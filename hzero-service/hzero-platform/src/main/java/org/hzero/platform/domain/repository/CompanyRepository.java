package org.hzero.platform.domain.repository;

import java.util.List;

import org.hzero.mybatis.base.BaseRepository;
import org.hzero.platform.domain.entity.Company;

/**
 * 公司信息资源库
 *
 * @author gaokuo.dai@hand-china.com 2018-07-04 19:49:15
 */
public interface CompanyRepository extends BaseRepository<Company> {
    
    /**
     * 
     * description
     * @param company
     * @return
     */
    List<Company> selectCompanies(Company company);
    
    /**
     * 根据公司编号查询公司
     * @param companyNum
     * @return
     */
    Company selectByNumber(String companyNum);
    
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
    List<Company> selectCompaniesByUser(Long tenantId, Long userId, String companyNum, String companyName, Integer enabledFlag);
    
    /**
     * 查询重复的编码数量
     * 
     * @param company
     * @return
     */
    int selectRepeatCount(Company company);
    
}
