package org.hzero.platform.domain.repository;

import io.choerodon.mybatis.pagehelper.domain.PageRequest;
import org.hzero.mybatis.base.BaseRepository;
import org.hzero.platform.api.dto.PermissionRangeDTO;
import org.hzero.platform.domain.entity.PermissionRange;

import java.util.List;

/**
 * 屏蔽范围资源库
 *
 * @author yunxiang.zhou01@hand-china.com 2018-07-23 15:01:44
 */
public interface PermissionRangeRepository extends BaseRepository<PermissionRange> {

    /**
     * 查询屏蔽范围
     *
     * @param pageRequest 分页
     * @param permissionRange 屏蔽范围
     * @return 屏蔽范围dtoList
     */
    List<PermissionRangeDTO> selectPermissionRange(PageRequest pageRequest, PermissionRange permissionRange);

    List<PermissionRange> listDisablePermissionRange(List<Long> permissionRuleIdList);

    List<PermissionRange> listEmptyRange(List<Long> rangeIdList);

    /**
     * 新增屏蔽范围
     *
     * @param permissionRange 屏蔽范围
     * @return 屏蔽范围
     */
    PermissionRange insertPermissionRange(PermissionRange permissionRange);

    /**
     * 更新屏蔽范围，同时更新redis缓存
     *
     * @param permissionRange 屏蔽范围
     * @return 屏蔽范围
     */
    PermissionRange updatePermissionRange(PermissionRange permissionRange);

    /**
     * 删除数据屏蔽范围并删除redis
     *
     * @param rangeId 数据屏蔽范围id
     * @param tenantId 租户id
     */
    default void deletePermissionRange(Long rangeId, Long tenantId) {
        deletePermissionRange(rangeId, tenantId, true);
    }

    /**
     * 删除数据屏蔽范围并删除redis
     *
     * @param rangeId 数据屏蔽范围id
     * @param tenantId 租户id
     */
    void deletePermissionRange(Long rangeId, Long tenantId, boolean validEditable);

    /**
     * 初始化所有数据屏蔽数据到redis中
     */
    void initAllData();

    /**
     * 唯一索引查询数据
     *
     * @param tableName   拦截表名称
     * @param serviceName 拦截服务名
     * @param tenantId    租户ID
     * @param sqlId       SQL唯一标识
     * @return 数据权限范围
     */
    PermissionRange queryPermissionRange(String tableName, String serviceName, Long tenantId, String sqlId);
}
