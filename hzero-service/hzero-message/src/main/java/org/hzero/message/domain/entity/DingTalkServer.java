package org.hzero.message.domain.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
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
import org.hzero.message.domain.repository.DingTalkServerRepository;
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
import java.util.Date;
import java.util.Objects;

import io.choerodon.core.exception.CommonException;
import io.choerodon.mybatis.annotation.ModifyAudit;
import io.choerodon.mybatis.annotation.VersionAudit;
import io.choerodon.mybatis.domain.AuditDomain;

/**
 * 钉钉配置
 *
 * @author zifeng.ding@hand-china.com 2019-11-13 14:36:25
 */
@ApiModel("钉钉配置")
@VersionAudit
@ModifyAudit
@MultiLanguage
@JsonInclude(value = JsonInclude.Include.NON_NULL)
@Table(name = "hmsg_dingtalk_server")
public class DingTalkServer extends AuditDomain {

    public static final String FIELD_SERVER_ID = "serverId";
    public static final String FIELD_SERVER_CODE = "serverCode";
    public static final String FIELD_SERVER_NAME = "serverName";
    public static final String FIELD_AUTH_TYPE = "authType";
    public static final String FIELD_APP_KEY = "appKey";
    public static final String FIELD_APP_SECRET = "appSecret";
    public static final String FIELD_AUTH_ADDRESS = "authAddress";
    public static final String FIELD_TENANT_ID = "tenantId";
    public static final String FIELD_AGENT_ID = "agentId";
    public static final String FIELD_ENABLED_FLAG = "enabledFlag";



    //
    // 业务方法(按public protected private顺序排列)
    // ------------------------------------------------------------------------------

    public void validate() {
        if (Objects.equals(authType, HmsgConstant.WeChatAuthType.WE_CHAT)) {
            Assert.hasLength(appKey, BaseConstants.ErrorCode.NOT_NULL);
        } else if (Objects.equals(authType, HmsgConstant.WeChatAuthType.THIRD)) {
            Assert.hasLength(authAddress, BaseConstants.ErrorCode.NOT_NULL);
        }
    }

    public void validateTenant(Long tenantId) {
        if (!Objects.equals(this.tenantId, tenantId)) {
            throw new CommonException(HmsgConstant.ErrorCode.TENANT_NO_MATCH);
        }
    }

