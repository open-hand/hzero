package org.hzero.admin.domain.repository;

import io.choerodon.core.domain.Page;
import io.choerodon.mybatis.pagehelper.domain.PageRequest;
import org.hzero.admin.domain.entity.HystrixConf;
import org.hzero.mybatis.base.BaseRepository;

/**
 * Hystrix保护设置资源库
 *
 * @author xingxing.wu@hand-china.com 2018-09-11 14:05:14
 */
public interface HystrixConfRepository extends BaseRepository<HystrixConf> {

    /**
     * 模糊查询
     *
     * @param pageRequest 分页参数
     * @param hystrixConf 模糊查询参数
     * @return
     */
    Page<HystrixConf> pageByCondition(PageRequest pageRequest, HystrixConf hystrixConf);

    /**
     * 查询除自身之外的数据条数
     *
     * @param hystrixConf 查询参数
     * @return 数量
     */
    int countExclusiveSelf(HystrixConf hystrixConf);

}
