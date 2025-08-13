package org.hzero.boot.message.feign;

import java.util.Map;

import org.hzero.boot.message.entity.*;
import org.hzero.boot.message.feign.fallback.MessageRemoteImpl;
import org.hzero.common.HZeroService;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

/**
 * <p>
 * 远程消息服务
 * </p>
 *
 * @author qingsheng.chen 2018/8/7 星期二 9:33
 */
@FeignClient(value = HZeroService.Message.NAME, path = "/v1", fallback = MessageRemoteImpl.class)
public interface MessageRemoteService {

    /**
     * 重发消息
     *
     * @param organizationId 租户Id
     * @param transactionId  消息事务Id
     * @return 重发结果
     */
    @PostMapping("/{organizationId}/messages/resend")
    ResponseEntity<String> resendMessage(@PathVariable("organizationId") Long organizationId,
                                         @RequestParam("transactionId") long transactionId);

    /**
     * 查询消息模板明细
     *
     * @param tenantId     租户ID
     * @param templateCode 模板编码
     * @param lang         语言
     * @return 消息模板明细
     */
    @GetMapping("/{organizationId}/message/templates/template-code")
    ResponseEntity<String> queryMessageTemplate(@PathVariable("organizationId") long tenantId,
                                                @RequestParam("templateCode") String templateCode,
                                                @RequestParam(value = "lang", required = false) String lang);

    /**
     * 查询消息接收人类型
     *
     * @param tenantId 租户ID
     * @param typeCode 接收人类型编码
     * @param args     自定义参数
     * @return 消息接收人类型
     */
    @PostMapping("/{organizationId}/message/receiver/type-code")
    ResponseEntity<String> queryReceiver(@PathVariable("organizationId") long tenantId,
                                         @RequestParam("typeCode") String typeCode,
                                         @RequestBody(required = false) Map<String, Object> args);

    /**
     * 查询第三方消息接收人
     *
     * @param tenantId          租户ID
     * @param thirdPlatformType 三方平台类型
     * @param typeCode          接收人类型编码
     * @param args              自定义参数
     * @return 接收人列表
     */
    @PostMapping("/{organizationId}/message/receiver/type-code/open")
    ResponseEntity<String> queryOpenReceiver(@PathVariable("organizationId") long tenantId,
                                             @RequestParam("thirdPlatformType") String thirdPlatformType,
                                             @RequestParam("typeCode") String typeCode,
                                             @RequestBody(required = false) Map<String, String> args);

    /**
     * 发送站内消息
     *
     * @param tenantId      租户ID
     * @param messageSender 消息发送内容
     * @return 站内消息
     */
    @PostMapping("/{organizationId}/messages/web")
    ResponseEntity<MessageTransmission> sendWebMessage(@PathVariable("organizationId") long tenantId,
                                                       @RequestBody MessageSender messageSender);

    /**
     * 发送邮件消息
     *
     * @param tenantId      租户ID
     * @param messageSender 消息发送内容
     * @return 邮件消息
     */
    @PostMapping("/{organizationId}/messages/email")
    ResponseEntity<MessageTransmission> sendEmail(@PathVariable("organizationId") long tenantId,
                                                  @RequestBody MessageSender messageSender);

    /**
     * 发送短信消息
     *
     * @param tenantId      租户ID
     * @param messageSender 消息发送内容
     * @return 短信消息
     */
    @PostMapping("/{organizationId}/messages/sms/template")
    ResponseEntity<MessageTransmission> sendSms(@PathVariable("organizationId") long tenantId,
                                                @RequestBody MessageSender messageSender);

    /**
     * 发送消息
     *
     * @param tenantId      租户ID
     * @param messageSender 发送消息内容
     * @return 发送结果
     */
    @PostMapping("/{organizationId}/message/relevance/with-receipt")
    ResponseEntity<String> sendMessage(@PathVariable("organizationId") long tenantId,
                                       @RequestBody MessageSender messageSender);

    /**
     * 关联发送消息
     *
     * @param tenantId 租户ID
     * @param sender   发送消息内容
     * @return 发送结果
     */
    @PostMapping("/{organizationId}/message/relevance/all/with-receipt")
    ResponseEntity<String> sendAllMessage(@PathVariable("organizationId") long tenantId,
                                          @RequestBody AllSender sender);

    /**
     * 查询消息模板配置行
     *
     * @param tenantId    租户ID
     * @param messageCode 消息编码
     * @return 消息模板配置行
     */
    @GetMapping("/{organizationId}/template-servers/detail/line")
    ResponseEntity<String> listTemplateServerLine(@PathVariable("organizationId") Long tenantId,
                                                  @RequestParam("messageCode") String messageCode);

    /**
     * 获取公告信息明细
     *
     * @param noticeId 公告Id
     * @return NoticeDTO
     */
    @GetMapping("/notices/{noticeId}/detail")
    ResponseEntity<String> getNoticeDetailsById(@PathVariable("noticeId") Long noticeId);

    /**
     * 发送微信公众号消息
     *
     * @param organizationId 租户ID
     * @param weChatSender   微信发送对象
     * @return 微信消息
     */
    @PostMapping("/{organizationId}/messages/wechat/official")
    ResponseEntity<MessageTransmission> sendWeChatOfficial(@PathVariable("organizationId") Long organizationId, @RequestBody WeChatSender weChatSender);

    /**
     * 发送企业微信应用消息
     *
     * @param organizationId 租户ID
     * @param weChatSender   微信发送对象
     * @return 微信消息
     */
    @PostMapping("/{organizationId}/messages/wechat/enterprise")
    ResponseEntity<MessageTransmission> sendWeChatEnterprise(@PathVariable("organizationId") Long organizationId, @RequestBody WeChatSender weChatSender);

    /**
     * 发送钉钉应用消息
     *
     * @param organizationId 租户ID
     * @param dingTalkSender 钉钉消息对象
     * @return 钉钉消息
     */
    @PostMapping("/{organizationId}/messages/dingtalk")
    ResponseEntity<MessageTransmission> sendDingTalk(@PathVariable("organizationId") Long organizationId, @RequestBody DingTalkSender dingTalkSender);

    /**
     * 发送webHook应用消息
     *
     * @param organizationId 租户ID
     * @param webHookSender  WebHook消息对象
     * @return WebHook消息
     */
    @PostMapping("/{organizationId}/messages/webhook")
    ResponseEntity<MessageTransmission> sendWebHookMessage(@PathVariable("organizationId") Long organizationId, WebHookSender webHookSender);
}
