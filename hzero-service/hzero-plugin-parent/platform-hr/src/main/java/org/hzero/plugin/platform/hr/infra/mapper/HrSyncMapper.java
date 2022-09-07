package org.hzero.plugin.platform.hr.infra.mapper;

import java.util.List;
import org.apache.ibatis.annotations.Param;
import org.hzero.plugin.platform.hr.domain.entity.HrSync;
import io.choerodon.mybatis.common.BaseMapper;

/**
 * hr基础数据同步外部系统Mapper
 *
 * @author minghui.qiu@hand-china.com 2019-10-14 21:20:14
 */
public interface HrSyncMapper extends BaseMapper<HrSync> {
    
    /**
     * 查询列表
     *
     * @param tenantId    租户ID
     * @param authType 编码
     * @param syncTypeCode 名称
     * @param enabledFlag 启用
     * @return 查询结果
     */
    List<HrSync> listHrSync(@Param("tenantId") Long tenantId,
                            @Param("syncTypeCode") String syncTypeCode,
                            @Param("authType") String authType,
                            @Param("enabledFlag") Integer enabledFlag);

    /**
     * 查询详情
     *
     * @param syncId 主键
     * @return 查询结果
     */
    HrSync getHrSyncById(@Param("syncId") Long syncId);

}
