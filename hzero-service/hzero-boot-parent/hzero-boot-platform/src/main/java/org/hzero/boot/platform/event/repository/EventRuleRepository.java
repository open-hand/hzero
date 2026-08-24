package org.hzero.boot.platform.event.repository;

import java.util.List;

import org.hzero.boot.platform.event.vo.EventRuleVO;

/**
 * 获取事件规则接口
 *
 * @author jiangzhou.bo@hand-china.com 2018/06/13 10:46
 */
public interface EventRuleRepository {

    /**
     * 根据事件编码查找事件规则
     * 
     * @param eventCode 事件编码
     * @return 事件规则
     */
    List<EventRuleVO> findByEventCode(String eventCode);

}
