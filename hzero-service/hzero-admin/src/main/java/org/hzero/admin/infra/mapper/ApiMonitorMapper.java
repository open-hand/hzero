package org.hzero.admin.infra.mapper;

import io.choerodon.mybatis.common.BaseMapper;
import org.hzero.admin.domain.entity.ApiMonitor;

/**
 * @author XCXCXCXCX
 * @version 1.2.0
 * @date 2019/12/9 3:21 下午
 */
public interface ApiMonitorMapper extends BaseMapper<ApiMonitor> {

    /**
     * 清空
     */
    void deleteAll();

}
