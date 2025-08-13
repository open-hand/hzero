package org.hzero.message.app.service

import org.hzero.boot.message.MessageClient
import org.hzero.boot.message.entity.Receiver
import org.hzero.message.MessageApplication
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootContextLoader
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.test.context.ContextConfiguration
import org.springframework.test.context.web.WebAppConfiguration
import spock.lang.Specification
import spock.lang.Timeout
import spock.lang.Title
import spock.lang.Unroll

/**
 * description
 *
 * @author shuangfei.zhu@hand-china.com 2019/01/07 10:13
 */
@WebAppConfiguration
@SpringBootTest(classes = MessageApplication.class)
@ContextConfiguration(loader = SpringBootContextLoader.class)
@Title("Unit test for hzero message rel send")
@Unroll
@Timeout(10)
class RelSendMessageServiceTest extends Specification {

    @Autowired
    MessageClient messageClient

    def "test relSendMessage"() {
        given:
        Long tenantId = 0L
        String lang = "zh_CN"
        String messageTemplateCode = "HIAM.TEST"
        Receiver email = new Receiver().setEmail("ThisOldDog@163.com")
        Receiver sms = new Receiver().setPhone("17621686404")
        //
        Receiver web = new Receiver().setUserId(150121L).setTargetUserTenantId(180136L)
        List<Receiver> receiverList = new ArrayList<>()
//        receiverList.add(email)
//        receiverList.add(sms)
        receiverList.add(web)
        Map<String, String> args = new HashMap<>()
        args.put("password", "123")
        args.put("captcha", "123")
        args.put("loginName", "123")
        args.put("identifycode", "123")

        when: "测试关联发送"
        messageClient.sendMessage(tenantId, messageTemplateCode, lang, receiverList, args)
        then: "发送成功"
    }

    def "test multi relSendMessage"() {
        given:
        Long tenantId = 0L
        String lang = "zh_CN"
        String messageTemplateCode = "HIAM.TEST"
        Receiver email = new Receiver().setEmail("ThisOldDog@163.com")
        Receiver sms = new Receiver().setPhone("17621686404")
        Receiver web = new Receiver().setUserId(1L)
        List<Receiver> receiverList = new ArrayList<>()
        receiverList.add(email)
        receiverList.add(sms)
        receiverList.add(web)
        Map<String, String> args = new HashMap<>()
        args.put("password", "123")
        args.put("captcha", "123")
        args.put("loginName", "123")
        args.put("identifycode", "123")

        when: "测试关联发送"
        for (int i = 0; i < 1; ++i)
            new Thread(
                    new Runnable() {
                        @Override
                        void run() {
                            messageClient.sendMessage(tenantId, messageTemplateCode, lang, receiverList, args)
                        }
                    }
            ).start()
        then: "发送成功"
    }
}
