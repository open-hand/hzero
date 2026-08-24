package org.hzero.plugin.platform.hr.domain.repository;

import io.choerodon.core.domain.Page;
import io.choerodon.mybatis.pagehelper.domain.PageRequest;
import org.hzero.mybatis.base.BaseRepository;
import org.hzero.plugin.platform.hr.api.dto.EmployeeDTO;
import org.hzero.plugin.platform.hr.api.dto.PositionDTO;
import org.hzero.plugin.platform.hr.api.dto.UnitPositionDTO;
import org.hzero.plugin.platform.hr.domain.entity.Position;

import java.util.Date;
import java.util.List;

/**
 * <p>
 * 组织架构岗位仓库
 * </p>
 *
 * @author qingsheng.chen 2018/6/20 星期三 14:49
 */
public interface PositionRepository extends BaseRepository<Position> {
    /**
     * 查询当前部门下主管岗位数量
     *
     * @param tenantId   租户ID
     * @param unitId     部门ID
     * @param positionId 岗位ID
     * @return 当前部门下主管岗位数量
     */
    int querySupervisorPositionFlagCount(Long tenantId, Long unitId, Long positionId);

    /**
     * 查询岗位
     *
     * @param tenantId     租户ID
     * @param unitId       部门ID
     * @param positionCode 编码
     * @param positionName 名称
     * @return 岗位
     */
    List<PositionDTO> selectByPositionCodeAndName(long tenantId, long unitId, String positionCode, String positionName);

    /**
     * 查询岗位以及自岗位
     *
     * @param tenantId  租户ID
     * @param levelPath 岗位路径
     * @return 岗位列表
     */
    List<Position> selectWithChild(Long tenantId, String levelPath);

    /**
     * 查询组织架构公司部门
     *
     * @param tenantId   租户ID
     * @param type       类型
     * @param name       名称
     * @param unitIdList 部门
     * @return 组织架构公司部门
     */
    List<UnitPositionDTO> listUnit(long tenantId, String type, String name, List<Long> unitIdList);

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
    List<UnitPositionDTO> listUnitPosition(long tenantId, long employeeId, String type, String name, List<Long> departmentIdList);

    /**
     * 拉取最近更新过的记录
     *
     * @param tenantId 租户ID
     * @param after    这个时间点之后
     * @return 最近更新过的记录
     */
    List<Position> listRecentPosition(Long tenantId, Date after);

    /**
     * 通过编码查询岗位信息
     *
     * @param positionCode 岗位编码
     * @param tenantId     租户id
     * @return 岗位
     */
    Position queryPositionByCode(String positionCode, Long tenantId);

    /**
     * 全量查询租户下的岗位信息
     *
     * @param tenantId 租户id
     * @return 岗位
     */
    List<Position> queryAllPositionsByTenant(Long tenantId);

    /**
     * 查询岗位信息
     *
     * @param pageRequest 分页信息
     * @param position    岗位查询条件
     * @return 岗位信息列表
     */
    Page<Position> pagePosition(PageRequest pageRequest, Position position);
    
    /**
     * 新版组织架构查询组织下岗位
     *
     * @param unitId 查询条件
     * @return 查询结果
     */
    Page<Position> pagePositionByUnit(PageRequest pageRequest, Long tenantId, Long unitId,String keyWord);
    
    /**
     * 查询岗位
     *
     * @param tenantId     租户ID
     * @param unitId       部门ID
     * @param positionCode 编码
     * @param positionName 名称
     * @return 岗位
     */
    List<PositionDTO> selectPlusPositionTree(Long tenantId, Long unitCompanyId,Long unitId,String keyWord);
    
    /**
     * 新版组织架构查询岗位下员工
     *
     * @param positionId 查询条件
     * @return 查询结果
     */
    Page<EmployeeDTO> pagePositionUsers(Long tenantId,Long positionId,String keyWord,String status,Integer primaryPositionFlag,PageRequest pageRequest);
}
