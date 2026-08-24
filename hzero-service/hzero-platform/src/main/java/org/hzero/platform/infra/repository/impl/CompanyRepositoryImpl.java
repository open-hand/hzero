package org.hzero.platform.infra.repository.impl;

import org.apache.commons.lang3.StringUtils;
import org.hzero.mybatis.base.impl.BaseRepositoryImpl;
import org.hzero.mybatis.domian.Condition;
import org.hzero.mybatis.util.Sqls;
import org.hzero.platform.domain.entity.Company;
import org.hzero.platform.domain.repository.CompanyRepository;
import org.hzero.platform.infra.mapper.CompanyMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * 公司信息 资源库实现
 *
 * @author gaokuo.dai@hand-china.com 2018-07-04 19:49:15
 */
@Component
public class CompanyRepositoryImpl extends BaseRepositoryImpl<Company> implements CompanyRepository {

    @Autowired
    private CompanyMapper companyMapper;

    @Override
    public List<Company> selectCompanies(Company company) {
        return this.selectByCondition(Condition.builder(Company.class)
                .andWhere(Sqls.custom().andEqualTo(Company.FIELD_TENANT_ID, company.getTenantId())
                        .andLike(Company.FIELD_COMPANY_NAME, company.getCompanyName(), true)
                        .andLike(Company.FIELD_COMPANY_NUM, company.getCompanyNum(), true))
                .orderByAsc(Company.FIELD_COMPANY_NUM)
                .build());
    }

    @Override
    public Company selectByNumber(String companyNum) {
        if (StringUtils.isEmpty(companyNum)) {
            return null;
        }
        Company queryParam = new Company();
        queryParam.setCompanyNum(companyNum);
        return this.companyMapper.selectOne(queryParam);
    }

    @Override
    public List<Company> selectCompaniesByUser(Long tenantId, Long userId, String companyNum, String companyName, Integer enabledFlag) {
        return this.companyMapper.selectCompaniesByUser(tenantId, userId, companyNum, companyName, enabledFlag);
    }

    @Override
    public int selectRepeatCount(Company company) {
        return this.companyMapper.selectRepeatCount(company);
    }

}
