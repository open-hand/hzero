package org.hzero.admin.infra.mapper;


import io.choerodon.mybatis.common.BaseMapper;
import org.hzero.admin.domain.entity.HystrixConf;

import java.util.List;

/**
 * Hystrix保护设置Mapper
 *
 * @author xingxing.wu@hand-china.com 2018-09-11 14:05:14
 */
public interface HystrixConfMapper extends BaseMapper<HystrixConf> {

    /**
     * 模糊查询
     *
     * @param hystrixConf 查询信息
     * @return 返回值
     */
    List<HystrixConf> listByCondition(HystrixConf hystrixConf);

    /**
     * 查询除自身之外的数据条数
     *
     * @param hystrixConf 查询信息
     * @return 返回值
     */
    int countExclusiveSelf(HystrixConf hystrixConf);

}
