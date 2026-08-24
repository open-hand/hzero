package org.hzero.iam.domain.repository;

import java.util.List;

import org.hzero.iam.domain.entity.DocType;
import org.hzero.mybatis.base.BaseRepository;

import io.choerodon.core.domain.Page;
import io.choerodon.mybatis.pagehelper.domain.PageRequest;

/**
 * 单据类型定义资源库
 *
 * @author min.wang01@hand-china.com 2018-08-08 16:32:49
 */
public interface DocTypeRepository extends BaseRepository<DocType> {
    /**
     * 分页查询单据权限类型定义
     *
     * @param tenantId    租户ID
     * @param docTypeCode 单据类型编码
     * @param docTypeName 单据类型名称
     * @param pageRequest 分页信息
     * @return 单据权限类型定义列表
     */
    Page<DocType> pageDocType(Long tenantId, String docTypeCode, String docTypeName, PageRequest pageRequest);

    /**
     * 查询单据类型定义明细
     *
     * @param tenantId      租户ID
     * @param docTypeId     单据类型定义ID
     * @param includeAssign 是否包含分配列表
     * @return 单据类型定义明细
     */
    DocType queryDocType(Long tenantId, Long docTypeId, boolean includeAssign);

    /**
     * 查询单据类型定义及其分配维度
     *
     * @param tenantId   租户ID
     * @param docTypeIds 单据类型定义ID列表
     * @return 单据类型定义及其分配维度
     */
    List<DocType> queryDocTypeWithDimension(Long tenantId, List<Long> docTypeIds);
}
