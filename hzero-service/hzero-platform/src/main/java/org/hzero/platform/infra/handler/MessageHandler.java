package org.hzero.platform.infra.handler;

import org.apache.commons.lang3.StringUtils;
import org.hzero.boot.message.constant.WebSocketConstant;
import org.hzero.boot.message.entity.Msg;
import org.hzero.platform.infra.constant.HpfmMsgCodeConstants;
import org.hzero.websocket.helper.SocketMessageHandler;
import org.hzero.websocket.vo.MsgVO;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import io.choerodon.core.exception.CommonException;

/**
 * description
 *
 * @author shuangfei.zhu@hand-china.com 2019/08/28 19:31
 */
@Component
public class MessageHandler extends SocketMessageHandler {

    /**
     * Hzero平台HTTP协议,默认http
     */
    @Value("${hzero.platform.httpProtocol:http}")
    private String hzeroPlatformHttpProtocol;

    @Autowired
    private RestTemplate restTemplate;

    @Override
    public boolean needPrincipal() {
        return true;
    }

    @Override
    public void processMessage(MsgVO msgVO) {
        String serverName = msgVO.getService();
        if (StringUtils.isBlank(serverName)) {
            return;
        }
        Msg msg = new Msg();
        BeanUtils.copyProperties(msgVO, msg);
        // 获取用户信息
        msg.setUserDetails(getCustomUserDetails());
        // 使用restTemplate通知消息客户端
        String url = this.hzeroPlatformHttpProtocol + "://" + serverName + WebSocketConstant.MESSAGE_HANDLER_PATH;
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON_UTF8);
        HttpEntity<Msg> entity = new HttpEntity<>(msg, headers);
        ResponseEntity response = restTemplate.postForEntity(url, entity, String.class);
        if (!response.getStatusCode().is2xxSuccessful()) {
            throw new CommonException(HpfmMsgCodeConstants.SEND_SOCKET);
        }
    }
}
