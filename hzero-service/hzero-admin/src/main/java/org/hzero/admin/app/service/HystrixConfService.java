package org.hzero.admin.app.service;


import io.choerodon.core.domain.Page;
import io.choerodon.mybatis.pagehelper.domain.PageRequest;
import org.hzero.admin.domain.entity.HystrixConf;

import java.util.List;


/**
 * Hystrix保护设置应用服务
 *
 * @author xingxing.wu@hand-china.com 2018-09-11 14:05:14
 */
public interface HystrixConfService {
    /**
     * 头行批量新建及更新
     *
     * @param hystrixConfs 参数
     * @return 返回值
     */
    List<HystrixConf> batchUpdateSelective(List<HystrixConf> hystrixConfs);

    /**
     * 刷新配置
     *
     * @param hystrixConfs 要刷新的数据
     * @return 返回值
     */
    List<HystrixConf> refresh(List<HystrixConf> hystrixConfs);

    Page<HystrixConf> pageByCondition(PageRequest pageRequest, HystrixConf hystrixConf);

    HystrixConf selectByConfigId(Long confId);
}
