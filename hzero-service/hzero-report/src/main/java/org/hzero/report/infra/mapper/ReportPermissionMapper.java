package org.hzero.report.infra.mapper;

import java.util.List;

import org.apache.ibatis.annotations.Param;
import org.hzero.report.domain.entity.ReportPermission;

import io.choerodon.mybatis.common.BaseMapper;

/**
 * 报表权限Mapper
 *
 * @author xianzhi.chen@hand-china.com 2018-11-29 10:57:31
 */
public interface ReportPermissionMapper extends BaseMapper<ReportPermission> {

    /**
     * 查询报表权限
     *
     * @param reportId 报表Id
     * @param tenantId 租户Id
     * @param flag     是否包含未指定角色记录
     * @return 并发请求权限列表
     */
    List<ReportPermission> selectReportPermissions(@Param("reportId") Long reportId,
                                                   @Param("tenantId") Long tenantId,
                                                   @Param("flag") boolean flag);

    /**
     * 查询引用数量
     *
     * @param tenantId 租户Id
     * @param roleId   角色Id
     * @param reportId 报表Id
     * @return 数量
     */
    int selectCountByUnique(@Param("tenantId") Long tenantId,
                            @Param("roleId") Long roleId,
                            @Param("reportId") Long reportId);

}
