package org.hzero.admin.infra.repository.impl;


import io.choerodon.core.domain.Page;
import io.choerodon.mybatis.pagehelper.PageHelper;
import io.choerodon.mybatis.pagehelper.domain.PageRequest;
import org.hzero.admin.domain.entity.HystrixConf;
import org.hzero.admin.domain.repository.HystrixConfRepository;
import org.hzero.admin.infra.mapper.HystrixConfMapper;
import org.hzero.mybatis.base.impl.BaseRepositoryImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * Hystrix保护设置 资源库实现
 *
 * @author xingxing.wu@hand-china.com 2018-09-11 14:05:14
 */
@Component
public class HystrixConfRepositoryImpl extends BaseRepositoryImpl<HystrixConf> implements HystrixConfRepository {

    @Autowired
    private HystrixConfMapper hystrixConfMapper;

    @Override
    public Page<HystrixConf> pageByCondition(PageRequest pageRequest, HystrixConf hystrixConf) {
        return PageHelper.doPageAndSort(pageRequest, () -> hystrixConfMapper.listByCondition(hystrixConf));
    }

    @Override
    public int countExclusiveSelf(HystrixConf hystrixConf) {
        return hystrixConfMapper.countExclusiveSelf(hystrixConf);
    }
}
