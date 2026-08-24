package org.hzero.boot.message.entity;

import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

/**
 * 微信消息发送对象
 *
 * @author shuangfei.zhu@hand-china.com 2019/10/16 15:00
 */
public class WeChatSender extends BaseSender {

    /**
     * 微信公众号参数
     */
    public static final String FIELD_URL = "url";
    public static final String FIELD_MINIPROGRAM = "miniprogram";
    public static final String FIELD_DATA = "data";

    /**
     * 企业微信参数
     */
    public static final String FIELD_ARGS = "args";
    public static final String FIELD_AGENT_ID = "agentId";
    public static final String FIELD_PARTY_LIST = "partyList";
    public static final String FIELD_TAG_LIST = "tagList";
    public static final String FIELD_SAFE = "safe";
    public static final String FIELD_MSG_TYPE = "msgType";

    /**
     * 消息接收配置编码
     */
    private String receiveConfigCode;

    /**
     * 账号代码
     */
    private String serverCode;
    /**
     * 公众号接收人openId列表
     */
    private List<String> userList;
    /**
     * 模板跳转链接（海外帐号没有跳转能力）
     */
    private String url;
    /**
     * 跳小程序所需数据，不需跳小程序可不用传该数据
     */
    private Miniprogram miniprogram;
    /**
     * 公众号消息参数
     */
    private Map<String, WeChatFont> data;
    /**
     * 企业微信消息参数
     */
    private Map<String, String> args;
    /**
     * 企业应用的id
     */
    private Long agentId;
    /**
     * 企业微信成员ID列表
     */
    private List<String> userIdList;
    /**
     * 企业微信部门ID列表
     */
    private List<String> partyList;
    /**
     * 本企业的标签ID列表，最多支持100个。
     */
    private List<String> tagList;
    /**
     * 企业微信表示是否是保密消息，0表示否，1表示是，默认0
     */
    private Integer safe = 0;
    /**
     * 消息发送内容
     */
    private Message message;

    /**
     * 消息发送配置使用，指定允许的微信消息类型，不指定则所有类型都发
     */
    private List<String> typeCodeList;
    /**
     * 消息类型
     */
    private WeChatMsgType msgType = WeChatMsgType.MARK_DOWN;

    public String getReceiveConfigCode() {
        return receiveConfigCode;
    }

    public WeChatSender setReceiveConfigCode(String receiveConfigCode) {
        this.receiveConfigCode = receiveConfigCode;
        return this;
    }

    @Override
    public WeChatSender setTenantId(Long tenantId) {
        this.tenantId = tenantId;
        return this;
    }

    @Override
    public WeChatSender setMessageCode(String messageCode) {
        this.messageCode = messageCode;
        return this;
    }

    @Override
    public WeChatSender setLang(String lang) {
        this.lang = lang;
        return this;
    }

    public String getServerCode() {
        return serverCode;
    }

    public WeChatSender setServerCode(String serverCode) {
        this.serverCode = serverCode;
        return this;
    }

    public List<String> getUserList() {
        return userList;
    }

    public WeChatSender setUserList(List<String> userList) {
        this.userList = userList;
        return this;
    }

    public String getUrl() {
        return url;
    }

    public WeChatSender setUrl(String url) {
        this.url = url;
        return this;
    }

    public Miniprogram getMiniprogram() {
        return miniprogram;
    }

    public WeChatSender setMiniprogram(Miniprogram miniprogram) {
        this.miniprogram = miniprogram;
        return this;
    }

    public Map<String, WeChatFont> getData() {
        return data;
    }

    public WeChatSender setData(Map<String, WeChatFont> data) {
        this.data = data;
        return this;
    }

    public Map<String, String> getArgs() {
        return args;
    }

    public WeChatSender setArgs(Map<String, String> args) {
        this.args = args;
        return this;
    }

    public Long getAgentId() {
        return agentId;
    }

    public WeChatSender setAgentId(Long agentId) {
        this.agentId = agentId;
        return this;
    }

    public List<String> getUserIdList() {
        return userIdList;
    }

    public WeChatSender setUserIdList(List<String> userIdList) {
        this.userIdList = userIdList;
        return this;
    }

    public List<String> getPartyList() {
        return partyList;
    }

    public WeChatSender setPartyList(List<String> partyList) {
        this.partyList = partyList;
        return this;
    }

    public List<String> getTagList() {
        return tagList;
    }

    public WeChatSender setTagList(List<String> tagList) {
        this.tagList = tagList;
        return this;
    }

    public Integer getSafe() {
        return safe;
    }

    public WeChatSender setSafe(Integer safe) {
        this.safe = safe;
        return this;
    }

    public Message getMessage() {
        return message;
    }

    public WeChatSender setMessage(Message message) {
        this.message = message;
        return this;
    }

    public List<String> getTypeCodeList() {
        return typeCodeList;
    }

    public WeChatSender setTypeCodeList(List<String> typeCodeList) {
        this.typeCodeList = typeCodeList;
        return this;
    }

    public WeChatMsgType getMsgType() {
        return msgType;
    }

    public WeChatSender setMsgType(WeChatMsgType msgType) {
        this.msgType = msgType;
        return this;
    }

    @Override
    public String toString() {
        return "WeChatSender{" +
                "receiveConfigCode='" + receiveConfigCode + '\'' +
                ", serverCode='" + serverCode + '\'' +
                ", userList=" + userList +
                ", url='" + url + '\'' +
                ", miniprogram=" + miniprogram +
                ", data=" + data +
                ", args=" + args +
                ", agentId=" + agentId +
                ", userIdList=" + userIdList +
                ", partyList=" + partyList +
                ", tagList=" + tagList +
                ", safe=" + safe +
                ", message=" + message +
                ", typeCodeList=" + typeCodeList +
                ", msgType=" + msgType +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        WeChatSender that = (WeChatSender) o;
        return new EqualsBuilder()
                .append(receiveConfigCode, that.receiveConfigCode)
                .append(serverCode, that.serverCode)
                .append(userList, that.userList)
                .append(url, that.url)
                .append(miniprogram, that.miniprogram)
                .append(data, that.data)
                .append(args, that.args)
                .append(agentId, that.agentId)
                .append(userIdList, that.userIdList)
                .append(partyList, that.partyList)
                .append(tagList, that.tagList)
                .append(safe, that.safe)
                .append(message, that.message)
                .append(typeCodeList, that.typeCodeList)
                .append(msgType, that.msgType)
                .isEquals();
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder(17, 37)
                .append(receiveConfigCode)
                .append(serverCode)
                .append(userList)
                .append(url)
                .append(miniprogram)
                .append(data)
                .append(args)
                .append(agentId)
                .append(userIdList)
                .append(partyList)
                .append(tagList)
                .append(safe)
                .append(message)
                .append(typeCodeList)
                .append(msgType)
                .toHashCode();
    }
}
