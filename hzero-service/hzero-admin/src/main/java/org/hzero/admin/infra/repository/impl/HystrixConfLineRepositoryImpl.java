package org.hzero.admin.infra.repository.impl;


import io.choerodon.core.domain.Page;
import io.choerodon.mybatis.pagehelper.PageHelper;
import io.choerodon.mybatis.pagehelper.domain.PageRequest;
import org.hzero.admin.domain.entity.HystrixConfLine;
import org.hzero.admin.domain.repository.HystrixConfLineRepository;
import org.hzero.admin.infra.mapper.HystrixConfLineMapper;
import org.hzero.mybatis.base.impl.BaseRepositoryImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * Hystrix保护设置行明细 资源库实现
 *
 * @author xingxing.wu@hand-china.com 2018-09-11 14:05:14
 */
@Component
public class HystrixConfLineRepositoryImpl extends BaseRepositoryImpl<HystrixConfLine> implements HystrixConfLineRepository {
    @Autowired
    private HystrixConfLineMapper hystrixConfLineMapper;

    @Override
    public Page<HystrixConfLine> pageAndSortByPropName(HystrixConfLine hystrixConfLine, PageRequest pageRequest) {
        return PageHelper.doPageAndSort(pageRequest, () -> hystrixConfLineMapper.listHystrixLinetByPropName(hystrixConfLine));
    }

    @Override
    public int countExclusiveSelf(HystrixConfLine hystrixConfLine) {
        return hystrixConfLineMapper.countExclusiveSelf(hystrixConfLine);
    }
}
