package org.hzero.oauth.security.custom.event;

import javax.annotation.Nonnull;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationListener;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import io.choerodon.core.oauth.DetailsHelper;

import org.hzero.oauth.domain.service.AuditLoginService;
import org.hzero.oauth.security.event.AuthorizeEvent;

/**
 *
 * @author bojiangzhou 2020/08/24
 */
@Order(50)
@Component
public class LoginAuditListener implements ApplicationListener<AuthorizeEvent> {

    @Autowired
    private AuditLoginService auditLoginService;

    @Override
    public void onApplicationEvent(@Nonnull AuthorizeEvent event) {
        auditLoginService.addLoginRecord(
                event.getServletRequest(),
                event.getAccessToken().getValue(),
                event.getAuthorizationRequest().getClientId(),
                DetailsHelper.getUserDetails()
        );
    }
}
