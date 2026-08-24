package org.hzero.plugin.platform.hr.domain.repository;

import org.hzero.mybatis.base.BaseRepository;
import org.hzero.plugin.platform.hr.domain.entity.HrSync;

import io.choerodon.core.domain.Page;
import io.choerodon.mybatis.pagehelper.domain.PageRequest;

/**
 * hr基础数据同步外部系统资源库
 *
 * @author minghui.qiu@hand-china.com 2019-10-14 21:20:14
 */
public interface HrSyncRepository extends BaseRepository<HrSync> {
    
    /**
     * 分页查询列表
     * @param pageRequest 分页属性
     * @param tenantId    租户ID
     * @param authType 编码
     * @param syncTypeCode 名称
     * @param enabledFlag 启用
     * @return 查询结果
     */
    Page<HrSync> listHrSync(PageRequest pageRequest, Long tenantId,String syncTypeCode,String authType, Integer enabledFlag);

    /**
     * 查询详情
     *
     * @param syncId 主键
     * @return 查询结果
     */
    HrSync getHrSyncById(Long syncId);
    
}
