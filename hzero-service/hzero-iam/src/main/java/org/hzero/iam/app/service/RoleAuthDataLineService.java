package org.hzero.iam.app.service;

import io.choerodon.core.domain.Page;
import io.choerodon.mybatis.pagehelper.domain.PageRequest;
import org.hzero.iam.api.dto.RoleAuthDataDTO;
import org.hzero.iam.domain.entity.RoleAuthData;
import org.hzero.iam.domain.entity.RoleAuthDataLine;

import java.util.List;

/**
 * 角色单据权限管理行应用服务
 *
 * @author qingsheng.chen@hand-china.com 2019-06-14 11:49:29
 */
public interface RoleAuthDataLineService {

    /**
     * 查询角色单据权限行
     *
     * @param roleAuthData 角色单据权限头
     * @param dataCode     数据编码
     * @param dataName     数据名称
     * @param pageRequest  分页信息
     * @return 角色单据权限行列表
     */
    List<RoleAuthDataLine> pageRoleAuthDataLine(RoleAuthData roleAuthData, String dataCode, String dataName, PageRequest pageRequest);

    /**
     * 新建角色单据权限行
     *
     * @param roleAuthDataDTO 角色单据权限
     * @return 角色单据权限
     */
    RoleAuthDataDTO createRoleAuthDataLine(RoleAuthDataDTO roleAuthDataDTO);

    /**
     * 删除角色单据权限行
     *
     * @param roleAuthDataLineList 角色单据权限行列表
     */
    void deleteRoleAuthDataLine(List<RoleAuthDataLine> roleAuthDataLineList);

    /**
     * 分页查询采购组织
     *
     * @param tenantId    租户ID
     * @param roleId      角色ID
     * @param dataCode    数据编码
     * @param dataName    数据名称
     * @param pageRequest 分页信息
     * @return 采购组织列表
     */
    Page<RoleAuthDataLine> pagePurOrg(Long tenantId, Long roleId, String dataCode, String dataName, PageRequest pageRequest);

    /**
     * 分页查询采购员
     *
     * @param tenantId    租户ID
     * @param roleId      角色ID
     * @param dataCode    数据编码
     * @param dataName    数据名称
     * @param pageRequest 分页信息
     * @return 采购员列表
     */
    Page<RoleAuthDataLine> pagePurAgent(Long tenantId, Long roleId, String dataCode, String dataName, PageRequest pageRequest);

    /**
     * 分页查询值集
     *
     * @param tenantId    租户ID
     * @param roleId      角色ID
     * @param dataCode    数据编码
     * @param dataName    数据名称
     * @param pageRequest 分页信息
     * @return 值集列表
     */
    Page<RoleAuthDataLine> pageLov(Long tenantId, Long roleId, String dataCode, String dataName, PageRequest pageRequest);

    /**
     * 分页查询值集视图
     *
     * @param tenantId    租户ID
     * @param roleId      角色ID
     * @param dataCode    数据编码
     * @param dataName    数据名称
     * @param pageRequest 分页信息
     * @return 值集视图列表
     */
    Page<RoleAuthDataLine> pageLovView(Long tenantId, Long roleId, String dataCode, String dataName, PageRequest pageRequest);

    /**
     * 批量插入角色单据权限行
     *
     * @param roleAuthDataLineList 角色单据权限行
     */
    void batchInsert(List<RoleAuthDataLine> roleAuthDataLineList);

    /**
     * 查询角色单据权限行
     *
     * @param authDataId 角色单据头ID
     * @param tenantId   租户ID
     * @return 角色单据权限行列表
     */
    List<RoleAuthDataLine> listRoleAuthDataLine(Long authDataId, Long tenantId);

    /**
     * 分页查询数据源
     *
     * @param tenantId    租户ID
     * @param roleId      角色ID
     * @param dataCode    数据编码
     * @param dataName    数据名称
     * @param pageRequest 分页信息
     * @return 数据源列表
     */
    Page<RoleAuthDataLine> pageDatasource(Long tenantId, Long roleId, String dataCode, String dataName, PageRequest pageRequest);

    /**
     * 分页查询数据源
     *
     * @param tenantId    租户ID
     * @param roleId      角色ID
     * @param groupCode    数据编码
     * @param groupName    数据名称
     * @param pageRequest 分页信息
     * @return 数据源列表
     */
    Page<RoleAuthDataLine> pageDataGroup(Long tenantId, Long roleId, String groupCode, String groupName, PageRequest pageRequest);}
