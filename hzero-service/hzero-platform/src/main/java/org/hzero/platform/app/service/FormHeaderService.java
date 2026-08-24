package org.hzero.platform.app.service;

import org.hzero.platform.domain.entity.FormHeader;

/**
 * 表单配置头应用服务
 *
 * @author xiaoyu.zhao@hand-china.com 2019-07-11 17:50:08
 */
public interface FormHeaderService {

    /**
     * 删除表单配置头参数
     *
     * @param formHeader 需要删除的参数
     */
    void deleteFormHeader(FormHeader formHeader);

    /**
     * 创建表单配置头参数
     *
     * @param formHeader 创建参数
     * @return 创建结果
     */
    FormHeader createFormHeader(FormHeader formHeader);

    /**
     * 更新表单头配置参数
     *
     * @param formHeader 更新参数
     * @return 更新结果
     */
    FormHeader updateFormHeader(FormHeader formHeader);
}
