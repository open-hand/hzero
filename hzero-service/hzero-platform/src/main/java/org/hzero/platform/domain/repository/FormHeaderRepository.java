package org.hzero.platform.domain.repository;

import io.choerodon.core.domain.Page;
import io.choerodon.mybatis.pagehelper.domain.PageRequest;
import org.hzero.mybatis.base.BaseRepository;
import org.hzero.platform.domain.entity.FormHeader;

import java.util.List;

/**
 * 表单配置头资源库
 *
 * @author xiaoyu.zhao@hand-china.com 2019-07-11 17:50:08
 */
public interface FormHeaderRepository extends BaseRepository<FormHeader> {

    /**
     * 分页查询表单配置头信息列表
     *
     * @param pageRequest 分页参数
     * @param formHeader  查询条件
     * @return 分页结果集
     */
    Page<FormHeader> pageFormHeaders(PageRequest pageRequest, FormHeader formHeader);

    /**
     * 查询表单配置头信息明细
     *
     * @param formHeaderId 表单头Id
     * @return FormHeader
     */
    FormHeader selectFormHeaderDetails(Long formHeaderId);

    /**
     * 校验参数是否唯一
     *
     * @param formHeader 校验参数
     */
    void checkRepeat(FormHeader formHeader);

    /**
     * 查询启用的表单配置头列表
     *
     * @param tenantId 租户Id
     * @return 返回结果集
     */
    List<FormHeader> listEnabledFormHeaders(Long tenantId);
}
