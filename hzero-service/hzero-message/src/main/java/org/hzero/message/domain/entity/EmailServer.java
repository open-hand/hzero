package org.hzero.message.domain.entity;

import com.fasterxml.jackson.annotation.JsonInclude;
import io.choerodon.mybatis.annotation.MultiLanguage;
import io.choerodon.mybatis.annotation.MultiLanguageField;
import io.swagger.annotations.ApiModelProperty;
import org.apache.commons.lang3.StringUtils;
import org.hibernate.validator.constraints.Length;
import org.hibernate.validator.constraints.Range;
import org.hzero.boot.platform.lov.annotation.LovValue;
import org.hzero.common.HZeroService;
import org.hzero.core.base.BaseConstants;
import org.hzero.core.redis.RedisHelper;
import org.hzero.core.util.Regexs;
import org.hzero.message.domain.repository.EmailServerRepository;
import org.hzero.message.infra.constant.HmsgConstant;
import org.hzero.mybatis.annotation.DataSecurity;
import org.hzero.starter.keyencrypt.core.Encrypt;
import org.springframework.util.CollectionUtils;

import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.Transient;
import javax.validation.Valid;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;
import java.util.List;
import java.util.Objects;

import io.choerodon.core.exception.CommonException;
import io.choerodon.mybatis.annotation.ModifyAudit;
import io.choerodon.mybatis.annotation.VersionAudit;
import io.choerodon.mybatis.domain.AuditDomain;

/**
 * 邮箱服务
 *
 * @author xianzhi.chen@hand-china.com 2018-07-27 11:30:58
 */
@VersionAudit
@ModifyAudit
@Table(name = "hmsg_email_server")
@MultiLanguage
@JsonInclude(JsonInclude.Include.NON_NULL)
public class EmailServer extends AuditDomain {

    public static final String FIELD_SERVER_ID = "serverId";
    public static final String FIELD_SERVER_CODE = "serverCode";
    public static final String FIELD_SERVER_NAME = "serverName";
    public static final String FIELD_HOST = "host";
    public static final String FIELD_PORT = "port";
    public static final String FIELD_PROTOCOL = "protocol";
    public static final String FIELD_TRY_TIMES = "tryTimes";
    public static final String FIELD_USERNAME = "username";
    public static final String FIELD_PASS_WORD = "passwordEncrypted";
    public static final String FIELD_SENDER = "sender";
    public static final String FIELD_FILTER_STRATEGY = "filterStrategy";
    public static final String FIELD_ENABLED_FLAG = "enabledFlag";
    public static final String FIELD_TENANT_ID = "tenantId";



    //
    // 业务方法(按public protected private顺序排列)
    // ------------------------------------------------------------------------------

    /**
     * 校验服务器编码重复
     *
     * @param repository 仓库
     */
    public void validateServerCodeRepeat(EmailServerRepository repository) {
        if (!CollectionUtils.isEmpty(repository.select(new EmailServer().setTenantId(tenantId).setServerCode(serverCode)))) {
            throw new CommonException(BaseConstants.ErrorCode.ERROR_CODE_REPEAT);
        }
    }

    /**
     * 判断租户id是否相等
     *
     * @param repository EmailServerRepository
     */
    public void validateTenant(EmailServerRepository repository) {
        EmailServer server = repository.selectByPrimaryKey(serverId);
        if (!Objects.equals(server.getTenantId(), tenantId)) {
            throw new CommonException(HmsgConstant.ErrorCode.TENANT_NO_MATCH);
        }
    }

    /**
     * 校验编码是否存在预定义的数据，是 - 允许删除自定义，否 - 不允许删除
     */
    public void validateCodeExistPredefined(EmailServerRepository repository) {
        EmailServer emailServer = repository.selectByCode(BaseConstants.DEFAULT_TENANT_ID, serverCode);
        if (emailServer == null || serverId.equals(emailServer.getServerId())) {
            // 非存在预定义数据 或者该条数据是平台的数据
            throw new CommonException(HmsgConstant.ErrorCode.DELETE_CONFIG_FAILED);
        }
    }

    // 缓存方法

    /**
     * 生成redis存储key
     *
     * @param serverCode 邮箱服务编码
     * @return key
     */
    private static String getCacheKey(Long tenantId, String serverCode) {
        return HZeroService.Message.CODE + ":server:email:" + serverCode + ":" + tenantId;
    }

    /**
     * 刷新缓存
     *
     * @param redisHelper redis
     * @param emailServer 邮箱服务，需要有邮箱参数
     */
    public static void refreshCache(RedisHelper redisHelper, EmailServer emailServer) {
        Long tenantId = emailServer.getTenantId();
        String serverCode = emailServer.getServerCode();
        // 清除缓存
        clearCache(redisHelper, tenantId, serverCode);
        // 新增缓存
        redisHelper.strSet(getCacheKey(tenantId, serverCode), redisHelper.toJson(emailServer));
    }

    /**
     * 查询缓存
     *
     * @param redisHelper redis
     * @param tenantId    租户Id
     * @param serverCode  邮箱服务编码
     */
    public static EmailServer getCache(RedisHelper redisHelper, Long tenantId, String serverCode) {
        String data = redisHelper.strGet(getCacheKey(tenantId, serverCode));
        if (StringUtils.isNotBlank(data)) {
            return redisHelper.fromJson(data, EmailServer.class);
        } else {
            return null;
        }
    }

