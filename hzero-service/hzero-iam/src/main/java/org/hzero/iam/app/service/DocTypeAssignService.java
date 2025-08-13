package org.hzero.iam.app.service;

import java.util.List;
import java.util.Set;

import org.hzero.iam.domain.entity.DocTypeAssign;

import io.choerodon.core.domain.Page;
import io.choerodon.mybatis.pagehelper.domain.PageRequest;

/**
 * 单据类型分配应用服务
 *
 * @author min.wang01@hand-china.com 2018-08-08 19:55:53
 */
public interface DocTypeAssignService {

    /**
     * 查询当前单据类型分配
     *
     * @param tenantId  租户ID
     * @param docTypeId 单据类型定义ID
     * @return 单据类型分配列表
     */
    List<DocTypeAssign> listAssign(Long tenantId, Long docTypeId);

    /**
     * 分页查询当前单据类型分配
     *
     * @param tenantId    租户ID
     * @param docTypeId   单据类型定义ID
     * @param pageRequest 分页信息
     * @return 单据类型分配列表
     */
    Page<DocTypeAssign> pageAssign(Long tenantId, long docTypeId, PageRequest pageRequest);

    /**
     * 新增单据类型定义分配
     *
     * @param docTypeId      单据类型ID
     * @param tenantId       租户ID
     * @param docTypeAssigns 单据类型定义分配列表
     */
    void createAssign(Long docTypeId, Long tenantId, List<DocTypeAssign> docTypeAssigns);

    /**
     * 新增单据类型定义分配
     *
     * @param docTypeId      单据类型ID
     * @param docTypeAssigns 单据类型定义分配列表
     */
    void createAssign(long docTypeId, List<DocTypeAssign> docTypeAssigns);

    /**
     * 更新单据类型定义分配
     *
     * @param docTypeId      单据类型ID
     * @param docTypeAssigns 单据类型定义分配列表
     */
    void updateAssign(long docTypeId, List<DocTypeAssign> docTypeAssigns);

    /**
     * 更新单据类型定义分配
     *
     * @param docTypeId      单据类型ID
     * @param tenantId       租户ID
     * @param docTypeAssigns 单据类型定义分配列表
     */
    void updateAssign(Long docTypeId, Long tenantId, List<DocTypeAssign> docTypeAssigns);

    /**
     * 获取租户级单据权限下分配的租户Ids
     *
     * @param tenantDocIds 租户级仅配置单据权限Ids
     * @return Set<Long> 分配的租户Ids
     */
    Set<Long> getAssignTenantIdsByDocIds(List<Long> tenantDocIds);
}
