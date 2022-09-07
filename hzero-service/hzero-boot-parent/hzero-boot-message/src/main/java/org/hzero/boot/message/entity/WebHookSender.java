package org.hzero.boot.message.entity;

import java.util.List;
import java.util.Map;
import java.util.Objects;

/**
 * WebHook消息发送对象
 *
 * @author xiaoyu.zhao@hand-china.com 2019/10/16 15:00
 */
public class WebHookSender extends BaseSender {

    public static final String FIELD_RECEIVER_CONFIG_CODE = "receiveConfigCode";
    public static final String FIELD_SERVER_CODE = "serverCode";
    public static final String FIELD_SERVER_TYPE = "serverType";
    public static final String FIELD_WEBHOOK_ADDRESS = "webhookAddress";
    public static final String FIELD_SECRET = "secret";

    /**
     * 消息接收配置编码
     */
    private String receiveConfigCode;

    /**
     * 接收组编码
     */
    private String receiverTypeCode;

    /**
     * 配置编码
     */
    private String serverCode;

    /**
     * 消息发送内容
     */
    private Message message;

    /**
     * wehhook消息类型
     */
    private String serverType;

    /**
     * webhook调用地址
     */
    private String webhookAddress;

    /**
     * 钉钉webhook秘钥
     */
    private String secret;

    /**
     * 消息参数
     */
    private Map<String, String> args;

    /**
     * 钉钉@人列表
     */
    private List<Receiver> receiverAddressList;

    public String getReceiverTypeCode() {
        return receiverTypeCode;
    }

    public WebHookSender setReceiverTypeCode(String receiverTypeCode) {
        this.receiverTypeCode = receiverTypeCode;
        return this;
    }

    public List<Receiver> getReceiverAddressList() {
        return receiverAddressList;
    }

    public WebHookSender setReceiverAddressList(List<Receiver> receiverAddressList) {
        this.receiverAddressList = receiverAddressList;
        return this;
    }

    public String getReceiveConfigCode() {
        return receiveConfigCode;
    }

    public WebHookSender setReceiveConfigCode(String receiveConfigCode) {
        this.receiveConfigCode = receiveConfigCode;
        return this;
    }

    public String getServerCode() {
        return serverCode;
    }

    public WebHookSender setServerCode(String serverCode) {
        this.serverCode = serverCode;
        return this;
    }

    public Message getMessage() {
        return message;
    }

    public WebHookSender setMessage(Message message) {
        this.message = message;
        return this;
    }

    public Map<String, String> getArgs() {
        return args;
    }

    public WebHookSender setArgs(Map<String, String> args) {
        this.args = args;
        return this;
    }

    public String getServerType() {
        return serverType;
    }

    public WebHookSender setServerType(String serverType) {
        this.serverType = serverType;
        return this;
    }

    public String getWebhookAddress() {
        return webhookAddress;
    }

    public WebHookSender setWebhookAddress(String webhookAddress) {
        this.webhookAddress = webhookAddress;
        return this;
    }

    public String getSecret() {
        return secret;
    }

    public WebHookSender setSecret(String secret) {
        this.secret = secret;
        return this;
    }

    @Override
    public WebHookSender setTenantId(Long tenantId) {
        this.tenantId = tenantId;
        return this;
    }

    @Override
    public WebHookSender setMessageCode(String messageCode) {
        this.messageCode = messageCode;
        return this;
    }

    @Override
    public WebHookSender setLang(String lang) {
        this.lang = lang;
        return this;
    }

    @Override
    public String toString() {
        return "WebHookSender{" + "receiveConfigCode='" + receiveConfigCode + '\'' + ", receiverTypeCode='"
                        + receiverTypeCode + '\'' + ", serverCode='" + serverCode + '\'' + ", message=" + message
                        + ", serverType='" + serverType + '\'' + ", webhookAddress='" + webhookAddress + '\''
                        + ", secret='" + secret + '\'' + ", args=" + args + ", receiverAddressList="
                        + receiverAddressList + '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o)
            return true;
        if (!(o instanceof WebHookSender))
            return false;
        WebHookSender that = (WebHookSender) o;
        return Objects.equals(getReceiveConfigCode(), that.getReceiveConfigCode())
                        && Objects.equals(getReceiverTypeCode(), that.getReceiverTypeCode())
                        && Objects.equals(getServerCode(), that.getServerCode())
                        && Objects.equals(getMessage(), that.getMessage())
                        && Objects.equals(getServerType(), that.getServerType())
                        && Objects.equals(getWebhookAddress(), that.getWebhookAddress())
                        && Objects.equals(getSecret(), that.getSecret()) && Objects.equals(getArgs(), that.getArgs())
                        && Objects.equals(getReceiverAddressList(), that.getReceiverAddressList());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getReceiveConfigCode(), getReceiverTypeCode(), getServerCode(), getMessage(),
                        getServerType(), getWebhookAddress(), getSecret(), getArgs(), getReceiverAddressList());
    }
}