    /**
     * 清除缓存
     *
     * @param redisHelper redis
     */
    public static void clearCache(RedisHelper redisHelper, Long tenantId, String serverCode) {
        redisHelper.delKey(getCacheKey(tenantId, serverCode));
    }

    //
    // 数据库字段
    // ------------------------------------------------------------------------------


    @Id
    @GeneratedValue
    @Encrypt
    private Long serverId;
    @NotBlank
    @Size(max = 30)
    @Pattern(regexp = Regexs.CODE_UPPER)
    private String serverCode;
    @NotBlank
    @Size(max = 60)
    @MultiLanguageField
    private String serverName;
    @NotBlank
    @Size(max = 60)
    private String host;
    @NotBlank
    @Size(max = 10)
    @Pattern(regexp = Regexs.DIGITAL)
    private String port;
    @ApiModelProperty(value = "协议，值集：HMSG.EMAIL_PROTOCOL")
    @NotBlank
    @Length(max = 30)
    @LovValue(value = "HMSG.EMAIL_PROTOCOL")
    private String protocol;
    @Range(min = 0, max = 5)
    private Integer tryTimes;
    @Length(max = 60)
    private String username;
    @Size(max = 120)
    @DataSecurity
    private String passwordEncrypted;
    @Size(max = 60)
    private String sender;
    @ApiModelProperty(value = "筛选策略，值集：HMSG.EMAIL.FILTER_STRATEGY")
    @Length(max = 30)
    @LovValue(value = "HMSG.EMAIL.FILTER_STRATEGY")
    private String filterStrategy;
    @NotNull
    private Integer enabledFlag;
    @NotNull
    @MultiLanguageField
    private Long tenantId;

    //
    // 非数据库字段
    // ------------------------------------------------------------------------------
    /**
     * 租户名称
     */
    @Transient
    private String tenantName;
    /**
     * 邮箱配置列表
     */
    @Valid
    @Transient
    private List<EmailProperty> emailProperties;

    @Transient
    private String protocolMeaning;
    @Transient
    private String filterStrategyMeaning;
    //
    // getter/setter
    // ------------------------------------------------------------------------------

    /**
     * @return 表ID，主键，供其他表做外键
     */
    public Long getServerId() {
        return serverId;
    }

    public EmailServer setServerId(Long serverId) {
        this.serverId = serverId;
        return this;
    }

    /**
     * @return 服务代码
     */
    public String getServerCode() {
        return serverCode;
    }

    public EmailServer setServerCode(String serverCode) {
        this.serverCode = serverCode;
        return this;
    }

    /**
     * @return 服务名称
     */
    public String getServerName() {
        return serverName;
    }

    public EmailServer setServerName(String serverName) {
        this.serverName = serverName;
        return this;
    }

    /**
     * @return 服务器
     */
    public String getHost() {
        return host;
    }

    public EmailServer setHost(String host) {
        this.host = host;
        return this;
    }

    /**
     * @return 端口
     */
    public String getPort() {
        return port;
    }

    public EmailServer setPort(String port) {
        this.port = port;
        return this;
    }

    /**
     * @return 协议
     */
    public String getProtocol() {
        return protocol;
    }

    public EmailServer setProtocol(String protocol) {
        this.protocol = protocol;
        return this;
    }

    /**
     * @return 重试次数
     */
    public Integer getTryTimes() {
        return tryTimes;
    }

    public EmailServer setTryTimes(Integer tryTimes) {
        this.tryTimes = tryTimes;
        return this;
    }

    /**
     * @return 用户名
     */
    public String getUsername() {
        return username;
    }

    public EmailServer setUsername(String username) {
        this.username = username;
        return this;
    }

    /**
     * @return 密码
     */
    public String getPasswordEncrypted() {
        return passwordEncrypted;
    }

    public EmailServer setPasswordEncrypted(String passwordEncrypted) {
        this.passwordEncrypted = passwordEncrypted;
        return this;
    }

    /**
     * @return 发件人
     */
    public String getSender() {
        return sender;
    }

    public EmailServer setSender(String sender) {
        this.sender = sender;
        return this;
    }

    /**
     * @return 启用标识
     */
    public Integer getEnabledFlag() {
        return enabledFlag;
    }

    public EmailServer setEnabledFlag(Integer enabledFlag) {
        this.enabledFlag = enabledFlag;
        return this;
    }

    /**
     * @return 租户ID
     */
    public Long getTenantId() {
        return tenantId;
    }

    public EmailServer setTenantId(Long tenantId) {
        this.tenantId = tenantId;
        return this;
    }

    public String getTenantName() {
        return tenantName;
    }

    public EmailServer setTenantName(String tenantName) {
        this.tenantName = tenantName;
        return this;
    }

    public List<EmailProperty> getEmailProperties() {
        return emailProperties;
    }

    public EmailServer setEmailProperties(List<EmailProperty> emailProperties) {
        this.emailProperties = emailProperties;
        return this;
    }

    public String getProtocolMeaning() {
        return protocolMeaning;
    }

    public EmailServer setProtocolMeaning(String protocolMeaning) {
        this.protocolMeaning = protocolMeaning;
        return this;
    }

    public String getFilterStrategy() {
        return filterStrategy;
    }

    public EmailServer setFilterStrategy(String filterStrategy) {
        this.filterStrategy = filterStrategy;
        return this;
    }

    public String getFilterStrategyMeaning() {
        return filterStrategyMeaning;
    }

    public EmailServer setFilterStrategyMeaning(String filterStrategyMeaning) {
        this.filterStrategyMeaning = filterStrategyMeaning;
        return this;
    }
}
