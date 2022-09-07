package org.hzero.platform.app.service.impl;

import java.io.IOException;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.hzero.boot.imported.app.service.IDoImportService;
import org.hzero.boot.imported.infra.validator.annotation.ImportService;
import org.hzero.core.base.BaseConstants;
import org.hzero.core.redis.RedisHelper;
import org.hzero.platform.domain.entity.Message;
import org.hzero.platform.domain.repository.ResponseMessageRepository;
import org.hzero.platform.infra.constant.Constants;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import io.choerodon.core.exception.CommonException;

/**
 * 返回消息导入
 *
 * @author dingzf 2019/09/25 20:18
 */
@ImportService(templateCode = Constants.ImportTemplateCode.MESSAGE_TEMP)
public class MessageImportServiceImpl implements IDoImportService {

    public static final Logger logger = LoggerFactory.getLogger(MessageImportServiceImpl.class);

    @Autowired
    private ResponseMessageRepository messageRepository;
    @Autowired
    private ObjectMapper objectMapper;
    @Autowired
    private RedisHelper redisHelper;

    @Override
    public Boolean doImport(String data) {
        try {
            Message message = objectMapper.readValue(data, Message.class);
            Message temp = new Message();
            temp.setCode(message.getCode());
            temp.setLang(message.getLang());
            Message oldMessage = messageRepository.selectOne(temp);
            if (oldMessage != null) {
                // 更新数据
                message.setMessageId(oldMessage.getMessageId());
                message.setObjectVersionNumber(oldMessage.getObjectVersionNumber());
                messageRepository.updateByPrimaryKeySelective(message);
            } else {
                messageRepository.insertSelective(message);
            }
            // 刷新缓存
            message.cacheMessage(redisHelper, objectMapper);
        } catch (IOException e) {
            throw new CommonException(BaseConstants.ErrorCode.DATA_INVALID);
        }
        return true;
    }
}
