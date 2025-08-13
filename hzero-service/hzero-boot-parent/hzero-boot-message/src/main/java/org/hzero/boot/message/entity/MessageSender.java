package org.hzero.boot.message.entity;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.validation.constraints.NotBlank;

import org.hzero.core.base.BaseConstants;

import com.google.common.base.Objects;

/**
 * <p>
 * 消息发送
 * </p>
 *
 * @author qingsheng.chen 2018/10/12 星期五 14:25
 */
public class MessageSender extends BaseSender {


    public MessageSender() {
    }

    public MessageSender(MessageSender sender) {
        this.tenantId = sender.getTenantId();
        this.messageCode = sender.getMessageCode();
        this.lang = sender.getLang();
        this.additionalInformation = sender.getAdditionalInformation();
        this.receiveConfigCode = sender.getReceiveConfigCode();
        this.serverCode = sender.getServerCode();
        this.receiverTypeCode = sender.getReceiverTypeCode();
        if (sender.getReceiverAddressList() != null) {
            List<Receiver> receiverList = new ArrayList<>();
            sender.getReceiverAddressList().forEach(item -> receiverList.add(new Receiver(item)));
            this.receiverAddressList = receiverList;
        } else {
            this.receiverAddressList = null;
        }
        this.typeCodeList = sender.typeCodeList;
        this.args = sender.getArgs();
        this.objectArgs = sender.getObjectArgs();
        this.message = sender.getMessage() == null ? null : new Message(sender.getMessage());
        if (sender.getMessageMap() != null) {
            Map<String, Message> map = new HashMap<>(16);
            sender.getMessageMap().forEach((k, v) -> map.put(k, new Message(v)));
            this.messageMap = map;
        } else {
            this.messageMap = null;
        }
        if (sender.getAttachmentList() != null) {
            List<Attachment> attachments = new ArrayList<>();
            sender.getAttachmentList().forEach(item -> attachments.add(new Attachment(item)));
            this.attachmentList = attachments;
        } else {
            this.attachmentList = null;
        }
        this.ccList = sender.getCcList();
        this.bccList = sender.getBccList();
        this.batchSend = sender.getBatchSend();
    }

    /**
     * 消息接收配置编码
     */
    private String receiveConfigCode;

    /**
     * 消息服务编码
     */
    @NotBlank(groups = {Sms.class, Email.class})
    private String serverCode;

    /**
     * 接收组编码
     */
    private String receiverTypeCode;
    /**
     * 接收人列表
     */
    private List<Receiver> receiverAddressList;

    /**
     * 允许发送的方式：WEB|EMAIL|SMS
     */
    private List<String> typeCodeList;

    /**
     * 消息参数(仅支持字符串)
     */
    private Map<String, String> args;

    /**
     * 消息参数
     */
    private Map<String, Object> objectArgs;

    /**
     * 指定发送方式：消息发送内容
     */
    private Message message;

    /**
     * 关联发送：消息内容
     */
    private Map<String, Message> messageMap;

    /**
     * 邮件附件列表
     */
    private List<Attachment> attachmentList;

    /**
     * 邮件抄送地址
     */
    private List<String> ccList;

    /**
     * 邮件密送地址
     */
    private List<String> bccList;

    /**
     * 是否批量发送，默认是
     */
    private Integer batchSend = BaseConstants.Flag.YES;

    @Override
    public MessageSender setTenantId(Long tenantId) {
        this.tenantId = tenantId;
        return this;
    }

    @Override
    public MessageSender setMessageCode(String messageCode) {
        this.messageCode = messageCode;
        return this;
    }

    @Override
    public MessageSender setLang(String lang) {
        this.lang = lang;
        return this;
    }

    public String getReceiveConfigCode() {
        return receiveConfigCode;
    }

    public MessageSender setReceiveConfigCode(String receiveConfigCode) {
        this.receiveConfigCode = receiveConfigCode;
        return this;
    }

    public String getServerCode() {
        return serverCode;
    }

    public MessageSender setServerCode(String serverCode) {
        this.serverCode = serverCode;
        return this;
    }

    public String getReceiverTypeCode() {
        return receiverTypeCode;
    }

    public MessageSender setReceiverTypeCode(String receiverTypeCode) {
        this.receiverTypeCode = receiverTypeCode;
        return this;
    }

    public List<Receiver> getReceiverAddressList() {
        return receiverAddressList;
    }

    public MessageSender setReceiverAddressList(List<Receiver> receiverAddressList) {
        this.receiverAddressList = receiverAddressList;
        return this;
    }

    public List<String> getTypeCodeList() {
        return typeCodeList;
    }

    public MessageSender setTypeCodeList(List<String> typeCodeList) {
        this.typeCodeList = typeCodeList;
        return this;
    }

    public Map<String, String> getArgs() {
        return args;
    }

    public MessageSender setArgs(Map<String, String> args) {
        this.args = args;
        return this;
    }

    public Map<String, Object> getObjectArgs() {
        return objectArgs;
    }

    public MessageSender setObjectArgs(Map<String, Object> objectArgs) {
        this.objectArgs = objectArgs;
        return this;
    }

    public Message getMessage() {
        return message;
    }

    public MessageSender setMessage(Message message) {
        this.message = message;
        return this;
    }

    public Map<String, Message> getMessageMap() {
        return messageMap;
    }

    public MessageSender setMessageMap(Map<String, Message> messageMap) {
        this.messageMap = messageMap;
        return this;
    }

    public List<Attachment> getAttachmentList() {
        return attachmentList;
    }

