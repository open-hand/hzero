package io.choerodon.core.oauth;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.oauth2.common.OAuth2AccessToken;
import org.springframework.security.oauth2.provider.ClientDetailsService;
import org.springframework.security.oauth2.provider.OAuth2Authentication;
import org.springframework.security.oauth2.provider.token.DefaultAccessTokenConverter;

/**
 * 定制的access token转换
 *
 * @author wuguokai
 */
@SuppressWarnings("unchecked")
public class CustomTokenConverter extends DefaultAccessTokenConverter {

    private static final Logger LOGGER = LoggerFactory.getLogger(CustomTokenConverter.class);
    private static final ObjectMapper MAPPER = new ObjectMapper();
    private static final String ID = "id";
    private static final String USER_ID = "userId";
    private static final String ORGANIZATION_ID = "organizationId";
    private static final String ADDITION_INFO = "additionInfo";
    private static final String ADDITION_INFO_MEANING = "additionInfoMeaning";
    private static final String REAL_NAME = "realName";
    private static final String ROLE_ID = "roleId";
    private static final String CLIENT_ID = "clientId";
    private static final String CURRENT_ROLE_ID = "currentRoleId";
    private static final String ROLE_IDS = "roleIds";
    private static final String SITE_ROLE_IDS = "siteRoleIds";
    private static final String TENANT_ROLE_IDS = "tenantRoleIds";
    private static final String ROLE_MERGE_FLAG = "roleMergeFlag";
    private static final String TENANT_ID = "tenantId";
    private static final String CURRENT_TENANT_ID = "currentTenantId";
    private static final String TENANT_NUM = "tenantNum";
    private static final String TENANT_IDS = "tenantIds";
    private static final String IMAGE_URL = "imageUrl";
    private static final String API_ENCRYPT_FLAG = "apiEncryptFlag";
    private static final String API_REPLAY_FLAG = "apiReplayFlag";
    private static final String ROLE_LABELS = "roleLabels";

    private UserDetailsService userDetailsService;
    private ClientDetailsService clientDetailsService;

    public UserDetailsService getUserDetailsService() {
        return userDetailsService;
    }

    public void setUserDetailsService(UserDetailsService userDetailsService) {
        this.userDetailsService = userDetailsService;
    }

    public ClientDetailsService getClientDetailsService() {
        return clientDetailsService;
    }

    public void setClientDetailsService(ClientDetailsService clientDetailsService) {
        this.clientDetailsService = clientDetailsService;
    }

    /**
     * 根据jwt token和认证对象查询出用户信息map集合
     *
     * @param token          access_token
     * @param authentication 认证信息
     * @return map     用户信息的map集合
     */
    @Override
    public Map<String, ?> convertAccessToken(OAuth2AccessToken token,
                                             OAuth2Authentication authentication) {
        Map<String, Object> map = (Map<String, Object>) super.convertAccessToken(token, authentication);
        Object details = authentication.getPrincipal();
        if (details instanceof CustomUserDetails) {
            CustomUserDetails user = (CustomUserDetails) userDetailsService
                    .loadUserByUsername(((CustomUserDetails) details).getUsername());
            map.put(USER_ID, user.getUserId().toString());
            map.put("language", user.getLanguage());
            map.put("timeZone", user.getTimeZone());
            map.put("email", user.getEmail());
            map.put(ORGANIZATION_ID, user.getOrganizationId().toString());
            map.put("admin", user.getAdmin());
            // Add by qingsheng.chen@hand-china.com
            // Access Token -> Map
            map.put(REAL_NAME, user.getRealName());
            map.put(ROLE_ID, user.getRoleId());
            map.put(ROLE_IDS, user.getRoleIds());
            map.put(SITE_ROLE_IDS, user.getSiteRoleIds());
            map.put(TENANT_ROLE_IDS, user.getTenantRoleIds());
            map.put(ROLE_MERGE_FLAG, user.isRoleMergeFlag());
            map.put(TENANT_ID, user.getTenantId());
            map.put(TENANT_NUM, user.getTenantNum());
            map.put(TENANT_IDS, user.getTenantIds());
            map.put(IMAGE_URL, user.getImageUrl());
            map.put(CLIENT_ID, user.getClientId());
            map.put(API_ENCRYPT_FLAG, user.getApiEncryptFlag());
            map.put(API_REPLAY_FLAG, user.getApiReplayFlag());
            map.put(ROLE_LABELS, user.getRoleLabels());
            // End
            if (user.getAdditionInfo() != null) {
                map.put(ADDITION_INFO, user.getAdditionInfo());
            }
            if (user.getAdditionInfoMeaning() != null) {
                map.put(ADDITION_INFO_MEANING, user.getAdditionInfoMeaning());
            }
        } else if (details instanceof String) {
            CustomClientDetails client = (CustomClientDetails) clientDetailsService
                    .loadClientByClientId((String) details);
            map.put(ID, client.getId());
            map.put(ORGANIZATION_ID, client.getOrganizationId());
            map.put(CURRENT_ROLE_ID, client.getCurrentRoleId());
            map.put(CURRENT_TENANT_ID, client.getCurrentTenantId());
            map.put(ROLE_IDS, client.getRoleIds());
            map.put(TENANT_IDS, client.getTenantIds());
            if (client.getAdditionalInformation() != null) {
                map.put(ADDITION_INFO, client.getAdditionalInformation());
            }
        }
        return map;
    }

