package org.hzero.plugin.platform.hr.infra.mapper;

import java.util.List;
import java.util.Set;

import org.apache.ibatis.annotations.Param;
import org.hzero.plugin.platform.hr.api.dto.EmployeeDTO;
import org.hzero.plugin.platform.hr.api.dto.PositionDTO;
import org.hzero.plugin.platform.hr.api.dto.UnitPositionDTO;
import org.hzero.plugin.platform.hr.domain.entity.Position;

import io.choerodon.mybatis.common.BaseMapper;

/**
 * <p>
 * 组织架构岗位数据库操作映射
 * </p>
 *
 * @author qingsheng.chen 2018/6/20 星期三 14:57
 */
public interface PositionMapper extends BaseMapper<Position> {

    /**
     * 查询当前部门下主管岗位数量
     *
     * @param tenantId   租户ID
     * @param unitId     部门ID
     * @param positionId 岗位ID
     * @return 当前部门下主管岗位数量
     */
    Integer querySupervisorPositionFlagCount(@Param("tenantId") Long tenantId, @Param("unitId") Long unitId, @Param("positionId") Long positionId);

    /**
     * 查询岗位
     *
     * @param tenantId     租户ID
     * @param unitId       部门ID
     * @param positionCode 编码
     * @param positionName 名称
     * @return 岗位
     */
    List<PositionDTO> selectByPositionCodeAndName(@Param("tenantId") long tenantId, @Param("unitId") long unitId, @Param("positionCode") String positionCode, @Param("positionName") String positionName);

    /**
     * 查询岗位以及自岗位
     *
     * @param tenantId  租户ID
     * @param levelPath 岗位路径
     * @return 岗位列表
     */
    List<Position> selectWithChild(@Param("tenantId") Long tenantId, @Param("levelPath") String levelPath);

    /**
     * 查询组织架构公司部门
     *
     * @param tenantId 租户ID
     * @param type     类型
     * @param name     名称
     * @param unitList 部门id
     * @return 组织架构公司部门
     */
    List<UnitPositionDTO> listUnit(@Param("tenantId") long tenantId, @Param("type") String type, @Param("name") String name, @Param("unitList") List<Long> unitList);

    /**
     * 查询父节点子节点
     *
     * @param tenantId      租户ID
     * @param levelPathList 等级路径列表
     * @return 父节点子节点
     */
    List<UnitPositionDTO> listKinsfolk(@Param("tenantId") long tenantId, @Param("levelPathList") List<String> levelPathList);

    /**
     * 查询父节点
     *
     * @param tenantId      租户ID
     * @param levelPathList 等级路径列表
     * @return 父节点
     */
    List<UnitPositionDTO> listParents(@Param("tenantId") long tenantId, @Param("levelPathList") List<String> levelPathList);

    /**
     * 查询组织架构公司部门岗位
     *
     * @param tenantId         租户ID
     * @param employeeId       员工ID
     * @param type             查询类型
     * @param name             岗位名称
     * @param departmentIdList 部门ID
     * @return 组织架构公司部门岗位
     */
    List<UnitPositionDTO> listUnitPosition(@Param("tenantId") long tenantId, @Param("employeeId") long employeeId, @Param("type") String type, @Param("name") String name, @Param("departmentIdList") List<Long> departmentIdList);

    /**
     * 查询父节点子节点
     *
     * @param tenantId      租户ID
     * @param employeeId    员工ID
     * @param levelPathList 等级路径列表
     * @return 父节点子节点
     */
    List<UnitPositionDTO> listKinsfolkPosition(@Param("tenantId") long tenantId, @Param("employeeId") long employeeId, @Param("levelPathList") List<String> levelPathList);

    /**
     * 查询子节点
     *
     * @param tenantId      租户ID
     * @param employeeId    员工ID
     * @param levelPathList 等级路径列表
     * @return 父节点
     */
    List<UnitPositionDTO> listChildrenPosition(@Param("tenantId") long tenantId, @Param("employeeId") long employeeId, @Param("levelPathList") List<String> levelPathList);

    /**
     * 查询岗位
     *
     * @param positionCode 岗位编码
     * @param positionName 岗位名称
     * @return 岗位列表
     */
    List<Position> selectPosition(@Param("tenantId") Long tenantId, @Param("positionCode") String positionCode, @Param("positionName") String positionName, @Param("companyName") String companyName);

    /**
     * 根据组织Id查询岗位
     *
     * @param tenantId
     * @param unitId
     * @return 岗位列表
     */
    List<Position> selectPositionByUnit(@Param("tenantId") Long tenantId, @Param("unitId") Long unitId, @Param("keyWord") String keyWord);

    /**
     * 查询岗位
     *
     * @param tenantId     租户ID
     * @param unitId       部门ID
     * @param positionCode 编码
     * @param positionName 名称
     * @return 岗位
     */
    List<PositionDTO> selectPlusPositionTree(@Param("tenantId") long tenantId, @Param("unitCompanyId") Long unitCompanyId, @Param("unitId") Long unitId, @Param("keyWord") String keyWord);

    /**
     * 新版组织架构查询岗位下员工
     *
     * @param positionId 查询条件
     * @return 查询结果
     */
    List<EmployeeDTO> queryEmployeesByPositionId(@Param("positionId") Long positionId, @Param("tenantId") Long tenantId, @Param("keyWord") String keyWord, @Param("status") String status, @Param("primaryPositionFlag") Integer primaryPositionFlag);

    /**
     * 通过岗位编码批量查询岗位信息
     *
     * @param tenantId      租户Id
     * @param unitId        部门Id
     * @param positionCodeSet  岗位编码查询集合
     * @return 筛选结果
     */
    List<PositionDTO> getPositionsByPositionCodes(@Param("tenantId") long tenantId,
            @Param("unitId") long unitId,
            @Param("positionCodeSet") Set<String> positionCodeSet);
}
