package org.hzero.platform.app.service.impl;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.collections4.CollectionUtils;
import org.hzero.platform.api.dto.AuditLoginExportForOrg;
import org.hzero.platform.api.dto.AuditLoginExportForSite;
import org.hzero.platform.app.service.AuditLoginService;
import org.hzero.platform.domain.entity.AuditLogin;
import org.hzero.platform.domain.repository.AuditLoginRepository;
import org.hzero.platform.infra.constant.Constants;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import io.choerodon.core.convertor.ApplicationContextHelper;
import io.choerodon.core.domain.Page;
import io.choerodon.mybatis.pagehelper.domain.PageRequest;

/**
 * 登录日志审计应用服务默认实现
 *
 * @author xingxing.wu@hand-china.com 2018-12-26 15:17:47
 */
@Service
public class AuditLoginServiceImpl implements AuditLoginService {

    @Autowired
    private AuditLoginRepository auditLoginRepository;

    @Override
    public List<AuditLoginExportForSite> exportForSite(AuditLogin auditLogin) {
        List<AuditLoginExportForSite> auditLoginExportList = new ArrayList<>();
        List<AuditLogin> auditLogins = auditLoginRepository.listAuditLogin(auditLogin);
        for (AuditLogin localAuditLogin : auditLogins) {
            AuditLoginExportForSite localAuditLoginExportForSite = new AuditLoginExportForSite();
            BeanUtils.copyProperties(localAuditLogin, localAuditLoginExportForSite);
            auditLoginExportList.add(localAuditLoginExportForSite);
        }
        return auditLoginExportList;
    }

    @Override
    public List<AuditLoginExportForOrg> exportForOrg(AuditLogin auditLogin) {
        List<AuditLoginExportForOrg> auditLoginExportList = new ArrayList<>();
        List<AuditLogin> auditLogins = auditLoginRepository.listAuditLogin(auditLogin);
        for (AuditLogin localAuditLogin : auditLogins) {
            AuditLoginExportForOrg localAuditLoginExportForOrg = new AuditLoginExportForOrg();
            BeanUtils.copyProperties(localAuditLogin, localAuditLoginExportForOrg);
            auditLoginExportList.add(localAuditLoginExportForOrg);
        }
        return auditLoginExportList;
    }

    @Override
    public void clearLog(String clearType, Long tenantId) {
        LocalDate now = LocalDate.now();
        AuditLoginService service = ApplicationContextHelper.getContext().getBean(AuditLoginService.class);
        switch (clearType) {
            case Constants.ClearLogType.THREE_DAY:
                service.asyncClearLog(now.minus(3, ChronoUnit.DAYS), tenantId);
                break;
            case Constants.ClearLogType.ONE_WEEK:
                service.asyncClearLog(now.minus(1, ChronoUnit.WEEKS), tenantId);
                break;
            case Constants.ClearLogType.ONE_MONTH:
                service.asyncClearLog(now.minus(1, ChronoUnit.MONTHS), tenantId);
                break;
            case Constants.ClearLogType.THREE_MONTH:
                service.asyncClearLog(now.minus(3, ChronoUnit.MONTHS), tenantId);
                break;
            case Constants.ClearLogType.SIX_MONTH:
                service.asyncClearLog(now.minus(6, ChronoUnit.MONTHS), tenantId);
                break;
            case Constants.ClearLogType.ONE_YEAR:
                service.asyncClearLog(now.minus(1, ChronoUnit.YEARS), tenantId);
                break;
            default:
                break;
        }
    }

    @Async("commonAsyncTaskExecutor")
    @Override
    public void asyncClearLog(LocalDate localDate, Long tenantId) {
        PageRequest pageRequest = new PageRequest(0, 1000);
        Page<Long> page;
        do {
            page = auditLoginRepository.listLogId(localDate, tenantId, pageRequest);
            List<Long> list = page.getContent();
            if (CollectionUtils.isNotEmpty(list)) {
                // 删除日志记录
                auditLoginRepository.batchDeleteById(list);
            }
        } while (CollectionUtils.isNotEmpty(page.getContent()));
    }
}
