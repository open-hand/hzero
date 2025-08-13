package org.hzero.plugin.platform.hr.domain.repository;

import java.util.Date;
import java.util.List;
import java.util.Set;

import org.hzero.mybatis.base.BaseRepository;
import org.hzero.plugin.platform.hr.api.dto.EmployeeDTO;
import org.hzero.plugin.platform.hr.api.dto.Receiver;
import org.hzero.plugin.platform.hr.api.dto.UnitDTO;
import org.hzero.plugin.platform.hr.api.dto.UnitUserDTO;
import org.hzero.plugin.platform.hr.domain.entity.Unit;

import io.choerodon.core.domain.Page;
import io.choerodon.mybatis.pagehelper.domain.PageRequest;

/**
 * 部门资源库
 *
 * @author gaokuo.dai@hand-china.com 2018-06-21 17:05:02
 */
public interface UnitRepository extends BaseRepository<Unit> {

    /**
     * 条件查询部门
     *
     * @param queryParam 查询条件
     * @return 查询结果
     */
    List<UnitDTO> selectDepartment(Unit queryParam);

    /**
     * 条件查询公司
     *
     * @param queryParam 查询条件
     * @return 查询结果
     */
    List<UnitDTO> selectCompany(Unit queryParam);

    /**
     * 查询与给定租户、代码重复的数据库记录数
     *
     * @param queryParam 查询条件
     * @return 重复的数据库记录数
     */
    int selectRepeatCodeCount(Unit queryParam);

    /**
     * 更新本组织及所有下属子组织的启用状态
     *
     * @param unit 本部门
     * @return 更新数量
     */
    int updateEnabledFlag(Unit unit);

    /**
     * 根据父级组织ID查询管理组织数量
     *
     * @param unit 查询条件
     * @return 管理组织数量
     */
    int selectSupervisorCountByParentUnitId(Unit unit);

    /**
     * 根据组织ID查询子节点的levelPath
     *
     * @param unitId 组织ID
     * @return 自己及其子节点
     */
    List<Unit> selectChildrenLevelPathById(Long unitId);


    /**
     * 获取所有部门
     *
     * @param tenantId    租户id
     * @param enabledFlag 是否有效
     * @return 部门
     */
    List<UnitDTO> selectAllDepartment(Long tenantId, Integer enabledFlag);

    /**
     * 查询部门
     *
     * @param tenantId      租户id
     * @param parentUnitIds 父部门id集合
     * @param unitIds       部门id集合
     * @return 部门
     */
    List<UnitDTO> selectDepartment(Long tenantId, List<Long> parentUnitIds, List<Long> unitIds);

    /**
     * 按类型获取部门
     *
     * @param typeCodes 指定的类型
     * @param tenantId  租户ID
     * @return List<UnitDTO>
     */
    List<UnitDTO> selectByTypeCodes(String[] typeCodes, Long tenantId);

    /**
     * 拉取最近更新过的记录
     *
     * @param tenantId 租户ID
     * @param after    这个时间点之后
     * @return 最近更新过的记录
     */
    List<Unit> listRecentUnit(Long tenantId, Date after);

    /**
     * 通过部门批量查询员工信息
     *
     * @param units                  部门信息
     * @param includeChildDepartment 是否包含子部门
     * @param typeCode               返回参数的种类，默认返回userId，参数种类包含
     *                               EMAIL（用户邮箱）,PHONE（用户手机号）,IDD（国际冠码）
     * @return Set<Receiver> 包含用户Id
     */
    Set<Receiver> listDepartmentUsers(List<UnitUserDTO> units, boolean includeChildDepartment, List<String> typeCode);

    /**
     * 查询传入参数的levelPath
     *
     * @param units 传入参数
     * @return 层级参数
     */
    List<Unit> selectAndSetCurrentLevelPath(List<Unit> units);

    /**
     * 查询单条数据的levelPath
     *
     * @param unit 传入参数
     * @return unit查询结果
     */
    Unit selectAndSetOneCurrentLevelPath(Unit unit);

    /**
     * 查询最上级公司信息列表
     *
     * @param tenantId 租户Id
     * @return List<Unit>
     */
    List<UnitDTO> selectRootNodeCompany(Long tenantId);

