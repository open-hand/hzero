package org.hzero.admin.app.service.impl;


import io.choerodon.core.domain.Page;
import io.choerodon.mybatis.pagehelper.domain.PageRequest;
import org.apache.commons.collections4.CollectionUtils;
import org.hzero.admin.app.service.HystrixConfLineService;
import org.hzero.admin.domain.entity.HystrixConfLine;
import org.hzero.admin.domain.repository.HystrixConfLineRepository;
import org.hzero.core.base.BaseConstants;
import org.hzero.mybatis.helper.SecurityTokenHelper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.Assert;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Hystrix保护设置行明细应用服务默认实现
 *
 * @author xingxing.wu@hand-china.com 2018-09-11 14:05:14
 */
@Service
public class HystrixConfLineServiceImpl implements HystrixConfLineService {

    @Lazy
    @Autowired
    private HystrixConfLineRepository hystrixConfLineRepository;

    @Override
    public Page<HystrixConfLine> pageAndSort(HystrixConfLine hystrixConfLine, PageRequest pageRequest) {
        Page<HystrixConfLine> list = hystrixConfLineRepository.pageAndSortByPropName(hystrixConfLine, pageRequest);
        return list;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public List<HystrixConfLine> batchUpdate(List<HystrixConfLine> hystrixConfLines, Long hystrixConfId) {
        //判空
        Assert.notNull(hystrixConfId, BaseConstants.ErrorCode.DATA_INVALID);
        if (CollectionUtils.isNotEmpty(hystrixConfLines)) {
            for (HystrixConfLine hystrixConfLine : hystrixConfLines) {
                //不确定头ID是否为空 直接赋值
                hystrixConfLine.setConfId(hystrixConfId);
                HystrixConfLine localHystrixConfLine = new HystrixConfLine();
                //判断是新增还是更新
                if (hystrixConfLine.getConfLineId() == null) {
                    //新增
                    //需要判断唯一性 configID和propertyName唯一约束
                    localHystrixConfLine.setPropertyName(hystrixConfLine.getPropertyName());
                    localHystrixConfLine.setConfId(hystrixConfLine.getConfId());
                    Assert.isTrue(hystrixConfLineRepository.selectCount(localHystrixConfLine) == 0,
                            BaseConstants.ErrorCode.DATA_EXISTS);
                    hystrixConfLineRepository.insertSelective(hystrixConfLine);
                } else {
                    //更新
                    //检查token
                    SecurityTokenHelper.validToken(hystrixConfLine);
                    //查询是否存在
                    Assert.isTrue(hystrixConfLineRepository.existsWithPrimaryKey(hystrixConfLine), BaseConstants.ErrorCode.ERROR);
                    //需要判断唯一性 configID和propertyName唯一约束
                    Assert.isTrue(hystrixConfLineRepository.countExclusiveSelf(hystrixConfLine) == 0,
                            BaseConstants.ErrorCode.DATA_EXISTS);
                    hystrixConfLineRepository.updateByPrimaryKeySelective(hystrixConfLine);
                }
            }
        }
        return hystrixConfLines;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void batchDelete(List<HystrixConfLine> hystrixConfLines) {
        SecurityTokenHelper.validToken(hystrixConfLines);
        List<Long> confLineIdList = hystrixConfLines.stream().map(HystrixConfLine::getConfLineId).collect(Collectors.toList());

        HystrixConfLine hystrixConfLine = new HystrixConfLine();
        if (CollectionUtils.isNotEmpty(confLineIdList)) {
            for (Long confLineId : confLineIdList) {
                //判空
                Assert.notNull(confLineId, BaseConstants.ErrorCode.DATA_INVALID);
                //查询是否存在
                hystrixConfLine.setConfLineId(confLineId);
                Assert.isTrue(hystrixConfLineRepository.existsWithPrimaryKey(hystrixConfLine), BaseConstants.ErrorCode.DATA_NOT_EXISTS);
                //执行删除函数
                hystrixConfLineRepository.deleteByPrimaryKey(confLineId);
            }
        }
    }

    @Override
    public HystrixConfLine selectByPrimaryKey(Long confLineId) {
        return hystrixConfLineRepository.selectByPrimaryKey(confLineId);
    }
}
