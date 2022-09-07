package org.hzero.platform.app.service;

import org.hzero.platform.domain.entity.StaticText;
import org.hzero.platform.domain.entity.StaticTextValue;

import java.util.List;

/**
 * 平台静态信息应用服务
 *
 * @author fatao.liu@hand-china.com 2018-07-23 14:19:42
 */
public interface StaticTextService {

    /**
     * 创建富文本
     */
    StaticText createText(StaticText staticText);

    /**
     * 更新文本
     */
    StaticText updateText(StaticText staticText);

    /**
     * 批量删除
     */
    void batchDelete(List<StaticText> texts);

    /**
     * 查询文本内容
     *
     * @param textId 文本ID
     * @return StaticTextValue
     */
    StaticTextValue getTextValueById(Long textId);
}
