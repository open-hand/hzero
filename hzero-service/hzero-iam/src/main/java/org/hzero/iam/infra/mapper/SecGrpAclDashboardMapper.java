package org.hzero.iam.infra.mapper;

import java.util.List;

import org.apache.ibatis.annotations.Param;

import io.choerodon.mybatis.common.BaseMapper;

import org.hzero.iam.domain.entity.SecGrpAclDashboard;

/**
 * 安全组工作台配置Mapper
 *
 * @author xingxing.wu@hand-china.com 2019-10-28 10:00:59
 */
public interface SecGrpAclDashboardMapper extends BaseMapper<SecGrpAclDashboard> {

    /**
     * 查询安全组下工作台
     */
    List<SecGrpAclDashboard> selectSecGrpDashboard(SecGrpAclDashboard queryDTO);

    /**
     * 查询安全组可分配的工作台
     */
    List<SecGrpAclDashboard> selectSecGrpAssignableDashboard(SecGrpAclDashboard queryDTO);

    /**
     * 查询供工作台配置
     *
     * @param secGrpIds 安全组ID列表
     */
    List<SecGrpAclDashboard> selectSecGrpAclDashboardByGrpIds(@Param("secGrpIds") List<Long> secGrpIds);

}
