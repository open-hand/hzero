package org.hzero.message.domain.repository;

import org.hzero.mybatis.base.BaseRepository;
import org.hzero.message.domain.entity.TemplateServerWh;

import io.choerodon.core.domain.Page;
import io.choerodon.mybatis.pagehelper.domain.PageRequest;
import java.util.List;

/**
 * 消息发送配置webhook资源库
 *
 * @author xiaoyu.zhao@hand-china.com 2020-05-15 10:29:57
 */
public interface TemplateServerWhRepository extends BaseRepository<TemplateServerWh> {

    /**
     * 分页查询webhook发送配置
     *
     * @param pageRequest 分页参数
     * @return Page<TemplateServerWh>
     */
    Page<TemplateServerWh> pageTemplateServerWh(Long tempServerLineId, PageRequest pageRequest);

    /**
     * 通过发送配置行Id查询关联的webhook信息
     *
     * @param tempServerLineId 发送配置行Id
     * @return List<TemplateServerWh>
     */
    List<TemplateServerWh> selectByTempServerLineId(Long tempServerLineId);
}
