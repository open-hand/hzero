package org.hzero.plugin.platform.hr.app.service;

import java.util.List;
import java.util.Set;

import org.hzero.plugin.platform.hr.api.dto.EmployeeDTO;
import org.hzero.plugin.platform.hr.api.dto.Receiver;
import org.hzero.plugin.platform.hr.api.dto.UnitDTO;
import org.hzero.plugin.platform.hr.api.dto.UnitUserDTO;
import org.hzero.plugin.platform.hr.domain.entity.Unit;

import io.choerodon.core.domain.Page;
import io.choerodon.mybatis.pagehelper.domain.PageRequest;

/**
 * 部门应用服务
 *
 * @author gaokuo.dai@hand-china.com 2018-06-21 17:05:02
 */
public interface UnitService {

    /**
     * 树形查询所有公司级组织
     *
     * @param tenantId 租户ID
     * @return 树
     */
    List<UnitDTO> treeCompany(Long tenantId);

    /**
     * 树形查询所有部门级组织
     *
     * @param tenantId      租户ID
     * @param unitCompanyId 公司级组织ID
     * @param enabledFlag   是否启用
     * @return 树
     */
    List<UnitDTO> treeDepartment(Long tenantId, Long unitCompanyId, Integer enabledFlag);

    /**
     * 批量插入部门
     *
     * @param units 待插入的部门
     * @return 插入后的部门
     */
    List<Unit> batchInsertSelective(List<Unit> units);

    /**
     * 更新部门
     *
     * @param unit 待更新的部门
     * @return 更新后的部门
     */
    Unit updateByPrimaryKey(Unit unit);

    /**
     * 禁用部门
     *
     * @param unitId              部门ID
     * @param tenantId            租户ID
     * @param objectVersionNumber 版本号
     * @return 是否成功
     */
    boolean disableUnit(Long unitId, Long tenantId, Long objectVersionNumber);

    /**
     * 启用部门
     *
     * @param unitId              部门ID
     * @param tenantId            租户ID
     * @param objectVersionNumber 版本号
     * @return 是否成功
     */
    boolean enableUnit(Long unitId, Long tenantId, Long objectVersionNumber);

    /**
     * 查询某个组织ID是否启用
     *
     * @param unitId 组织ID
     * @return 是否启用
     */
    boolean checkEnabled(Long unitId);

    /**
     * 条件查询公司<br/>
     * 如果有模糊搜索需求,查询结果需要组织成树形返回
     *
     * @param queryParam 查询条件
     * @return 查询结果
     */
    List<UnitDTO> selectCompany(Unit queryParam);

    /**
     * 条件查询部门<br/>
     * 如果有模糊搜索需求,查询结果需要组织成树形返回
     *
     * @param queryParam 查询条件
     * @return 查询结果
     */
    List<UnitDTO> selectDepartment(Unit queryParam);


    /**
     * 获取所有部门
     *
     * @param tenantId    租户id
     * @param enabledFlag 是否有效
     * @return 部门组织数据
     */
    List<UnitDTO> selectAllDepartment(Long tenantId, Integer enabledFlag);

    /**
     * 批量查询 部门 unit
     *
     * @param tenantId      tenantId
     * @param parentUnitIds unit父id
     * @param unitIds       unitId
     * @return unit 集合
     */
    List<UnitDTO> selectDepartment(Long tenantId, List<Long> parentUnitIds, List<Long> unitIds);

    /**
     * 按类型获取部门
     *
     * @param typeCodes 指定的类型
     * @param tenantId  租户Id
     * @return List<UnitDTO>
     */
    List<UnitDTO> selectByTypeCodes(String[] typeCodes, Long tenantId);

    /**
     * 拉取最近更新过的记录
     *
     * @param tenantId 租户ID
     * @param before   过去多久内(单位：ms，默认5min)
     * @return 最近更新过的记录
     */
    List<Unit> listRecentUnit(Long tenantId, long before);

    /**
     * 树形懒加载查询公司级组织
     *
     * @param tenantId 租户Id
     * @param unitId   部门Id
     * @return 树形参数
     */
    List<UnitDTO> lazyTreeCompany(Long tenantId, Long unitId);

    /**
     * 分页打平查询组织架构信息
     *
     * @param unit        查询条件
     * @param pageRequest 分页参数
     * @return 分页查询结果
     */
    Page<UnitDTO> pageCompanyUnits(Unit unit, PageRequest pageRequest);

    /**
     * 新版组织架构公司树查询
     * 如果有模糊搜索需求,查询结果需要组织成树形返回
     *
     * @param keyWord 查询条件
     * @return 查询结果
     */
    List<UnitDTO> selectPlusCompany(String keyWord, Long tenantId);

    /**
     * 新版组织架构公司树查询
     * 如果有模糊搜索需求,查询结果需要组织成树形返回
     *
     * @param keyWord 查询条件
     * @return 查询结果
     */
    List<UnitDTO> selectPlusDepartment(String keyWord, Long unitCompanyId, Long tenantId);

    /**
     * 新版组织架构查询组织下员工
     *
     * @param unitId 查询条件
     * @return 查询结果
     */
    Page<EmployeeDTO> pageUnitUsers(Long tenantId, Long unitId, String keyWord, String status, Integer primaryPositionFlag, PageRequest pageRequest);

    /**
     * 新版组织架构查询组织下部门
     *
     * @param unitId 查询条件
     * @return 查询结果
     */
    Page<UnitDTO> pageUnitDept(Long tenantId, Long unitId, String keyWord, Integer enabledFlag, PageRequest pageRequest);

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
     * @param tenantId    租户Id
     * @param unitId      组织Id
     * @param unitCode    组织编码
     * @param unitName    组织名称
     * @param pageRequest 分页参数
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
