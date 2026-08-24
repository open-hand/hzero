package org.hzero.plugin.platform.hr.domain.repository;

import java.util.List;

import org.hzero.mybatis.base.BaseRepository;
import org.hzero.plugin.platform.hr.domain.entity.HrSyncDept;

/**
 * HR部门数据同步资源库
 *
 * @author zifeng.ding@hand-china.com 2019-12-25 09:35:06
 */
public interface HrSyncDeptRepository extends BaseRepository<HrSyncDept> {
    /**
     * 查询同步时需要新增的部门
     * @param syncTypeCode    同步类型
     * @param tenantId        租户Id
     * @return
     */
    List<HrSyncDept> getCreateDept(String syncTypeCode, Long tenantId);

    /**
     * 查询同步时需要删除的部门
     * @param syncTypeCode    同步类型
     * @param tenantId        租户Id
     * @return
     */
    List<HrSyncDept> getDeleteDept(String syncTypeCode, Long tenantId);


    /**
     * 查询同步时需要更新的部门
     * @param syncTypeCode    同步类型
     * @param tenantId        租户Id
     * @return
     */
    List<HrSyncDept> getUpdateDept(String syncTypeCode, Long tenantId);

    /**
     * 根据第三方部门id查询的同步部门
     * @param syncTypeCode   同步类型
     * @param tenantId       租户ID
     * @param deptIds        第三方部门ID
     * @return
     */
    List<HrSyncDept> getByDeptIds(String syncTypeCode, Long tenantId, List<Long> deptIds);

    /**
     * 根据部门id查询的同步部门
     * @param syncTypeCode   同步类型
     * @param tenantId       租户ID
     * @param unitIds        部门ID
     * @return
     */
    List<HrSyncDept> getByUnitIds(String syncTypeCode, Long tenantId, List<Long> unitIds);
}
