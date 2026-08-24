package org.hzero.admin.infra.mapper;


import io.choerodon.mybatis.common.BaseMapper;
import org.hzero.admin.domain.entity.HystrixConfLine;

import java.util.List;

/**
 * Hystrix保护设置行明细Mapper
 *
 * @author xingxing.wu@hand-china.com 2018-09-11 14:05:14
 */
public interface HystrixConfLineMapper extends BaseMapper<HystrixConfLine> {
    /**
     * 参数名模糊查询
     *
     * @param hystrixConfLine 参数名
     * @return 返回值
     */
    List<HystrixConfLine> listHystrixLinetByPropName(HystrixConfLine hystrixConfLine);

    /**
     * 查询个数
     *
     * @param hystrixConfLine 参数名
     * @return 返回值
     */
    int countExclusiveSelf(HystrixConfLine hystrixConfLine);
}
