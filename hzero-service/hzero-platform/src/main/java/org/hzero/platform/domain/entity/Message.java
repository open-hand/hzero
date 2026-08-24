package org.hzero.platform.domain.entity;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.choerodon.core.exception.CommonException;
import io.choerodon.mybatis.annotation.ModifyAudit;
import io.choerodon.mybatis.annotation.VersionAudit;
import io.choerodon.mybatis.domain.AuditDomain;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import org.apache.commons.lang3.StringUtils;
import org.hibernate.validator.constraints.Length;
import javax.validation.constraints.NotBlank;
import org.hzero.boot.platform.lov.annotation.LovValue;
import org.hzero.core.base.BaseConstants;
import org.hzero.core.redis.RedisHelper;
import org.hzero.core.util.Regexs;
import org.hzero.mybatis.domian.Condition;
import org.hzero.mybatis.util.Sqls;
import org.hzero.platform.domain.repository.ResponseMessageRepository;
import org.hzero.platform.domain.vo.MessageVO;
import org.hzero.platform.infra.constant.Constants;
import org.hzero.starter.keyencrypt.core.Encrypt;

import javax.persistence.*;
import javax.validation.constraints.Pattern;

/**
 * 后端消息
 *
 * @author xiaoyu.zhao@hand-china.com 2019-01-08 15:04:28
 */
@ApiModel("后端消息")
@VersionAudit
@ModifyAudit
@Table(name = "hpfm_message")
@JsonInclude(JsonInclude.Include.NON_NULL)
public class Message extends AuditDomain {

    public static final String FIELD_MESSAGE_ID = "messageId";
    public static final String FIELD_CODE = "code";
    public static final String FIELD_TYPE = "type";
    public static final String FIELD_TYPE_MEANING = "typeMeaning";
    public static final String FIELD_LANG = "lang";
    public static final String FIELD_DESCRIPTION = "description";


    /**
     * 后端消息返回
     */
    public static final String MESSAGE_KEY = Constants.APP_CODE + ":message";

    //
    // 业务方法(按public protected private顺序排列)
    // ------------------------------------------------------------------------------

    /**
     * 判断消息是否重复
     *
     * @param messageRepository 后端消息资源库
     */
    public void validate(ResponseMessageRepository messageRepository) {
        int count = messageRepository.selectCountByCondition(Condition.builder(Message.class)
                .andWhere(Sqls.custom()
                        .andEqualTo(Message.FIELD_CODE, code)
                        .andEqualTo(Message.FIELD_LANG, lang)
                )
                .build());
        if (count != BaseConstants.Digital.ZERO) {
            throw new CommonException(BaseConstants.ErrorCode.ERROR_CODE_REPEAT);
        }
    }

    /**
     * 缓存 message 信息
     */
    public void cacheMessage(RedisHelper redisHelper, ObjectMapper objectMapper) {
        String uniqueKey = this.buildUniqueKey(code, lang);
        MessageVO mv = new MessageVO(type,description,code);
        try {
            redisHelper.hshPut(uniqueKey,code,objectMapper.writeValueAsString(mv));
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }
    }

    /**
     * 清除缓存
     */
    public void clearCache(RedisHelper redisHelper) {
        String uniqueKey = this.buildUniqueKey(code, lang);
        redisHelper.hshDelete(uniqueKey, code);
    }

    /**
     * 生成 redis 唯一键
     *
     * @param code 消息编码
     * @param lang 语言
     * @return uniqueKey
     */
    public String buildUniqueKey(String code, String lang) {
        // 使用 code 和 lang 组成缓存 hash 的唯一键
        return StringUtils.join(MESSAGE_KEY,BaseConstants.Symbol.COLON, lang);
    }

    //
    // 数据库字段
    // ------------------------------------------------------------------------------

    @ApiModelProperty("表ID，主键，供其他表做外键")
    @Id
    @GeneratedValue
    @Encrypt
    private Long messageId;
    @ApiModelProperty(value = "消息编码")
    @NotBlank
    @Length(max = 180)
    @Pattern(regexp = Regexs.CODE)
    private String code;
    @ApiModelProperty(value = "消息类型: info/warn/error")
    @NotBlank
    @LovValue(lovCode = "HPFM.MESSAGE_TYPE", meaningField = FIELD_TYPE_MEANING)
    private String type;
    @ApiModelProperty(value = "语言")
    @NotBlank
    private String lang;
    @ApiModelProperty(value = "消息描述")
    @NotBlank
    @Length(max = 1000)
    private String description;

    @Transient
    private String langMeaning;
    @Transient
    private String typeMeaning;

    //
    // 非数据库字段
    // ------------------------------------------------------------------------------

    //
    // getter/setter
    // ------------------------------------------------------------------------------

    /**
     * @return 表ID，主键，供其他表做外键
     */
    public Long getMessageId() {
        return messageId;
    }

    public void setMessageId(Long messageId) {
        this.messageId = messageId;
    }

    /**
     * @return 消息编码
     */
    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    /**
     * @return 消息类型: info/warn/error
     */
    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    /**
     * @return 语言
     */
    public String getLang() {
        return lang;
    }

    public void setLang(String lang) {
        this.lang = lang;
    }

    /**
     * @return 消息描述
     */
    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getLangMeaning() {
        return langMeaning;
    }

    public void setLangMeaning(String langMeaning) {
        this.langMeaning = langMeaning;
    }

    public String getTypeMeaning() {
        return typeMeaning;
    }

    public void setTypeMeaning(String typeMeaning) {
        this.typeMeaning = typeMeaning;
    }
}
