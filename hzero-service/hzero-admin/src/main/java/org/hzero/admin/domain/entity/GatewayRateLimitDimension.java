package org.hzero.admin.domain.entity;

import com.fasterxml.jackson.annotation.JsonInclude;
import io.choerodon.mybatis.annotation.ModifyAudit;
import io.choerodon.mybatis.annotation.VersionAudit;
import io.choerodon.mybatis.domain.AuditDomain;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import org.apache.commons.lang3.StringUtils;
import org.hzero.boot.platform.lov.annotation.LovValue;
import org.hzero.starter.keyencrypt.core.Encrypt;

import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.Transient;
import javax.validation.constraints.NotNull;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @author XCXCXCXCX
 * @version 1.0
 * @date 2019/11/18 3:55 下午
 */
@ApiModel("网关限流维度")
@VersionAudit
@ModifyAudit
@Table(name = "hadm_gw_rate_limit_dim")
@JsonInclude(JsonInclude.Include.NON_NULL)
public class GatewayRateLimitDimension extends AuditDomain {

    public static final String ENCRYPT_KEY = "hadm_gw_rate_limit_dim";

    public static final String FIElD_RATE_LIMIT_DIMENSION_ID = "rateLimitDimId";
    public static final String FIElD_RATE_LIMIT_LINE_ID = "rateLimitLineId";

    @Encrypt
    @ApiModelProperty("表ID，主键，供其他表做外键")
    @Id
    @GeneratedValue
    private Long rateLimitDimId;

    @Encrypt
    @ApiModelProperty(value = "限流配置ID")
    @NotNull
    private Long rateLimitId;

    @Encrypt
    @ApiModelProperty(value = "限流配置行ID")
    @NotNull
    private Long rateLimitLineId;

    @ApiModelProperty(value = "限流维度")
    @NotNull
    private String rateLimitDimension;

    @ApiModelProperty(value = "限流维度Key")
    @NotNull
    private String dimensionKey;

    @ApiModelProperty(value = "每秒流量限制值")
    @NotNull
    private Integer replenishRate;

    @ApiModelProperty(value = "突发流量限制值")
    @NotNull
    private Integer burstCapacity;

    @Transient
    @LovValue(lovCode = "HADM.GATEWAY_RATE_LIMIT_DIMENSION", meaningField = "rateLimitDimensionMeaningList")
    private List<String> rateLimitDimensionList;
    @Transient
    private String rateLimitDimensionMeaningList;

    @Transient
    @LovValue(lovCode = "LOV_USER", meaningField = "userKeyMeaning")
    private String userKey;
    @Transient
    @LovValue(lovCode = "LOV_ROLE", meaningField = "roleKeyMeaning")
    private String roleKey;
    @Transient
    @LovValue(lovCode = "LOV_TENANT", meaningField = "tenantKeyMeaning")
    private String tenantKey;
    @Transient
    private String originKey;
    @Transient
    private String urlKey;

    @Transient
    private String userKeyMeaning;
    @Transient
    private String roleKeyMeaning;
    @Transient
    private String tenantKeyMeaning;
    @Transient
    private String originKeyMeaning;
    @Transient
    private String urlKeyMeaning;

    public Long getRateLimitDimId() {
        return rateLimitDimId;
    }

    public void setRateLimitDimId(Long rateLimitDimId) {
        this.rateLimitDimId = rateLimitDimId;
    }

    public Long getRateLimitId() {
        return rateLimitId;
    }

    public void setRateLimitId(Long rateLimitId) {
        this.rateLimitId = rateLimitId;
    }

    public Long getRateLimitLineId() {
        return rateLimitLineId;
    }

    public void setRateLimitLineId(Long rateLimitLineId) {
        this.rateLimitLineId = rateLimitLineId;
    }

    public String getRateLimitDimension() {
        return rateLimitDimension;
    }

