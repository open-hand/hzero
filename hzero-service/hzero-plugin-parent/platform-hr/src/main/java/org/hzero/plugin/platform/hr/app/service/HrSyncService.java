package org.hzero.plugin.platform.hr.app.service;

import org.hzero.plugin.platform.hr.domain.entity.HrSync;
import org.hzero.plugin.platform.hr.domain.entity.HrSyncLog;
import org.springframework.http.ResponseEntity;

import io.choerodon.core.domain.Page;
import io.choerodon.mybatis.pagehelper.domain.PageRequest;

/**
 * hr基础数据同步外部系统应用服务
 *
 * @author minghui.qiu@hand-china.com 2019-10-14 21:20:14
 */
public interface HrSyncService {

    /**
     * 增量同步组织架构
     *
     * @param hrSyncDto          同步第三方系统配置
     * @param useGeneratedUnitId 是否使用生成的部门id，true: 部门id第三方平台生成，false：部门id使用指定id
     */
    void syncNow(HrSync hrSyncDto, Boolean useGeneratedUnitId);

    ResponseEntity<HrSyncLog> logDetail(Long logId);

    Page<HrSync> pageAndSort(PageRequest pageRequest, Long tenantId, HrSync hrSync);

    HrSync insertHrSync(HrSync dto);

    HrSync selectHrSync(Long syncId);

    HrSync updateHrSync(HrSync dto);

    int deleteHrSync(HrSync dto);

    /**
     * 第三方组织信息同步到平台
     *
     * @param tenantId  租户ID
     * @param hrSyncDto hr基础数据同步外部系统
     */
    void syncToLocal(Long tenantId, HrSync hrSyncDto);

}
