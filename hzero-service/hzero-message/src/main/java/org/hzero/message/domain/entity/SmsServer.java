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
import org.hzero.message.domain.repository.SmsServerRepository;
import org.hzero.message.infra.constant.HmsgConstant;
import org.hzero.mybatis.annotation.DataSecurity;
import org.hzero.starter.keyencrypt.core.Encrypt;
import org.springframework.util.CollectionUtils;

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
 * 短信服务
 *
 * @author xianzhi.chen@hand-china.com 2018-07-27 11:30:58
 */
@VersionAudit
@ModifyAudit
@Table(name = "hmsg_sms_server")
@MultiLanguage
@JsonInclude(JsonInclude.Include.NON_NULL)
@ApiModel("短信服务")
@SuppressWarnings("all")
public class SmsServer extends AuditDomain {

    public static final String FIELD_SERVER_ID = "serverId";
    public static final String FIELD_SERVER_CODE = "serverCode";
    public static final String FIELD_SERVER_NAME = "serverName";
    public static final String FIELD_SERVER_TYPE_CODE = "serverTypeCode";
    public static final String FIELD_END_POINT = "endPoint";
    public static final String FIELD_ACCESS_KEY = "accessKey";
    public static final String FIELD_ACCESS_KEY_SECRET = "accessKeySecret";
    public static final String FIELD_SIGN_NAME = "signName";
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
    public void validateServerCodeRepeat(SmsServerRepository repository) {
        if (!CollectionUtils.isEmpty(repository.select(new SmsServer().setTenantId(tenantId).setServerCode(serverCode)))) {
            throw new CommonException(BaseConstants.ErrorCode.ERROR_CODE_REPEAT);
        }
    }

    /**
     * 校验租户id是否相等
     *
     * @param repository SmsServerRepository
     */
    public void validateTenant(SmsServerRepository repository) {
        SmsServer smsServer = repository.selectByPrimaryKey(serverId);
        if (!Objects.equals(smsServer.getTenantId(), tenantId)) {
            throw new CommonException(HmsgConstant.ErrorCode.TENANT_NO_MATCH);
        }
    }

