package org.hzero.message.domain.entity;

import com.fasterxml.jackson.annotation.JsonInclude;
import io.choerodon.mybatis.annotation.MultiLanguage;
import io.choerodon.mybatis.annotation.MultiLanguageField;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import org.apache.commons.lang3.StringUtils;
import org.hibernate.validator.constraints.Length;
import org.hibernate.validator.constraints.Range;
import org.hzero.boot.platform.lov.annotation.LovValue;
import org.hzero.common.HZeroService;
import org.hzero.core.base.BaseConstants;
import org.hzero.core.redis.RedisHelper;
import org.hzero.core.util.Regexs;
import org.hzero.message.domain.repository.WeChatOfficialRepository;
import org.hzero.message.infra.constant.HmsgConstant;
import org.hzero.mybatis.annotation.DataSecurity;
import org.hzero.mybatis.annotation.Unique;
import org.hzero.starter.keyencrypt.core.Encrypt;
import org.springframework.util.Assert;

import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.Transient;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;
import java.util.Objects;

import io.choerodon.core.exception.CommonException;
import io.choerodon.mybatis.annotation.ModifyAudit;
import io.choerodon.mybatis.annotation.VersionAudit;
import io.choerodon.mybatis.domain.AuditDomain;

/**
 * 微信公众号配置
 *
 * @author shuangfei.zhu@hand-china.com 2019-10-15 14:33:21
 */
@ApiModel("微信公众号配置")
@VersionAudit
@ModifyAudit
@Table(name = "hmsg_wechat_official")
@MultiLanguage
@JsonInclude(JsonInclude.Include.NON_NULL)
public class WechatOfficial extends AuditDomain {

    public static final String FIELD_SERVER_ID = "serverId";
    public static final String FIELD_SERVER_CODE = "serverCode";
    public static final String FIELD_SERVER_NAME = "serverName";
    public static final String FIELD_AUTH_TYPE = "authType";
    public static final String FIELD_APPID = "appid";
    public static final String FIELD_SECRET = "secret";
    public static final String FIELD_AUTH_ADDRESS = "authAddress";
    public static final String FIELD_TENANT_ID = "tenantId";
    public static final String FIELD_ENABLED_FLAG = "enabledFlag";

    //
    // 业务方法(按public protected private顺序排列)
    // ------------------------------------------------------------------------------

    public void validate() {
        if (Objects.equals(authType, HmsgConstant.WeChatAuthType.WE_CHAT)) {
            Assert.hasLength(appid, BaseConstants.ErrorCode.NOT_NULL);
        } else if (Objects.equals(authType, HmsgConstant.WeChatAuthType.THIRD)) {
            Assert.hasLength(authAddress, BaseConstants.ErrorCode.NOT_NULL);
        }
    }

    /**
     * 校验租户id是否相等
     *
     * @param repository WeChatOfficialRepository
     */
    public void validateTenant(WeChatOfficialRepository repository) {
        WechatOfficial official = repository.selectByPrimaryKey(serverId);
        if (!Objects.equals(official.getTenantId(), tenantId)) {
            throw new CommonException(HmsgConstant.ErrorCode.TENANT_NO_MATCH);
        }
    }

    /**
     * 校验编码是否存在预定义的数据，是 - 允许删除自定义，否 - 不允许删除
     */
    public void validateCodeExistPredefined(WeChatOfficialRepository repository) {
        WechatOfficial wechatOfficial = repository.selectByCode(BaseConstants.DEFAULT_TENANT_ID, serverCode);
        if (wechatOfficial == null || serverId.equals(wechatOfficial.getServerId())) {
            // 非存在预定义数据 或者该条数据是平台的数据
            throw new CommonException(HmsgConstant.ErrorCode.DELETE_CONFIG_FAILED);
        }
    }

    // 缓存方法

    /**
     * 生成redis存储key
     *
     * @param tenantId   租户ID
     * @param serverCode 短信配置编码
     * @return key
     */
    private static String getCacheKey(Long tenantId, String serverCode) {
        return HZeroService.Message.CODE + ":server:wechat-official:" + serverCode + ":" + tenantId;
    }

    /**
     * 刷新缓存
     *
     * @param redisHelper    redis
     * @param wechatOfficial 短信配置
     */
    public static void refreshCache(RedisHelper redisHelper, WechatOfficial wechatOfficial) {
        Long tenantId = wechatOfficial.getTenantId();
        String serverCode = wechatOfficial.getServerCode();
        // 清除缓存
        clearCache(redisHelper, tenantId, serverCode);
        // 新增缓存
        redisHelper.strSet(getCacheKey(tenantId, serverCode), redisHelper.toJson(wechatOfficial));
    }

