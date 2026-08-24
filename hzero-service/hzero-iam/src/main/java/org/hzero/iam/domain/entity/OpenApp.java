package org.hzero.iam.domain.entity;

import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.Transient;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

import org.hibernate.validator.constraints.Length;
import org.hibernate.validator.constraints.URL;
import org.hzero.boot.platform.lov.annotation.LovValue;
import org.hzero.core.util.FileUtils;
import org.hzero.core.util.Regexs;
import org.hzero.iam.domain.repository.OpenAppRepository;
import org.hzero.mybatis.annotation.DataSecurity;
import org.hzero.mybatis.domian.Condition;
import org.hzero.mybatis.util.Sqls;
import org.hzero.starter.keyencrypt.core.Encrypt;

import com.fasterxml.jackson.annotation.JsonInclude;

import io.choerodon.core.exception.CommonException;
import io.choerodon.mybatis.annotation.ModifyAudit;
import io.choerodon.mybatis.annotation.MultiLanguage;
import io.choerodon.mybatis.annotation.MultiLanguageField;
import io.choerodon.mybatis.annotation.VersionAudit;
import io.choerodon.mybatis.domain.AuditDomain;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

/**
 * 三方网站管理
 *
 * @author jiaxu.cui@hand-china.com 2018/9/29 13:48
 */
@ApiModel("三方网站")
@VersionAudit
@ModifyAudit
@Table(name = "hiam_open_app")
@JsonInclude(JsonInclude.Include.NON_NULL)
@MultiLanguage
public class OpenApp extends AuditDomain {

    public static final String ENCRYPT_KEY = "hiam_open_app";

    public static final String FIELD_ORDER_SEQ = "orderSeq";
    public static final String FIELD_ENABLED_FLAG = "enabledFlag";
    public static final String FIELD_APP_CODE = "appCode";
    public static final String FIELD_APP_NAME = "appName";
    public static final String FIELD_OPEN_APP_ID = "openAppId";
    public static final String FIELD_CHANNEL = "channel";
    public static final String FIELD_APP_IMAGE = "appImage";
    public static final String FIELD_APP_KEY = "appKey";
    public static final String FIELD_APP_ID = "appId";
    public static final String FIELD_SUB_APP_ID = "subAppId";
    public static final String FIELD_SCOPE = "scope";
    public static final String ESCAPE_QUESTION = "\\?";


    public void validateAppCode(OpenAppRepository openAppRepository) {
        //校验应用编码是否重复
        int existFlag = openAppRepository.selectCountByCondition(Condition.builder(OpenApp.class)
                .andWhere(Sqls.custom()
                        .andEqualTo(OpenApp.FIELD_APP_CODE, appCode)
                        .andEqualTo(OpenApp.FIELD_CHANNEL, channel)
                        .andNotEqualTo(OpenApp.FIELD_OPEN_APP_ID, openAppId,true))
                .build());
        if (existFlag != 0) {
            throw new CommonException("open-app.app-code.exist");
        }
        //校验应用编码是否符合格式
        if (!Regexs.is(appCode, Regexs.CODE) || appCode.length() > 60) {
            throw new CommonException("open-app.app-code.not-format");
        }
        if (appName.length() > 60) {
            throw new CommonException("open-app.app-name.not-format");
        }

        //默认启用
        this.enabledFlag = 1;
    }

    @ApiModelProperty("表Id")
    @Id
    @GeneratedValue
    @Encrypt
    private Long openAppId;
    @ApiModelProperty("应用编码")
    @NotBlank
    @LovValue(lovCode = "HIAM.OPEN_APP_CODE", meaningField = "appCodeMeaning")
    private String appCode;
    @ApiModelProperty("应用名称")
    @NotBlank
    @MultiLanguageField
    private String appName;
    @ApiModelProperty("应用图片地址")
    @NotBlank
    @URL
    private String appImage;
    @ApiModelProperty("第三方平台方appid")
    @NotBlank
    private String appId;
    @ApiModelProperty("appid对应的授权码")
    @DataSecurity
    private String appKey;
    @ApiModelProperty("子应用ID")
    private String subAppId;
    @ApiModelProperty("排序号")
    @NotNull
    private Integer orderSeq;
    @ApiModelProperty("是否启用")
    private Integer enabledFlag;
    @ApiModelProperty("登录渠道，值集HIAM.CHANNEL")
    @LovValue(lovCode = "HIAM.CHANNEL", meaningField = "channelMeaning")
    @Length(max = 30)
    @NotNull
    private String channel;
    @ApiModelProperty("租户ID")
    @MultiLanguageField
    private Long organizationId;
    @Length(max = 240)
    @ApiModelProperty("授权列表")
    private String scope;

    @Transient
    @ApiModelProperty("用于返回登录渠道含义")
    private String channelMeaning;

    @Transient
    @ApiModelProperty("用于返回应用编码含义")
    private String appCodeMeaning;

    @Transient
    @ApiModelProperty("用于返回图片文件名称")
    private String fileName;

    public String getAppCodeMeaning() {
        return appCodeMeaning;
    }

    public void setAppCodeMeaning(String appCodeMeaning) {
        this.appCodeMeaning = appCodeMeaning;
    }

    public String getFileName() {
        if (appImage != null) {
            return FileUtils.getFileName(appImage);
        }
        return null;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    public String getChannelMeaning() {
        return channelMeaning;
    }

    public void setChannelMeaning(String channelMeaning) {
        this.channelMeaning = channelMeaning;
    }

    public String getChannel() {
        return channel;
    }

    public void setChannel(String channel) {
        this.channel = channel;
    }

    public Long getOrganizationId() {
        return organizationId;
    }

    public void setOrganizationId(Long organizationId) {
        this.organizationId = organizationId;
    }

    public Long getOpenAppId() {
        return openAppId;
    }

    public void setOpenAppId(Long openAppId) {
        this.openAppId = openAppId;
    }

    public String getAppCode() {
        return appCode;
    }

    public void setAppCode(String appCode) {
        this.appCode = appCode;
    }

    public String getAppName() {
        return appName;
    }

    public void setAppName(String appName) {
        this.appName = appName;
    }

    public String getAppImage() {
        return appImage;
    }

    public void setAppImage(String appImage) {
        this.appImage = appImage;
    }

    public String getAppId() {
        return appId;
    }

    public void setAppId(String appId) {
        this.appId = appId;
    }

    public String getAppKey() {
        return appKey;
    }

    public void setAppKey(String appKey) {
        this.appKey = appKey;
    }

    public String getSubAppId() {
        return subAppId;
    }

    public void setSubAppId(String subAppId) {
        this.subAppId = subAppId;
    }

    public Integer getOrderSeq() {
        return orderSeq;
    }

    public void setOrderSeq(Integer orderSeq) {
        this.orderSeq = orderSeq;
    }

    public Integer getEnabledFlag() {
        return enabledFlag;
    }

    public void setEnabledFlag(Integer enabledFlag) {
        this.enabledFlag = enabledFlag;
    }

    public String getScope() {
        return scope;
    }

    public void setScope(String scope) {
        this.scope = scope;
    }
}
