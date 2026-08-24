package org.hzero.message.infra.mapper;

import org.apache.ibatis.annotations.Param;
import org.hzero.message.domain.entity.TemplateServerWh;
import io.choerodon.mybatis.common.BaseMapper;
import java.util.List;

/**
 * 消息发送配置webhookMapper
 *
 * @author xiaoyu.zhao@hand-china.com 2020-05-15 10:29:57
 */
public interface TemplateServerWhMapper extends BaseMapper<TemplateServerWh> {

    /**
     * 查询webhook配置
     *
     * @param tempServerLineId 模板行Id
     * @return 查询结果
     */
    List<TemplateServerWh> selectTemplateServerWh(@Param("tempServerLineId") Long tempServerLineId);
}