    /**
     * 校验编码是否存在预定义的数据，是 - 允许删除自定义，否 - 不允许删除
     */
    public void validateCodeExistPredefined(SmsServerRepository repository) {
        SmsServer smsServer = repository.selectByCode(BaseConstants.DEFAULT_TENANT_ID, serverCode);
        if (smsServer == null || serverId.equals(smsServer.getServerId())) {
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
        return HZeroService.Message.CODE + ":server:sms:" + serverCode + ":" + tenantId;
    }

    /**
     * 刷新缓存
     *
     * @param redisHelper redis
     * @param smsServer   短信配置
     */
    public static void refreshCache(RedisHelper redisHelper, SmsServer smsServer) {
        Long tenantId = smsServer.getTenantId();
        String serverCode = smsServer.getServerCode();
        // 清除缓存
        clearCache(redisHelper, tenantId, serverCode);
        // 新增缓存
        redisHelper.strSet(getCacheKey(tenantId, serverCode), redisHelper.toJson(smsServer));
    }

    /**
     * 查询缓存
     *
     * @param redisHelper redis
     * @param tenantId    租户ID
     * @param serverCode  短信配置编码
     * @return 短信配置
     */
    public static SmsServer getCache(RedisHelper redisHelper, Long tenantId, String serverCode) {
        String data = redisHelper.strGet(getCacheKey(tenantId, serverCode));
        if (StringUtils.isNotBlank(data)) {
            return redisHelper.fromJson(data, SmsServer.class);
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
    @ApiModelProperty("短信服务ID")
    @Encrypt
    private Long serverId;
    @NotBlank
    @Length(max = 30)
    @ApiModelProperty("服务代码")
    @Pattern(regexp = Regexs.CODE_UPPER)
    private String serverCode;
    @NotBlank
    @Length(max = 240)
    @MultiLanguageField
    @ApiModelProperty("服务名称")
    private String serverName;
    @NotBlank
    @Length(max = 30)
    @LovValue(lovCode = "HMSG.SMS_SERVER_TYPE")
    @ApiModelProperty("短信服务类型，值集：HMSG.SMS_SERVER_TYPE")
    private String serverTypeCode;
    @Length(max = 120)
    @ApiModelProperty("服务端点")
    private String endPoint;
    @NotBlank
    @Length(max = 120)
    @ApiModelProperty("Access Key")
    private String accessKey;
    @Length(max = 120)
    @ApiModelProperty("Access Key Secret")
    @DataSecurity
    private String accessKeySecret;
    @NotBlank
    @Length(max = 30)
    @ApiModelProperty("短信签名")
    private String signName;
    @NotNull
    @Range(min = 0, max = 1)
    @ApiModelProperty("启用标记")
    private Integer enabledFlag;
    @NotNull
    @ApiModelProperty("租户ID")
    @MultiLanguageField
    private Long tenantId;


    //
    // 非数据库字段
    // ------------------------------------------------------------------------------
    /**
     * 租户名称
     */
    @Transient
    private String type;
    @Transient
    private String tenantName;
    @Transient
    private String serverTypeMeaning;

    //
    // getter/setter
    // ------------------------------------------------------------------------------

    /**
     * @return 表ID，主键，供其他表做外键
     */
    public Long getServerId() {
        return serverId;
    }

    public SmsServer setServerId(Long serverId) {
        this.serverId = serverId;
        return this;
    }

    /**
     * @return 服务代码
     */
    public String getServerCode() {
        return serverCode;
    }

    public SmsServer setServerCode(String serverCode) {
        this.serverCode = serverCode;
        return this;
    }

    /**
     * @return 服务名称
     */
    public String getServerName() {
        return serverName;
    }

    public void setServerName(String serverName) {
        this.serverName = serverName;
    }

    /**
     * @return 短信服务类型，值集:HMSG.SMS_SERVER_TYPE
     */
    public String getServerTypeCode() {
        return serverTypeCode;
    }

    public SmsServer setServerTypeCode(String serverTypeCode) {
        this.serverTypeCode = serverTypeCode;
        return this;
    }

    /**
     * @return 端点
     */
    public String getEndPoint() {
        return endPoint;
    }

    public SmsServer setEndPoint(String endPoint) {
        this.endPoint = endPoint;
        return this;
    }

    /**
     * @return AccessKeyId
     */
    public String getAccessKey() {
        return accessKey;
    }

    public void setAccessKey(String accessKey) {
        this.accessKey = accessKey;
    }

    /**
     * @return AccessKeySecret
     */
    public String getAccessKeySecret() {
        return accessKeySecret;
    }

    public void setAccessKeySecret(String accessKeySecret) {
        this.accessKeySecret = accessKeySecret;
    }

    /**
     * @return 短信签名
     */
    public String getSignName() {
        return signName;
    }

    public void setSignName(String signName) {
        this.signName = signName;
    }


    /**
     * @return 启用标识
     */
    public Integer getEnabledFlag() {
        return enabledFlag;
    }

    public void setEnabledFlag(Integer enabledFlag) {
        this.enabledFlag = enabledFlag;
    }

    /**
     * @return 租户ID
     */
    public Long getTenantId() {
        return tenantId;
    }

    public SmsServer setTenantId(Long tenantId) {
        this.tenantId = tenantId;
        return this;
    }

    /**
     * @return 租户名称
     */
    public String getTenantName() {
        return tenantName;
    }

    public SmsServer setTenantName(String tenantName) {
        this.tenantName = tenantName;
        return this;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }


    /**
     * @return 服务类型名称
     */
    public String getServerTypeMeaning() {
        return serverTypeMeaning;
    }

    public SmsServer setServerTypeMeaning(String serverTypeMeaning) {
        this.serverTypeMeaning = serverTypeMeaning;
        return this;
    }

}
