package org.hzero.boot.imported.infra.util;

import org.hzero.boot.imported.config.ImportConfig;

import io.choerodon.core.convertor.ApplicationContextHelper;

/**
 * 步长工具
 *
 * @author shuangfei.zhu@hand-china.com 2019/08/21 20:24
 */
public class StepUtils {

    private StepUtils() {
    }

    /**
     * 获取进度刷新步长
     *
     * @param count 数据总量
     * @return 步长
     */
    public static Integer getStepSize(int count) {
        ImportConfig importConfig = ApplicationContextHelper.getContext().getBean(ImportConfig.class);
        int minStep = importConfig.getMinStepSize();
        int frequency = importConfig.getFrequency();
        int step = (int) Math.ceil((double) count / frequency);
        if (step < minStep) {
            step = minStep;
        }
        return step;
    }
}
