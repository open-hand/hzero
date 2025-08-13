package org.hzero.boot.platform.event.repository;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.choerodon.core.exception.CommonException;
import org.apache.commons.collections4.CollectionUtils;
import org.hzero.boot.platform.event.Constants;
import org.hzero.boot.platform.event.vo.EventRuleVO;
import org.hzero.core.redis.RedisHelper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * 事件规则资源库模式实现，从redis缓存查找
 *
 * @author jiangzhou.bo@hand-china.com 2018/06/13 10:49
 */
public class DefaultEventRuleRepository implements EventRuleRepository {

    @Autowired
    private RedisHelper redisHelper;
    @Autowired
    private ObjectMapper objectMapper;

    private static final Logger logger = LoggerFactory.getLogger(DefaultEventRuleRepository.class);

    @Override
    public List<EventRuleVO> findByEventCode(String eventCode) {
        List<String> redisRules = redisHelper.lstAll(Constants.EVENT_KEY + ":" + eventCode);
        List<EventRuleVO> ruleVOList = new ArrayList<>();
        if (CollectionUtils.isNotEmpty(redisRules)) {
            redisRules.forEach(redisRule -> {
                try {
                    EventRuleVO ruleVO = objectMapper.readValue(redisRule, EventRuleVO.class);
                    ruleVOList.add(ruleVO);
                } catch (IOException e) {
                    logger.error(">>>>> 从redis读取EventRule失败");
                    throw new CommonException(e.getMessage());
                }
            });
        }
        return ruleVOList;
    }
}