    /**
     * 查询缓存
     *
     * @param redisHelper redis
     * @param tenantId    租户ID
     * @param serverCode  钉钉配置编码
     * @return 短信配置
     */
    public static WechatOfficial getCache(RedisHelper redisHelper, Long tenantId, String serverCode) {
        String data = redisHelper.strGet(getCacheKey(tenantId, serverCode));
        if (StringUtils.isNotBlank(data)) {
            return redisHelper.fromJson(data, WechatOfficial.class);
        } else {
            return null;
        }
    }

    /**
     * 清除缓存
     *
     * @param redisHelper redis
     * @param tenantId    租户ID
     * @param serverCode  钉钉配置编码
     */
    public static void clearCache(RedisHelper redisHelper, Long tenantId, String serverCode) {
        redisHelper.delKey(getCacheKey(tenantId, serverCode));
    }

    //
    // 数据库字段
    // ------------------------------------------------------------------------------

    @ApiModelProperty(value = "表ID，主键，供其他表做外键", required = true)
    @Id
    @GeneratedValue
    @Encrypt
    private Long serverId;
    @ApiModelProperty("配置编码")
    @NotBlank
    @Length(max = 30)
    @Unique
    @Pattern(regexp = Regexs.CODE_UPPER)
    private String serverCode;
    @ApiModelProperty(value = "配置名称", required = true)
    @NotBlank
    @Length(max = 60)
    @MultiLanguageField
    private String serverName;
    @ApiModelProperty(value = "授权类型，值集：HMSG.WECHAT.AUTH_TYPE", required = true)
    @NotBlank
    @Length(max = 30)
    @LovValue("HMSG.WECHAT.AUTH_TYPE")
    private String authType;
    @ApiModelProperty(value = "用户凭证")
    @Length(max = 60)
    private String appid;
    @ApiModelProperty(value = "用户密钥")
    @Length(max = 120)
    @DataSecurity
    private String secret;
    @ApiModelProperty(value = "第三方授权地址")
    @Length(max = 480)
    private String authAddress;
    @ApiModelProperty(value = "租户ID，hpfm_tenant.tenant_id", required = true)
    @NotNull
    @Unique
    @MultiLanguageField
    private Long tenantId;
    @ApiModelProperty(value = "启用标识", required = true)
    @NotNull
    @Range(max = 1)
    private Integer enabledFlag;

    //
    // 非数据库字段
    // ------------------------------------------------------------------------------

    @Transient
    private String tenantName;
    @Transient
    private String authTypeMeaning;

    //
    // getter/setter
    // ------------------------------------------------------------------------------

    public Long getServerId() {
        return serverId;
    }

    public WechatOfficial setServerId(Long serverId) {
        this.serverId = serverId;
        return this;
    }

    public String getServerCode() {
        return serverCode;
    }

    public WechatOfficial setServerCode(String serverCode) {
        this.serverCode = serverCode;
        return this;
    }

    public String getServerName() {
        return serverName;
    }

    public WechatOfficial setServerName(String serverName) {
        this.serverName = serverName;
        return this;
    }

    public String getAuthType() {
        return authType;
    }

    public WechatOfficial setAuthType(String authType) {
        this.authType = authType;
        return this;
    }

    public String getAppid() {
        return appid;
    }

    public WechatOfficial setAppid(String appid) {
        this.appid = appid;
        return this;
    }

    public String getSecret() {
        return secret;
    }

    public WechatOfficial setSecret(String secret) {
        this.secret = secret;
        return this;
    }

    public String getAuthAddress() {
        return authAddress;
    }

    public WechatOfficial setAuthAddress(String authAddress) {
        this.authAddress = authAddress;
        return this;
    }

    public Long getTenantId() {
        return tenantId;
    }

    public WechatOfficial setTenantId(Long tenantId) {
        this.tenantId = tenantId;
        return this;
    }

    public Integer getEnabledFlag() {
        return enabledFlag;
    }

    public WechatOfficial setEnabledFlag(Integer enabledFlag) {
        this.enabledFlag = enabledFlag;
        return this;
    }

    public String getTenantName() {
        return tenantName;
    }

    public WechatOfficial setTenantName(String tenantName) {
        this.tenantName = tenantName;
        return this;
    }

    public String getAuthTypeMeaning() {
        return authTypeMeaning;
    }

    public WechatOfficial setAuthTypeMeaning(String authTypeMeaning) {
        this.authTypeMeaning = authTypeMeaning;
        return this;
    }
}
