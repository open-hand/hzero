package org.hzero.message.app.service.impl;

import java.util.List;

import org.hzero.core.base.BaseAppService;
import org.hzero.message.app.service.TemplateServerWhService;
import org.hzero.message.domain.entity.TemplateServerWh;
import org.hzero.message.domain.repository.TemplateServerWhRepository;
import org.hzero.mybatis.helper.SecurityTokenHelper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import io.choerodon.core.domain.Page;
import io.choerodon.mybatis.pagehelper.domain.PageRequest;

/**
 * 消息发送配置webHook应用服务默认实现
 *
 * @author xiaoyu.zhao@hand-china.com 2020-05-15 10:29:57
 */
@Service
public class TemplateServerWhServiceImpl extends BaseAppService implements TemplateServerWhService {

    @Autowired
    private TemplateServerWhRepository templateServerWhRepository;

    @Override
    public Page<TemplateServerWh> pageTemplateServerWh(Long tempServerLineId, PageRequest pageRequest) {
        return templateServerWhRepository.pageTemplateServerWh(tempServerLineId, pageRequest);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public List<TemplateServerWh> batchCreateTemplateServerWh(Long tempServerLineId, List<TemplateServerWh> templateServerWhs) {
        templateServerWhs.forEach(templateServerWh -> templateServerWh.setTempServerLineId(tempServerLineId));
        validList(templateServerWhs);
        templateServerWhRepository.batchInsertSelective(templateServerWhs);
        return templateServerWhs;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void batchDeleteTemplateServerWh(List<TemplateServerWh> templateServerWhs) {
        SecurityTokenHelper.validToken(templateServerWhs);
        // 批量删除即可
        templateServerWhRepository.batchDeleteByPrimaryKey(templateServerWhs);
    }
}
