package org.hzero.oauth.security.custom;

import java.util.Collections;
import java.util.Map;
import java.util.Optional;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.oauth2.provider.ClientDetails;
import org.springframework.security.oauth2.provider.ClientDetailsService;
import org.springframework.security.oauth2.provider.NoSuchClientException;
import org.springframework.util.StringUtils;

import io.choerodon.core.oauth.CustomClientDetails;

import org.hzero.boot.oauth.domain.entity.BaseClient;
import org.hzero.boot.oauth.domain.repository.BaseClientRepository;
import org.hzero.core.base.BaseConstants;

public class CustomClientDetailsService implements ClientDetailsService {
    private static final Logger LOGGER = LoggerFactory.getLogger(CustomClientDetailsService.class);

    private static final ObjectMapper mapper = BaseConstants.MAPPER;

    private final BaseClientRepository baseClientRepository;

    private static final ThreadLocal<CustomClientDetails> LOCAL_CLIENT_DETAILS = new ThreadLocal<>();

    public CustomClientDetailsService(BaseClientRepository baseClientRepository) {
        this.baseClientRepository = baseClientRepository;
    }

    @Override
    public ClientDetails loadClientByClientId(String name) {
        CustomClientDetails clientDetails = LOCAL_CLIENT_DETAILS.get();
        if (clientDetails != null) {
            return clientDetails;
        }

        BaseClient client = baseClientRepository.selectClient(name);
        if (client == null) {
            throw new NoSuchClientException("No client found : " + name);
        }
        clientDetails = new CustomClientDetails();
        clientDetails.setId(client.getId());
        clientDetails.setAuthorizedGrantTypes(StringUtils.commaDelimitedListToSet(client.getAuthorizedGrantTypes()));
        clientDetails.setClientId(client.getName());
        clientDetails.setClientSecret(client.getSecret());
        clientDetails.setResourceIds(StringUtils.commaDelimitedListToSet(client.getResourceIds()));
        clientDetails.setScope(StringUtils.commaDelimitedListToSet(client.getScope()));
        clientDetails.setRegisteredRedirectUri(StringUtils.commaDelimitedListToSet(client.getWebServerRedirectUri()));
        clientDetails.setAuthorities(Collections.emptyList());
        int accessTokenValidity = Optional.ofNullable(client.getAccessTokenValidity()).orElse(7200L).intValue();
        clientDetails.setAccessTokenValiditySeconds(accessTokenValidity);
        int refreshTokenValidity = Optional.ofNullable(client.getRefreshTokenValidity()).orElse(7200L).intValue();
        clientDetails.setRefreshTokenValiditySeconds(refreshTokenValidity);
        clientDetails.setOrganizationId(client.getOrganizationId());
        String json = client.getAdditionalInformation();
        if (!StringUtils.isEmpty(json)) {
            try {
                Map<String, Object> additionalInformation = mapper.readValue(json, Map.class);
                clientDetails.setAdditionalInformation(additionalInformation);
            } catch (Exception e) {
                LOGGER.warn("parser addition info error: {}", e.getMessage(), e);
            }
        }
        clientDetails.setAutoApproveScopes(StringUtils.commaDelimitedListToSet(client.getAutoApprove()));
        clientDetails.setTimeZone(client.getTimeZone());
        clientDetails.setApiEncryptFlag(client.getApiEncryptFlag());
        clientDetails.setApiReplayFlag(client.getApiReplayFlag());

        LOCAL_CLIENT_DETAILS.set(clientDetails);

        return clientDetails;
    }

    public static void clearLocalResource() {
        LOCAL_CLIENT_DETAILS.remove();
    }

}
