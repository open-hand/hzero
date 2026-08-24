package org.hzero.message.domain.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.core.type.TypeReference;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import org.apache.commons.lang3.StringUtils;
import org.hzero.common.HZeroService;
import org.hzero.core.redis.RedisHelper;
import org.hzero.message.domain.repository.UserReceiveConfigRepository;
import org.hzero.message.domain.vo.UserInfoVO;
import org.hzero.message.infra.constant.HmsgConstant;
import org.hzero.starter.keyencrypt.core.Encrypt;
import org.springframework.util.Assert;

import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import io.choerodon.mybatis.annotation.ModifyAudit;
import io.choerodon.mybatis.annotation.VersionAudit;
import io.choerodon.mybatis.domain.AuditDomain;

/**
 * 用户接收配置
 *
 * @author shuangfei.zhu@hand-china.com 2018-11-19 20:42:53
 */
@ApiModel("用户接收配置")
@VersionAudit
@ModifyAudit
@Table(name = "hmsg_user_receive_config")
@JsonInclude(JsonInclude.Include.NON_NULL)
public class UserReceiveConfig extends AuditDomain {

    public static final String FIELD_USER_RECEIVE_ID = "userReceiveId";
    public static final String FIELD_USER_ID = "userId";
    public static final String FIELD_RECEIVE_CODE = "receiveCode";
    public static final String FIELD_RECEIVE_TYPE = "receiveType";
    public static final String FIELD_TENANT_ID = "tenantId";
    public static final String DEFAULT_CONFIG_CODE = "OVERALL";

    //
    // 业务方法(按public protected private顺序排列)
    // ------------------------------------------------------------------------------

    public void validateRepeat(UserReceiveConfigRepository repository) {
        UserReceiveConfig config = new UserReceiveConfig().setUserId(userId).setReceiveCode(receiveCode).setTenantId(tenantId);
        Assert.isTrue(repository.selectCount(config) == 0, HmsgConstant.ErrorCode.RECEIVE_CODE_REPEAT);
    }

    // 黑名单缓存

    /**
     * 生成redis存储key
     *
     * @param msgType 消息类型
     * @return key
     */
    private static String getCacheKey(String msgType) {
        return HZeroService.Message.CODE + ":user-receive-config:" + msgType;
    }

    private static String getValue(String msgType, UserInfoVO userInfo) {
        switch (msgType) {
            case HmsgConstant.MessageType.WEB:
                return String.valueOf(userInfo.getId());
            case HmsgConstant.MessageType.SMS:
            case HmsgConstant.MessageType.CALL:
                return userInfo.getPhone();
            case HmsgConstant.MessageType.EMAIL:
                return userInfo.getEmail();
            case HmsgConstant.MessageType.WC_E:
            case HmsgConstant.MessageType.DT:
                return userInfo.getOpenUserId();
            default:
                return null;
        }
    }

    /**
     * 刷新缓存
     *
     * @param redisHelper redis
     * @param msgTypeList 消息类型
     * @param userInfo    用户信息
     * @param configCode  配置编码
     */
    public static void refreshCache(RedisHelper redisHelper, List<String> msgTypeList, UserInfoVO userInfo, String configCode) {
        for (String msgType : msgTypeList) {
            String value = getValue(msgType, userInfo);
            refreshCache(redisHelper, msgType, value, configCode);
        }
    }

    /**
     * 刷新缓存
     *
     * @param redisHelper redis
     * @param msgType     消息类型
     * @param userInfo    用户信息
     * @param configCode  配置编码
     */
    public static void refreshCache(RedisHelper redisHelper, String msgType, UserInfoVO userInfo, String configCode) {
        String value = getValue(msgType, userInfo);
        refreshCache(redisHelper, msgType, value, configCode);
    }

    /**
     * 刷新缓存
     *
     * @param redisHelper redis
     * @param msgType     消息类型
     * @param value       接收人地址
     * @param configCode  配置编码
     */
    public static void refreshCache(RedisHelper redisHelper, String msgType, String value, String configCode) {
        if (StringUtils.isBlank(value)) {
            return;
        }
        String result = redisHelper.hshGet(getCacheKey(msgType), value);
        Set<String> configCodeSet;
        if (StringUtils.isBlank(result)) {
            configCodeSet = new HashSet<>();
        } else {
            configCodeSet = redisHelper.fromJson(result, new TypeReference<Set<String>>() {
            });
        }
        configCodeSet.add(configCode);
        redisHelper.hshPut(getCacheKey(msgType), value, redisHelper.toJson(configCodeSet));
    }

