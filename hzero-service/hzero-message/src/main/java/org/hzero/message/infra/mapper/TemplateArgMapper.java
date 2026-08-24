package org.hzero.message.infra.mapper;

import java.util.List;

import org.hzero.message.domain.entity.TemplateArg;

import io.choerodon.mybatis.common.BaseMapper;

/**
 * 消息模板参数Mapper
 *
 * @author fanghan.liu@hand-china.com 2019-10-08 16:49:41
 */
public interface TemplateArgMapper extends BaseMapper<TemplateArg> {

    /**
     * 查询单个消息模板参数
     *
     * @param templateArg 模板参数实体
     * @return List<TemplateArg>
     */
    List<TemplateArg> selectByTemplateId(TemplateArg templateArg);
}
