package org.hzero.gateway.helper.service.impl;

import java.io.IOException;
import java.util.*;
import java.util.stream.Collectors;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.commons.collections4.MapUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.cloud.client.ServiceInstance;
import org.springframework.cloud.client.discovery.DiscoveryClient;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.util.Assert;
import org.springframework.web.client.RestTemplate;

import io.choerodon.core.oauth.CustomUserDetails;

import org.hzero.common.HZeroService;
import org.hzero.core.base.BaseConstants;
import org.hzero.core.base.TokenConstants;
import org.hzero.core.user.UserType;
import org.hzero.gateway.helper.domain.CustomUserDetailsWithResult;
import org.hzero.gateway.helper.entity.CheckState;
import org.hzero.gateway.helper.service.GetUserDetailsService;

public class GetUserDetailsServiceImpl implements GetUserDetailsService {
    private static final Logger LOGGER = LoggerFactory.getLogger(GetUserDetailsService.class);

    private static final String PRINCIPAL = "principal";
    private static final String OAUTH2REQUEST = "oauth2Request";
    private static final String ADDITION_INFO = "additionInfo";
    private static final String ADDITION_INFO_MEANING = "additionInfoMeaning";
    private static final String USER_ID = "userId";
    protected static final String ANONYMOUS_LANGUAGE = "zh_CN";
    protected static final String ANONYMOUS_TIME_ZONE = "GMT+8";

    private final ObjectMapper objectMapper = new ObjectMapper();
    private final RestTemplate restTemplate;
    private final DiscoveryClient discoveryClient;

    private String oauthUserApi = "http://hzero-oauth/oauth/api/user";
    private volatile boolean isInit = false;

    public GetUserDetailsServiceImpl(@Qualifier("helperRestTemplate") RestTemplate restTemplate,
                                     DiscoveryClient discoveryClient) {
        this.restTemplate = restTemplate;
        this.discoveryClient = discoveryClient;
    }

    @Override
    @SuppressWarnings("unchecked")
    public CustomUserDetailsWithResult getUserDetails(String token) {
        return getUserDetails(token, false);
    }

    public CustomUserDetailsWithResult getUserDetails(String token, boolean implicitCall) {
        HttpHeaders headers = new HttpHeaders();
        headers.set(TokenConstants.HEADER_AUTH, token);
        HttpEntity<String> entity = new HttpEntity<>("", headers);
        try {
            ResponseEntity<String> responseEntity = restTemplate.exchange(getOauthUserApi() + (implicitCall ? "?implicitCall=true" : ""), HttpMethod.GET, entity, String.class);
            if (responseEntity.getStatusCode().is2xxSuccessful()) {
                if (implicitCall) {
                    return null;
                }
                CustomUserDetails userDetails = extractPrincipal(objectMapper.readValue(responseEntity.getBody(), Map.class));
                return new CustomUserDetailsWithResult(userDetails, CheckState.SUCCESS_PASS_SITE);
            } else {
                return new CustomUserDetailsWithResult(CheckState.PERMISSION_GET_USE_DETAIL_FAILED,
                        "Get customUserDetails error from [" + getOauthUserApi() + "], token: " + token + ", response: " + responseEntity);
            }
        } catch (RuntimeException e) {
            LOGGER.warn("Get customUserDetails error from hzero-oauth, token: {}", token, e);
            return new CustomUserDetailsWithResult(CheckState.PERMISSION_ACCESS_TOKEN_EXPIRED,
                    "Access_token is expired or invalid, Please re-login and set correct access_token by HTTP header 'Authorization'");
        } catch (IOException e) {
            return new CustomUserDetailsWithResult(CheckState.EXCEPTION_GATEWAY_HELPER,
                    "Gateway helper error happened: " + e.toString());
        }
    }

    public void getUserDetailsImplicit(String token) {
        CustomUserDetailsWithResult userDetails = getUserDetails(token, true);
        if (userDetails != null) {
            LOGGER.debug("[AccessToken] {}", userDetails);
        }
    }

