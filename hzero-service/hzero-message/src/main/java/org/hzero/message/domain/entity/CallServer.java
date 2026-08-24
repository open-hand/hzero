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
import org.hzero.core.redis.RedisHelper;
import org.hzero.core.util.Regexs;
import org.hzero.mybatis.annotation.DataSecurity;
import org.hzero.mybatis.annotation.Unique;
import org.hzero.starter.keyencrypt.core.Encrypt;

import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.Transient;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;

import io.choerodon.mybatis.annotation.ModifyAudit;
import io.choerodon.mybatis.annotation.VersionAudit;
import io.choerodon.mybatis.domain.AuditDomain;

/**
 * 语音消息服务
 *
 * @author shuangfei.zhu@hand-china.com 2020-02-26 15:59:05
 */
@ApiModel("语音消息服务")
@VersionAudit
@ModifyAudit
@MultiLanguage
@JsonInclude(value = JsonInclude.Include.NON_NULL)
@Table(name = "hmsg_call_server")
public class CallServer extends AuditDomain {

    public static final String FIELD_SERVER_ID = "serverId";
    public static final String FIELD_SERVER_CODE = "serverCode";
    public static final String FIELD_SERVER_NAME = "serverName";
    public static final String FIELD_SERVER_TYPE_CODE = "serverTypeCode";
    public static final String FIELD_ACCESS_KEY = "accessKey";
    public static final String FIELD_ACCESS_SECRET = "accessSecret";
    public static final String FIELD_EXT_PARAM = "extParam";
    public static final String FIELD_TENANT_ID = "tenantId";
    public static final String FIELD_ENABLED_FLAG = "enabledFlag";


    //
    // 业务方法(按public protected private顺序排列)
    // ------------------------------------------------------------------------------

    // 缓存方法

    /**
     * 生成redis存储key
     *
     * @param tenantId   租户ID
     * @param serverCode 短信配置编码
     * @return key
     */
    private static String getCacheKey(Long tenantId, String serverCode) {
        return HZeroService.Message.CODE + ":server:call:" + serverCode + ":" + tenantId;
    }

    /**
     * 刷新缓存
     *
     * @param redisHelper redis
     * @param callServer  语音配置
     */
    public static void refreshCache(RedisHelper redisHelper, CallServer callServer) {
        Long tenantId = callServer.getTenantId();
        String serverCode = callServer.getServerCode();
        // 清除缓存
        clearCache(redisHelper, tenantId, serverCode);
        // 新增缓存
        redisHelper.strSet(getCacheKey(tenantId, serverCode), redisHelper.toJson(callServer));
    }

    /**
     * 查询缓存
     *
     * @param redisHelper redis
     * @param tenantId    租户ID
     * @param serverCode  短信配置编码
     * @return 短信配置
     */
    public static CallServer getCache(RedisHelper redisHelper, Long tenantId, String serverCode) {
        String data = redisHelper.strGet(getCacheKey(tenantId, serverCode));
        if (StringUtils.isNotBlank(data)) {
            return redisHelper.fromJson(data, CallServer.class);
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
    @ApiModelProperty(value = "服务类型，值集：HMSG.CALL.SERVER_TYPE", required = true)
    @NotBlank
    @Length(max = 30)
    @LovValue(value = "HMSG.CALL.SERVER_TYPE")
    private String serverTypeCode;
    @ApiModelProperty(value = "aapId或accessKeyId", required = true)
    @NotBlank
    @Length(max = 240)
    private String accessKey;
    @ApiModelProperty(value = "密钥", required = true)
    @Length(max = 480)
    @DataSecurity
    private String accessSecret;
    @ApiModelProperty(value = "配置扩展参数")
    @Length(max = 480)
    private String extParam;
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
    private String serverTypeMeaning;

    //
    // getter/setter
    // ------------------------------------------------------------------------------

    public Long getServerId() {
        return serverId;
    }

    public CallServer setServerId(Long serverId) {
        this.serverId = serverId;
        return this;
    }

    public String getServerCode() {
        return serverCode;
    }

    public CallServer setServerCode(String serverCode) {
        this.serverCode = serverCode;
        return this;
    }

    public String getServerName() {
        return serverName;
    }

    public CallServer setServerName(String serverName) {
        this.serverName = serverName;
        return this;
    }

    public String getServerTypeCode() {
        return serverTypeCode;
    }

    public CallServer setServerTypeCode(String serverTypeCode) {
        this.serverTypeCode = serverTypeCode;
        return this;
    }

    public String getAccessKey() {
        return accessKey;
    }

    public CallServer setAccessKey(String accessKey) {
        this.accessKey = accessKey;
        return this;
    }

    public String getAccessSecret() {
        return accessSecret;
    }

    public CallServer setAccessSecret(String accessSecret) {
        this.accessSecret = accessSecret;
        return this;
    }

    public String getExtParam() {
        return extParam;
    }

    public CallServer setExtParam(String extParam) {
        this.extParam = extParam;
        return this;
    }

    public Long getTenantId() {
        return tenantId;
    }

    public CallServer setTenantId(Long tenantId) {
        this.tenantId = tenantId;
        return this;
    }

    public Integer getEnabledFlag() {
        return enabledFlag;
    }

    public CallServer setEnabledFlag(Integer enabledFlag) {
        this.enabledFlag = enabledFlag;
        return this;
    }

    public String getTenantName() {
        return tenantName;
    }

    public CallServer setTenantName(String tenantName) {
        this.tenantName = tenantName;
        return this;
    }

    public String getServerTypeMeaning() {
        return serverTypeMeaning;
    }

    public CallServer setServerTypeMeaning(String serverTypeMeaning) {
        this.serverTypeMeaning = serverTypeMeaning;
        return this;
    }
}
