package org.hzero.plugin.platform.hr.infra.mapper;

import java.util.List;

import org.apache.ibatis.annotations.Param;
import org.hzero.plugin.platform.hr.domain.entity.HrSyncDept;

import io.choerodon.mybatis.common.BaseMapper;

/**
 * HR部门数据同步Mapper
 *
 * @author zifeng.ding@hand-china.com 2019-12-25 09:35:06
 */
public interface HrSyncDeptMapper extends BaseMapper<HrSyncDept> {

    /**
     * 查询同步时需要新增的部门
     * @param syncTypeCode    同步类型
     * @param tenantId        租户Id
     * @return
     */
    List<HrSyncDept> getCreateDept(@Param("syncTypeCode") String syncTypeCode,@Param("tenantId") Long tenantId);

    /**
     * 查询同步时需要删除的部门
     * @param syncTypeCode    同步类型
     * @param tenantId        租户Id
     * @return
     */
    List<HrSyncDept> getDeleteDept(@Param("syncTypeCode") String syncTypeCode,@Param("tenantId") Long tenantId);


    /**
     * 查询同步时需要更新的部门
     * @param syncTypeCode    同步类型
     * @param tenantId        租户Id
     * @return
     */
    List<HrSyncDept> getUpdateDept(@Param("syncTypeCode") String syncTypeCode,@Param("tenantId") Long tenantId);
}
