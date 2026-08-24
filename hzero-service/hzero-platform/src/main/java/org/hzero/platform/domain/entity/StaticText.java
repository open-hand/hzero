package org.hzero.platform.domain.entity;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonInclude;
import io.choerodon.core.exception.CommonException;
import io.choerodon.mybatis.annotation.ModifyAudit;
import io.choerodon.mybatis.annotation.VersionAudit;
import io.choerodon.mybatis.domain.AuditDomain;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.hibernate.validator.constraints.Length;
import org.hzero.core.base.BaseConstants;
import org.hzero.core.redis.RedisHelper;
import org.hzero.core.util.Regexs;
import org.hzero.mybatis.domian.Condition;
import org.hzero.mybatis.util.Sqls;
import org.hzero.platform.domain.repository.StaticTextRepository;
import org.hzero.platform.infra.constant.FndConstants;
import org.hzero.platform.infra.constant.HpfmMsgCodeConstants;
import org.hzero.platform.infra.util.Dates;
import org.hzero.starter.keyencrypt.core.Encrypt;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.util.Assert;

import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.Transient;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

/**
 * 平台静态信息
 *
 * @author fatao.liu@hand-china.com 2018-07-23 14:19:42
 */
@ApiModel("静态文本")
@VersionAudit
@ModifyAudit
@Table(name = "hpfm_static_text")
@JsonInclude(JsonInclude.Include.NON_NULL)
public class StaticText extends AuditDomain {

    public StaticText() {

    }

    public StaticText(Long textId, Long tenantId, Long companyId) {
        this.textId = textId;
        this.tenantId = tenantId;
        this.companyId = companyId;
    }

    public static final Long ROOT_ID = 0L;
    public static final String SPLIT_POINT = ",";

    public static final String FIELD_TEXT_ID = "textId";
    public static final String FIELD_TEXT_CODE = "textCode";
    public static final String FIELD_TITLE = "title";
    public static final String FIELD_DESCRIPTION = "description";
    public static final String FIELD_PARENT_ID = "parentId";
    public static final String FIELD_START_DATE = "startDate";
    public static final String FIELD_END_DATE = "endDate";
    public static final String FIELD_TENANT_ID = "tenantId";



    public void validate(StaticTextRepository staticTextRepository) {
        // 去掉首尾空格
        this.textCode = StringUtils.trim(textCode);
        // 默认父ID
        this.parentId = Optional.ofNullable(this.parentId).orElse(ROOT_ID);

        if (parentId.equals(ROOT_ID)) {
            this.parentIds = ROOT_ID.toString();
        } else {
            StaticText parent = staticTextRepository.selectByPrimaryKey(this.parentId);
            Assert.notNull(parent, "parent text not exists.");
            this.parentIds = parent.getParentIds() + SPLIT_POINT + this.parentId;
        }
        Assert.notNull(textCode, "text code not be null.");
        Assert.notNull(title, "title not be null.");
        Assert.notNull(startDate, "start date not be null.");
        Assert.isTrue(endDate == null || endDate.isAfter(startDate), "end date should bigger than start date.");

        // 同一编码 日期不能重叠
        validateCode(staticTextRepository);
    }


    /**
     * 同一编码 日期不能重叠
     */
    public void validateCode(StaticTextRepository staticTextRepository) {
        List<StaticText> sameCodeTexts = staticTextRepository.selectByCondition(Condition.builder(StaticText.class)
                .andWhere(Sqls.custom()
                        .andEqualTo(FIELD_TEXT_CODE, this.textCode)
                        .andEqualTo(FIELD_TENANT_ID, this.tenantId)).build());
        if (CollectionUtils.isNotEmpty(sameCodeTexts)) {
            for (StaticText staticText : sameCodeTexts) {
                if (textId != null && textId.equals(staticText.getTextId())) {
                    continue;
                }
                if (staticText.getEndDate() == null && endDate == null) {
                    throw new IllegalArgumentException(HpfmMsgCodeConstants.REPEAT_DATE);
                }
                if (staticText.getEndDate() != null) {
                    if (Dates.isBetweenOrEqual(startDate, staticText.getStartDate(), staticText.getEndDate())) {
                        throw new IllegalArgumentException(HpfmMsgCodeConstants.REPEAT_DATE);
                    }
                    if (startDate.isBefore(staticText.getStartDate())
                            && (endDate == null || Dates.isAfterOrEqual(endDate, staticText.getStartDate()))) {
                        throw new IllegalArgumentException(HpfmMsgCodeConstants.REPEAT_DATE);
                    }
                } else {
                    if (Dates.isAfterOrEqual(endDate, staticText.getStartDate())) {
                        throw new IllegalArgumentException(HpfmMsgCodeConstants.REPEAT_DATE);
                    }
                }
            }
        }
    }

    public StaticTextValue generateStaticTextValue() {
        StaticTextValue value = new StaticTextValue();
        value.setTextId(this.textId);
        value.setLang(this.lang);
        value.setText(text);
        value.setTitle(this.title);
        value.setDescription(description);
        value.setTenantId(tenantId);
        return value;
    }


