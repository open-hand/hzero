package org.hzero.admin.app.service;


import io.choerodon.core.domain.Page;
import io.choerodon.mybatis.pagehelper.domain.PageRequest;
import org.hzero.admin.domain.entity.HystrixConfLine;

import java.util.List;


/**
 * Hystrix保护设置行明细应用服务
 *
 * @author xingxing.wu@hand-china.com 2018-09-11 14:05:14
 */
public interface HystrixConfLineService {

    /**
     * 分页查询保护设置行明细
     *
     * @param hystrixConfLine
     * @param pageRequest
     * @return
     */
    Page<HystrixConfLine> pageAndSort(HystrixConfLine hystrixConfLine, PageRequest pageRequest);

    /**
     * 批量更新保护设置行明细
     *
     * @param hystrixConfLines
     * @param hystrixConfId
     * @return
     */
    List<HystrixConfLine> batchUpdate(List<HystrixConfLine> hystrixConfLines, Long hystrixConfId);

    /**
     * 批量删除保护设置行明细
     *
     * @param hystrixConfLines
     */
    void batchDelete(List<HystrixConfLine> hystrixConfLines);

    HystrixConfLine selectByPrimaryKey(Long confLineId);
}
