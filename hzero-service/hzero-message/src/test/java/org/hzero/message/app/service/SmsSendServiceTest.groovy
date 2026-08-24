package org.hzero.message.app.service

import org.hzero.boot.message.MessageClient
import org.hzero.boot.message.entity.Receiver
import org.hzero.message.MessageApplication
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootContextLoader
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.test.annotation.Rollback
import org.springframework.test.context.ContextConfiguration
import org.springframework.test.context.web.WebAppConfiguration
import org.springframework.transaction.annotation.Transactional
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
@Title("Unit test for hzero message sms send")
@Unroll
@Transactional
@Rollback
@Timeout(10)
class SmsSendServiceTest extends Specification {

    @Autowired
    MessageClient messageClient

    def "test sendSms"() {
        given:
        Long tenantId = 0L
        String serverCode = "HZERO_ALIYUN"
        String messageTemplateCode = "HIAM.CAPTCHA"
        Receiver receiver = new Receiver().setPhone("13033102885")
        List<Receiver> receiverList = new ArrayList<>()
        receiverList.add(receiver)
        Map<String, String> args = new HashMap<>()
        args.put("password", "123")
        args.put("captcha", "123")
        args.put("loginName", "123")
        args.put("identifycode", "123")

        when: "测试发送短信"
        Integer flag = messageClient.sendSms(tenantId, serverCode, messageTemplateCode, receiverList, args).getSendFlag()
        then: "短信发送成功"
        flag == 1
    }
}
