package org.hzero.iam.domain.repository;

import org.hzero.core.util.Pair;
import org.hzero.iam.domain.entity.Label;
import org.hzero.iam.domain.entity.LabelRel;
import org.hzero.iam.infra.constant.LabelAssignType;
import org.hzero.mybatis.base.BaseRepository;

import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * 标签关系表资源库
 *
 * @author bo.he02@hand-china.com 2020-04-26 10:04:19
 */
public interface LabelRelRepository extends BaseRepository<LabelRel> {
    /**
     * 通过数据类型和数据ID查询与之相关联的标签
     *
     * @param type   数据类型
     * @param dataId 数据ID
     * @return 查询到的满足条件的标签
     */
    List<Label> selectLabelsByDataTypeAndDataId(String type, Long dataId);

    /**
     * 通过数据类型和数据IDs查询与之相关联的标签
     *
     * @param type    数据类型
     * @param dataIds 数据IDs
     * @return 查询到的满足条件的标签
     */
    Map<Long, List<Label>> selectLabelsByDataTypeAndDataIds(String type, Set<Long> dataIds);

    /**
     * 通过数据类型和数据ID查询与之相关联的标签
     *
     * @param type   数据类型
     * @param dataId 数据ID
     * @return 查询到的满足条件的标签关系(内部包含标签信息)
     */
    List<LabelRel> selectLabelRelsByDataTypeAndDataId(String type, Long dataId);

    /**
     * 通过数据类型和数据ID查询与之相关联的前端可见标签
     *
     * @param type   数据类型
     * @param dataId 数据ID
     * @return 查询到的满足条件的标签
     */
    List<LabelRel> selectViewLabelsByDataTypeAndDataId(String type, Long dataId);

    /**
     * 通过数据类型和数据ID集合查询与之相关联的标签
     *
     * @param type    数据类型
     * @param dataIds 数据IDs
     * @return 查询到的满足条件的标签关系(带标签)     key ---> value === dataId ---> Labels
     */
    Map<Long, List<LabelRel>> selectLabelRelsByDataTypeAndDataIds(String type, Set<Long> dataIds);

    /**
     * 通过数据类型和数据IDs查询已分配的可继承的标签IDs
     *
     * @param type    数据类型
     * @param dataIds 数据IDs
     * @return 可继承的标签对象IDs
     */
    Set<Long> selectLabelIdsByDataTypeAndDataIds(String type, Set<Long> dataIds);

    /**
     * 通过数据类型和数据IDs查询已分配的可继承的标签IDs
     *
     * @param type    数据类型
     * @param dataIds 数据IDs
     * @return 可继承的标签对象IDs
     */
    Set<Long> selectInheritLabelIdsByDataTypeAndDataIds(String type, Set<Long> dataIds);

    /**
     * 通过标签视图数据，更新标签关系数据
     *
     * @param type         数据类型
     * @param dataId       数据
     * @param assignType   分配类型
     * @param viewLabelIds 标签视图数据IDs
     * @return 更新结果   <A, B> === <AddedLabels, RemovedLabels>
     */
    Pair<List<Label>, List<Label>> updateLabelRelationsByLabelView(String type, Long dataId, LabelAssignType assignType, Set<Long> viewLabelIds);

    /**
     * 给指定的数据类型和数据ID增加标签(待添加的标签不能存在，如果待添加的标签存在，就会报错)
     *
     * @param type       数据类型
     * @param dataId     数据ID
     * @param assignType 分配类型
     * @param labelIds   待增加的标签Ids
     * @return 创建的标签关系数据
     */
    List<LabelRel> addLabels(String type, Long dataId, LabelAssignType assignType, Set<Long> labelIds);

    /**
     * 给指定的数据类型和数据ID增加标签(待添加的标签不能存在，如果待添加的标签存在，就会报错)
     *
     * @param type       数据类型
     * @param dataId     数据ID
     * @param assignType 分配类型
     * @param labels     待增加的标签
     * @return 创建的标签关系数据
     */
    List<LabelRel> addLabels(String type, Long dataId, LabelAssignType assignType, List<Label> labels);

    /**
     * 移除指定的数据类型和数据ID的标签(待移除的标签不能是自动分配的，如果待删除的标签存在自动分配的标签，就会报错)
     *
     * @param type     数据类型
     * @param dataId   数据ID
     * @param labelIds 待删除的标签Ids
     */
    void removeLabels(String type, Long dataId, Set<Long> labelIds);

    /**
     * 移除指定的数据类型和数据ID的标签(待移除的标签不能是自动分配的，如果待删除的标签存在自动分配的标签，就会报错)
     *
     * @param type   数据类型
     * @param dataId 数据ID
     * @param labels 待删除的标签
     */
    void removeLabels(String type, Long dataId, List<Label> labels);

    /**
     * 强制分配标签(如果待分配的标签已存在，就会删除原有的标签，重新按照当前方式分配标签)
     *
     * @param type       数据类型
     * @param assignType 分配类型
     * @param dataIds    数据IDs
     * @param labelIds   标签IDs
     */
    void assignLabels(String type, LabelAssignType assignType, Set<Long> dataIds, Set<Long> labelIds);

    /**
     * 强制移除标签(移除指定的所有标签)
     *
     * @param type     数据类型
     * @param dataIds  数据IDs
     * @param labelIds 标签IDs
     */
    void recycleLabels(String type, Set<Long> dataIds, Set<Long> labelIds);

    /**
     * 强制回收指定数据的所有标签
     *
     * @param type    数据类型
     * @param dataIds 数据IDs
     */
    void recycleAllLabels(String type, Set<Long> dataIds);

    /**
     * 通过ID批量删除
     *
     * @param labelRelIds 待删除的ID
     */
    void batchDeleteByIds(List<Long> labelRelIds);
}
