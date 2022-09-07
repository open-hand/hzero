package org.hzero.iam.infra.mapper;

import io.choerodon.mybatis.common.BaseMapper;
import org.apache.ibatis.annotations.Param;
import org.hzero.iam.api.dto.RoleAuthorityDTO;
import org.hzero.iam.domain.entity.DocType;

import java.util.List;

/**
 * 单据类型定义Mapper
 *
 * @author min.wang01@hand-china.com 2018-08-08 16:32:49
 */
public interface DocTypeMapper extends BaseMapper<DocType> {
    /**
     * 查询单据权限类型定义
     *
     * @param tenantId    租户ID
     * @param docTypeCode 单据类型编码
     * @param docTypeName 单据类型名称
     * @return 单据权限类型定义列表
     */
    List<DocType> pageDocType(@Param("tenantId") Long tenantId,
                              @Param("docTypeCode") String docTypeCode,
                              @Param("docTypeName") String docTypeName);

    /**
     * 查询单据类型定义明细
     *
     * @param tenantId      租户ID
     * @param docTypeId     单据类型定义ID
     * @param includeAssign 是否包含分配列表
     * @return 单据类型定义明细
     */
    DocType queryDocType(@Param("tenantId") Long tenantId,
                         @Param("docTypeId") Long docTypeId,
                         @Param("includeAssign") boolean includeAssign);

    /**
     * 查询单据类型定义及其分配维度
     *
     * @param tenantId   租户ID
     * @param docTypeIds 单据类型定义ID列表
     * @return 单据类型定义及其分配维度
     */
    List<DocType> queryDocTypeWithDimension(@Param("tenantId") Long tenantId,
                                            @Param("docTypeIds") List<Long> docTypeIds);

}
