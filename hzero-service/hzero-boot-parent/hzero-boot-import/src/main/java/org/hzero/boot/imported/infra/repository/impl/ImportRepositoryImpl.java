package org.hzero.boot.imported.infra.repository.impl;

import java.util.Date;

import org.hzero.boot.imported.domain.entity.Import;
import org.hzero.boot.imported.domain.repository.ImportRepository;
import org.hzero.boot.imported.infra.mapper.ImportMapper;
import org.hzero.boot.imported.infra.redis.AmountRedis;
import org.hzero.core.redis.RedisHelper;
import org.hzero.mybatis.base.impl.BaseRepositoryImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import io.choerodon.core.domain.Page;
import io.choerodon.mybatis.pagehelper.PageHelper;
import io.choerodon.mybatis.pagehelper.domain.PageRequest;

/**
 * 资源库实现
 *
 * @author shuangfei.zhu@hand-china.com 2018-12-19 14:57:57
 */
@Component
public class ImportRepositoryImpl extends BaseRepositoryImpl<Import> implements ImportRepository {

    @Autowired
    private ImportMapper importMapper;
    @Autowired
    private RedisHelper redisHelper;

    @Override
    public Page<Import> pageImportHistory(Long tenantId, String templateCode, Date creationDateFrom, Date creationDateTo, PageRequest pageRequest) {
        return PageHelper.doPageAndSort(pageRequest, () -> importMapper.listImport(tenantId, templateCode, creationDateFrom, creationDateTo));
    }

    @Override
    public Import getStatus(String batch) {
        Import im = selectOne(new Import().setBatch(batch));
        if (im != null) {
            // 获取就绪数量
            Integer ready = AmountRedis.getReady(redisHelper, batch);
            im.setReady(ready);
            // 获取导入总数
            Integer count = AmountRedis.getCount(redisHelper, batch);
            im.setCount(count);
        }
        return im;
    }
}
