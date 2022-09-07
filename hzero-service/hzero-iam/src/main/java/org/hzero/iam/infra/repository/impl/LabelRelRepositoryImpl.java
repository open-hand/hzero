package org.hzero.iam.infra.repository.impl;

import io.choerodon.core.exception.CommonException;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.collections4.ListUtils;
import org.apache.commons.collections4.MapUtils;
import org.apache.commons.lang3.StringUtils;
import org.hzero.core.base.BaseConstants;
import org.hzero.core.util.Pair;
import org.hzero.iam.domain.entity.Label;
import org.hzero.iam.domain.entity.LabelRel;
import org.hzero.iam.domain.repository.LabelRelRepository;
import org.hzero.iam.domain.repository.LabelRepository;
import org.hzero.iam.infra.constant.LabelAssignType;
import org.hzero.iam.infra.mapper.LabelRelMapper;
import org.hzero.mybatis.base.impl.BaseRepositoryImpl;
import org.hzero.mybatis.domian.Condition;
import org.hzero.mybatis.util.Sqls;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.*;
import java.util.stream.Collectors;

import static io.choerodon.mybatis.domain.AuditDomain.*;
import static java.util.stream.Collectors.*;

/**
 * 标签关系表 资源库实现
 *
 * @author bo.he02@hand-china.com 2020-04-26 10:04:19
 */
@Component
public class LabelRelRepositoryImpl extends BaseRepositoryImpl<LabelRel> implements LabelRelRepository {
    @Autowired
    private LabelRelMapper labelRelMapper;
    @Autowired
    private LabelRepository labelRepository;

    @Override
    public List<Label> selectLabelsByDataTypeAndDataId(String type, Long dataId) {
        // 查询结果
        return Optional.ofNullable(this.selectLabelsByDataTypeAndDataIds(type, Collections.singleton(dataId)))
                .orElse(Collections.emptyMap())
                .get(dataId);
    }

    @Override
    public Map<Long, List<Label>> selectLabelsByDataTypeAndDataIds(String type, Set<Long> dataIds) {
        // 消息关系数据
        Map<Long, List<LabelRel>> labelRelsMap = this.selectLabelRelsByDataTypeAndDataIds(type, dataIds);
        if (MapUtils.isNotEmpty(labelRelsMap)) {
            // 标签map
            Map<Long, List<Label>> labelMap = new HashMap<>(labelRelsMap.size());

            // 遍历数据，并转换
            labelRelsMap.forEach((dataId, labelRels) -> {
                // 数据转换
                labelMap.put(dataId, labelRels.stream().map(LabelRel::getLabel).collect(toList()));
            });

            // 返回结果
            return labelMap;
        }

        // 返回空对象
        return Collections.emptyMap();
    }

    @Override
    public List<LabelRel> selectLabelRelsByDataTypeAndDataId(String type, Long dataId) {
        // 查询结果
        return Optional.ofNullable(this.selectLabelRelsByDataTypeAndDataIds(type, Collections.singleton(dataId)))
                .orElse(Collections.emptyMap())
                .get(dataId);
    }

    @Override
    public List<LabelRel> selectViewLabelsByDataTypeAndDataId(String type, Long dataId) {
        return Optional.ofNullable(this.selectLabelRelsByDataTypeAndDataId(type, dataId)).orElse(Collections.emptyList())
                .stream()
                // 有标签对象
                .filter(labelRel -> Objects.nonNull(labelRel.getLabel()))
                // 标签前端可见
                .filter(labelRel -> BaseConstants.Flag.YES.equals(labelRel.getLabel().getVisibleFlag()))
                .collect(toList());
    }

