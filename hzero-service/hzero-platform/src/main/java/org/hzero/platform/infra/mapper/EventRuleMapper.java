package org.hzero.platform.infra.mapper;

import java.util.List;

import org.apache.ibatis.annotations.Param;
import org.hzero.platform.api.dto.EventRuleDTO;
import org.hzero.platform.domain.entity.EventRule;

import io.choerodon.mybatis.common.BaseMapper;

/**
 * 事件规则Mapper
 *
 * @author jiangzhou.bo@hand-china.com 2018/06/11 17:01
 */
public interface EventRuleMapper extends BaseMapper<EventRule> {

    /**
     * 查询事件规则
     * 
     * @param eventRule 事件规则参数
     * @return List<EventRuleDTO>
     */
    List<EventRuleDTO> selectEventRule(EventRuleDTO eventRule);

    /**
     * 根据事件ID删除事件规则
     * 
     * @param eventId 事件ID
     */
    void removeByEventId(@Param("eventId") Long eventId);
}
