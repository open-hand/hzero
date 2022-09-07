package org.hzero.plugin.platform.hr.infra.mapper;

import java.util.List;

import org.apache.ibatis.annotations.Param;
import org.hzero.plugin.platform.hr.domain.entity.HrSyncEmployee;

import io.choerodon.mybatis.common.BaseMapper;

/**
 * HR员工数据同步Mapper
 *
 * @author zifeng.ding@hand-china.com 2019-12-25 09:35:06
 */
public interface HrSyncEmployeeMapper extends BaseMapper<HrSyncEmployee> {
    /**
     * 查询同步时需要新增的员工
     * @param syncTypeCode    同步类型
     * @param tenantId        租户Id
     * @return
     */
    List<HrSyncEmployee> getCreateEmployee(@Param("syncTypeCode") String syncTypeCode,@Param("tenantId") Long tenantId);
    /**
     * 查询同步时需要删除的员工
     * @param syncTypeCode    同步类型
     * @param tenantId        租户Id
     * @return
     */
    List<HrSyncEmployee> getDeleteEmployee(@Param("syncTypeCode") String syncTypeCode,@Param("tenantId") Long tenantId);
    /**
     * 查询同步时需要更新的员工
     * @param syncTypeCode    同步类型
     * @param tenantId        租户Id
     * @return
     */
    List<HrSyncEmployee> getUpdateEmployee(@Param("syncTypeCode") String syncTypeCode,@Param("tenantId") Long tenantId);
}
