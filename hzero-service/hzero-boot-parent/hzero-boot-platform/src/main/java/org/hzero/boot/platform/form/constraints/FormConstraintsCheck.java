package org.hzero.boot.platform.form.constraints;

import org.hzero.boot.platform.form.domain.vo.FormLineVO;
import org.hzero.boot.platform.form.result.ConstraintsCheckResult;

import java.util.List;

/**
 * 表单配置校验
 *
 * @author liufanghan 2019/11/22 14:15
 */
public interface FormConstraintsCheck {

    /**
     * 校验表单配置值
     *
     * @param configValue  配置文本 JSON格式
     * @param formLineList 表单行集合
     * @return 校验结果
     */
    ConstraintsCheckResult checkConfigValue(String configValue, List<FormLineVO> formLineList);

    /**
     * 校验表单配置值与校验更新允许
     *
     * @param oldConfig    旧配置文本 JSON格式
     * @param newConfig    新配置文本 JSON格式
     * @param formLineList 表单行集合
     * @return 校验结果
     */
    ConstraintsCheckResult checkConfigValue(String oldConfig, String newConfig, List<FormLineVO> formLineList);


}
