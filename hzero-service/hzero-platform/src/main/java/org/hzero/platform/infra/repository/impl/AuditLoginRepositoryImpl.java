package org.hzero.platform.infra.repository.impl;

import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;

import org.hzero.boot.platform.lov.annotation.ProcessLovValue;
import org.hzero.mybatis.base.impl.BaseRepositoryImpl;
import org.hzero.platform.api.dto.OnLineUserDTO;
import org.hzero.platform.domain.entity.AuditLogin;
import org.hzero.platform.domain.repository.AuditLoginRepository;
import org.hzero.platform.infra.mapper.AuditLoginMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import io.choerodon.core.domain.Page;
import io.choerodon.core.oauth.CustomUserDetails;
import io.choerodon.core.oauth.DetailsHelper;
import io.choerodon.mybatis.pagehelper.PageHelper;
import io.choerodon.mybatis.pagehelper.domain.PageRequest;

/**
 * 登录日志审计 资源库实现
 *
 * @author xingxing.wu@hand-china.com 2018-12-26 15:17:47
 */
@Component
public class AuditLoginRepositoryImpl extends BaseRepositoryImpl<AuditLogin> implements AuditLoginRepository {

    @Autowired
    private AuditLoginMapper auditLoginMapper;

    @Override
    @ProcessLovValue
    public Page<AuditLogin> pageAuditLogin(PageRequest pageRequest, AuditLogin auditLogin) {
        return PageHelper.doPageAndSort(pageRequest, () -> auditLoginMapper.listAuditLogin(auditLogin));
    }

    @Override
    @ProcessLovValue
    public List<AuditLogin> listAuditLogin(AuditLogin auditLogin) {
        return auditLoginMapper.listAuditLogin(auditLogin);
    }

    @Override
    public List<AuditLogin> getSelfTenantsWithLogs() {
        CustomUserDetails userDetails = DetailsHelper.getUserDetails();
        List<AuditLogin> resultList = new ArrayList<>();
        List<AuditLogin> auditLogins = auditLoginMapper.selectSelfTenantsWithLogs(userDetails.getUserId());
        Map<Long, List<AuditLogin>> resultMap =
                auditLogins.stream().collect(
                        Collectors.groupingBy(AuditLogin::getTenantId));
        resultMap.values().forEach(x -> {
            if (x.get(0).getLoginDate() != null) {
                Optional<AuditLogin> collect = x.stream().max(Comparator.comparing(AuditLogin::getLoginDate));
                resultList.add(collect.orElse(new AuditLogin()));
            } else {
                resultList.add(x.get(0));
            }
        });
        return resultList;
    }

    @Override
    public List<AuditLogin> listUserInfo(List<String> accessTokenList) {
        return auditLoginMapper.listUserInfo(accessTokenList);
    }

    @Override
    public List<OnLineUserDTO> listUserTenant(List<Long> tenantIdList) {
        return auditLoginMapper.listUserTenant(tenantIdList);
    }

    @Override
    public Page<Long> listLogId(LocalDate time, Long tenantId, PageRequest pageRequest) {
        return PageHelper.doPage(pageRequest, () -> auditLoginMapper.listLogIdByTime(time, tenantId));
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void batchDeleteById(List<Long> auditLoginIds) {
        auditLoginMapper.batchDeleteById(auditLoginIds);
    }
}
