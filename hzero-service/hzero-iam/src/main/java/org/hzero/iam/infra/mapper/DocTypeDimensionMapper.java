package org.hzero.iam.infra.mapper;

import java.util.List;

import org.apache.ibatis.annotations.Param;
import org.hzero.iam.domain.entity.DocTypeDimension;

import io.choerodon.mybatis.common.BaseMapper;

/**
 * 单据类型维度Mapper
 *
 * @author zifeng.ding@hand-china.com 2019-09-18 11:48:01
 */
public interface DocTypeDimensionMapper extends BaseMapper<DocTypeDimension> {

    /**
     * 单据类型维度
     *
     * @param dimensionId 单据类型维度id
     * @return 客户端明细
     */
    DocTypeDimension selectDocTypeDimensionById(Long dimensionId);

    /**
     * 单据类型维度列表
     *
     * @param dimensionCodes 单据类型维度编码集合
     * @return 单据类型维度列表
     */
    List<DocTypeDimension> listDocTypeDimensionBydimensionCodes(@Param("dimensionCodes") List<String> dimensionCodes);

    /**
     * 查询数据权限
     * @param docTypeId
     * @return
     */
    List<DocTypeDimension> listDocTypeDimensionByDocType(@Param("docTypeId") Long docTypeId);

    List<DocTypeDimension> listBizDocTypeDimension(@Param("tenantId") long tenantId);

    /**
     * 通过维度编码查询维度关联的启用单据Ids
     *
     * @param docTypeDimension 单据维度
     * @return 单据Ids
     */
    List<Long> selectDocTypeIdByDimensionCode(DocTypeDimension docTypeDimension);

    /**
     * 获取维度禁用但单据仍为启用的数据权限Id信息，该种数据权限需被禁用掉
     *
     * @return 需禁用的数据权限Id
     */
    List<Long> selectDisabledPermissionRuleId();
}