    public MessageSender setAttachmentList(List<Attachment> attachmentList) {
        this.attachmentList = attachmentList;
        return this;
    }

    public List<String> getCcList() {
        return ccList;
    }

    public MessageSender setCcList(List<String> ccList) {
        this.ccList = ccList;
        return this;
    }

    public List<String> getBccList() {
        return bccList;
    }

    public MessageSender setBccList(List<String> bccList) {
        this.bccList = bccList;
        return this;
    }

    public Integer getBatchSend() {
        return batchSend;
    }

    public MessageSender setBatchSend(Integer batchSend) {
        this.batchSend = batchSend;
        return this;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof MessageSender)) {
            return false;
        }
        MessageSender that = (MessageSender) o;
        return Objects.equal(tenantId, that.tenantId) &&
                Objects.equal(messageCode, that.messageCode) &&
                Objects.equal(serverCode, that.serverCode) &&
                Objects.equal(lang, that.lang) &&
                Objects.equal(receiverTypeCode, that.receiverTypeCode) &&
                Objects.equal(receiverAddressList, that.receiverAddressList) &&
                Objects.equal(typeCodeList, that.typeCodeList) &&
                Objects.equal(args, that.args) &&
                Objects.equal(objectArgs, that.objectArgs) &&
                Objects.equal(message, that.getMessage()) &&
                Objects.equal(messageMap, that.getMessageMap()) &&
                Objects.equal(attachmentList, that.attachmentList) &&
                Objects.equal(ccList, that.ccList) &&
                Objects.equal(bccList, that.bccList) &&
                Objects.equal(batchSend, that.batchSend);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(tenantId, messageCode, serverCode, lang, receiverTypeCode, receiverAddressList, typeCodeList, args, objectArgs, message, messageMap, attachmentList, ccList, bccList, batchSend);
    }

    public static MessageSenderBuilder builder() {
        return new MessageSenderBuilder(new MessageSender());
    }

    @Override
    public String toString() {
        return "MessageSender{" +
                "tenantId=" + tenantId +
                ", messageCode='" + messageCode + '\'' +
                ", receiveConfigCode='" + receiveConfigCode + '\'' +
                ", serverCode='" + serverCode + '\'' +
                ", lang='" + lang + '\'' +
                ", receiverTypeCode='" + receiverTypeCode + '\'' +
                ", receiverAddressList=" + receiverAddressList +
                ", typeCodeList=" + typeCodeList +
                ", args=" + args +
                ", objectArgs=" + objectArgs +
                ", message=" + message +
                ", messageMap=" + messageMap +
                ", attachmentList=" + attachmentList +
                ", ccList=" + ccList +
                ", bccList=" + bccList +
                ", batchSend=" + batchSend +
                '}';
    }

    public static class MessageSenderBuilder {
        private MessageSender messageSender;

        public MessageSenderBuilder(MessageSender messageSender) {
            this.messageSender = messageSender;
        }

        public MessageSenderBuilder tenantId(long tenantId) {
            messageSender.setTenantId(tenantId);
            return this;
        }

        public MessageSenderBuilder messageCode(String messageCode) {
            messageSender.setMessageCode(messageCode);
            return this;
        }

        public MessageSenderBuilder receiveConfigCode(String receiveConfigCode) {
            messageSender.setReceiveConfigCode(receiveConfigCode);
            return this;
        }

        public MessageSenderBuilder serverCode(String serverCode) {
            messageSender.setServerCode(serverCode);
            return this;
        }

        public MessageSenderBuilder lang(String lang) {
            messageSender.setLang(lang);
            return this;
        }

        public MessageSenderBuilder receiverTypeCode(String receiverTypeCode) {
            messageSender.setReceiverTypeCode(receiverTypeCode);
            return this;
        }

        public MessageSenderBuilder receiverAddressList(List<Receiver> receiverAddressList) {
            messageSender.setReceiverAddressList(receiverAddressList);
            return this;
        }

        public MessageSenderBuilder typeCodeList(List<String> typeCodeList) {
            messageSender.setTypeCodeList(typeCodeList);
            return this;
        }

        public MessageSenderBuilder args(Map<String, String> args) {
            messageSender.setArgs(args);
            return this;
        }

        public MessageSenderBuilder objectArgs(Map<String, Object> objectArgs) {
            messageSender.setObjectArgs(objectArgs);
            return this;
        }

        public MessageSenderBuilder addArg(String argName, String argValue) {
            if (messageSender.getArgs() == null) {
                messageSender.setArgs(new HashMap<>(BaseConstants.Digital.SIXTEEN));
            }
            messageSender.getArgs().put(argName, argValue);
            return this;
        }

        public MessageSenderBuilder attachmentList(List<Attachment> attachmentList) {
            messageSender.setAttachmentList(attachmentList);
            return this;
        }

        public MessageSenderBuilder ccList(List<String> ccList) {
            messageSender.setCcList(ccList);
            return this;
        }

        public MessageSenderBuilder bccList(List<String> bccList) {
            messageSender.setBccList(bccList);
            return this;
        }

        public MessageSenderBuilder batchSend(Integer batchSend) {
            messageSender.setBatchSend(batchSend);
            return this;
        }

        public MessageSender build() {
            if (messageSender.getTenantId() == null) {
                messageSender.setTenantId(BaseConstants.DEFAULT_TENANT_ID);
            }
            return messageSender;
        }
    }

    public interface WebMessage {
    }

    public interface Sms {
    }

    public interface Email {
    }

    public interface Call {

    }
}