    /**
     * 校验编码是否存在预定义的数据，是 - 允许删除自定义数据；否 - 不允许删除
     *
     * @param dingTalkServerRepository
     */
    public void validateCodeExistPredefine(DingTalkServerRepository dingTalkServerRepository) {
        DingTalkServer dingTalkServer = dingTalkServerRepository.selectByCode(BaseConstants.DEFAULT_TENANT_ID, serverCode);
        if (dingTalkServer == null || dingTalkServer.getServerCode().equals(serverCode)) {
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
        return HZeroService.Message.CODE + ":server:ding-talk:" + serverCode + ":" + tenantId;
    }

    /**
     * 刷新缓存
     *
     * @param redisHelper    redis
     * @param dingTalkServer 短信配置
     */
    public static void refreshCache(RedisHelper redisHelper, DingTalkServer dingTalkServer) {
        Long tenantId = dingTalkServer.getTenantId();
        String serverCode = dingTalkServer.getServerCode();
        // 清除缓存
        clearCache(redisHelper, tenantId, serverCode);
        // 新增缓存
        redisHelper.strSet(getCacheKey(tenantId, serverCode), redisHelper.toJson(dingTalkServer));
    }

    /**
     * 查询缓存
     *
     * @param redisHelper redis
     * @param tenantId    租户ID
     * @param serverCode  钉钉配置编码
     * @return 短信配置
     */
    public static DingTalkServer getCache(RedisHelper redisHelper, Long tenantId, String serverCode) {
        String data = redisHelper.strGet(getCacheKey(tenantId, serverCode));
        if (StringUtils.isNotBlank(data)) {
            return redisHelper.fromJson(data, DingTalkServer.class);
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


    @ApiModelProperty("表ID，主键，供其他表做外键")
    @Id
    @GeneratedValue
    @Encrypt
    private Long serverId;
    @ApiModelProperty(value = "配置编码", required = true)
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
    @ApiModelProperty(value = "授权类型，值集：HMSG.DINGTALK.AUTH_TYPE", required = true)
    @NotBlank
    @Length(max = 30)
    @LovValue("HMSG.DINGTALK.AUTH_TYPE")
    private String authType;
    @ApiModelProperty(value = "应用key")
    @Length(max = 60)
    private String appKey;
    @ApiModelProperty(value = "应用密钥")
    @Length(max = 240)
    @DataSecurity
    private String appSecret;
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
    @ApiModelProperty(value = "应用ID")
    private Long agentId;

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

    /**
     * @return 表ID，主键，供其他表做外键
     */
    public Long getServerId() {
        return serverId;
    }

    public void setServerId(Long serverId) {
        this.serverId = serverId;
    }

    /**
     * @return 配置编码
     */
    public String getServerCode() {
        return serverCode;
    }

    public DingTalkServer setServerCode(String serverCode) {
        this.serverCode = serverCode;
        return this;
    }

    /**
     * @return 配置名称
     */
    public String getServerName() {
        return serverName;
    }

    public DingTalkServer setServerName(String serverName) {
        this.serverName = serverName;
        return this;
    }

    /**
     * @return 授权类型，值集：HMSG.WECHAT.AUTH_TYPE
     */
    public String getAuthType() {
        return authType;
    }

    public DingTalkServer setAuthType(String authType) {
        this.authType = authType;
        return this;
    }

    /**
     * @return 应用key
     */
    public String getAppKey() {
        return appKey;
    }

    public DingTalkServer setAppKey(String appKey) {
        this.appKey = appKey;
        return this;
    }

    /**
     * @return 应用密钥
     */
    public String getAppSecret() {
        return appSecret;
    }

    public DingTalkServer setAppSecret(String appSecret) {
        this.appSecret = appSecret;
        return this;
    }

    /**
     * @return 第三方授权地址
     */
    public String getAuthAddress() {
        return authAddress;
    }

    public DingTalkServer setAuthAddress(String authAddress) {
        this.authAddress = authAddress;
        return this;
    }

    /**
     * @return 租户ID，hpfm_tenant.tenant_id
     */
    public Long getTenantId() {
        return tenantId;
    }

    public DingTalkServer setTenantId(Long tenantId) {
        this.tenantId = tenantId;
        return this;
    }

    /**
     * @return 启用标识
     */
    public Integer getEnabledFlag() {
        return enabledFlag;
    }

    public DingTalkServer setEnabledFlag(Integer enabledFlag) {
        this.enabledFlag = enabledFlag;
        return this;
    }

    public Long getAgentId() {
        return agentId;
    }

    public DingTalkServer setAgentId(Long agentId) {
        this.agentId = agentId;
        return this;
    }

    public String getTenantName() {
        return tenantName;

    }

    public DingTalkServer setTenantName(String tenantName) {
        this.tenantName = tenantName;
        return this;
    }

    public String getAuthTypeMeaning() {
        return authTypeMeaning;
    }

    public DingTalkServer setAuthTypeMeaning(String authTypeMeaning) {
        this.authTypeMeaning = authTypeMeaning;
        return this;
    }

    // 重写父类Getter、Setter
    @Override
    @JsonIgnore
    public Date getCreationDate() {
        return super.getCreationDate();
    }

    @Override
    @JsonIgnore
    public Long getCreatedBy() {
        return super.getCreatedBy();
    }

    @Override
    @JsonIgnore
    public Date getLastUpdateDate() {
        return super.getLastUpdateDate();
    }

    @Override
    @JsonIgnore
    public Long getLastUpdatedBy() {
        return super.getLastUpdatedBy();
    }
}