    public void setRateLimitDimension(String rateLimitDimension) {
        this.rateLimitDimension = rateLimitDimension;
    }

    public String getDimensionKey() {
        return dimensionKey;
    }

    public void setDimensionKey(String dimensionKey) {
        this.dimensionKey = dimensionKey;
    }

    public Integer getReplenishRate() {
        return replenishRate;
    }

    public void setReplenishRate(Integer replenishRate) {
        this.replenishRate = replenishRate;
    }

    public Integer getBurstCapacity() {
        return burstCapacity;
    }

    public void setBurstCapacity(Integer burstCapacity) {
        this.burstCapacity = burstCapacity;
    }

    public List<String> getRateLimitDimensionList() {
        return rateLimitDimensionList;
    }

    public void setRateLimitDimensionList(List<String> rateLimitDimensionList) {
        this.rateLimitDimensionList = rateLimitDimensionList;
    }

    public String getRateLimitDimensionMeaningList() {
        return rateLimitDimensionMeaningList;
    }

    public void setRateLimitDimensionMeaningList(String rateLimitDimensionMeaningList) {
        this.rateLimitDimensionMeaningList = rateLimitDimensionMeaningList;
    }

    public String getUserKey() {
        return userKey;
    }

    public void setUserKey(String userKey) {
        this.userKey = userKey;
    }

    public String getRoleKey() {
        return roleKey;
    }

    public void setRoleKey(String roleKey) {
        this.roleKey = roleKey;
    }

    public String getTenantKey() {
        return tenantKey;
    }

    public void setTenantKey(String tenantKey) {
        this.tenantKey = tenantKey;
    }

    public String getOriginKey() {
        return originKey;
    }

    public void setOriginKey(String originKey) {
        this.originKey = originKey;
    }

    public String getUrlKey() {
        return urlKey;
    }

    public void setUrlKey(String urlKey) {
        this.urlKey = urlKey;
    }

    public String getUserKeyMeaning() {
        return userKeyMeaning;
    }

    public void setUserKeyMeaning(String userKeyMeaning) {
        this.userKeyMeaning = userKeyMeaning;
    }

    public String getRoleKeyMeaning() {
        return roleKeyMeaning;
    }

    public void setRoleKeyMeaning(String roleKeyMeaning) {
        this.roleKeyMeaning = roleKeyMeaning;
    }

    public String getTenantKeyMeaning() {
        return tenantKeyMeaning;
    }

    public void setTenantKeyMeaning(String tenantKeyMeaning) {
        this.tenantKeyMeaning = tenantKeyMeaning;
    }

    public String getOriginKeyMeaning() {
        return originKeyMeaning;
    }

    public void setOriginKeyMeaning(String originKeyMeaning) {
        this.originKeyMeaning = originKeyMeaning;
    }

    public String getUrlKeyMeaning() {
        return urlKeyMeaning;
    }

    public void setUrlKeyMeaning(String urlKeyMeaning) {
        this.urlKeyMeaning = urlKeyMeaning;
    }

    public GatewayRateLimitDimension translate() {
        /**
         * 转化为数组，便于lov翻译
         */
        if (!StringUtils.isEmpty(rateLimitDimension)) {
            String[] parts = rateLimitDimension.split(",");
            //parts.length == keyParts.length
            String[] keyParts = dimensionKey.split(",");
            if (parts.length > 0) {
                List<String> rateLimitDimensionList = new ArrayList<>(Arrays.asList(parts));
                setRateLimitDimensionList(rateLimitDimensionList);
                for (int i = 0; i < parts.length; i++) {
                    String dimension = rateLimitDimensionList.get(i);
                    String key = i > keyParts.length - 1 ? null : keyParts[i];
                    String extractValue = Dimension.extractDimension(dimension);
                    String extractTemplate = Dimension.extractTemplate(dimension);
                    if ("user".equals(dimension)) {
                        setUserKey(key);
                    } else if ("role".equals(dimension)) {
                        setRoleKey(key);
                    } else if ("tenant".equals(dimension)) {
                        setTenantKey(key);
                    } else if ("origin".equals(dimension)) {
                        setOriginKey(key);
                    } else if ("url".equals(dimension) || "url".equals(extractValue)) {
                        if (extractTemplate != null && key != null){
                            setUrlKey(Dimension.buildUrlKey(extractTemplate, key));
                        }
                    }
                }
            }
        }
        if (!StringUtils.isEmpty(originKey)) {
            setOriginKeyMeaning(originKey);
        }
        if (!StringUtils.isEmpty(urlKey)) {
            setUrlKeyMeaning(urlKey);
        }
        return this;
    }

