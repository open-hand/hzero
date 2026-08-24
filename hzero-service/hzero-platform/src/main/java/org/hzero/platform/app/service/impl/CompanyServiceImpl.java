package org.hzero.platform.app.service.impl;

import java.util.List;
import java.util.Objects;

import org.apache.commons.collections4.CollectionUtils;
import org.hzero.core.base.BaseConstants;
import org.hzero.platform.app.service.CodeRuleService;
import org.hzero.platform.app.service.CompanyService;
import org.hzero.platform.domain.entity.Company;
import org.hzero.platform.domain.repository.CompanyRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.Assert;

import io.choerodon.core.oauth.CustomUserDetails;
import io.choerodon.core.oauth.DetailsHelper;
/**
 * 公司信息应用服务默认实现
 *
 * @author gaokuo.dai@hand-china.com 2018-07-04 19:49:15
 */
@Service
public class CompanyServiceImpl implements CompanyService {

    @Autowired
    private CompanyRepository companyRepository;
    @Autowired
    private CodeRuleService codeRuleService;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Company insertOrUpdate(Company company) {
        if(company == null) {
            return null;
        }
        if(company.getCompanyId() == null) {
            // insert
            company.insertIntoDb(this.companyRepository, this.codeRuleService);
        }else {
            // update
            // check
            Company companyInDb = this.companyRepository.selectByPrimaryKey(company);
            Assert.notNull(companyInDb, BaseConstants.ErrorCode.DATA_NOT_EXISTS);
            Assert.isTrue(Objects.equals(companyInDb.getTenantId(), company.getTenantId()), BaseConstants.ErrorCode.DATA_INVALID);
            company = companyInDb.updateBasedOnGivenData(company, this.companyRepository);
        }
        return company;
    }
    
    @Override
    @Transactional(rollbackFor = Exception.class)
    public List<Company> batchInsertOrUpdate(List<Company> companies) {
        if(CollectionUtils.isEmpty(companies)) {
            return companies;
        }
        for(Company company : companies) {
            this.insertOrUpdate(company);
        }
        return companies;
    }

    @Override
    public List<Company> listByUser(Long tenantId, String companyNum, String companyName, Integer enabledFlag) {
        CustomUserDetails user = DetailsHelper.getUserDetails();
        Assert.notNull(user, BaseConstants.ErrorCode.DATA_INVALID);
        return companyRepository.selectCompaniesByUser(tenantId, user.getUserId(), companyNum, companyName, enabledFlag);
    }


    @Override
    @Transactional(rollbackFor = Exception.class)
    public Company enable(Company company) {
        Assert.notNull(company, BaseConstants.ErrorCode.DATA_INVALID);
        Assert.notNull(company.getCompanyId(), BaseConstants.ErrorCode.DATA_INVALID);
        company.setEnabledFlag(BaseConstants.Flag.YES);
        this.companyRepository.updateOptional(company, Company.FIELD_ENABLED_FLAG);
        return company;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Company disable(Company company) {
        Assert.notNull(company, BaseConstants.ErrorCode.DATA_INVALID);
        Assert.notNull(company.getCompanyId(), BaseConstants.ErrorCode.DATA_INVALID);
        company.setEnabledFlag(BaseConstants.Flag.NO);
        this.companyRepository.updateOptional(company, Company.FIELD_ENABLED_FLAG);
        return company;
    }

}
