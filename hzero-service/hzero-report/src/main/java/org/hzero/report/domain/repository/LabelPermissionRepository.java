package org.hzero.report.domain.repository;

import org.hzero.mybatis.base.BaseRepository;
import org.hzero.report.domain.entity.LabelPermission;

import io.choerodon.core.domain.Page;
import io.choerodon.mybatis.pagehelper.domain.PageRequest;

/**
 * 标签权限资源库
 *
 * @author fanghan.liu@hand-china.com 2019-12-02 10:27:44
 */
public interface LabelPermissionRepository extends BaseRepository<LabelPermission> {

    /**
     * 分页查询标签权限
     *
     * @param labelTemplateId 标签模板ID
     * @param tenantId        租户ID
     * @param flag            是否包含未指定角色记录
     * @param pageRequest     分页请求
     * @return 分页数据
     */
    Page<LabelPermission> pageLabelPermission(Long labelTemplateId, Long tenantId, boolean flag, PageRequest pageRequest);
}
