package org.hzero.boot.message.entity;

import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

/**
 * 钉钉消息发送对象
 *
 * @author zifeng.ding@hand-china.com 2019/11/14 10:38
 */
public class DingTalkSender extends BaseSender {


    public static final String FIELD_ARGS = "args";
    public static final String FIELD_AGENT_ID = "agentId";
    public static final String FIELD_USER_ID_LIST = "userIdList";
    public static final String FIELD_DEPT_ID_LIST = "deptIdList";
    public static final String FIELD_TO_ALL_USER = "toAllUser";
    public static final String FIELD_MSG_TYPE = "msgType";

    /**
     * 消息接收配置编码
     */
    private String receiveConfigCode;
    /**
     * 钉钉消息参数
     */
    private Map<String, String> args;
    /**
     * 账号代码
     */
    private String serverCode;
    /**
     * 应用agentId
     */
    private Long agentId;
    /**
     * 接收者的用户ID列表
     */
    private List<String> userIdList;
    /**
     * 接收者的部门iID列表
     */
    private List<String> deptIdList;
    /**
     * 是否发送给企业全部用户
     */
    private Boolean toAllUser;
    /**
     * 消息发送内容
     */
    private Message message;
    /**
     * 消息类型
     */
    private DingTalkMsgType msgType = DingTalkMsgType.MARK_DOWN;

    public String getReceiveConfigCode() {
        return receiveConfigCode;
    }

    public DingTalkSender setReceiveConfigCode(String receiveConfigCode) {
        this.receiveConfigCode = receiveConfigCode;
        return this;
    }

    @Override
    public DingTalkSender setTenantId(Long tenantId) {
        this.tenantId = tenantId;
        return this;
    }

    @Override
    public DingTalkSender setMessageCode(String messageCode) {
        this.messageCode = messageCode;
        return this;
    }

    @Override
    public DingTalkSender setLang(String lang) {
        this.lang = lang;
        return this;
    }

    public Map<String, String> getArgs() {
        return args;
    }

    public DingTalkSender setArgs(Map<String, String> args) {
        this.args = args;
        return this;
    }

    public String getServerCode() {
        return serverCode;
    }

    public DingTalkSender setServerCode(String serverCode) {
        this.serverCode = serverCode;
        return this;
    }

    public Long getAgentId() {
        return agentId;
    }

    public DingTalkSender setAgentId(Long agentId) {
        this.agentId = agentId;
        return this;
    }

    public List<String> getUserIdList() {
        return userIdList;
    }

    public DingTalkSender setUserIdList(List<String> userIdList) {
        this.userIdList = userIdList;
        return this;
    }

    public List<String> getDeptIdList() {
        return deptIdList;
    }

    public DingTalkSender setDeptIdList(List<String> deptIdList) {
        this.deptIdList = deptIdList;
        return this;
    }

    public Boolean getToAllUser() {
        return toAllUser;
    }

    public DingTalkSender setToAllUser(Boolean toAllUser) {
        this.toAllUser = toAllUser;
        return this;
    }

    public Message getMessage() {
        return message;
    }

    public DingTalkSender setMessage(Message message) {
        this.message = message;
        return this;
    }

    public DingTalkMsgType getMsgType() {
        return msgType;
    }

    public DingTalkSender setMsgType(DingTalkMsgType msgType) {
        this.msgType = msgType;
        return this;
    }

    @Override
    public String toString() {
        return "DingTalkSender{" +
                "receiveConfigCode='" + receiveConfigCode + '\'' +
                ", args=" + args +
                ", serverCode='" + serverCode + '\'' +
                ", agentId=" + agentId +
                ", userIdList=" + userIdList +
                ", deptIdList=" + deptIdList +
                ", toAllUser=" + toAllUser +
                ", message=" + message +
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
        DingTalkSender that = (DingTalkSender) o;

        return new EqualsBuilder()
                .append(receiveConfigCode, that.receiveConfigCode)
                .append(args, that.args)
                .append(serverCode, that.serverCode)
                .append(agentId, that.agentId)
                .append(userIdList, that.userIdList)
                .append(deptIdList, that.deptIdList)
                .append(toAllUser, that.toAllUser)
                .append(message, that.message)
                .append(msgType, that.msgType)
                .isEquals();
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder(17, 37)
                .append(receiveConfigCode)
                .append(args)
                .append(serverCode)
                .append(agentId)
                .append(userIdList)
                .append(deptIdList)
                .append(toAllUser)
                .append(message)
                .append(msgType)
                .toHashCode();
    }
}
