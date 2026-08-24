package org.hzero.oauth.security.custom.event;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationListener;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import org.hzero.oauth.domain.service.AuditLoginService;
import org.hzero.oauth.security.event.LogoutEvent;
import org.hzero.oauth.security.service.LoginRecordService;

/**
 * @author bojiangzhou 2020/08/24
 */
@Order(0)
@Component
public class LogoutAuditListener implements ApplicationListener<LogoutEvent> {

    @Autowired
    private AuditLoginService auditLoginService;
    @Autowired
    private LoginRecordService loginRecordService;

    @Override
    public void onApplicationEvent(LogoutEvent event) {
        String clientId = auditLoginService.getClientId(event.getServletRequest());
        auditLoginService.addLogOutRecord(event.getServletRequest(), loginRecordService.getLocalLoginUser(), clientId);
    }
}