    private String getOauthUserApi() {
        if (isInit) {
            return oauthUserApi;
        }
        synchronized (this) {
            if (isInit) {
                return oauthUserApi;
            }
            boolean instanceUp = false;
            String name = HZeroService.getRealName(HZeroService.Oauth.NAME);
            ServiceInstance instance = Optional.ofNullable(discoveryClient.getInstances(name)).flatMap(list -> list.stream().findFirst()).orElse(null);
            String context = "";
            if (instance == null) {
                context = "/oauth";
            } else {
                instanceUp = true;
                if (MapUtils.isNotEmpty(instance.getMetadata())) {
                    context = instance.getMetadata().get("CONTEXT");
                }
            }

            oauthUserApi = "http://" + name + context + "/api/user";
            isInit = instanceUp;
        }
        return oauthUserApi;
    }

    @SuppressWarnings("unchecked")
    private CustomUserDetails extractPrincipal(Map<String, Object> map) {
        boolean isClientOnly = false;
        Map<String, Object> oauth2request = null;
        if (map.get(OAUTH2REQUEST) != null) {
            oauth2request = (Map) map.get(OAUTH2REQUEST);
            Assert.notNull(oauth2request.get("grantType"), "grantType not be null.");
            if ("client_credentials".equals(oauth2request.get("grantType"))) {
                isClientOnly = true;
            }
        }
        if (map.get(PRINCIPAL) != null) {
            map = (Map) map.get(PRINCIPAL);
        }

        return setUserDetails(map, isClientOnly, oauth2request);
    }

