package org.hzero.boot.platform.form.domain.repository;

import java.util.List;

import org.hzero.boot.platform.form.domain.vo.FormLineVO;

/**
 * 基础表单配置行资源库，封装添加和获取表单配置行缓存逻辑
 *
 * @author xiaoyu.zhao@hand-china.com 2019/11/22 10:32
 */
public interface BaseFormLineRepository {

    /**
     * 添加表单配置行缓存
     *
     * @param formHeaderCode 表单配置头编码
     * @param formLineVO     缓存参数
     */
    void saveFormLineCache(String formHeaderCode, FormLineVO formLineVO);

    /**
     * 删除表单配置行缓存
     *
     * @param formHeaderCode 表单配置头编码
     * @param tenantId       租户Id
     * @param itemCode       表单行编码
     */
    void deleteFormLineCache(String formHeaderCode, Long tenantId, String itemCode);

    /**
     * 获取单条表单配置行缓存
     *
     * @param formHeaderCode 表单配置头编码
     * @param tenantId       租户Id
     * @param itemCode       表单行编码
     * @return FormLineVO
     */
    FormLineVO getOneFromLineCache(String formHeaderCode, Long tenantId, String itemCode);

    /**
     * 获取指定表单头下所有的表单行缓存信息
     *
     * @param formHeaderCode 表单配置头编码
     * @param tenantId       租户Id
     * @return List<FormLineVO>
     */
    List<FormLineVO> getAllFormLineCache(String formHeaderCode, Long tenantId);

    /**
     * 翻译表单行数据
     *
     * @param formHeaderCode 表单头编码
     * @param tenantId 租户Id
     * @param translateJson 需翻译的Json数据
     * @return 翻译之后的Json数据
     */
    String translateAndProcessFormLineData(String formHeaderCode, Long tenantId, String translateJson);
}
