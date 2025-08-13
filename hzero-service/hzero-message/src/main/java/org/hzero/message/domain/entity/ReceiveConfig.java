package org.hzero.message.domain.entity;

import com.fasterxml.jackson.annotation.JsonInclude;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import org.apache.commons.lang3.StringUtils;
import org.hibernate.validator.constraints.Range;
import org.hzero.common.HZeroService;
import org.hzero.core.base.BaseConstants;
import org.hzero.core.redis.RedisHelper;
import org.hzero.core.util.Regexs;
import org.hzero.message.domain.repository.ReceiveConfigRepository;
import org.hzero.message.infra.constant.HmsgConstant;
import org.hzero.starter.keyencrypt.core.Encrypt;
import org.springframework.util.Assert;

import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import io.choerodon.mybatis.annotation.ModifyAudit;
import io.choerodon.mybatis.annotation.MultiLanguage;
import io.choerodon.mybatis.annotation.MultiLanguageField;
import io.choerodon.mybatis.annotation.VersionAudit;
import io.choerodon.mybatis.domain.AuditDomain;

/**
 * 接收配置
 *
 * @author shuangfei.zhu@hand-china.com 2018-11-19 20:42:53
 */
@ApiModel("接收配置")
@VersionAudit
@ModifyAudit
@Table(name = "hmsg_receive_config")
@MultiLanguage
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ReceiveConfig extends AuditDomain {

    public static final String FIELD_RECEIVE_ID = "receiveId";
    public static final String FIELD_RECEIVE_CODE = "receiveCode";
    public static final String FIELD_RECEIVE_NAME = "receiveName";
    public static final String FIELD_DEFAULT_RECEIVE_TYPE = "defaultReceiveType";
    public static final String FIELD_PARENT_RECEIVE_ID = "parentReceiveId";
    public static final String FIELD_PARENT_RECEIVE_CODE = "parentReceiveCode";
    public static final String FIELD_LEVEL_NUMBER = "levelNumber";
    public static final String FIELD_EDITABLE_FLAG = "editableFlag";

    //
    // 业务方法(按public protected private顺序排列)
    // ------------------------------------------------------------------------------

    /**
     * 唯一性校验
     *
     * @param repository 仓库
     */
    public void validateRepeat(ReceiveConfigRepository repository) {
        Assert.isTrue(repository.selectCount(new ReceiveConfig().setReceiveCode(receiveCode).setTenantId(tenantId)) == 0, HmsgConstant.ErrorCode.RECEIVE_CODE_REPEAT);
    }

    // 缓存方法

    /**
     * 生成redis存储key
     *
     * @param configCode 配置编码
     * @return key
     */
    private static String getCacheKey(Long tenantId, String configCode) {
        return HZeroService.Message.CODE + ":receive-config:" + tenantId + ":" + configCode;
    }

    /**
     * 刷新缓存
     *
     * @param redisHelper   redis
     * @param receiveConfig 接收配置
     */
    public static void refreshCache(RedisHelper redisHelper, ReceiveConfig receiveConfig) {
        Long tenantId = receiveConfig.getTenantId();
        String configCode = receiveConfig.getReceiveCode();
        // 清除缓存
        clearCache(redisHelper, tenantId, configCode);
        // 新增缓存
        redisHelper.strSet(getCacheKey(tenantId, configCode), receiveConfig.getDefaultReceiveType());
    }

    /**
     * 查询缓存
     *
     * @param redisHelper redis
     * @param tenantId    租户Id
     * @param configCode  配置编码
     */
    public static List<String> getCache(RedisHelper redisHelper, Long tenantId, String configCode) {
        String data = redisHelper.strGet(getCacheKey(tenantId, configCode));
        if (StringUtils.isNotBlank(data)) {
            return Arrays.asList(data.split(BaseConstants.Symbol.COMMA));
        } else {
            return Collections.emptyList();
        }
    }

    /**
     * 清除缓存
     *
     * @param redisHelper redis
     */
    public static void clearCache(RedisHelper redisHelper, Long tenantId, String configCode) {
        redisHelper.delKey(getCacheKey(tenantId, configCode));
    }

    //
    // 数据库字段
    // ------------------------------------------------------------------------------


    @ApiModelProperty("表ID，主键，供其他表做外键")
    @Id
    @GeneratedValue
    @Encrypt
    private Long receiveId;
    @ApiModelProperty(value = "接收配置编码", required = true)
    @NotBlank(groups = {ReceiveConfig.Validate.class})
    @Size(max = 30)
    @Pattern(regexp = Regexs.CODE_UPPER)
    private String receiveCode;
    @ApiModelProperty(value = "接收配置名称(TL)", required = true)
    @NotBlank(groups = {ReceiveConfig.Validate.class})
    @Size(max = 120)
    @MultiLanguageField
    private String receiveName;
    @ApiModelProperty(value = "默认接收的方式", required = true)
    @NotBlank(groups = {ReceiveConfig.Validate.class})
    @Size(max = 480)
    private String defaultReceiveType;

    @ApiModelProperty(value = "上级配置目录")
    @NotNull(groups = {ReceiveConfig.Validate.class})
    private String parentReceiveCode;
    @ApiModelProperty(value = "层级深度", required = true)
    private Integer levelNumber;
    @MultiLanguageField
    private Long tenantId;
    @ApiModelProperty(value = "更新标识")
    @Range(max = 1, min = 0)
    private Integer editableFlag;

    public interface Validate {
    }

    //
    // 非数据库字段
    // ------------------------------------------------------------------------------

    //
    // getter/setter
    // ------------------------------------------------------------------------------


    public Long getReceiveId() {
        return receiveId;
    }

    public ReceiveConfig setReceiveId(Long receiveId) {
        this.receiveId = receiveId;
        return this;
    }

    public String getReceiveCode() {
        return receiveCode;
    }

    public ReceiveConfig setReceiveCode(String receiveCode) {
        this.receiveCode = receiveCode;
        return this;
    }

    public String getReceiveName() {
        return receiveName;
    }

    public ReceiveConfig setReceiveName(String receiveName) {
        this.receiveName = receiveName;
        return this;
    }

    public String getDefaultReceiveType() {
        return defaultReceiveType;
    }

    public ReceiveConfig setDefaultReceiveType(String defaultReceiveType) {
        this.defaultReceiveType = defaultReceiveType;
        return this;
    }

    public String getParentReceiveCode() {
        return parentReceiveCode;
    }

    public ReceiveConfig setParentReceiveCode(String parentReceiveCode) {
        this.parentReceiveCode = parentReceiveCode;
        return this;
    }

    public Integer getLevelNumber() {
        return levelNumber;
    }

    public ReceiveConfig setLevelNumber(Integer levelNumber) {
        this.levelNumber = levelNumber;
        return this;
    }

    public Long getTenantId() {
        return tenantId;
    }

    public ReceiveConfig setTenantId(Long tenantId) {
        this.tenantId = tenantId;
        return this;
    }

    public Integer getEditableFlag() {
        return editableFlag;
    }

    public ReceiveConfig setEditableFlag(Integer editableFlag) {
        this.editableFlag = editableFlag;
        return this;
    }
}
