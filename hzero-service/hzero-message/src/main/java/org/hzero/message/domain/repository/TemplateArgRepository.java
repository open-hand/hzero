package org.hzero.message.domain.repository;

import org.hzero.message.domain.entity.TemplateArg;
import org.hzero.mybatis.base.BaseRepository;

import java.util.List;

/**
 * 消息模板参数资源库
 *
 * @author fanghan.liu@hand-china.com 2019-10-08 16:49:41
 */
public interface TemplateArgRepository extends BaseRepository<TemplateArg> {

    /**
     * 分页查询消息模板参数
     *
     * @param templateArg 参数实体
     * @return 查询结果
     */
    List<TemplateArg> selectByTemplateId(TemplateArg templateArg);
}
