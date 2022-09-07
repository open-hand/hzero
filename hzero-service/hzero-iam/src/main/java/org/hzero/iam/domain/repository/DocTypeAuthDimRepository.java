package org.hzero.iam.domain.repository;

import org.hzero.iam.domain.entity.DocTypeAuthDim;
import org.hzero.mybatis.base.BaseRepository;

import java.util.List;

/**
 * 单据类型权限维度定义资源库
 *
 * @author min.wang01@hand-china.com 2018-08-08 19:55:53
 */
public interface DocTypeAuthDimRepository extends BaseRepository<DocTypeAuthDim> {
    /**
     * 查询当前单据类型下的权限维度
     *
     * @param tenantId  租户ID
     * @param docTypeId 单据权限类型ID
     * @return 单据权限分配维度列表
     */
    List<DocTypeAuthDim> listAuthDim(Long tenantId, long docTypeId);
}
