package org.hzero.iam.domain.repository;

import java.util.List;

import org.hzero.mybatis.base.BaseRepository;
import org.hzero.iam.domain.entity.HrUnit;

/**
 * <p>
 * 描述
 * </p>
 *
 * @author allen 2018/6/26
 */
public interface HpfmHrUnitRepository extends BaseRepository<HrUnit> {
    /**
     * <p>
     *     通过用户ID查询其所直属组织
     * </p>
     *
     * @param userId 用户ID
     * @return 直属组织
     */
    List<HrUnit> queryHrUnitBelongedByUserId(Long userId);

    /**
     * <p>
     *     递归查询组织树(向上)
     * </p>
     *
     * @param unitId
     * @return
     */
    HrUnit queryHrUnitParentTreeByUnitId(Long unitId);

    /**
     * 查询用户在某租户下所属的组织树<br/>
     *
     * 可能会存在多棵树<br/>
     * @param tenantId
     * @param userId 用户ID
     * @return
     */
    List<HrUnit> queryWholeHrUnitTreeByTenantId(Long tenantId, Long userId);

    /**
     * 批量查询组织<br/>
     *
     * @param unitIdList
     * @return
     */
    List<HrUnit> batchQueryHrUnitsByUnitIds(List<Long> unitIdList);

    /**
     * 查询结果树
     *
     * @param tenantId 租户id
     * @param parentHrUnitId 父亲id
     * @param unitCode 代码
     * @param unitName 名称
     * @return
     */
    List<HrUnit> queryHrUnitSubTree(Long tenantId, Long parentHrUnitId,String unitCode,String unitName);
}
