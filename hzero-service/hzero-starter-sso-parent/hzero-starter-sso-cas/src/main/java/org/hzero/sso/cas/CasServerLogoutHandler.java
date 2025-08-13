package org.hzero.sso.cas;

import static org.hzero.sso.cas.CasAttributes.KEY_CAS_TICKET_TOKEN;
import static org.hzero.sso.cas.CasAttributes.KEY_CAS_TOKEN_TICKET;

import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.List;
import java.util.zip.Inflater;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.xml.bind.DatatypeConverter;

import org.apache.commons.lang3.StringUtils;
import org.jasig.cas.client.Protocol;
import org.jasig.cas.client.configuration.ConfigurationKeys;
import org.jasig.cas.client.util.CommonUtils;
import org.jasig.cas.client.util.XmlUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.hzero.core.redis.RedisHelper;
import org.hzero.sso.core.common.SsoServerLogoutHandler;

/**
 * @author bojiangzhou 2020/08/19
 */
public class CasServerLogoutHandler implements SsoServerLogoutHandler {

    private static final Logger LOGGER = LoggerFactory.getLogger(CasServerLogoutHandler.class);

    private final static int DECOMPRESSION_FACTOR = 10;

    private String artifactParameterName = Protocol.CAS2.getArtifactParameterName();
    private String logoutParameterName = ConfigurationKeys.LOGOUT_PARAMETER_NAME.getDefaultValue();
    private List<String> safeParameters = Arrays.asList(this.logoutParameterName, this.artifactParameterName);

    private final RedisHelper redisHelper;


    public CasServerLogoutHandler(RedisHelper redisHelper) {
        this.redisHelper = redisHelper;
    }

    @Override
    public boolean isLogoutRequest(HttpServletRequest request) {
        if ("POST".equalsIgnoreCase(request.getMethod())) {
            return !isMultipartRequest(request)
                    && CommonUtils.isNotBlank(CommonUtils.safeGetParameter(request, this.logoutParameterName, this.safeParameters));
        }

        if ("GET".equalsIgnoreCase(request.getMethod())) {
            return CommonUtils.isNotBlank(CommonUtils.safeGetParameter(request, this.logoutParameterName, this.safeParameters));
        }
        return false;
    }

    @Override
    public String handleLogoutRequest(HttpServletRequest request, HttpServletResponse response) {
        String logoutMessage = CommonUtils.safeGetParameter(request, this.logoutParameterName, this.safeParameters);
        LOGGER.debug("cas sso logout request, logoutMessage: [{}]", logoutMessage);
        if (CommonUtils.isBlank(logoutMessage)) {
            LOGGER.error("Could not locate logout message of the request from {}", this.logoutParameterName);
            return null;
        }

        if (!logoutMessage.contains("SessionIndex")) {
            logoutMessage = uncompressLogoutMessage(logoutMessage);
        }

        final String ticket = XmlUtils.getTextForElement(logoutMessage, "SessionIndex");

        if (StringUtils.isBlank(ticket)) {
            return null;
        }

        String token = obtainTokenByTicket(ticket);

        LOGGER.debug("cas logout request, ticket: [{}], token: [{}]", ticket, token);

        return token;
    }

    /**
     * 根据 ticket 移除关联的 token
     */
    private String obtainTokenByTicket(String ticket) {
        String token = redisHelper.strGet(KEY_CAS_TICKET_TOKEN + ticket);

        // 清除 ticket_token
        redisHelper.delKey(KEY_CAS_TICKET_TOKEN + ticket);
        redisHelper.delKey(KEY_CAS_TOKEN_TICKET + token);

        return token;
    }

    /**
     * @param name Name of the authentication token parameter.
     */
    public void setArtifactParameterName(final String name) {
        this.artifactParameterName = name;
        this.safeParameters = Arrays.asList(this.logoutParameterName, this.artifactParameterName);
    }

    /**
     * @param name Name of parameter containing CAS logout request message for SLO.
     */
    public void setLogoutParameterName(final String name) {
        this.logoutParameterName = name;
        this.safeParameters = Arrays.asList(this.logoutParameterName, this.artifactParameterName);
    }

    private boolean isMultipartRequest(final HttpServletRequest request) {
        return request.getContentType() != null && request.getContentType().toLowerCase().startsWith("multipart");
    }

    /**
     * Uncompress a logout message (base64 + deflate).
     *
     * @param originalMessage the original logout message.
     * @return the uncompressed logout message.
     */
    private String uncompressLogoutMessage(final String originalMessage) {
        final byte[] binaryMessage = DatatypeConverter.parseBase64Binary(originalMessage);

        Inflater decompresser = null;
        try {
            // decompress the bytes
            decompresser = new Inflater();
            decompresser.setInput(binaryMessage);
            final byte[] result = new byte[binaryMessage.length * DECOMPRESSION_FACTOR];

            final int resultLength = decompresser.inflate(result);

            // decode the bytes into a String
            return new String(result, 0, resultLength, StandardCharsets.UTF_8);
        } catch (final Exception e) {
            LOGGER.error("Unable to decompress logout message", e);
            throw new RuntimeException(e);
        } finally {
            if (decompresser != null) {
                decompresser.end();
            }
        }
    }


}
