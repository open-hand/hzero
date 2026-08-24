package org.hzero.admin.domain.repository;

import io.choerodon.core.domain.Page;
import io.choerodon.mybatis.pagehelper.domain.PageRequest;
import org.hzero.admin.domain.entity.HystrixConfLine;
import org.hzero.mybatis.base.BaseRepository;

/**
 * Hystrix保护设置行明细资源库
 *
 * @author xingxing.wu@hand-china.com 2018-09-11 14:05:14
 */
public interface HystrixConfLineRepository extends BaseRepository<HystrixConfLine> {
    /**
     * 参数名模糊查询
     *
     * @param hystrixConfLine 参数
     * @param pageRequest     分页信息
     * @return 返回值
     */
    Page<HystrixConfLine> pageAndSortByPropName(HystrixConfLine hystrixConfLine, PageRequest pageRequest);

    /**
     * 查询个数
     *
     * @param hystrixConfLine
     * @return 数量
     */
    int countExclusiveSelf(HystrixConfLine hystrixConfLine);
}
