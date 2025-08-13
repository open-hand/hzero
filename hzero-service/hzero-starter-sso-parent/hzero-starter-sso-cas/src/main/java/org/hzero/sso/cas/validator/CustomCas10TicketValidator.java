package org.hzero.sso.cas.validator;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.StringReader;

import org.apache.commons.lang3.StringUtils;
import org.jasig.cas.client.validation.AbstractCasProtocolUrlBasedTicketValidator;
import org.jasig.cas.client.validation.Assertion;
import org.jasig.cas.client.validation.AssertionImpl;
import org.jasig.cas.client.validation.TicketValidationException;
import org.springframework.lang.Nullable;

/**
 * 自定义Cas10 Ticket校验器
 *
 * @author berg-turing 2022/03/18
 */
public class CustomCas10TicketValidator extends AbstractCasProtocolUrlBasedTicketValidator implements ConfigurableTicketValidator {
    /**
     * 默认到url后缀
     */
    private static final String DEFAULT_URL_SUFFIX = "validate";
    /**
     * url后缀
     */
    private String urlSuffix = DEFAULT_URL_SUFFIX;

    public CustomCas10TicketValidator(final String casServerUrlPrefix) {
        super(casServerUrlPrefix);
    }

    @Override
    protected String getUrlSuffix() {
        return StringUtils.isNotBlank(this.urlSuffix) ?
                this.urlSuffix : DEFAULT_URL_SUFFIX;
    }

    @Override
    protected Assertion parseResponseFromServer(final String response) throws TicketValidationException {
        if (!response.startsWith("yes")) {
            throw new TicketValidationException("CAS Server could not validate ticket.");
        }

        try {
            final BufferedReader reader = new BufferedReader(new StringReader(response));
            reader.readLine();
            final String name = reader.readLine();

            return new AssertionImpl(name);
        } catch (final IOException e) {
            throw new TicketValidationException("Unable to parse response.", e);
        }
    }

    @Override
    public void setUrlSuffix(@Nullable String urlSuffix) {
        this.urlSuffix = urlSuffix;
    }
}
