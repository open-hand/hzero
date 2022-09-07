package org.hzero.message.infra.supporter;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.hzero.boot.message.entity.WeChatSender;
import org.hzero.message.domain.entity.Message;
import org.hzero.message.infra.exception.SendMessageException;
import org.hzero.wechat.dto.TemplateSendDTO;
import org.hzero.wechat.dto.TemplateSendResultDTO;
import org.hzero.wechat.service.BaseWechatService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import io.choerodon.core.convertor.ApplicationContextHelper;
import io.choerodon.core.exception.CommonException;

/**
 * description
 *
 * @author shuangfei.zhu@hand-china.com 2019/10/17 16:22
 */
public class WeChatOfficialSupporter {

    private WeChatOfficialSupporter() {
    }

    private static final Logger logger = LoggerFactory.getLogger(WeChatOfficialSupporter.class);

    private static final ObjectMapper OBJECT_MAPPER = ApplicationContextHelper.getContext().getBean(ObjectMapper.class);

    private static List<TemplateSendDTO> generateMessage(Message message, List<String> userList) {
        List<TemplateSendDTO> templateSendList = new ArrayList<>();
        String templateId = message.getExternalCode();
        // 模板参数
        Map<String, Map<String, String>> data = null;
        // 小程序参数
        TemplateSendDTO.MiniprogramBean miniprogramBean = null;
        // 调转地址
        String url = null;
        try {
            Map<String, String> map = OBJECT_MAPPER.readValue(message.getSendArgs(), new TypeReference<Map<String, String>>() {
            });
            if (map.containsKey(WeChatSender.FIELD_DATA)) {
                data = OBJECT_MAPPER.readValue(map.get(WeChatSender.FIELD_DATA), new TypeReference<Map<String, Map<String, String>>>() {
                });
            }
            if (map.containsKey(WeChatSender.FIELD_MINIPROGRAM)) {
                miniprogramBean = OBJECT_MAPPER.readValue(map.get(WeChatSender.FIELD_MINIPROGRAM), TemplateSendDTO.MiniprogramBean.class);
            }
            if (map.containsKey(WeChatSender.FIELD_URL)) {
                url = map.get(WeChatSender.FIELD_URL);
            }
        } catch (Exception e) {
            logger.error("Incorrect parameter format");
            throw new CommonException(e);
        }
        for (String item : userList) {
            TemplateSendDTO templateSendDTO = new TemplateSendDTO();
            templateSendDTO.setMiniprogram(miniprogramBean);
            templateSendDTO.setUrl(url);
            templateSendDTO.setTemplate_id(templateId);
            templateSendDTO.setData(data);
            templateSendDTO.setTouser(item);
            templateSendList.add(templateSendDTO);
        }
        return templateSendList;
    }

    /**
     * 发送微信公众号模板消息
     */
    public static void sendMessage(BaseWechatService weChatService, String token, Message message, List<String> userList) {
        List<TemplateSendDTO> list = generateMessage(message, userList);
        List<TemplateSendResultDTO> response = weChatService.batchSendTemplateMessage(list, token);
        // 有一个失败，都算失败
        for (TemplateSendResultDTO item : response) {
            if (item.getErrcode() != 0) {
                throw new SendMessageException(String.format("wechat official message send failed! code: [%s] , message: [%s], messageId: [%s]", item.getErrcode(), item.getErrmsg(), item.getMsgid()));
            }
        }
    }
}
