package org.hzero.platform.app.service;

import java.util.List;

import org.hzero.platform.domain.entity.Company;

/**
 * 公司信息应用服务
 *
 * @author gaokuo.dai@hand-china.com 2018-07-04 19:49:15
 */
public interface CompanyService {
    
    
    /**
     * 启用公司
     * @param company
     * @return
     */
    Company enable(Company company);

    /**
     * 禁用公司
     * @param company
     * @return
     */
    Company disable(Company company);
    
    /**
     * 新建或更新公司
     * @param company
     * @return
     */
    Company insertOrUpdate(Company company);
    
    /**
     * 批量新建或更新公司
     * @param companies
     * @return
     */
    List<Company> batchInsertOrUpdate(List<Company> companies);

    /**
     *
     *
     *
     * @param tenantId
     * @param companyNum
     * @param companyName
     * @param enabledFlag
     * @return List<Company>
     */
    List<Company> listByUser(Long tenantId, String companyNum, String companyName, Integer enabledFlag);
}
