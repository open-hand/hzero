package org.hzero.iam.app.service;

import org.hzero.iam.domain.entity.DocTypeAuthDim;

import java.util.List;

/**
 * 单据类型权限维度定义应用服务
 *
 * @author min.wang01@hand-china.com 2018-08-08 19:55:53
 */
public interface DocTypeAuthDimService {

    /**
     * 查询当前单据类型下的权限维度
     *
     * @param tenantId  租户ID
     * @param docTypeId 单据权限类型ID
     * @return 单据权限分配维度列表
     */
    List<DocTypeAuthDim> listAuthDim(Long tenantId, long docTypeId);

    /**
     * 保存单据类型权限维度定义
     *
     * @param tenantId        租户ID
     * @param docTypeId       单据权限类型ID
     * @param docTypeAuthDims 单据权限类型维度列表
     * @return 单据类型权限维度定义
     */
    List<DocTypeAuthDim> saveAuthDim(Long tenantId, long docTypeId, List<DocTypeAuthDim> docTypeAuthDims);
}