    @Override
    public Map<Long, List<LabelRel>> selectLabelRelsByDataTypeAndDataIds(String type, Set<Long> dataIds) {
        if (StringUtils.isBlank(type) || CollectionUtils.isEmpty(dataIds)) {
            return Collections.emptyMap();
        }

        // 根据数据类型和数据ID查询标签关系数据
        List<LabelRel> labelRelations = ListUtils.partition(new ArrayList<>(dataIds), 1000)
                .stream().map(subDataIds -> {
                    // 查询数据
                    return this.selectByCondition(Condition.builder(LabelRel.class)
                            .andWhere(Sqls.custom()
                                    // 数据类型
                                    .andEqualTo(LabelRel.FIELD_DATA_TYPE, type)
                                    // 数据ID
                                    .andIn(LabelRel.FIELD_DATA_ID, subDataIds)
                            ).build()
                            // 排除who字段
                            .excludeProperties(FIELD_LAST_UPDATE_DATE, FIELD_LAST_UPDATED_BY, FIELD_CREATED_BY, FIELD_CREATION_DATE));
                }).filter(CollectionUtils::isNotEmpty)
                .flatMap(Collection::stream)
                .collect(toList());
        if (CollectionUtils.isEmpty(labelRelations)) {
            return Collections.emptyMap();
        }

        // 查询所有标签
        List<Label> labels = ListUtils.partition(labelRelations, 1000)
                .stream().map(subLabelRelations -> {
                    // 查询数据
                    return this.labelRepository.selectByCondition(Condition.builder(Label.class)
                            .andWhere(Sqls.custom()
                                    // 启用
                                    .andEqualTo(Label.FIELD_ENABLED_FLAG, BaseConstants.Flag.YES)
                                    // 标签IDs
                                    .andIn(Label.FIELD_ID, subLabelRelations.stream().map(LabelRel::getLabelId).collect(toSet()))
                            ).build()
                            // 排除who字段
                            .excludeProperties(FIELD_LAST_UPDATE_DATE, FIELD_LAST_UPDATED_BY, FIELD_CREATED_BY, FIELD_CREATION_DATE));
                }).filter(CollectionUtils::isNotEmpty)
                .flatMap(Collection::stream)
                .collect(toList());
        if (CollectionUtils.isEmpty(labels)) {
            return Collections.emptyMap();
        }

        // 将查询到的标签转换成map    key ---> value === labelId ---> label
        Map<Long, Label> labelMap = labels.stream().collect(toMap(Label::getId, t -> t));

        // 设置Label,并按数据ID分组返回数据
        return labelRelations.stream()
                // 要查询到的标签才返回关系数据
                .filter(labelRel -> labelMap.containsKey(labelRel.getLabelId()))
                // 设置标签
                .peek(labelRel -> labelRel.setLabel(labelMap.get(labelRel.getLabelId())))
                .collect(Collectors.groupingBy(LabelRel::getDataId));
    }

    @Override
    public Set<Long> selectLabelIdsByDataTypeAndDataIds(String type, Set<Long> dataIds) {
        return this.selectLabelIds(type, dataIds, null);
    }

    @Override
    public Set<Long> selectInheritLabelIdsByDataTypeAndDataIds(String type, Set<Long> dataIds) {
        return this.selectLabelIds(type, dataIds, BaseConstants.Flag.YES);
    }

