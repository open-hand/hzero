package org.hzero.sso.cas.validator;

import org.apache.commons.lang3.StringUtils;
import org.jasig.cas.client.validation.Cas20ProxyTicketValidator;
import org.springframework.lang.Nullable;

/**
 * 自定义Cas20Proxy Ticket校验器
 *
 * @author berg-turing 2022/03/18
 */
public class CustomCas20ProxyTicketValidator extends Cas20ProxyTicketValidator implements ConfigurableTicketValidator {
    /**
     * url后缀
     */
    private String urlSuffix;

    public CustomCas20ProxyTicketValidator(String casServerUrlPrefix) {
        super(casServerUrlPrefix);
    }

    @Override
    protected String getUrlSuffix() {
        return StringUtils.isNotBlank(this.urlSuffix) ?
                this.urlSuffix : super.getUrlSuffix();
    }

    @Override
    public void setUrlSuffix(@Nullable String urlSuffix) {
        this.urlSuffix = urlSuffix;
    }
}
