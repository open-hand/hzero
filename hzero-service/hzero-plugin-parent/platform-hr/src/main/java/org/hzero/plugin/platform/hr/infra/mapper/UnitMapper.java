package org.hzero.plugin.platform.hr.infra.mapper;

import java.util.List;
import java.util.Set;

import org.apache.ibatis.annotations.Param;
import org.hzero.plugin.platform.hr.api.dto.EmployeeDTO;
import org.hzero.plugin.platform.hr.api.dto.Receiver;
import org.hzero.plugin.platform.hr.api.dto.UnitDTO;
import org.hzero.plugin.platform.hr.api.dto.UnitUserDTO;
import org.hzero.plugin.platform.hr.domain.entity.Unit;

import io.choerodon.mybatis.common.BaseMapper;

/**
 * 部门Mapper
 *
 * @author gaokuo.dai@hand-china.com 2018-06-21 17:05:02
 */
public interface UnitMapper extends BaseMapper<Unit> {

    /**
     * 条件查询公司级组织
     *
     * @param queryParam 查询条件
     * @return 查询结果
     */
    List<UnitDTO> selectCompany(@Param("queryParam") Unit queryParam, @Param("topUnitCodes") List<String> topUnitCodes);

    /**
     * 条件查询部门级组织
     *
     * @param queryParam   查询条件
     * @param topUnitCodes 最上级组织编码集合
     * @return 查询结果
     */
    List<UnitDTO> selectDepartment(@Param("queryParam") Unit queryParam, @Param("topUnitCodes") List<String> topUnitCodes);

    /**
     * 查询与给定租户、代码重复的数据库记录数
     *
     * @param queryParam 查询条件
     * @return 重复的数据库记录数
     */
    int selectRepeatCodeCount(Unit queryParam);

    /**
     * 更新本部门及所有下属子部门的启用状态
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
     * 根据组织ID查询自己及其子节点的levelPath
     *
     * @param unitId 部门ID
     * @return 自己及其子节点
     */
    List<Unit> selectChildrenLevelPathById(@Param("unitId") Long unitId);

    /**
     * 获取所有部门
     *
     * @param tenantId     租户id
     * @param enabledFlag  是否有效
     * @param topUnitCodes 最上级组织类型编码
     * @return 部门
     */
    List<UnitDTO> selectAllDepartment(@Param("tenantId") Long tenantId,
                                      @Param("enabledFlag") Integer enabledFlag,
                                      @Param("topUnitCodes") List<String> topUnitCodes);

    /**
     * 批量条件 查询部门
     *
     * @param tenantId      租户id
     * @param parentUnitIds 父部门id
     * @param unitIds       部门id
     * @param topUnitCodes  最上级组织类型编码
     * @return 查询结果级
     */
    List<UnitDTO> hcbmSelectDepartment(@Param("tenantId") Long tenantId,
                                       @Param("parentUnitIds") List<Long> parentUnitIds,
                                       @Param("unitIds") List<Long> unitIds,
                                       @Param("topUnitCodes") List<String> topUnitCodes
    );

    /**
     * 按类型查询组织数据（G：集团；C:公司；D:部门）
     *
     * @param typeCodes 指定的类型
     * @param tenantId  租户Id
     * @return List<UnitDTO>
     */
    List<UnitDTO> selectByTypeCodes(@Param("typeCodes") String[] typeCodes, @Param("tenantId") Long tenantId);

    /**
     * 查询获取部门及其子部门下的用户信息
     *
     * @param unit                   查询条件，包含部门id及对应的租户Id
     * @param includeChildDepartment 是否查询子部门
     * @param typeCode               返回参数的种类，默认返回userId，参数种类包含
     *                               EMAIL（用户邮箱）,PHONE（用户手机号）,IDD（国际冠码）
     * @return Set<Receiver>
     */
    List<Receiver> selectDepartmentUsers(UnitUserDTO unit, @Param("includeChildDepartment") boolean includeChildDepartment, List<String> typeCode);

    /**
     * 查询传入参数的levelPath
     *
     * @param units 传入参数
     * @return 层级参数
     */
    List<Unit> selectAndSetCurrentLevelPath(@Param("units") List<Unit> units);


    /**
     * 查询传入参数的levelPath
     *
     * @param unit 传入参数
     * @return unit返回结果
     */
    Unit selectAndSetOneCurrentLevelPath(@Param("unit") Unit unit);

    /**
     * 查询最上级公司组织
     *
     * @param tenantId     租户Id
     * @param topUnitCodes 最上级组织类型编码
     * @return List<Unit>
     */
    List<UnitDTO> selectRootNodeCompany(@Param("tenantId") Long tenantId, @Param("topUnitCodes") List<String> topUnitCodes);

    /**
     * 查询传入UnitId的下级公司（集团）信息
     *
     * @param tenantId     租户Id
     * @param unitId       unitId
     * @param topUnitCodes 最上级组织类型编码
     * @return List<UnitDTO>
     */
    List<UnitDTO> selectSubNodeCompany(@Param("tenantId") Long tenantId, @Param("unitId") Long unitId,
                                       @Param("topUnitCodes") List<String> topUnitCodes);

