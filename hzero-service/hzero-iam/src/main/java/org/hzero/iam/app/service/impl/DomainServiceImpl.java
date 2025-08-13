package org.hzero.iam.app.service.impl;

import java.util.*;
import java.util.stream.Collectors;

import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.modelmapper.internal.util.Assert;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import io.choerodon.core.domain.Page;
import io.choerodon.mybatis.pagehelper.PageHelper;
import io.choerodon.mybatis.pagehelper.domain.PageRequest;

import org.hzero.core.base.BaseConstants;
import org.hzero.iam.app.service.DomainService;
import org.hzero.iam.domain.entity.Domain;
import org.hzero.iam.domain.entity.DomainAssign;
import org.hzero.iam.domain.repository.DomainAssignRepository;
import org.hzero.iam.domain.repository.DomainRepository;

/**
 * 门户分配应用服务默认实现
 *
 * @author minghui.qiu@hand-china.com 2019-06-27 20:50:16
 */
@Service
public class DomainServiceImpl implements DomainService {

    private final Logger logger = LoggerFactory.getLogger(getClass());

    @Autowired
    private DomainRepository domainRepository;
    @Autowired
    private DomainAssignRepository domainAssignRepository;

    @Override
    public Page<Domain> selectByOptions(PageRequest pageRequest, Domain domain) {
        return PageHelper.doPageAndSort(pageRequest, () -> domainRepository.selectByOptions(domain));
    }

    @Override
    public Domain selectByDomainId(Long domainId) {
        return domainRepository.selectByDomainId(domainId);
    }

    @Override
    public int updateDomain(Domain domain) {
        domain.vaidateDomainUrl(domainRepository);
        Domain oldDomain = domainRepository.selectByPrimaryKey(domain.getDomainId());

        int cnt = domainRepository.updateOptional(domain, Domain.FIELD_DOMAIN_URL, Domain.FIELD_SSO_TYPE_CODE,
                        Domain.FIELD_SSO_SERVER_URL, Domain.FIELD_SSO_LOGIN_URL, Domain.FIELD_SSO_LOGOUT_URL,
                        Domain.FIELD_SSO_CLIENT_ID, Domain.FIELD_SSO_CLIENT_PWD, Domain.FIELD_SSO_USER_INFO,
                        Domain.FIELD_SAML_META_URL, Domain.FIELD_CLIENT_HOST_URL, Domain.FIELD_COMPANY_ID,
                        Domain.FIELD_LOGIN_NAME_FIELD, Domain.FIELD_REMARK);
        domainRepository.updateDomainCache(oldDomain, domain);
        return cnt;
    }

    @Override
    public int insertDomain(Domain domain) {
        domain.vaidateDomainUrl(domainRepository);
        int cnt = domainRepository.insertSelective(domain);
        domainRepository.updateDomainCache(null, domain);
        return cnt;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public int deleteDomain(Domain domain) {
        Domain exists = domainRepository.selectByPrimaryKey(domain.getDomainId());
        Assert.notNull(exists, BaseConstants.ErrorCode.DATA_NOT_EXISTS);
        // 先删除域名下关联分配的租户信息
        domainAssignRepository.delete(new DomainAssign().setDomainId(domain.getDomainId()));
        int cnt = domainRepository.deleteByPrimaryKey(domain.getDomainId());
        domainRepository.deleteDomainCache(exists);
        return cnt;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Map<String, Object> fixSsoDomain() {
        List<Domain> domains = domainRepository.selectAll();
        List<Domain> updateList = new ArrayList<>(domains.size());
        List<Domain> samlList = new ArrayList<>(domains.size());
        List<Domain> unknownList = new ArrayList<>(domains.size());
        List<DomainAssign> domainAssignList = new ArrayList<>(domains.size());
        for (Domain domain : domains) {
            // 域名分配关系
            DomainAssign domainAssign = new DomainAssign();
            domainAssign.setDomainId(domain.getDomainId());
            domainAssign.setTenantId(domain.getTenantId());
            domainAssign.setCompanyId(domain.getCompanyId());
            domainAssignList.add(domainAssign);

            // 1.4 到 1.5 主要变化的就是 clientHostUrl

            if (domain.getSsoTypeCode().toLowerCase().startsWith("cas")) {
                handleFix(domain, updateList, "/oauth/login/cas", "/oauth/sso/cas");
            }

            else if ("auth".equalsIgnoreCase(domain.getSsoTypeCode())) {
                handleFix(domain, updateList, "/oauth/login/auth", "/oauth/sso/auth");
            }

            else if ("azure".equalsIgnoreCase(domain.getSsoTypeCode())) {
                handleFix(domain, updateList, "/oauth/login/azure", "/oauth/sso/azure");
            }

            else if ("saml".equalsIgnoreCase(domain.getSsoTypeCode())) {
                samlList.add(domain);
            }

            else if ("idm".equalsIgnoreCase(domain.getSsoTypeCode())) {
                handleFix(domain, updateList, "/oauth/login/idm", "/oauth/sso/idm");
            }

            else {
                unknownList.add(domain);
            }
        }

        if (CollectionUtils.isNotEmpty(updateList)) {
            domainRepository.batchUpdateOptional(updateList, Domain.FIELD_CLIENT_HOST_URL);
        }

        if (CollectionUtils.isNotEmpty(domainAssignList)) {
            Iterator<DomainAssign> iterator = domainAssignList.iterator();
            while (iterator.hasNext()) {
                DomainAssign domainAssign = iterator.next();
                int count = domainAssignRepository.selectCount(domainAssign);
                if (count == 0) {
                    domainAssignRepository.insertSelective(domainAssign);
                } else {
                    iterator.remove();
                }
            }
        }

        // 全量更新缓存
        domainRepository.initCacheDomain();

        Map<String, Object> result = new HashMap<>(8);

        result.put("totalDomain", "总的的域名数量：" + domains.size());
        result.put("domainUpdate", "更新的域名数量：" + updateList.size());
        result.put("domainAssignInsert", "插入的域名分配数量：" + domainAssignList.size());
        result.put("unknownSsoTypeNotHandle", "未处理的域名（未知的单点登录类型），数量：" + unknownList.size() + ", 域名ID：" + unknownList.stream().map(Domain::getDomainId).collect(Collectors.toList()));
        result.put("samlNotHandle", "未处理的域名（SAML元数据地址需重新下载生成，自行处理），数量：" + samlList.size() + ", 域名ID：" + samlList.stream().map(Domain::getDomainId).collect(Collectors.toList()));

        return result;
    }



    private void handleFix(Domain domain, List<Domain> updateList, String searchStr, String replaceStr) {
        String clientHostUrl = domain.getClientHostUrl();
        int index = -1;
        if (StringUtils.isNotBlank(clientHostUrl) && (index = clientHostUrl.indexOf(searchStr)) > 0) {
            String newClientHostUrl = clientHostUrl.substring(0, index) + replaceStr;
            logger.info("Fix domain clientHostUrl, domainId: {}, ssoType:{}, originalUrl: [{}], newUrl: {}",
                    domain.getDomainId(), domain.getSsoTypeCode(), clientHostUrl, newClientHostUrl);
            domain.setClientHostUrl(newClientHostUrl);
            updateList.add(domain);
        }
    }

}
