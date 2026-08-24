package org.hzero.boot.platform.form;

import org.hzero.boot.platform.form.constraints.FormConstraintsCheck;
import org.hzero.boot.platform.form.domain.repository.BaseFormLineRepository;
import org.hzero.boot.platform.form.domain.vo.FormLineVO;
import org.hzero.boot.platform.form.result.ConstraintsCheckResult;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * 表单配置客户端
 *
 * @author liufanghan 2019/11/22 9:13
 */
@Component
public class FormClient {

    private FormConstraintsCheck formConstraintsCheck;
    private BaseFormLineRepository formLineRepository;

    public FormClient(FormConstraintsCheck formConstraintsCheck, BaseFormLineRepository formLineRepository) {
        this.formConstraintsCheck = formConstraintsCheck;
        this.formLineRepository = formLineRepository;
    }

    /**
     * 校验配置value是否匹配值约束
     *
     * @param configValue 配置值 JSON格式
     * @param formCode    表单编码
     * @param tenantId    租户ID
     * @return 检查结果
     */
    public ConstraintsCheckResult checkConfigValue(String configValue, String formCode, Long tenantId) {
        return formConstraintsCheck.checkConfigValue(configValue, formLineRepository.getAllFormLineCache(formCode, tenantId));
    }

    /**
     * 校验配置value是否匹配值约束和是否可更新约束
     *
     * @param oldConfigValue 旧配置值 JSON格式
     * @param newConfigValue 新配置值 JSON格式
     * @param formCode       表单编码
     * @param tenantId       租户ID
     * @return 检查结果
     */
    public ConstraintsCheckResult checkConfigValue(String oldConfigValue, String newConfigValue, String formCode, Long tenantId) {
        return formConstraintsCheck.checkConfigValue(oldConfigValue, newConfigValue, formLineRepository.getAllFormLineCache(formCode, tenantId));
    }

    /**
     * 添加表单配置行缓存
     *
     * @param formHeaderCode 表单配置头编码
     * @param formLineVO     缓存参数
     */
    public <T> void saveFormLineCache(String formHeaderCode, T formLineVO) {
        FormLineVO vo = new FormLineVO();
        BeanUtils.copyProperties(formLineVO, vo);
        formLineRepository.saveFormLineCache(formHeaderCode, vo);
    }

    /**
     * 删除表单配置行缓存
     *
     * @param formHeaderCode 表单配置头编码
     * @param tenantId       租户Id
     * @param itemCode       表单行编码
     */
    public void deleteFormLineCache(String formHeaderCode, Long tenantId, String itemCode) {
        formLineRepository.deleteFormLineCache(formHeaderCode, tenantId, itemCode);
    }

    /**
     * 获取单条表单配置行缓存
     *
     * @param formHeaderCode 表单配置头编码
     * @param tenantId       租户Id
     * @param itemCode       表单行编码
     * @return FormLineVO
     */
    public FormLineVO getOneFromLineCache(String formHeaderCode, Long tenantId, String itemCode) {
        return formLineRepository.getOneFromLineCache(formHeaderCode, tenantId, itemCode);
    }

    /**
     * 获取指定表单头下所有的表单行缓存信息
     *
     * @param formHeaderCode 表单配置头编码
     * @param tenantId       租户Id
     * @return List<FormLineVO>
     */
    public List<FormLineVO> getAllFormLineCache(String formHeaderCode, Long tenantId) {
        return formLineRepository.getAllFormLineCache(formHeaderCode, tenantId);
    }

    /**
     * 获取指定表单头下所有的表单行缓存信息
     *
     * @param formHeaderCode 表单配置头编码
     * @param tenantId       租户Id
     * @param translateJson  需转换的Json
     * @return 翻译之后的json结果
     */
    public String translateAndProcessFormLineData(String formHeaderCode, Long tenantId, String translateJson) {
        return formLineRepository.translateAndProcessFormLineData(formHeaderCode, tenantId, translateJson);
    }

}
