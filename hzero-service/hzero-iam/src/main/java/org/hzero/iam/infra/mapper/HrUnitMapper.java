package org.hzero.iam.infra.mapper;

import io.choerodon.mybatis.common.BaseMapper;
import org.apache.ibatis.annotations.Param;
import org.hzero.iam.domain.entity.HrUnit;

import java.util.List;

/**
 * <p>
 * 描述
 * </p>
 *
 * @author allen 2018/6/26
 */
public interface HrUnitMapper extends BaseMapper<HrUnit> {
    /**
     * <p>
     *     通过用户ID查询其所直属组织
     * </p>
     *
     * <p>
     *     Fix-20180727: 用户可被分配至多个岗位, 不同的岗位可能对应不同的部门
     * </p>
     *
     * @param userId 用户ID
     * @return 直属组织
     */
    List<HrUnit> queryHrUnitBelongedByUserId(Long userId);

    /**
     * <p>
     *     递归查询组织结构树(含当前组织, 向上)
     * </p>
     *
     * @param unitId
     * @return
     */
    HrUnit queryHrUnitParentTreeByUnitId(Long unitId);

    /**
     * 批量查询组织<br/>
     *
     * @param unitIdList
     * @return
     */
    List<HrUnit> batchQueryHrUnitsByUnitIds(@Param("unitIdList") List<Long> unitIdList);

    /**
     * <p>
     *     通过用户ID查询其所属组织树列表
     * </p>
     *
     * <p>
     *     Fix-20180727: 用户可被分配至多个岗位, 不同的岗位可能对应不同的部门
     * </p>
     *
     * @param tenantId 租户ID
     * @param userId 用户ID
     * @return 直属组织
     */
    List<HrUnit> queryHrUnitTreeBelongedByUserId(@Param("tenantId") Long tenantId, @Param("userId") Long userId);

    /**
     * 查询组织树子树
     *
     * @param tenantId
     * @param parentHrUnitId
     * @return
     */
    List<HrUnit> queryHrUnitSubTree(@Param("tenantId") Long tenantId, @Param("parentHrUnitId") Long parentHrUnitId);
}
