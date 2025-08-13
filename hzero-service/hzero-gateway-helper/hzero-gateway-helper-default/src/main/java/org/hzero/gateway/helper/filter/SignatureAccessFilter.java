package org.hzero.gateway.helper.filter;


import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.BooleanUtils;
import org.hzero.core.base.BaseConstants;
import org.hzero.gateway.helper.api.HelperFilter;
import org.hzero.gateway.helper.config.GatewayHelperProperties;
import org.hzero.gateway.helper.entity.CheckState;
import org.hzero.gateway.helper.entity.RequestContext;
import org.hzero.gateway.helper.service.SignatureService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.Assert;

import java.util.Optional;

/**
 * 验证码签名接口
 *
 * @author bojiangzhou 2019/12/26
 */
public class SignatureAccessFilter implements HelperFilter {

    private final GatewayHelperProperties properties;
    private final SignatureService signatureService;
    private final boolean enabled;
    private final String signLabel;

    public SignatureAccessFilter(GatewayHelperProperties properties,
                                 @Autowired(required = false) SignatureService signatureService) {
        this.properties = properties;
        if (properties.getSignature().isEnabled()) {
            Assert.notNull(signatureService, "No qualifying bean of type 'org.hzero.gateway.helper.service.SignatureService' available.");
        }
        this.signatureService = signatureService;
        this.enabled = properties.getSignature().isEnabled();
        this.signLabel = properties.getSignature().getSignLabel();
    }

    @Override
    public int filterOrder() {
        return 25;
    }

    @Override
    public boolean shouldFilter(RequestContext context) {
        return enabled &&
                (BooleanUtils.isTrue(context.getPermission().getSignAccess()) ||
                        ArrayUtils.contains(Optional.ofNullable(context.getPermission().getTag()).orElse("").split(BaseConstants.Symbol.COMMA), signLabel));
    }

    @Override
    public boolean run(RequestContext context) {
        boolean pass = signatureService.verifySignature(context);
        if (pass) {
            context.response.setStatus(CheckState.SUCCESS_SIGNATURE_ACCESS);
            context.response.setMessage("Have access to this 'signAccess' interface, permission: " + context.getPermission());
        } else {
            context.response.setStatus(CheckState.PERMISSION_NOT_PASS_SIGNATURE);
            context.response.setMessage("No access to this 'signAccess' interface, permission: " + context.getPermission());
        }
        return false;
    }


}
