package org.hzero.platform.app.service;

import java.util.List;

import org.hzero.boot.platform.lov.dto.LovValueDTO;
import org.hzero.platform.domain.entity.FormLine;

/**
 * 表单配置行应用服务
 *
 * @author xiaoyu.zhao@hand-china.com 2019-07-11 17:50:08
 */
public interface FormLineService {

    /**
     * 创建表单配置行
     *
     * @param formLine 新建参数
     * @return 创建结果
     */
    FormLine createFormLine(FormLine formLine);

    /**
     * 更新表单配置行
     *
     * @param formLine 更新参数
     * @return 更新结果
     */
    FormLine updateFormLine(FormLine formLine);

    /**
     * 删除表单配置行
     *
     * @param formLine 删除参数
     */
    void deleteFormLine(FormLine formLine);

    /**
     * 按照表单编码查询表单配置行
     *
     * @param formCode form编码
     * @param tenantId 租户Id
     * @return 表单配置行
     */
    List<FormLine> listFormLineByHeaderCode(String formCode, Long tenantId);

    /**
     * 按照表单头Id查询表单配置行信息
     *
     * @param formHeaderId 表单头Id
     * @param tenantId 租户Id
     * @return 表单配置行
     */
    List<FormLine> listFormLineByHeaderId(Long formHeaderId, Long tenantId);

    /**
     * 翻译值集/值集视图类型表单值
     *
     * @param organizationId 租户Id
     * @param formLineId 表单行Id
     * @param params 翻译参数
     * @return 翻译结果
     */
    List<LovValueDTO> translateValueSet(Long organizationId, Long formLineId, List<String> params);
}
