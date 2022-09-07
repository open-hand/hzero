package org.hzero.gateway.helper.config;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.cloud.context.config.annotation.RefreshScope;

/**
 * 加载GatewayHelper配置信息的配置类
 *
 * @author flyleft
 */
@RefreshScope
@ConfigurationProperties(prefix = GatewayHelperProperties.PREFIX)
public class GatewayHelperProperties {

    public static final String PREFIX = "hzero.gateway.helper";

    private Permission permission = new Permission();

    private Signature signature = new Signature();

    private boolean enabled = true;

    private boolean retryable = false;

    private boolean enabledJwtLog = false;

    private String jwtKey = "hzero";

    private String[] helperSkipPaths;

    private Filter filter = new Filter();

    public GatewayHelperProperties() {
        //保留一个空构造器
    }

    public boolean isEnabled() {
        return enabled;
    }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }

    public boolean isRetryable() {
        return retryable;
    }

    public void setRetryable(boolean retryable) {
        this.retryable = retryable;
    }

    public boolean isEnabledJwtLog() {
        return enabledJwtLog;
    }

    public void setEnabledJwtLog(boolean enabledJwtLog) {
        this.enabledJwtLog = enabledJwtLog;
    }

    public Permission getPermission() {
        return permission;
    }

    public void setPermission(Permission permission) {
        this.permission = permission;
    }

    public String getJwtKey() {
        return jwtKey;
    }

    public void setJwtKey(String jwtKey) {
        this.jwtKey = jwtKey;
    }

    public String[] getHelperSkipPaths() {
        return helperSkipPaths;
    }

    public void setHelperSkipPaths(String[] helperSkipPaths) {
        this.helperSkipPaths = helperSkipPaths;
    }

    public Signature getSignature() {
        return signature;
    }

    public void setSignature(Signature signature) {
        this.signature = signature;
    }

    public Filter getFilter() {
        return filter;
    }

    public void setFilter(Filter filter) {
        this.filter = filter;
    }

    @Override
    public String toString() {
        return "GatewayProperties{" +
                "permission=" + permission +
                ", enabled=" + enabled +
                ", retryable=" + retryable +
                ", enabledJwtLog=" + enabledJwtLog +
                ", jwtKey='" + jwtKey + '\'' +
                '}';
    }

    public static class Permission {
        private Boolean enabled = true;

        private List<String> skipPaths = Arrays.asList("/**/skip/**", "/oauth/**");

        private List<String> internalPaths = Arrays.asList("/oauth/admin/**", "/oauth/api/**", "/oauth/v2/market/**", "/v2/choerodon/api-docs");

        private Long cacheSeconds = 600L;

        private Long cacheSize = 5000L;

        public Boolean getEnabled() {
            return enabled;
        }

        public void setEnabled(Boolean enabled) {
            this.enabled = enabled;
        }

        public List<String> getSkipPaths() {
            return skipPaths;
        }

        public void setSkipPaths(List<String> skipPaths) {
            this.skipPaths = skipPaths;
        }

        public List<String> getInternalPaths() {
            return internalPaths;
        }

        public void setInternalPaths(List<String> internalPaths) {
            this.internalPaths = internalPaths;
        }

        public Long getCacheSeconds() {
            return cacheSeconds;
        }

        public void setCacheSeconds(Long cacheSeconds) {
            this.cacheSeconds = cacheSeconds;
        }

        public Long getCacheSize() {
            return cacheSize;
        }

        public void setCacheSize(Long cacheSize) {
            this.cacheSize = cacheSize;
        }

    }

    public static class Signature {
        /**
         * 是否启用签名验证
         */
        private boolean enabled = false;
        /**
         * 签名加密客户端信息
         */
        private List<Secret> secrets = new ArrayList<>();
        /**
         * 签名过期时间，单位秒，默认600秒
         */
        private int expireTime = 600;

        /**
         * 签名API标签
         */
        private String signLabel = "SIGN_API";

        public String getSignLabel() {
            return signLabel;
        }

        public void setSignLabel(String signLabel) {
            this.signLabel = signLabel;
        }

        public boolean isEnabled() {
            return enabled;
        }

        public void setEnabled(boolean enabled) {
            this.enabled = enabled;
        }

        public List<Secret> getSecrets() {
            return secrets;
        }

        public void setSecrets(List<Secret> secrets) {
            this.secrets = secrets;
        }

        public int getExpireTime() {
            return expireTime;
        }

        public void setExpireTime(int expireTime) {
            this.expireTime = expireTime;
        }
    }

    public static class Secret {
        private String secretId;
        private String secretKey;

        public Secret() {
        }

        public Secret(String secretId, String secretKey) {
            this.secretId = secretId;
            this.secretKey = secretKey;
        }

        public String getSecretId() {
            return secretId;
        }

        public void setSecretId(String secretId) {
            this.secretId = secretId;
        }

        public String getSecretKey() {
            return secretKey;
        }

        public void setSecretKey(String secretKey) {
            this.secretKey = secretKey;
        }
    }

    public static class Filter {

        private CollectSpan collectSpan = new CollectSpan();

        private CommonRequest commonRequest = new CommonRequest();

        private MenuPermission menuPermission = new MenuPermission();

        private ApiReplay apiReplay = new ApiReplay();

        public CollectSpan getCollectSpan() {
            return collectSpan;
        }

        public void setCollectSpan(CollectSpan collectSpan) {
            this.collectSpan = collectSpan;
        }

        public CommonRequest getCommonRequest() {
            return commonRequest;
        }

        public void setCommonRequest(CommonRequest commonRequest) {
            this.commonRequest = commonRequest;
        }

        public MenuPermission getMenuPermission() {
            return menuPermission;
        }

        public void setMenuPermission(MenuPermission menuPermission) {
            this.menuPermission = menuPermission;
        }

        public ApiReplay getApiReplay() {
            return apiReplay;
        }

        public void setApiReplay(ApiReplay apiReplay) {
            this.apiReplay = apiReplay;
        }

        public static class CollectSpan {
            /**
             * 是否启用API统计
             */
            private boolean enabled = true;

            public boolean isEnabled() {
                return enabled;
            }

            public void setEnabled(boolean enabled) {
                this.enabled = enabled;
            }
        }

        public static class CommonRequest {
            /**
             * 是否校验用户角色权限
             */
            private boolean enabled = true;
            /**
             * 是否校验用户有此租户的访问权限
             */
            private boolean checkTenant = true;
            /**
             * 是否校验用户有此项目的访问权限
             */
            private boolean checkProject = true;
            /**
             * 是否仅校验当前角色有此权限，否则只要有一个角色有权限即可
             */
            private boolean checkCurrentRole = false;
            /**
             * URI路径上租户ID参数
             */
            private List<String> parameterTenantId = Arrays.asList("organization_id", "organizationId", "tenantId");
            /**
             * URI路径上项目ID参数
             */
            private List<String> parameterProjectId = Arrays.asList("project_id", "projectId");

            public boolean isEnabled() {
                return enabled;
            }

            public void setEnabled(boolean enabled) {
                this.enabled = enabled;
            }

            public boolean isCheckTenant() {
                return checkTenant;
            }

            public void setCheckTenant(boolean checkTenant) {
                this.checkTenant = checkTenant;
            }

            public boolean isCheckProject() {
                return checkProject;
            }

            public void setCheckProject(boolean checkProject) {
                this.checkProject = checkProject;
            }

            public boolean isCheckCurrentRole() {
                return checkCurrentRole;
            }

            public void setCheckCurrentRole(boolean checkCurrentRole) {
                this.checkCurrentRole = checkCurrentRole;
            }

            public List<String> getParameterTenantId() {
                return parameterTenantId;
            }

            public void setParameterTenantId(List<String> parameterTenantId) {
                this.parameterTenantId = parameterTenantId;
            }

            public List<String> getParameterProjectId() {
                return parameterProjectId;
            }

            public void setParameterProjectId(List<String> parameterProjectId) {
                this.parameterProjectId = parameterProjectId;
            }
        }

        public static class MenuPermission {
            /**
             * 是否启用菜单权限校验
             */
            private boolean enabled = false;

            public boolean isEnabled() {
                return enabled;
            }

            public void setEnabled(boolean enabled) {
                this.enabled = enabled;
            }
        }

        public static class ApiReplay {
            /**
             * 是否启用api防重放校验
             */
            private boolean enabled = false;

            private List<String> skipPaths = new ArrayList<>();

            public boolean isEnabled() {
                return enabled;
            }

            public void setEnabled(boolean enabled) {
                this.enabled = enabled;
            }

            public List<String> getSkipPaths() {
                return skipPaths;
            }

            public void setSkipPaths(List<String> skipPaths) {
                this.skipPaths = skipPaths;
            }
        }
    }
}
