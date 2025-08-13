package org.hzero.iam.app.service;

import io.choerodon.core.domain.Page;
import io.choerodon.mybatis.pagehelper.domain.PageRequest;
import org.hzero.iam.api.dto.RoleAuthorityDTO;
import org.hzero.iam.domain.entity.DocType;

import java.util.List;

/**
 * 单据类型定义应用服务
 *
 * @author min.wang01@hand-china.com 2018-08-08 16:32:49
 */
public interface DocTypeService {

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
     * 新增单据类型定义
     *
     * @param docType 单据类型定义
     * @return 单据类型定义
     */
    DocType createDocType(DocType docType);

    /**
     * 更新单据类型定义
     *
     * @param docType 单据类型定义
     * @return 单据类型定义
     */
    DocType updateDocType(DocType docType);


    /**
     * 生成单据屏蔽规则
     *
     * @param tenantId   租户ID
     * @param docTypeIds 单据类型定义主键列表
     */
    void generateShieldRule(Long tenantId, List<Long> docTypeIds);


    /**
     * 修复单据权限数据（禁用的单据权限不修复）
     */
    void fixDocTypeData();
}
