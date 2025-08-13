package org.hzero.message.infra.supporter;

import java.util.List;
import java.util.Map;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.commons.lang3.StringUtils;
import org.hzero.boot.message.entity.WeChatMsgType;
import org.hzero.boot.message.entity.WeChatSender;
import org.hzero.message.domain.entity.Message;
import org.hzero.message.infra.constant.HmsgConstant;
import org.hzero.message.infra.exception.SendMessageException;
import org.hzero.message.infra.util.ClearHtmlUtils;
import org.hzero.wechat.enterprise.dto.MarkdownMessageDTO;
import org.hzero.wechat.enterprise.dto.MessageSendResultDTO;
import org.hzero.wechat.enterprise.dto.TextMessageDTO;
import org.hzero.wechat.enterprise.service.WechatCorpMessageService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.CollectionUtils;

import io.choerodon.core.convertor.ApplicationContextHelper;
import io.choerodon.core.exception.CommonException;

/**
 * description
 *
 * @author shuangfei.zhu@hand-china.com 2019/10/25 9:42
 */
public class WeChatEnterpriseSupporter {

    private WeChatEnterpriseSupporter() {
    }

    private static final Logger logger = LoggerFactory.getLogger(WeChatEnterpriseSupporter.class);

    private static final ObjectMapper OBJECT_MAPPER = ApplicationContextHelper.getContext().getBean(ObjectMapper.class);

    private static TextMessageDTO generateTextMessage(List<String> userList, Message message) {
        // 明文消息不存在，使用content
        String msgContent = StringUtils.isEmpty(message.getPlainContent()) ? message.getContent() : message.getPlainContent();
        TextMessageDTO textMessage = new TextMessageDTO();
        try {
            textMessage.setTouser(buildParam(userList));
            // 处理额外参数
            Map<String, String> map = OBJECT_MAPPER.readValue(message.getSendArgs(), new TypeReference<Map<String, String>>() {
            });
            textMessage.setAgentid(Long.parseLong(map.get(WeChatSender.FIELD_AGENT_ID)));
            if (map.containsKey(WeChatSender.FIELD_PARTY_LIST)) {
                textMessage.setToparty(buildParam(OBJECT_MAPPER.readValue(map.get(WeChatSender.FIELD_PARTY_LIST), new TypeReference<List<String>>() {
                })));
            }
            if (map.containsKey(WeChatSender.FIELD_TAG_LIST)) {
                textMessage.setTotag(buildParam(OBJECT_MAPPER.readValue(map.get(WeChatSender.FIELD_TAG_LIST), new TypeReference<List<String>>() {
                })));
            }
            if (map.containsKey(WeChatSender.FIELD_TAG_LIST)) {
                textMessage.setTotag(buildParam(OBJECT_MAPPER.readValue(map.get(WeChatSender.FIELD_TAG_LIST), new TypeReference<List<String>>() {
                })));
            }
            if (map.containsKey(WeChatSender.FIELD_SAFE)) {
                textMessage.setSafe(Integer.parseInt(map.get(WeChatSender.FIELD_SAFE)));
            }
            textMessage.setMsgtype("text");
            TextMessageDTO.TextBean textBean = new TextMessageDTO.TextBean();
            if (HmsgConstant.TemplateEditType.RICH_TEXT.equals(message.getTemplateEditType())) {
                // 富文本模板，去除模板的html标签
                msgContent = ClearHtmlUtils.clearHtml(msgContent);
            }
            textBean.setContent(String.format("%s%n%s", message.getSubject(), msgContent));
            textMessage.setText(textBean);
            return textMessage;
        } catch (Exception e) {
            logger.error("Incorrect parameter format");
            throw new CommonException(e);
        }
    }

    private static MarkdownMessageDTO generateMarkDownMessage(List<String> userList, Message message) {
        // 明文消息不存在，使用content
        String msgContent = StringUtils.isEmpty(message.getPlainContent()) ? message.getContent() : message.getPlainContent();
        MarkdownMessageDTO markdownMessage = new MarkdownMessageDTO();
        try {
            markdownMessage.setTouser(buildParam(userList));
            // 处理额外参数
            Map<String, String> map = OBJECT_MAPPER.readValue(message.getSendArgs(), new TypeReference<Map<String, String>>() {
            });
            markdownMessage.setAgentid(Long.parseLong(map.get(WeChatSender.FIELD_AGENT_ID)));
            if (map.containsKey(WeChatSender.FIELD_PARTY_LIST)) {
                markdownMessage.setToparty(buildParam(OBJECT_MAPPER.readValue(map.get(WeChatSender.FIELD_PARTY_LIST), new TypeReference<List<String>>() {
                })));
            }
            if (map.containsKey(WeChatSender.FIELD_TAG_LIST)) {
                markdownMessage.setTotag(buildParam(OBJECT_MAPPER.readValue(map.get(WeChatSender.FIELD_TAG_LIST), new TypeReference<List<String>>() {
                })));
            }
            markdownMessage.setMsgtype("markdown");
            MarkdownMessageDTO.MarkdownBean bean = new MarkdownMessageDTO.MarkdownBean();
            if (HmsgConstant.TemplateEditType.RICH_TEXT.equals(message.getTemplateEditType())) {
                // 富文本模板，去除模板的html标签
                msgContent = ClearHtmlUtils.clearHtml(msgContent);
            }
            bean.setContent(String.format("## %s%n%s", message.getSubject(), msgContent));
            markdownMessage.setMarkdown(bean);
            return markdownMessage;
        } catch (Exception e) {
            logger.error("Incorrect parameter format");
            throw new CommonException(e);
        }
    }

    private static String buildParam(List<String> list) {
        if (CollectionUtils.isEmpty(list)) {
            return null;
        }
        StringBuilder sb = new StringBuilder();
        list.forEach(item -> sb.append(item).append("|"));
        return sb.toString().endsWith("|") ? sb.substring(0, sb.length() - 1) : null;
    }

    /**
     * 发送微信公众号模板消息
     */
    public static void sendMessage(WechatCorpMessageService messageService, String token, List<String> userList, Message message, WeChatMsgType msgType) {
        if (msgType == null) {
            msgType = WeChatMsgType.MARK_DOWN;
        }
        MessageSendResultDTO response = null;
        switch (msgType) {
            case TEXT:
                TextMessageDTO textMessage = generateTextMessage(userList, message);
                response = messageService.sendTextMsg(textMessage, token);
                break;
            case MARK_DOWN:
                MarkdownMessageDTO markdownMessage = generateMarkDownMessage(userList, message);
                response = messageService.sendMarkdownMsg(markdownMessage, token);
                break;
            default:
                break;
        }
        if (response != null && response.getErrcode() != 0) {
            throw new SendMessageException(String.format("wechat official message send failed! code: [%s] , message: [%s]", response.getErrcode(), response.getErrmsg()));
        }
    }
}
