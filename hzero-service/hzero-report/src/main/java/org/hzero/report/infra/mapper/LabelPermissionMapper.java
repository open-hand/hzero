package org.hzero.report.infra.mapper;

import org.apache.ibatis.annotations.Param;
import org.hzero.report.domain.entity.LabelPermission;

import java.util.List;

import io.choerodon.mybatis.common.BaseMapper;

/**
 * 标签权限Mapper
 *
 * @author fanghan.liu@hand-china.com 2019-12-02 10:27:44
 */
public interface LabelPermissionMapper extends BaseMapper<LabelPermission> {

    /**
     * 获取标签权限
     *
     * @param labelTemplateId 标签模板ID
     * @param tenantId        租户ID
     * @param flag            是否包含未指定角色记录
     * @return 查询结果
     */
    List<LabelPermission> selectLabelPermissions(@Param("labelTemplateId") Long labelTemplateId,
                                                 @Param("tenantId") Long tenantId,
                                                 @Param("flag") boolean flag);
}