    /**
     * 生成缓存key
     *
     * @param staticText
     * @return
     */
    public static String generateCacheKey(StaticText staticText) {
        String textCode = staticText.getTextCode();
        if (StringUtils.isNotBlank(textCode)) {
            StringBuilder sb = new StringBuilder();
            return sb.append(FndConstants.CacheKey.STATIC_TEXT).append(FndConstants.CacheKey.STATIC_TEXT_HEAD)
                    .append(":").append(textCode).toString();
        }
        return null;
    }

    /**
     * 删除redis
     *
     * @param redisHelper redisHelper
     * @param staticText  staticText
     */
    public static void deleteCache(RedisHelper redisHelper, StaticText staticText) {

        String cacheKey = StaticText.generateCacheKey(staticText);
        if (StringUtils.isNotBlank(cacheKey)) {
            redisHelper.hshDelete(cacheKey);
        }
    }

    /**
     * 将头数据存入到redis
     *
     * @param redisHelper redisHelper
     * @param staticText  staticText
     */
    public static void initCache(RedisHelper redisHelper, StaticText staticText) {
        String cacheKey = StaticText.generateCacheKey(staticText);
        if (StringUtils.isNotBlank(cacheKey)) {
            redisHelper.objectSet(cacheKey, staticText);
        }
    }

    /**
     * 根据keys从redis中获取值，如果不存在则返回null
     *
     * @param staticText staticText
     * @return 静态文本
     */
    public static String getValueByKey(StaticText staticText, RedisHelper redisHelper) {
        String cacheKey = StaticText.generateCacheKey(staticText);
        if (cacheKey != null) {
            String value = redisHelper.strGet(cacheKey);
            if (value != null) {
                return value;
            }
        }
        return null;
    }

    /**
     * 校验传递的静态文本数据的合法性
     */
    public void checkOrgStaticText(StaticTextRepository staticTextRepository) {
        StaticText staticText = staticTextRepository.selectByPrimaryKey(textId);
        Assert.notNull(staticText, BaseConstants.ErrorCode.DATA_NOT_EXISTS);
        if (!Objects.equals(staticText.getTenantId(), this.tenantId)) {
            throw new CommonException(HpfmMsgCodeConstants.ERROR_STATIC_TEXT_TENANT_NOT_MATCH);
        }
    }

    //
    // getter/setter
    // ------------------------------------------------------------------------------

    @Id
    @GeneratedValue
    @ApiModelProperty("文本ID")
    @Encrypt
    private Long textId;
    @ApiModelProperty("租户ID")
    private Long tenantId;
    @ApiModelProperty("公司ID")
    @Encrypt
    private Long companyId;
    @Pattern(regexp = Regexs.CODE_UPPER)
    @ApiModelProperty("文本编码")
    @Length(max = 30)
    private String textCode;
    @ApiModelProperty("标题")
    @Length(max = 120)
    private String title;
    @ApiModelProperty("描述")
    @Length(max = 240)
    private String description;
    @ApiModelProperty("父级ID")
    @Encrypt
    private Long parentId;
    @ApiModelProperty("所有父级ID")
    private String parentIds;
    @ApiModelProperty("有效期起")
    @JsonFormat(pattern = BaseConstants.Pattern.DATETIME)
    @DateTimeFormat(pattern = BaseConstants.Pattern.DATETIME)
    private LocalDateTime startDate;
    @ApiModelProperty("有效期止")
    @JsonFormat(pattern = BaseConstants.Pattern.DATETIME)
    @DateTimeFormat(pattern = BaseConstants.Pattern.DATETIME)
    private LocalDateTime endDate;

    @Transient
    @ApiModelProperty("语言")
    private String lang;
    @Transient
    @ApiModelProperty("富文本")
    private String text;

    /**
     * @return 表ID
     */
    public Long getTextId() {
        return textId;
    }

    public void setTextId(Long textId) {
        this.textId = textId;
    }

    /**
     * @return 编码
     */
    public String getTextCode() {
        return textCode;
    }

    public void setTextCode(String textCode) {
        this.textCode = textCode;
    }

    /**
     * @return 标题
     */
    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    /**
     * @return 描述
     */
    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    /**
     * @return 父级ID
     */
    public Long getParentId() {
        return parentId;
    }

    public void setParentId(Long parentId) {
        this.parentId = parentId;
    }

    public Long getTenantId() {
        return tenantId;
    }

    public void setTenantId(Long tenantId) {
        this.tenantId = tenantId;
    }

    public Long getCompanyId() {
        return companyId;
    }

    public void setCompanyId(Long companyId) {
        this.companyId = companyId;
    }

    /**
     * @return 有效期从
     */
    public LocalDateTime getStartDate() {
        return startDate;
    }

    public void setStartDate(LocalDateTime startDate) {
        this.startDate = startDate;
    }

    /**
     * @return 有效期至
     */
    public LocalDateTime getEndDate() {
        return endDate;
    }

    public void setEndDate(LocalDateTime endDate) {
        this.endDate = endDate;
    }

    public String getLang() {
        return lang;
    }

    public void setLang(String lang) {
        this.lang = lang;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public String getParentIds() {
        return parentIds;
    }

    public void setParentIds(String parentIds) {
        this.parentIds = parentIds;
    }

}
