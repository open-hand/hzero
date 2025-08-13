package org.hzero.message.infra.repository.impl;

import java.util.List;

import org.hzero.boot.platform.lov.annotation.ProcessLovValue;
import org.hzero.message.domain.entity.TemplateServerWh;
import org.hzero.message.domain.repository.TemplateServerWhRepository;
import org.hzero.message.infra.mapper.TemplateServerWhMapper;
import org.hzero.mybatis.base.impl.BaseRepositoryImpl;
import org.hzero.mybatis.domian.Condition;
import org.hzero.mybatis.util.Sqls;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import io.choerodon.core.domain.Page;
import io.choerodon.mybatis.pagehelper.PageHelper;
import io.choerodon.mybatis.pagehelper.domain.PageRequest;

/**
 * 消息发送配置webhook 资源库实现
 *
 * @author xiaoyu.zhao@hand-china.com 2020-05-15 10:29:57
 */
@Component
public class TemplateServerWhRepositoryImpl extends BaseRepositoryImpl<TemplateServerWh>
                implements TemplateServerWhRepository {

    @Autowired
    private TemplateServerWhMapper templateServerWhMapper;

    @Override
    @ProcessLovValue
    public Page<TemplateServerWh> pageTemplateServerWh(Long tempServerLineId, PageRequest pageRequest) {
        return PageHelper.doPageAndSort(pageRequest,
                        () -> templateServerWhMapper.selectTemplateServerWh(tempServerLineId));
    }

    @Override
    public List<TemplateServerWh> selectByTempServerLineId(Long tempServerLineId) {
        return selectByCondition(Condition
                        .builder(TemplateServerWh.class).andWhere(Sqls.custom()
                                        .andEqualTo(TemplateServerWh.FIELD_TEMP_SERVER_LINE_ID, tempServerLineId))
                        .build());
    }
}
