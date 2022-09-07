package org.hzero.boot.platform.event.provider;

import org.hzero.boot.message.MessageClient;
import org.hzero.boot.platform.event.Constants;
import org.hzero.boot.platform.event.helper.RuleMatcher;
import org.hzero.boot.platform.event.vo.EventParam;
import org.hzero.boot.platform.event.vo.EventRuleVO;
import org.hzero.boot.platform.event.vo.WebHookParam;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.annotation.Nonnull;
import java.util.HashMap;
import java.util.Map;

/**
 * WebHook提供器
 *
 * @author bergturing 2020/08/11 11:00
 */
@Component
public class WebHookScheduleProvider extends AbstractScheduleProvider {
    /**
     * 日志对象
     */
    private static final Logger LOGGER = LoggerFactory.getLogger(WebHookScheduleProvider.class);
    /**
     * 消息客户端
     */
    private final MessageClient messageClient;

    @Autowired
    public WebHookScheduleProvider(RuleMatcher ruleMatcher,
                                   MessageClient messageClient) {
        super(ruleMatcher);
        this.messageClient = messageClient;
    }

    @Override
    protected Object doSchedule(@Nonnull EventRuleVO eventRuleVO, EventParam eventParam) {
        // 同步调用
        if (eventRuleVO.syncCall()) {
            LOGGER.debug(">>>>> 同步方法调用");
            // 发送WebHook消息，并返回消息对象
            return this.messageClient.sync()
                    .sendWebHookMessage(eventRuleVO.getMessageCode(), eventRuleVO.getServerCode(), this.parseWebHookParam(eventParam));
        } else {
            // 异步调用
            LOGGER.debug(">>>>> 异步方法调用");
            return this.messageClient.async()
                    .sendWebHookMessage(eventRuleVO.getMessageCode(), eventRuleVO.getServerCode(), this.parseWebHookParam(eventParam));
        }
    }

    @Override
    public String supportType() {
        return Constants.CallType.WEB_HOOK;
    }

    /**
     * 解析webhook参数
     *
     * @param eventParam 事件参数
     * @return webhook参数
     */
    private Map<String, String> parseWebHookParam(EventParam eventParam) {
        Map<String, String> webHookParams = new HashMap<>(16);

        if (eventParam instanceof WebHookParam) {
            eventParam.forEach((key, value) -> webHookParams.put(key, String.valueOf(value)));
        }

        return webHookParams;
    }
}