    /**
     * 根据用户信息集合提取出一个认证信息对象
     *
     * @param map 用户信息集合
     * @return OAuth2Authentication
     */
    @Override
    @SuppressWarnings("Duplicates")
    public OAuth2Authentication extractAuthentication(Map<String, ?> map) {
        if (map.get("principal") != null) {
            map = (Map<String, Object>) map.get("principal");
        }
        if (!map.containsKey("user_name")) {
            ((Map<String, Object>) map).put("user_name", ((Map<String, Object>) map).get("username"));
        }
        OAuth2Authentication authentication = super.extractAuthentication(map);
        if (map.containsKey(USER_ID)) {
            CustomUserDetails user = new CustomUserDetails(authentication.getName(), "unknown password", (String) map.get("userType"), authentication.getAuthorities());
            user.setOrganizationId(Long.parseLong(String.valueOf(Optional.ofNullable(map.get(ORGANIZATION_ID)).orElseThrow(() -> new IllegalArgumentException("Organization Id is missing from user information.")))));
            if (((Map<String, Object>) map).get(USER_ID) != null) {
                user.setUserId(Long.parseLong(String.valueOf(map.get(USER_ID))));
                user.setLanguage((String) map.get("language"));
                user.setAdmin((Boolean) map.get("admin"));
                user.setTimeZone((String) map.get("timeZone"));
                user.setOrganizationId(Long.parseLong(String.valueOf(map.get(ORGANIZATION_ID))));
                if (map.get("email") != null) {
                    user.setEmail((String) map.get("email"));
                }
                // Add by qingsheng.chen@hand-china.com
                // Access Token <- Map
                if (map.get(REAL_NAME) != null) {
                    user.setRealName(String.valueOf(map.get(REAL_NAME)));
                }
                if (map.get(ROLE_ID) != null) {
                    user.setRoleId(Long.valueOf(String.valueOf(map.get(ROLE_ID))));
                }
                if (map.get(ROLE_IDS) != null) {
                    Object userRoleIds = map.get(ROLE_IDS);
                    if (userRoleIds instanceof List) {
                        @SuppressWarnings("rawtypes")
                        List<Object> roleIds = (List) userRoleIds;
                        user.setRoleIds(roleIds.stream().map(item -> Long.valueOf(String.valueOf(item))).collect(Collectors.toList()));
                    }
                }
                if (map.get(SITE_ROLE_IDS) != null) {
                    Object userSiteRoleIds = map.get(SITE_ROLE_IDS);
                    if (userSiteRoleIds instanceof List) {
                        @SuppressWarnings("rawtypes")
                        List<Object> roleIds = (List) userSiteRoleIds;
                        user.setSiteRoleIds(roleIds.stream().map(item -> Long.valueOf(String.valueOf(item))).collect(Collectors.toList()));
                    }
                }
                if (map.get(TENANT_ROLE_IDS) != null) {
                    Object userTenantRoleIds = map.get(TENANT_ROLE_IDS);
                    if (userTenantRoleIds instanceof List) {
                        @SuppressWarnings("rawtypes")
                        List<Object> roleIds = (List) userTenantRoleIds;
                        user.setTenantRoleIds(roleIds.stream().map(item -> Long.valueOf(String.valueOf(item))).collect(Collectors.toList()));
                    }
                }
                if (map.get(ROLE_MERGE_FLAG) != null) {
                    user.setRoleMergeFlag(Boolean.valueOf(String.valueOf(map.get(ROLE_MERGE_FLAG))));
                }
                if (map.get(TENANT_ID) != null) {
                    user.setTenantId(Long.valueOf(String.valueOf(map.get(TENANT_ID))));
                }
                if (map.get(TENANT_NUM) != null) {
                    user.setTenantNum(String.valueOf(map.get(TENANT_NUM)));
                }
                if (map.get(TENANT_IDS) != null) {
                    Object tenantIds = map.get(TENANT_IDS);
                    if (tenantIds instanceof List) {
                        @SuppressWarnings("rawtypes")
                        List<Object> userTenantIds = (List) tenantIds;
                        user.setTenantIds(userTenantIds.stream().map(item -> Long.valueOf(String.valueOf(item))).collect(Collectors.toList()));
                    }
                }
                if (map.get(IMAGE_URL) != null) {
                    user.setImageUrl(String.valueOf(map.get(IMAGE_URL)));
                }
                if (map.get(ROLE_LABELS) != null) {
                    Object roleLabels = map.get(ROLE_LABELS);
                    if (roleLabels instanceof Collection) {
                        @SuppressWarnings("rawtypes")
                        Collection<Object> labels = (Collection) roleLabels;
                        user.setRoleLabels(labels.stream().map(String::valueOf).collect(Collectors.toSet()));
                    }
                }
                // End
            }
            if (((Map<String, Object>) map).get("clientId") != null) {
                user.setClientId(Long.parseLong(String.valueOf(map.get("clientId"))));
                user.setClientName((String) map.get("clientName"));
                user.setClientAccessTokenValiditySeconds((Integer) map.get("clientAccessTokenValiditySeconds"));
                user.setClientRefreshTokenValiditySeconds((Integer) map.get("clientRefreshTokenValiditySeconds"));
                user.setClientAuthorizedGrantTypes((Collection<String>) map.get("clientAuthorizedGrantTypes"));
                user.setClientAutoApproveScopes((Collection<String>) map.get("clientAutoApproveScopes"));
                user.setClientRegisteredRedirectUri((Collection<String>) map.get("clientRegisteredRedirectUri"));
                user.setClientResourceIds((Collection<String>) map.get("clientResourceIds"));
                user.setClientScope((Collection<String>) map.get("clientScope"));
            }
            else if (((Map<String, Object>) map).get("clientName") != null) {
                user.setClientName((String) map.get("clientName"));
            }
            try {
                if (map.get(ADDITION_INFO) != null) {
                    user.setAdditionInfo((Map) map.get(ADDITION_INFO));
                }
            } catch (Exception e) {
                LOGGER.warn("parser addition info error", e);
            }
            try {
                if (map.get(ADDITION_INFO_MEANING) != null) {
                    user.setAdditionInfoMeaning((Map) map.get(ADDITION_INFO_MEANING));
                }
            } catch (Exception e) {
                LOGGER.warn("parser addition info meaning error", e);
            }
            if (map.containsKey(API_ENCRYPT_FLAG)) {
                user.setApiEncryptFlag((Integer) map.get(API_ENCRYPT_FLAG));
            }
            if (map.containsKey(API_REPLAY_FLAG)) {
                user.setApiReplayFlag((Integer) map.get(API_REPLAY_FLAG));
            }
            authentication.setDetails(user);
        } else {
            CustomClientDetails client = new CustomClientDetails();
            client.setClientId(authentication.getName());
            client.setAuthorities(authentication.getAuthorities());
            client.setOrganizationId(Long.parseLong(String.valueOf(map.get(ORGANIZATION_ID))));
            if (map.get(CURRENT_ROLE_ID) != null) {
                client.setCurrentRoleId(Long.valueOf(String.valueOf(map.get(CURRENT_ROLE_ID))));
            }
            if (map.get(CURRENT_TENANT_ID) != null) {
                client.setCurrentTenantId(Long.valueOf(String.valueOf(map.get(CURRENT_TENANT_ID))));
            }
            if (map.get(ROLE_IDS) != null) {
                Object roleIds = map.get(ROLE_IDS);
                if (roleIds instanceof List) {
                    @SuppressWarnings("rawtypes")
                    List<Object> clientRoleIds = (List) roleIds;
                    client.setRoleIds(clientRoleIds.stream().map(item -> Long.valueOf(String.valueOf(item))).collect(Collectors.toList()));
                }
            }

            if (map.get(TENANT_IDS) != null) {
                Object tenantIds = map.get(TENANT_IDS);
                if (tenantIds instanceof List) {
                    @SuppressWarnings("rawtypes")
                    List<Object> clientTenantIds = (List) tenantIds;
                    client.setTenantIds(clientTenantIds.stream().map(item -> Long.valueOf(String.valueOf(item))).collect(Collectors.toList()));
                }
            }
            try {
                if (map.get(ADDITION_INFO) != null) {
                    client.setAdditionalInformation(MAPPER.readValue((String) map.get(ADDITION_INFO), Map.class));
                }
            } catch (Exception e) {
                LOGGER.warn("parser addition info error", e);
            }
            if (map.containsKey(API_ENCRYPT_FLAG)) {
                client.setApiEncryptFlag((Integer) map.get(API_ENCRYPT_FLAG));
            }
            if (map.containsKey(API_REPLAY_FLAG)) {
                client.setApiReplayFlag((Integer) map.get(API_REPLAY_FLAG));
            }
            authentication.setDetails(client);
        }
        return authentication;
    }

}
