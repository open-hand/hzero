package org.hzero.platform.infra.mapper;

import io.choerodon.mybatis.common.BaseMapper;
import org.apache.ibatis.annotations.Param;
import org.hzero.platform.api.dto.PermissionRangeDTO;
import org.hzero.platform.domain.entity.PermissionRange;

import java.util.List;

/**
 * 屏蔽范围Mapper
 *
 * @author yunxiang.zhou01@hand-china.com 2018-07-23 15:01:44
 */
public interface PermissionRangeMapper extends BaseMapper<PermissionRange> {

    /**
     * 查询屏蔽范围
     *
     * @param permissionRange 屏蔽范围
     * @return 屏蔽范围dtoList
     */
    List<PermissionRangeDTO> selectPermissionRange(PermissionRange permissionRange);

    /**
     * 唯一索引查询数据
     *
     * @param tableName   拦截表名称
     * @param serviceName 拦截服务名
     * @param tenantId    租户ID
     * @param sqlId       SQL唯一标识
     * @return 数据权限规则
     */
    PermissionRange queryPermissionRule(@Param("tableName") String tableName,
                                        @Param("serviceName") String serviceName,
                                        @Param("tenantId") Long tenantId,
                                        @Param("sqlId") String sqlId);

    List<PermissionRange> listDisablePermissionRange(@Param("permissionRuleIdList") List<Long> permissionRuleIdList);

    List<PermissionRange> listEmptyRange(@Param("rangeIdList") List<Long> rangeIdList);
}