    public enum Dimension {

        TENANT("TenantKeyResolver"),
        ROLE("RoleKeyResolver"),
        USER("UserKeyResolver"),
        ORIGIN("OriginKeyResolver"),
        /**
         * special
         */
        URL("UrlKeyResolver");

        public static final String COMBINED_KEY_RESOLVER = "CombinedKeyResolver";

        private static final Pattern PATTERN = Pattern.compile("(\\S+)\\((\\S+)\\)");

        private static final String LIST_SPLIT = ";";

        private String className;

        Dimension(String className) {
            this.className = className;
        }

        public static String getClassName(String dimensionName) throws IllegalArgumentException {
            for (Dimension dimension : values()) {
                if (dimension.name().toLowerCase().equals(dimensionName)) {
                    return dimension.className;
                }
                if (dimension == URL) {
                    Matcher matcher = PATTERN.matcher(dimensionName);
                    if (matcher.matches() && "url".equals(matcher.group(1))) {
                        return dimension.className;
                    }
                }
            }
            throw new IllegalArgumentException("unknown dimension [" + dimensionName + "]");
        }

        /**
         * url(/v1/{1}/invoke?namespace={2}&serverCode={3}&interfaceCode={4})
         *
         * @param dimensionName eg.url(/v1/{1}/invoke?namespace={2}&serverCode={3}&interfaceCode={4})
         * @return template     eg./v1/{1}/invoke?namespace={2}&serverCode={3}&interfaceCode={4}
         */
        public static String extractTemplate(String dimensionName) {
            String temp = dimensionName.replaceAll(" ", "");
            Matcher matcher = PATTERN.matcher(temp);
            if (matcher.find()) {
                return matcher.group(2);
            }
            return null;
        }

        /**
         * url(/v1/{1}/invoke?namespace={2}&serverCode={3}&interfaceCode={4})
         *
         * @param dimensionName eg.url(/v1/{1}/invoke?namespace={2}&serverCode={3}&interfaceCode={4})
         * @return dimension    eg.url
         */
        public static String extractDimension(String dimensionName){
            String temp = dimensionName.replaceAll(" ", "");
            Matcher matcher = PATTERN.matcher(temp);
            if (matcher.find()) {
                return matcher.group(1);
            }
            return null;
        }

        /**
         * template /v1/{1}/invoke?namespace={2}&serverCode={3}&interfaceCode={4}
         *
         * @param template      eg./v1/{1}/invoke?namespace={2}&serverCode={3}&interfaceCode={4})
         * @param paramString   eg.rest,test,test,test
         * @return urlKey       eg./v1/rest/invoke?namespace=test&serverCode=test&interfaceCode=test
         */
        public static String buildUrlKey(String template, String paramString){
            String[] params = paramString.split(LIST_SPLIT);
            int count = Integer.parseInt(template.substring(template.lastIndexOf("{") + 1, template.lastIndexOf("}")));
            if (count != params.length){
                throw new IllegalArgumentException("template and params do not correspond.");
            }
            for (int i = 1; i <= count; i++){
                template = template.replace("{" + i + "}", params[i - 1]);
            }
            return template;
        }

    }

}