    @SuppressWarnings({"unchecked", "Duplicates"})
    private CustomUserDetails setUserDetails(final Map<String, Object> map, boolean isClientOnly, Map<String, Object> oauth2request) {
        if (map.containsKey(USER_ID)) {
            String userType = null;
            if (map.get("userType") != null) {
                userType = map.get("userType") != null ? map.get("userType").toString() : null;
            }
            CustomUserDetails user = new CustomUserDetails((String) map.get("username"), "unknown password", UserType.ofDefault(userType).value(), Collections.emptyList());
            if (map.get("organizationId") != null) {
                user.setOrganizationId(Long.parseLong(String.valueOf(map.get("organizationId"))));
            }
            if (map.get(USER_ID) != null) {
                user.setUserId(Long.parseLong(String.valueOf(map.get(USER_ID))));
            }
            if (map.get("language") != null) {
                user.setLanguage((String) map.get("language"));
            }
            if (map.get("admin") != null) {
                user.setAdmin((Boolean) map.get("admin"));
            }
            if (map.get("timeZone") != null) {
                user.setTimeZone((String) map.get("timeZone"));
            }
            if (map.get("email") != null) {
                user.setEmail((String) map.get("email"));
            }
            if (map.get("realName") != null) {
                user.setRealName(String.valueOf(map.get("realName")));
            }
            if (map.get("roleId") != null) {
                user.setRoleId(Long.valueOf(String.valueOf(map.get("roleId"))));
            }
            if (map.get("roleIds") != null) {
                Object userRoleIds = map.get("roleIds");
                if (userRoleIds instanceof List) {
                    List<Object> roleIds = (List) userRoleIds;
                    user.setRoleIds(roleIds.stream().map(item -> Long.valueOf(String.valueOf(item))).collect(Collectors.toList()));
                }
            }
            if (map.get("tenantId") != null) {
                user.setTenantId(Long.valueOf(String.valueOf(map.get("tenantId"))));
            }
            if (map.get("tenantNum") != null) {
                user.setTenantNum(String.valueOf(map.get("tenantNum")));
            }
            if (map.get("tenantIds") != null) {
                Object tenantIds = map.get("tenantIds");
                if (tenantIds instanceof List) {
                    List<Object> userTenantIds = (List) tenantIds;
                    user.setTenantIds(userTenantIds.stream().map(item -> Long.valueOf(String.valueOf(item))).collect(Collectors.toList()));
                }
            }
            if (map.get("siteRoleIds") != null) {
                Object userSiteRoleIds = map.get("siteRoleIds");
                if (userSiteRoleIds instanceof List) {
                    List<Object> roleIds = (List) userSiteRoleIds;
                    user.setSiteRoleIds(roleIds.stream().map(item -> Long.valueOf(String.valueOf(item))).collect(Collectors.toList()));
                }
            }
            if (map.get("tenantRoleIds") != null) {
                Object userTenantRoleIds = map.get("tenantRoleIds");
                if (userTenantRoleIds instanceof List) {
                    List<Object> roleIds = (List) userTenantRoleIds;
                    user.setTenantRoleIds(roleIds.stream().map(item -> Long.valueOf(String.valueOf(item))).collect(Collectors.toList()));
                }
            }
            if (map.get("roleMergeFlag") != null) {
                user.setRoleMergeFlag(Boolean.valueOf(String.valueOf(map.get("roleMergeFlag"))));
            }
            if (map.get("imageUrl") != null) {
                user.setImageUrl(String.valueOf(map.get("imageUrl")));
            }
            if (map.containsKey("apiEncryptFlag")) {
                user.setApiEncryptFlag((Integer) map.get("apiEncryptFlag"));
            }
            if (map.containsKey("apiReplayFlag")) {
                user.setApiReplayFlag((Integer) map.get("apiReplayFlag"));
            }

            if (map.get("roleLabels") != null) {
                Object roleLabels = map.get("roleLabels");
                if (roleLabels instanceof Collection) {
                    @SuppressWarnings("rawtypes")
                    Collection<Object> labels = (Collection) roleLabels;
                    user.setRoleLabels(labels.stream().map(String::valueOf).collect(Collectors.toSet()));
                }
            }

            if (isClientOnly) {
                user.setClientId(Long.parseLong(String.valueOf(map.get("clientId"))));
                user.setClientName((String) map.get("clientName"));
                user.setClientAccessTokenValiditySeconds(Integer.parseInt(String.valueOf(map.get("clientAccessTokenValiditySeconds"))));
                user.setClientRefreshTokenValiditySeconds(Integer.parseInt(String.valueOf(map.get("clientRefreshTokenValiditySeconds"))));
                user.setClientAuthorizedGrantTypes((Collection<String>) map.get("clientAuthorizedGrantTypes"));
                user.setClientAutoApproveScopes((Collection<String>) map.get("clientAutoApproveScopes"));
                user.setClientRegisteredRedirectUri((Collection<String>) map.get("clientRegisteredRedirectUri"));
                user.setClientResourceIds((Collection<String>) map.get("clientResourceIds"));
                user.setClientScope((Collection<String>) map.get("clientScope"));
            } else if (oauth2request != null) {
                user.setClientName(oauth2request.containsKey("clientId") ? String.valueOf(oauth2request.get("clientId")) : null);
            }
            try {
                if (map.get(ADDITION_INFO) != null) {
                    user.setAdditionInfo((Map) map.get(ADDITION_INFO));
                }
            } catch (Exception e) {
                LOGGER.warn("Parser addition info error:{}", e.getMessage());
            }
            try {
                if (map.get(ADDITION_INFO_MEANING) != null) {
                    user.setAdditionInfoMeaning((Map) map.get(ADDITION_INFO_MEANING));
                }
            } catch (Exception e) {
                LOGGER.warn("Parser addition meaning info error:{}", e.getMessage());
            }
            return user;
        } else if (map.containsKey("id")) {
            CustomUserDetails user = new CustomUserDetails(BaseConstants.ANONYMOUS_USER_NAME, "unknown password", Collections.emptyList());
            user.setUserId(BaseConstants.ANONYMOUS_USER_ID);
            user.setClientId(Long.valueOf(String.valueOf(map.get("id"))));
            user.setLanguage(ANONYMOUS_LANGUAGE);
            user.setTimeZone(ANONYMOUS_TIME_ZONE);
            if (map.containsKey("scope")) {
                user.setClientScope(Collections.singleton(String.valueOf(map.get("scope"))));
            }
            if (map.containsKey("organizationId")) {
                user.setOrganizationId(Long.valueOf(String.valueOf(map.get("organizationId"))));
            }
            if (map.containsKey("currentRoleId")) {
                user.setRoleId(Long.valueOf(String.valueOf(map.get("currentRoleId"))));
            }
            if (map.containsKey("currentTenantId")) {
                user.setTenantId(Long.valueOf(String.valueOf(map.get("currentTenantId"))));
            }
            if (map.get("roleIds") != null) {
                Object userRoleIds = map.get("roleIds");
                if (userRoleIds instanceof List) {
                    List<Object> roleIds = (List) userRoleIds;
                    user.setRoleIds(roleIds.stream().map(item -> Long.valueOf(String.valueOf(item))).collect(Collectors.toList()));
                }
            }
            if (map.get("tenantIds") != null) {
                Object tenantIds = map.get("tenantIds");
                if (tenantIds instanceof List) {
                    List<Object> userTenantIds = (List) tenantIds;
                    user.setTenantIds(userTenantIds.stream().map(item -> Long.valueOf(String.valueOf(item))).collect(Collectors.toList()));
                }
            }
            if (map.containsKey("client_id")) {
                user.setClientName(String.valueOf(map.get("client_id")));
            }
            if (map.get("resource_ids") != null) {
                Object resourceIds = map.get("resource_ids");
                if (resourceIds instanceof List) {
                    List<Object> resourceIdList = (List) resourceIds;
                    user.setClientResourceIds(resourceIdList.stream().map(String::valueOf).collect(Collectors.toList()));
                }
            }
            if (map.get("authorized_grant_types") != null) {
                Object grantTypes = map.get("authorized_grant_types");
                if (grantTypes instanceof List) {
                    List<Object> grantTypeList = (List) grantTypes;
                    user.setClientAuthorizedGrantTypes(grantTypeList.stream().map(String::valueOf).collect(Collectors.toList()));
                }
            }
            if (map.get("redirect_uri") != null) {
                Object redirectUris = map.get("redirect_uri");
                if (redirectUris instanceof List) {
                    List<Object> redirectUriList = (List) redirectUris;
                    user.setClientRegisteredRedirectUri(redirectUriList.stream().map(String::valueOf).collect(Collectors.toList()));
                }
            }
            if (map.get("autoapprove") != null) {
                Object autoApproves = map.get("autoapprove");
                if (autoApproves instanceof List) {
                    List<Object> autoApproveList = (List) autoApproves;
                    user.setClientAutoApproveScopes(autoApproveList.stream().map(String::valueOf).collect(Collectors.toList()));
                }
            }
            if (map.containsKey("access_token_validity")) {
                user.setClientAccessTokenValiditySeconds(Integer.valueOf(String.valueOf(map.get("access_token_validity"))));
            }
            if (map.containsKey("refresh_token_validity")) {
                user.setClientRefreshTokenValiditySeconds(Integer.valueOf(String.valueOf(map.get("refresh_token_validity"))));
            }
            if (map.containsKey("timeZone")) {
                user.setTimeZone(String.valueOf(map.get("timeZone")));
            }
            if (map.containsKey("apiEncryptFlag")) {
                user.setApiEncryptFlag((Integer) map.get("apiEncryptFlag"));
            }
            if (map.containsKey("apiReplayFlag")) {
                user.setApiReplayFlag((Integer) map.get("apiReplayFlag"));
            }
            return user;
        }
        return null;
    }
}