    @Override
    public Pair<List<Label>, List<Label>> updateLabelRelationsByLabelView(String type, Long dataId,
                                                                          LabelAssignType assignType, Set<Long> viewLabelIds) {
        if (StringUtils.isBlank(type) || Objects.isNull(dataId) || Objects.isNull(assignType)) {
            return new Pair<>(Collections.emptyList(), Collections.emptyList());
        }
        List<Label> viewLabels = Collections.emptyList();
        if (CollectionUtils.isNotEmpty(viewLabelIds)) {
            // 查询视图标签
            viewLabels = this.labelRepository.selectByIds(StringUtils.join(viewLabelIds, BaseConstants.Symbol.COMMA));
        }

        // 处理数据，去除不可见标签
        viewLabels = Optional.ofNullable(viewLabels).orElse(Collections.emptyList())
                .stream().filter(label -> BaseConstants.Flag.YES.equals(label.getVisibleFlag()))
                .collect(toList());
        // 数据库的标签
        List<Label> dbLabels = Optional.ofNullable(this.selectViewLabelsByDataTypeAndDataId(type, dataId))
                .orElse(Collections.emptyList()).stream().map(LabelRel::getLabel).collect(Collectors.toList());

        // 当前视图的标签除去菜单数据库的标签,即为需要增加的标签
        List<Label> needAddedLabels = ListUtils.removeAll(viewLabels, dbLabels);
        // 数据库的标签出去菜单当前视图的标签,即为需要移除的标签
        List<Label> needRemovedLabels = ListUtils.removeAll(dbLabels, viewLabels);

        // 添加标签
        if (CollectionUtils.isNotEmpty(needAddedLabels)) {
            this.addLabels(type, dataId, assignType, needAddedLabels);
        }
        // 删除标签
        if (CollectionUtils.isNotEmpty(needRemovedLabels)) {
            this.removeLabels(type, dataId, needRemovedLabels);
        }

        // 返回结果
        return new Pair<>(needAddedLabels, needRemovedLabels);
    }

    @Override
    public List<LabelRel> addLabels(String type, Long dataId, LabelAssignType assignType, Set<Long> labelIds) {
        if (StringUtils.isBlank(type) || Objects.isNull(dataId) || Objects.isNull(assignType) || CollectionUtils.isEmpty(labelIds)) {
            return Collections.emptyList();
        }

        // 查询已经分配的标签关系
        List<LabelRel> dbLabelRelations = this.selectByCondition(Condition.builder(LabelRel.class)
                .select(LabelRel.FIELD_LABEL_ID)
                .andWhere(Sqls.custom()
                        .andEqualTo(LabelRel.FIELD_DATA_TYPE, type)
                        .andEqualTo(LabelRel.FIELD_DATA_ID, dataId)
                        .andIn(LabelRel.FIELD_LABEL_ID, labelIds)
                ).build());
        if (CollectionUtils.isNotEmpty(dbLabelRelations)) {
            // 查询标签数据
            List<String> dbLabelNames = this.labelRepository.selectByCondition(Condition.builder(Label.class)
                    .select(Label.FIELD_NAME)
                    .andWhere(Sqls.custom()
                            .andIn(Label.FIELD_ID, dbLabelRelations.stream().map(LabelRel::getLabelId).collect(toSet()))
                    ).build()).stream().map(Label::getName).collect(toList());
            // 分配标签失败, 数据类型: [{0}] - 数据ID: [{1}] 存在已经分配的标签: [{2}]
            throw new CommonException("hiam.error.label_rel.add_label.exists_rels",
                    type, dataId, StringUtils.join(dbLabelNames, BaseConstants.Symbol.COMMA));
        }

        // 构建关系数据
        return labelIds.stream()
                // 转换数据
                .map(labelId -> LabelRel.built(type, dataId, labelId, assignType.getCode()))
                // 插入数据
                .peek(this::insertSelective)
                .collect(toList());
    }

    @Override
    public List<LabelRel> addLabels(String type, Long dataId, LabelAssignType assignType, List<Label> labels) {
        if (Objects.isNull(type) || Objects.isNull(dataId) || Objects.isNull(assignType) || CollectionUtils.isEmpty(labels)) {
            return Collections.emptyList();
        }

        // 添加标签,并返回结果
        return this.addLabels(type, dataId, assignType, labels.stream().map(Label::getId).collect(toSet()));
    }