    /**
     * 分页查询公司（集团）信息
     *
     * @param unit         查询条件
     * @param topUnitCodes 最上级组织类型编码
     * @return 查询结果
     */
    List<UnitDTO> selectPageCompany(@Param("unit") Unit unit, @Param("topUnitCodes") List<String> topUnitCodes);

    /**
     * 通过租户Id和组织编码查询获取组织名称信息
     *
     * @param tenantId     租户Id
     * @param unitCodes    组织编码
     * @param topUnitCodes 最上级组织类型编码
     * @return unitName
     */
    List<String> selectUnitNameByTenantAndCode(@Param("tenantId") Long tenantId, @Param("unitCodes") String[] unitCodes,
                                               @Param("topUnitCodes") List<String> topUnitCodes);

    /**
     * 根据公司Id或部门Id树形查询下级部门信息
     *
     * @param tenantId     租户Id
     * @param unitIds      部门Id
     * @param enabledFlag  是否启用
     * @param topUnitCodes 最上级组织类型编码
     * @return 树形查询结果
     */
    List<UnitDTO> selectDepartmentByCondition(@Param("tenantId") Long tenantId, @Param("unitIds") List<Long> unitIds,
                                              @Param("enabledFlag") Integer enabledFlag, @Param("topUnitCodes") List<String> topUnitCodes);

    List<UnitDTO> selectAllUnit(@Param("tenantId") Long tenantId);


    /**
     * 新版组织架构查询公司级组织
     *
     * @param keyWord 查询条件
     * @return 查询结果
     */
    List<UnitDTO> selectPlusCompany(@Param("keyWord") String keyWord, @Param("tenantId") Long tenantId, @Param("lang") String lang);

    /**
     * 新版组织架构查询部门级组织
     *
     * @param keyWord 查询条件
     * @return 查询结果
     */
    List<UnitDTO> selectPlusDepartment(@Param("keyWord") String keyWord, @Param("unitCompanyId") Long unitCompanyId, @Param("tenantId") Long tenantId, @Param("lang") String lang);

    /**
     * 新版组织架构查询组织下员工
     *
     * @param unitId 查询条件
     * @return 查询结果
     */
    List<EmployeeDTO> queryAllUnitEmployees(@Param("unitId") Long unitId, @Param("tenantId") Long tenantId, @Param("keyWord") String keyWord, @Param("status") String status, @Param("primaryPositionFlag") Integer primaryPositionFlag);

    /**
     * 新版组织架构查询组织下部门列表
     *
     * @param unitId 查询条件
     * @return 查询结果
     */
    List<UnitDTO> queryUnitDept(@Param("unitId") Long unitId, @Param("tenantId") Long tenantId, @Param("keyWord") String keyWord, @Param("enabledFlag") Integer enabledFlag);

    /**
     * 根据levelPath  获取所有上级部门
     *
     * @param levelPath
     * @param tenantId
     * @return
     */
    List<Unit> queryParentUnitsByLevelPath(@Param("levelPath") String levelPath, @Param("tenantId") Long tenantId);

    /**
     * 获取上一级组织
     *
     * @param unitIds 部门ID
     * @return 组织
     */
    List<Unit> selectParentUnit(List<Long> unitIds);

    /**
     * 获取部门员工对应的三方平台用户ID
     *
     * @param unit                   组织
     * @param includeChildDepartment 是否包含子部门
     * @param thirdPlatformType      三方平台类型
     * @return 用户ID
     */
    List<Receiver> selectOpenDepartmentUsers(@Param("unit") UnitUserDTO unit,
                                             @Param("includeChildDepartment") boolean includeChildDepartment,
                                             @Param("thirdPlatformType") String thirdPlatformType);

    /**
     * 查询符合条件的公司集团信息
     *
     * @param tenantId   租户Id
     * @param unitId     组织Id
     * @param unitCode   组织编码
     * @param unitName   组织名称
     * @return 查询结果
     */
    List<Unit> selectCompanyUnits(@Param("tenantId") Long tenantId,
                                  @Param("unitId") Long unitId,
                                  @Param("unitCode") String unitCode,
                                  @Param("unitName") String unitName);

    /**
     * 查询符合条件的部门信息
     *
     * @param tenantId      租户Id
     * @param unitCompanyId 部门所属公司Id
     * @param unitId        组织Id
     * @param unitCode      组织编码
     * @param unitName      组织名称
     * @return 查询结果
     */
    List<Unit> listDepartmentUnits(@Param("tenantId") Long tenantId,
                                   @Param("unitCompanyId") Long unitCompanyId,
                                   @Param("unitId") Long unitId,
                                   @Param("unitCode") String unitCode,
                                   @Param("unitName") String unitName);

    /**
     * 根据部门编码批量获取部门信息
     *
     * @param tenantId 租户Id
     * @param unitCompanyId 组织Id
     * @param unitCodeSet 部门编码集
     * @param topUnitCodes 最上级组织类型编码
     * @return 匹配结果
     */
    List<UnitDTO> getDepartmentsByUnitCodes(@Param("tenantId") Long tenantId,
            @Param("unitCompanyId") Long unitCompanyId,
            @Param("unitCodeSet") Set<String> unitCodeSet,
            @Param("topUnitCodes") List<String> topUnitCodes);
}
