package org.hzero.iam.domain.repository;

import org.hzero.iam.domain.service.authdata.condition.AuthDataCondition;
import org.hzero.iam.domain.service.authdata.vo.AuthDataVo;

/**
 * 权限数据仓库接口
 *
 * @author bo.he02@hand-china.com 2020/06/05 11:52
 */
public interface AuthDataRepository {
    /**
     * 查询公司数据源信息
     *
     * @param authDataCondition 权限数据条件对象
     * @return 查询结果数据
     */
    AuthDataVo queryComDataSourceInfo(AuthDataCondition authDataCondition);

    /**
     * 查询业务实体数据源信息
     *
     * @param authDataCondition 权限数据条件对象
     * @return 查询结果数据
     */
    AuthDataVo queryOuDataSourceInfo(AuthDataCondition authDataCondition);

    /**
     * 查询库存组织数据源信息
     *
     * @param authDataCondition 权限数据条件对象
     * @return 查询结果数据
     */
    AuthDataVo queryInvOrgDataSourceInfo(AuthDataCondition authDataCondition);

    /**
     * 查询采购组织数据源信息
     *
     * @param authDataCondition 权限数据条件对象
     * @return 查询结果数据
     */
    AuthDataVo queryPurOrgDataSourceInfo(AuthDataCondition authDataCondition);

    /**
     * 查询采购员数据源信息
     *
     * @param authDataCondition 权限数据条件对象
     * @return 查询结果数据
     */
    AuthDataVo queryPurAgentDataSourceInfo(AuthDataCondition authDataCondition);

    /**
     * 查询采购品种数据源信息
     *
     * @param authDataCondition 权限数据条件对象
     * @return 查询结果数据
     */
    AuthDataVo queryPurCatDataSourceInfo(AuthDataCondition authDataCondition);

    /**
     * 查询数据源信息
     *
     * @param authDataCondition 权限数据条件对象
     * @return 查询结果数据
     */
    AuthDataVo queryDataSourceInfo(AuthDataCondition authDataCondition);

    /**
     * 查询值集数据源信息
     *
     * @param authDataCondition 权限数据条件对象
     * @return 查询结果数据
     */
    AuthDataVo queryLovDataSourceInfo(AuthDataCondition authDataCondition);

    /**
     * 查询值集视图数据源信息
     *
     * @param authDataCondition 权限数据条件对象
     * @return 查询结果数据
     */
    AuthDataVo queryLovViewDataSourceInfo(AuthDataCondition authDataCondition);
}
