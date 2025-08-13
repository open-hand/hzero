package org.hzero.iam.app.service;

import org.hzero.iam.domain.entity.LabelRel;

import java.util.List;
import java.util.Set;

/**
 * 标签关系表应用服务
 *
 * @author bo.he02@hand-china.com 2020-04-26 10:04:19
 */
public interface LabelRelService {
    /**
     * 通过数据类型和数据ID查询与之相关联的可见标签
     *
     * @param dataType 数据类型
     * @param dataId   数据ID
     * @return 查询到的满足条件的标签
     */
    List<LabelRel> selectViewLabelsByDataTypeAndDataId(String dataType, Long dataId);

    /**
     * 给指定的数据类型和数据ID增加标签
     *
     * @param type       数据类型
     * @param dataId     数据ID
     * @param assignType 分配类型
     * @param labelIds   待增加的标签Ids
     * @return 创建的标签关系数据
     */
    List<LabelRel> addLabels(String type, Long dataId, String assignType, Set<Long> labelIds);

    /**
     * 移除指定的数据类型和数据ID的标签
     *
     * @param type     数据类型
     * @param dataId   数据ID
     * @param labelIds 待删除的标签Ids
     */
    void removeLabels(String type, Long dataId, Set<Long> labelIds);
}
