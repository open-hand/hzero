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
 * @author shuangfei.zhu@hand-china.com 2019/01/07 10:12
 */
@WebAppConfiguration
@SpringBootTest(classes = MessageApplication.class)
@ContextConfiguration(loader = SpringBootContextLoader.class)
@Title("Unit test for hzero message email send")
@Unroll
@Transactional
@Rollback
@Timeout(10)
class EmailSendServiceTest extends Specification {

    @Autowired
    MessageClient messageClient

    def "test sendEmail"() {
        given:
        Long tenantId = 0L
        String serverCode = "HAND"
        String messageTemplateCode = "HIAM.MODIFY_PASSWORD"
        Receiver receiver = new Receiver().setEmail("13033102885@163.com")
        List<Receiver> receiverList = new ArrayList<>()
        receiverList.add(receiver)
        Map<String, String> args = new HashMap<>()
        args.put("password", "123")
        args.put("captcha", "123")
        args.put("loginName", "123")
        args.put("identifycode", "123")

        when: "测试发送邮件"
        Integer flag = messageClient.sendEmail(tenantId, serverCode, messageTemplateCode, receiverList, args).getSendFlag()
        then: "邮件发送成功"
        flag == 1
    }
}