    /**
     * 查询传入UnitId的下级公司（集团）信息
     *
     * @param tenantId 租户Id
     * @param unitId   unitId
     * @return List<UnitDTO>
     */
    List<UnitDTO> selectSubNodeCompany(Long tenantId, Long unitId);

    /**
     * 分页查询公司（集团）信息
     *
     * @param unit        查询条件
     * @param pageRequest 分页参数
     * @return 分页结果
     */
    Page<UnitDTO> pageCompanyUnits(Unit unit, PageRequest pageRequest);

    /**
     * 根据公司Id或部门Id树形查询下级部门信息
     *
     * @param tenantId    租户Id
     * @param unitIds     部门Id
     * @param enabledFlag 是否启用
     * @return 树形查询结果
     */
    List<UnitDTO> treeDepartmentByCondition(Long tenantId, List<Long> unitIds, Integer enabledFlag);

    /**
     * 根据公司Id或部门Id查询下级部门信息列表
     *
     * @param tenantId    租户Id
     * @param unitIds     部门Id
     * @param enabledFlag 是否启用
     * @return 列表查询结果
     */
    List<UnitDTO> listDepartmentByCondition(Long tenantId, List<Long> unitIds, Integer enabledFlag);

    List<UnitDTO> selectAllUnit(Long tenantId);

    List<String> getTopUnitCodes(Long tenantId);

    /**
     * 新版组织架构查询公司
     *
     * @param keyWord 查询条件
     * @return 查询结果
     */
    List<UnitDTO> selectPlusCompany(String keyWord, Long tenantId, String lang);

    /**
     * 新版组织架构查询公司
     *
     * @param keyWord 查询条件
     * @return 查询结果
     */
    List<UnitDTO> selectPlusDepartment(String keyWord, Long unitCompanyId, Long tenantId, String lang);

    /**
     * 新版组织架构查询组织下员工
     *
     * @param unitId 查询条件
     * @return 查询结果
     */
    Page<EmployeeDTO> pageUnitUsers(Long tenantId, Long unitId, String keyWord, String status, Integer primaryPositionFlag, PageRequest pageRequest);

    /**
     * 新版组织架构查询组织下部门列表
     *
     * @param unitId 查询条件
     * @return 查询结果
     */
    Page<UnitDTO> pageUnitDept(Long unitId, Long tenantId, String keyWord, Integer enabledFlag, PageRequest pageRequest);

    /**
     * 根据levelPath  获取所有上级部门
     *
     * @param levelPath
     * @param tenantId
     * @return
     */
    List<Unit> queryParentUnitsByLevelPath(String levelPath, Long tenantId);

    /**
     * 获取上一级组织
     *
     * @param units 部门
     * @return 组织
     */
    List<Unit> selectParentUnit(List<Long> unitIds);

    /**
     * 获取部门员工对应的三方平台用户ID
     *
     * @param units                  组织
     * @param includeChildDepartment 是否包含子部门
     * @param thirdPlatformType      三方平台类型
     * @return 用户ID
     */
    Set<Receiver> listOpenDepartmentUsers(List<UnitUserDTO> units, boolean includeChildDepartment, String thirdPlatformType);

    /**
     * 查询符合条件的公司集团信息
     *
     * @param tenantId      租户Id
     * @param unitId        组织Id
     * @param unitCode      组织编码
     * @param unitName      组织名称
     * @param pageRequest   组织名称
     * @return 查询结果
     */
    Page<Unit> listCompanyUnits(Long tenantId, Long unitId, String unitCode, String unitName, PageRequest pageRequest);

    /**
     * 查询符合条件的部门信息
     *
     * @param tenantId      租户Id
     * @param unitCompanyId 部门所属公司Id
     * @param unitId        组织Id
     * @param unitCode      组织编码
     * @param unitName      组织名称
     * @param pageRequest   分页参数
     * @return 查询结果
     */
    Page<Unit> listDepartmentUnits(Long tenantId, Long unitCompanyId, Long unitId, String unitCode, String unitName, PageRequest pageRequest);
}