    /**
     * 清除缓存
     *
     * @param redisHelper redis
     * @param msgTypeList 消息类型
     * @param userInfo    用户信息
     * @param configCode  配置编码
     */
    public static void deleteCache(RedisHelper redisHelper, List<String> msgTypeList, UserInfoVO userInfo, String configCode) {
        for (String msgType : msgTypeList) {
            String value = getValue(msgType, userInfo);
            deleteCache(redisHelper, msgType, value, configCode);
        }
    }

    /**
     * 清除缓存
     *
     * @param redisHelper redis
     * @param msgType     消息类型
     * @param userInfo    用户信息
     * @param configCode  配置编码
     */
    public static void deleteCache(RedisHelper redisHelper, String msgType, UserInfoVO userInfo, String configCode) {
        String value = getValue(msgType, userInfo);
        deleteCache(redisHelper, msgType, value, configCode);
    }

    /**
     * 清除缓存
     *
     * @param redisHelper redis
     * @param msgType     消息类型
     * @param value       接收人地址
     * @param configCode  配置编码
     */
    public static void deleteCache(RedisHelper redisHelper, String msgType, String value, String configCode) {
        if (StringUtils.isBlank(value)) {
            return;
        }
        String result = redisHelper.hshGet(getCacheKey(msgType), value);
        Set<String> configCodeSet;
        if (StringUtils.isBlank(result)) {
            return;
        }
        configCodeSet = redisHelper.fromJson(result, new TypeReference<Set<String>>() {
        });
        configCodeSet.remove(configCode);
        if (configCodeSet.isEmpty()) {
            redisHelper.hshDelete(getCacheKey(msgType), value);
        } else {
            redisHelper.hshPut(getCacheKey(msgType), value, redisHelper.toJson(configCodeSet));
        }
    }

    /**
     * 查询缓存
     *
     * @param redisHelper redis
     * @param msgType     消息类型
     * @param address     接收人地址
     * @param configCode  配置编码
     * @return true在黑名单
     */
    public static boolean check(RedisHelper redisHelper, String msgType, String address, String configCode, Long tenantId) {
        if (StringUtils.isBlank(configCode) || StringUtils.isBlank(address)) {
            return false;
        }
        List<String> defaultTypeList = ReceiveConfig.getCache(redisHelper, tenantId, configCode);
        // 查询用户接收配置黑名单缓存
        String result = redisHelper.hshGet(getCacheKey(msgType), address);
        if (StringUtils.isBlank(result)) {
            return false;
        }
        List<String> userTypeList = redisHelper.fromJsonList(result, String.class);
        // 接收配置包含该类型，并且在用户黑名单中返回true
        // 在用户黑名单中，但是接收配置不含该类型返回false
        return defaultTypeList.contains(msgType) && userTypeList.contains(configCode);
    }

    //
    // 数据库字段
    // ------------------------------------------------------------------------------


    @ApiModelProperty("表ID，主键，供其他表做外键")
    @Id
    @GeneratedValue
    @Encrypt
    private Long userReceiveId;
    @ApiModelProperty(value = "用户id,iam_user.id", required = true)
    @JsonIgnore
    private Long userId;
    @ApiModelProperty(value = "hmsg_receive_config .receiver_code", required = true)
    @NotBlank
    @Size(max = 30)
    private String receiveCode;
    @ApiModelProperty(value = "接收方式", required = true)
    @Size(max = 30)
    private String receiveType;
    private Long tenantId;

    //
    // 非数据库字段
    // ------------------------------------------------------------------------------


    //
    // getter/setter
    // ------------------------------------------------------------------------------

    /**
     * @return 表ID，主键，供其他表做外键
     */
    public Long getUserReceiveId() {
        return userReceiveId;
    }

    public UserReceiveConfig setUserReceiveId(Long userReceiveId) {
        this.userReceiveId = userReceiveId;
        return this;
    }

    /**
     * @return 用户id, iam_user.id
     */
    public Long getUserId() {
        return userId;
    }

    public UserReceiveConfig setUserId(Long userId) {
        this.userId = userId;
        return this;
    }

    /**
     * @return hmsg_receive_config .receiver_code
     */
    public String getReceiveCode() {
        return receiveCode;
    }

    public UserReceiveConfig setReceiveCode(String receiveCode) {
        this.receiveCode = receiveCode;
        return this;
    }

    /**
     * @return 接收方式
     */
    public String getReceiveType() {
        return receiveType;
    }

    public UserReceiveConfig setReceiveType(String receiveType) {
        this.receiveType = receiveType;
        return this;
    }

    public Long getTenantId() {
        return tenantId;
    }

    public UserReceiveConfig setTenantId(Long tenantId) {
        this.tenantId = tenantId;
        return this;
    }
}
