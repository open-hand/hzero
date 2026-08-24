package org.hzero.export.lov;

import java.util.List;

/**
 * 获取值集值，平台客户端实现了该接口
 *
 * @author shuangfei.zhu@hand-china.com 2020/12/25 10:25
 */
public interface IExportLovAdapter {

    /**
     * 获取值集值
     *
     * @param tenantId 租户ID
     * @param lovCode  值集编码
     * @return 值集值
     */
    List<ExportLovValue> getLovValue(Long tenantId, String lovCode);
}
