package org.hzero.iam.domain.repository;

import java.util.List;

import javax.annotation.Nonnull;

import org.hzero.iam.domain.entity.DocTypeDimension;
import org.hzero.mybatis.base.BaseRepository;

import io.choerodon.core.domain.Page;
import io.choerodon.mybatis.pagehelper.domain.PageRequest;

/**
 * 单据类型维度资源库
 *
 * @author zifeng.ding@hand-china.com 2019-09-18 11:48:01
 */
public interface DocTypeDimensionRepository extends BaseRepository<DocTypeDimension> {

    /**
     * @param organizationId  租户id
     * @param dimensionCode   维度编码
     * @param dimensionName   维度名称
     * @param dimensionType   维度类型
     * @param valueSourceType 值来源类型
     * @param enabledFlag     是否启用
     * @param pageRequest     分页
     * @return 分页数据
     */
    Page<DocTypeDimension> pageAndSortDocTypeDimension(Long organizationId, String dimensionCode, String dimensionName,
                                                       PageRequest pageRequest, String dimensionType, String valueSourceType, Integer enabledFlag);

    List<DocTypeDimension> listBizDocTypeDimension(long tenantId);

    /**
     * @param dimensionId 单据类型维度主键
     * @return 单据类型维度
     */
    DocTypeDimension selectDocTypeDimensionById(Long dimensionId);

    /**
     * @param dimensionCodes 单据类型维度编码集合
     * @return 单据类型维度集合
     */
    List<DocTypeDimension> listDocTypeDimensionBydimensionCodes(List<String> dimensionCodes);

    /**
     * 依据单据id 查维度
     *
     * @param docTypeId 单据id
     * @return 维度值
     */
    List<DocTypeDimension> listDocTypeDimensionByDocTypeId(Long docTypeId);

    /**
     * 通过单据类型维度编码寻找单据维度对象
     *
     * @param dimensionCode 单据类型维度编码
     * @return 单据维度对象
     */
    DocTypeDimension findDocTypeDimensionByDimensionCode(@Nonnull String dimensionCode);

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