    @Override
    public void removeLabels(String type, Long dataId, Set<Long> labelIds) {
        if (StringUtils.isBlank(type) || Objects.isNull(dataId) || CollectionUtils.isEmpty(labelIds)) {
            return;
        }

        // 查询自动分配的标签关系对象
        List<LabelRel> dbAutoLabelRelations = this.selectByCondition(Condition.builder(LabelRel.class)
                .select(LabelRel.FIELD_LABEL_REL_ID, LabelRel.FIELD_ASSIGN_TYPE, LabelRel.FIELD_LABEL_ID)
                .andWhere(Sqls.custom()
                        .andEqualTo(LabelRel.FIELD_DATA_TYPE, type)
                        .andEqualTo(LabelRel.FIELD_DATA_ID, dataId)
                        .andEqualTo(LabelRel.FIELD_ASSIGN_TYPE, LabelAssignType.AUTO.getCode())
                        .andIn(LabelRel.FIELD_LABEL_ID, labelIds)
                ).build());
        if (CollectionUtils.isNotEmpty(dbAutoLabelRelations)) {
            // 查询标签数据
            List<String> dbLabelNames = this.labelRepository.selectByCondition(Condition.builder(Label.class)
                    .select(Label.FIELD_NAME)
                    .andWhere(Sqls.custom()
                            .andIn(Label.FIELD_ID, dbAutoLabelRelations.stream().map(LabelRel::getLabelId).collect(toSet()))
                    ).build()).stream().map(Label::getName).collect(toList());
            // 移除标签失败, 数据类型: [{0}] - 数据ID: [{1}] 存在自动分配的标签: [{2}]
            throw new CommonException("hiam.error.label_rel.remove_label.exists_auto_rels",
                    type, dataId, StringUtils.join(dbLabelNames, BaseConstants.Symbol.COMMA));
        }

        // 查询手动分配的标签
        List<LabelRel> dbManualLabelRelations = this.selectByCondition(Condition.builder(LabelRel.class)
                .select(LabelRel.FIELD_LABEL_REL_ID, LabelRel.FIELD_ASSIGN_TYPE, LabelRel.FIELD_LABEL_ID)
                .andWhere(Sqls.custom()
                        .andEqualTo(LabelRel.FIELD_DATA_TYPE, type)
                        .andEqualTo(LabelRel.FIELD_DATA_ID, dataId)
                        .andEqualTo(LabelRel.FIELD_ASSIGN_TYPE, LabelAssignType.MANUAL.getCode())
                        .andIn(LabelRel.FIELD_LABEL_ID, labelIds)
                ).build());
        if (CollectionUtils.isEmpty(dbManualLabelRelations)) {
            return;
        }

        // 删除数据
        dbManualLabelRelations.forEach(this::deleteByPrimaryKey);
    }

    @Override
    public void removeLabels(String type, Long dataId, List<Label> labels) {
        if (Objects.isNull(type) || Objects.isNull(dataId) || CollectionUtils.isEmpty(labels)) {
            return;
        }

        // 删除标签
        this.removeLabels(type, dataId, labels.stream().map(Label::getId).collect(toSet()));
    }

    @Override
    public void assignLabels(String type, LabelAssignType assignType, Set<Long> dataIds, Set<Long> labelIds) {
        if (StringUtils.isBlank(type) || Objects.isNull(assignType) || CollectionUtils.isEmpty(dataIds) || CollectionUtils.isEmpty(labelIds)) {
            return;
        }

        // 查询所有原有标签关系
        List<Long> labelRelIds = Optional.ofNullable(this.selectByCondition(Condition.builder(LabelRel.class)
                .select(LabelRel.FIELD_LABEL_REL_ID)
                .andWhere(Sqls.custom()
                        .andEqualTo(LabelRel.FIELD_DATA_TYPE, type)
                        .andIn(LabelRel.FIELD_DATA_ID, dataIds)
                        .andIn(LabelRel.FIELD_LABEL_ID, labelIds)
                ).build())).orElse(Collections.emptyList()).stream().map(LabelRel::getLabelRelId).collect(toList());
        // 删除原有的标签关系
        this.batchDeleteByIds(labelRelIds);

        // 构建新的标签关系
        List<LabelRel> labelRelations = dataIds.stream().flatMap(dataId ->
                labelIds.stream().map(labelId -> LabelRel.built(type, dataId, labelId, assignType.getCode())))
                .collect(toList());

        // 添加标签关系
        this.batchInsertSelective(labelRelations);
    }

