package org.hzero.iam.domain.repository;

import java.util.List;
import java.util.Set;

import io.choerodon.core.domain.Page;
import io.choerodon.mybatis.pagehelper.domain.PageRequest;
import org.hzero.mybatis.base.BaseRepository;
import org.hzero.iam.domain.entity.DocTypeAssign;

/**
 * 单据类型分配资源库
 *
 * @author min.wang01@hand-china.com 2018-08-08 19:55:53
 */
public interface DocTypeAssignRepository extends BaseRepository<DocTypeAssign> {

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
     * 查询当前单据类型分配
     *
     * @param tenantId    租户ID
     * @param docTypeId   单据类型定义ID
     * @return 单据类型分配列表
     */
    List<DocTypeAssign> listAssign(Long tenantId, long docTypeId);

    /**
     * 删除单据类型的租户分配信息
     * 
     * @param docTypeId
     * @return
     */
    int deleteByDocTypeId(Long docTypeId);


    /**
     * 查询单据类型下的租户分配
     * 
     * @param docTypeId
     * @return
     */
    List<DocTypeAssign> selectByDocTypeId(Long docTypeId);

    /**
     * 获取租户级单据权限下分配的租户Ids
     *
     * @param tenantDocIds 租户级仅配置单据权限Ids
     * @return Set<Long> 分配的租户Ids
     */
    Set<Long> getAssignTenantIdsByDocIds(List<Long> tenantDocIds);
}
