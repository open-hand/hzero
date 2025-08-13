package org.hzero.message.app.service;

import org.hzero.message.domain.entity.TemplateServerWh;

import io.choerodon.core.domain.Page;
import io.choerodon.mybatis.pagehelper.domain.PageRequest;
import java.util.List;

/**
 * 消息发送配置webhook应用服务
 *
 * @author xiaoyu.zhao@hand-china.com 2020-05-15 10:29:57
 */
public interface TemplateServerWhService {

    /**
     * 分页查询webhook发送配置分配信息
     *
     * @param tempServerLineId 发送配置行Id
     * @param pageRequest pageRequest
     * @return Page<TemplateServerWh>
     */
    Page<TemplateServerWh> pageTemplateServerWh(Long tempServerLineId, PageRequest pageRequest);

    /**
     * 批量新增webhook发送配置
     *
     * @param tempServerLineId  消息发送配置行Id
     * @param templateServerWhs 新增对象
     * @return 新增结果
     */
    List<TemplateServerWh> batchCreateTemplateServerWh(Long tempServerLineId, List<TemplateServerWh> templateServerWhs);

    /**
     * 批量删除webhook发送配置
     *
     * @param templateServerWhs 批量删除数据
     */
    void batchDeleteTemplateServerWh(List<TemplateServerWh> templateServerWhs);
}