    @Override
    public void recycleLabels(String type, Set<Long> dataIds, Set<Long> labelIds) {
        if (StringUtils.isBlank(type) || CollectionUtils.isEmpty(dataIds) || CollectionUtils.isEmpty(labelIds)) {
            return;
        }

        // 查询所有原有标签关系
        List<Long> labelRelIds = Optional.ofNullable(this.selectByCondition(Condition.builder(LabelRel.class)
                .select(LabelRel.FIELD_LABEL_REL_ID)
                .andWhere(Sqls.custom()
                        .andEqualTo(LabelRel.FIELD_DATA_TYPE, type)
                        .andIn(LabelRel.FIELD_DATA_ID, dataIds)
                        .andIn(LabelRel.FIELD_LABEL_ID, labelIds)
                ).build())).orElse(Collections.emptyList()).stream().map(LabelRel::getLabelRelId).collect(toList());
        // 删除数据
        this.batchDeleteByIds(labelRelIds);
    }

    @Override
    public void recycleAllLabels(String type, Set<Long> dataIds) {
        if (StringUtils.isBlank(type) || CollectionUtils.isEmpty(dataIds)) {
            return;
        }

        // 查询所有原有标签关系
        List<Long> labelRelIds = Optional.ofNullable(this.selectByCondition(Condition.builder(LabelRel.class)
                .select(LabelRel.FIELD_LABEL_REL_ID)
                .andWhere(Sqls.custom()
                        .andEqualTo(LabelRel.FIELD_DATA_TYPE, type)
                        .andIn(LabelRel.FIELD_DATA_ID, dataIds)
                ).build())).orElse(Collections.emptyList()).stream().map(LabelRel::getLabelRelId).collect(toList());
        // 删除数据
        this.batchDeleteByIds(labelRelIds);
    }

    @Override
    public void batchDeleteByIds(List<Long> labelRelIds) {
        if (CollectionUtils.isEmpty(labelRelIds)) {
            return;
        }

        // 分片处理，每个分片最多处理1000条数据(sql in 语句的限制)
        ListUtils.partition(labelRelIds, 1000).forEach(subList -> {
            // 批量删除数据
            this.labelRelMapper.batchDeleteByIds(subList);
        });
    }

    /**
     * 查询标签IDs
     *
     * @param type        数据类型
     * @param dataIds     数据IDs
     * @param inheritFlag 是否可继承标识   ignoreNull
     * @return 满足条件的数据
     */
    private Set<Long> selectLabelIds(String type, Set<Long> dataIds, Integer inheritFlag) {
        if (StringUtils.isBlank(type) || CollectionUtils.isEmpty(dataIds)) {
            return Collections.emptySet();
        }

        // 查询父级角色或继承角色的标签
        Set<Long> labelIds = Optional.ofNullable(this.selectByCondition(Condition.builder(LabelRel.class)
                .selectDistinct(LabelRel.FIELD_LABEL_ID)
                .andWhere(Sqls.custom()
                        .andEqualTo(LabelRel.FIELD_DATA_TYPE, type)
                        .andIn(LabelRel.FIELD_DATA_ID, dataIds)
                ).build())).orElse(Collections.emptyList())
                .stream().map(LabelRel::getLabelId).collect(Collectors.toSet());

        if (CollectionUtils.isEmpty(labelIds)) {
            return Collections.emptySet();
        }

        // 查询可继承的标签
        return Optional.ofNullable(this.labelRepository.selectByCondition(Condition.builder(Label.class)
                .select(Label.FIELD_ID)
                .andWhere(Sqls.custom()
                        .andIn(Label.FIELD_ID, labelIds)
                        .andEqualTo(Label.FIELD_INHERIT_FLAG, inheritFlag, true)
                ).build())).orElse(Collections.emptyList())
                .stream().map(Label::getId).collect(Collectors.toSet());
    }
}
