package org.hzero.message.infra.supporter;

import java.util.List;
import java.util.Objects;
import java.util.Properties;

import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;

import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.hzero.core.base.BaseConstants;
import org.hzero.message.domain.entity.EmailServer;
import org.hzero.message.domain.entity.Message;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.JavaMailSenderImpl;
import org.springframework.mail.javamail.MimeMessageHelper;

import io.choerodon.core.exception.CommonException;

/**
 * <p>
 * 邮件发送支持
 * </p>
 *
 * @author qingsheng.chen 2019/1/21 星期一 20:17
 */
public class EmailSupporter {

    private EmailSupporter() {
    }

    public static JavaMailSender javaMailSender(EmailServer emailServer) {
        JavaMailSenderImpl javaMailSender = new JavaMailSenderImpl();
        javaMailSender.setHost(emailServer.getHost());
        javaMailSender.setPort(Integer.parseInt(emailServer.getPort()));
        javaMailSender.setUsername(emailServer.getUsername());
        javaMailSender.setPassword(emailServer.getPasswordEncrypted());
        if (StringUtils.isNotBlank(emailServer.getProtocol())) {
            javaMailSender.setProtocol(emailServer.getProtocol());
        }
        if (CollectionUtils.isNotEmpty(emailServer.getEmailProperties())) {
            Properties properties = new Properties();
            emailServer.getEmailProperties().forEach(item -> properties.setProperty(item.getPropertyCode(), item.getPropertyValue()));
            javaMailSender.setJavaMailProperties(properties);
        }
        return javaMailSender;
    }

    public static void sendEmail(JavaMailSender javaMailSender, EmailServer emailServer, Message message, List<String> to, Integer batchSend) throws MessagingException {
        // 明文消息不存在，使用content
        String mailContent = StringUtils.isEmpty(message.getPlainContent()) ? message.getContent() : message.getPlainContent();
        MimeMessage mimeMessage = javaMailSender.createMimeMessage();
        MimeMessageHelper mimeMessageHelper = new MimeMessageHelper(mimeMessage, true, BaseConstants.DEFAULT_CHARSET);
        mimeMessageHelper.setFrom(emailServer.getSender());
        mimeMessageHelper.setSubject(message.getSubject());
        mimeMessageHelper.setText(mailContent, true);
        // 附件
        if (!CollectionUtils.isEmpty(message.getAttachmentList())) {
            message.getAttachmentList().forEach(item -> {
                try {
                    mimeMessageHelper.addAttachment(item.getFileName(), new ByteArrayResource(item.getFile()));
                } catch (Exception e) {
                    throw new CommonException(e);
                }
            });
        }
        // 抄送
        if (CollectionUtils.isNotEmpty(message.getCcList())) {
            mimeMessageHelper.setCc(message.getCcList().toArray(new String[]{}));
        }
        // 密送
        if (CollectionUtils.isNotEmpty(message.getBccList())) {
            mimeMessageHelper.setBcc(message.getBccList().toArray(new String[]{}));
        }
        if (Objects.equals(batchSend, BaseConstants.Flag.NO)) {
            for (String email : to) {
                mimeMessageHelper.setTo(email);
                javaMailSender.send(mimeMessage);
            }
        } else {
            mimeMessageHelper.setTo(to.toArray(new String[]{}));
            javaMailSender.send(mimeMessage);
        }
    }
}
