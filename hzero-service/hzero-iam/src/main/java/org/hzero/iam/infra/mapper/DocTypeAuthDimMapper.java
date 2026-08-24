package org.hzero.iam.infra.mapper;

import io.choerodon.mybatis.common.BaseMapper;
import org.apache.ibatis.annotations.Param;
import org.hzero.iam.domain.entity.DocTypeAuthDim;

import java.util.List;

/**
 * 单据类型权限维度定义Mapper
 *
 * @author min.wang01@hand-china.com 2018-08-08 19:55:53
 */
public interface DocTypeAuthDimMapper extends BaseMapper<DocTypeAuthDim> {

    /**
     * 查询当前单据类型下的权限维度
     *
     * @param tenantId  租户ID
     * @param docTypeId 单据权限类型ID
     * @return 单据权限分配维度列表
     */
    List<DocTypeAuthDim> listAuthDim(@Param("tenantId") Long tenantId,
                                     @Param("docTypeId") long docTypeId);

}
