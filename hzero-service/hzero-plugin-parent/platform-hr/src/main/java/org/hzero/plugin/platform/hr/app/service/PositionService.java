package org.hzero.plugin.platform.hr.app.service;

import java.util.List;

import org.hzero.plugin.platform.hr.api.dto.EmployeeDTO;
import org.hzero.plugin.platform.hr.api.dto.PositionDTO;
import org.hzero.plugin.platform.hr.api.dto.UnitPositionDTO;
import org.hzero.plugin.platform.hr.domain.entity.Position;

import io.choerodon.core.domain.Page;
import io.choerodon.mybatis.pagehelper.domain.PageRequest;

/**
 * <p>
 * 组织架构岗位管理接口
 * </p>
 *
 * @author qingsheng.chen 2018/6/20 星期三 14:11
 */
public interface PositionService {
    /**
     * 查询岗位
     *
     * @param positionId 岗位ID
     * @return 岗位
     */
    Position queryPosition(long positionId);

    /**
     * 查询部门下的所有岗位
     *
     * @param tenantId     租户ID
     * @param unitId       部门ID
     * @param positionCode 编码
     * @param positionName 名称
     * @return 当前租户部门下的所有岗位
     */
    List<PositionDTO> treePosition(long tenantId, long unitId, String positionCode, String positionName);

    /**
     * 查询组织架构公司部门岗位树
     *
     * @param tenantId   租户ID
     * @param employeeId 员工ID
     * @param type       类型
     * @param name       名称
     * @return 组织架构公司部门岗位树
     */
    List<UnitPositionDTO> treeUnitPosition(long tenantId, long employeeId, String type, String name);

    /**
     * 拉取最近更新过的记录
     *
     * @param tenantId 租户ID
     * @param before   过去多久内(单位：ms，默认5min)
     * @return 最近更新过的记录
     */
    List<Position> listRecentPosition(Long tenantId, long before);

    /**
     * 新增岗位
     *
     * @param position 新增岗位内容
     * @return 返回新增之后的岗位内容
     */
    Position createPosition(Position position);

    /**
     * 批量新增岗位
     *
     * @param tenantId      租户ID
     * @param unitCompanyId 公司ID
     * @param unitId        部门ID
     * @param positionList  岗位
     * @return 新增岗位
     */
    List<Position> batchCreatePosition(long tenantId, long unitCompanyId, long unitId, List<Position> positionList);

    /**
     * 更新岗位
     *
     * @param position 岗位内容
     * @return 返回更新之后的岗位内容
     */
    Position updatePosition(Position position);

    /**
     * 批量更新
     *
     * @param tenantId      租户ID
     * @param unitCompanyId 公司ID
     * @param unitId        部门ID
     * @param positionList  岗位内容列表
     * @return 返回更新之后的岗位内容列表
     */
    List<Position> batchUpdatePosition(long tenantId, long unitCompanyId, long unitId, List<Position> positionList);

    /**
     * 启用岗位
     *
     * @param position 岗位
     * @return 岗位
     */
    List<Position> enablePosition(Position position);

    /**
     * 禁用岗位
     *
     * @param position 岗位
     * @return 岗位
     */
    List<Position> disablePosition(Position position);
    
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
    List<PositionDTO> selectPlusPositionTree(Long tenantId,Long unitCompanyId,Long unitId, String keyWord);
    
    /**
     * 新版组织架构查询岗位下员工
     *
     * @param positionId 查询条件
     * @return 查询结果
     */
    Page<EmployeeDTO> pagePositionUsers(Long tenantId,Long positionId,String keyWord,String status,Integer primaryPositionFlag,PageRequest pageRequest);
}
