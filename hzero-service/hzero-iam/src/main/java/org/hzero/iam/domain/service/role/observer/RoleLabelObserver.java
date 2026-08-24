package org.hzero.iam.domain.service.role.observer;

import org.apache.commons.collections4.CollectionUtils;
import org.hzero.core.base.BaseConstants;
import org.hzero.core.observer.Observer;
import org.hzero.iam.domain.entity.Label;
import org.hzero.iam.domain.entity.Role;
import org.hzero.iam.domain.repository.LabelRepository;
import org.hzero.iam.domain.service.role.AbstractRoleLabelService;
import org.hzero.iam.infra.constant.LabelAssignType;
import org.hzero.mybatis.domian.Condition;
import org.hzero.mybatis.util.Sqls;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.annotation.Nonnull;
import java.util.*;
import java.util.stream.Collectors;

/**
 * 角色标签
 *
 * @author bojiangzhou 2020/07/08
 */
@Component
public class RoleLabelObserver extends AbstractRoleLabelService implements Observer<Role> {

    private static final Logger LOGGER = LoggerFactory.getLogger(RoleLabelObserver.class);

    @Autowired
    protected LabelRepository labelRepository;

    @Override
    public int order() {
        return 10;
    }

    @Override
    public void update(@Nonnull Role role, Object... args) {
        // 父级角色
        Role parentRole = role.getParentRole();
        // 继承角色
        Role inheritRole = role.getInheritRole();

        // 获取角色标签视图,处理不可见标签
        List<Label> roleLabels = Optional.ofNullable(role.getRoleLabels()).orElse(new ArrayList<>());
        if (CollectionUtils.isNotEmpty(roleLabels)) {
            // 标签IDs
            Set<Long> labelIds = roleLabels.stream().filter(Objects::nonNull)
                    .map(Label::getId).filter(Objects::nonNull).collect(Collectors.toSet());
            if (CollectionUtils.isNotEmpty(labelIds)) {
                // 查询不可见的标签
                List<Label> notVisibleLabels = this.labelRepository.selectByCondition(Condition.builder(Label.class)
                        .select(Label.FIELD_ID)
                        .andWhere(Sqls.custom()
                                // 不可见
                                .andEqualTo(Label.FIELD_VISIBLE_FLAG, BaseConstants.Flag.NO)
                                .andIn(Label.FIELD_ID, labelIds)
                        ).build());
                if (CollectionUtils.isNotEmpty(notVisibleLabels)) {
                    LOGGER.debug("Handle role labels, roleId: [{}], notVisibleLabels: [{}]]", role.getId(), notVisibleLabels);
                    // 分配标签
                    this.labelRelRepository.addLabels(Role.LABEL_DATA_TYPE, role.getId(), LabelAssignType.MANUAL,
                            notVisibleLabels.stream().map(Label::getId).collect(Collectors.toSet()));
                }
            }
        }

        // 需要添加的标签IDs
        Set<Long> needAddedLabelIds = Collections.emptySet();
        if (Objects.nonNull(inheritRole)) {
            // 查询基础角色的所有标签
            List<Label> labels = this.labelRelRepository.selectLabelsByDataTypeAndDataId(Role.LABEL_DATA_TYPE, inheritRole.getId());
            // 判断角色是否具有模板标签
            if (this.hasTemplateLabel(labels)) {
                // 如果继承角色是模板角色，就直接拷贝除模板标签的所有标签
                needAddedLabelIds = labels.stream()
                        // 非模板标签
                        .filter(label -> !this.isTemplateLabel(label))
                        .map(Label::getId).collect(Collectors.toSet());
            } else {
                // 如果继承角色非模板角色，就只继承可继承的标签
                needAddedLabelIds = this.labelRelRepository
                        .selectInheritLabelIdsByDataTypeAndDataIds(Role.LABEL_DATA_TYPE, Collections.singleton(inheritRole.getId()));
            }
        } else if (Objects.nonNull(parentRole)) {
            // 继承父角色可继承的标签
            needAddedLabelIds = this.labelRelRepository
                    .selectInheritLabelIdsByDataTypeAndDataIds(Role.LABEL_DATA_TYPE, Collections.singleton(parentRole.getId()));
        }

        LOGGER.info("Handle roleLabel from parentRole or inheritRole, labelIds: {}", needAddedLabelIds);
        if (CollectionUtils.isNotEmpty(needAddedLabelIds)) {
            // 分配标签
            this.labelRelRepository.addLabels(Role.LABEL_DATA_TYPE, role.getId(), LabelAssignType.AUTO, needAddedLabelIds);

            // 将可继承的标签加到视图标签中
            roleLabels.addAll(needAddedLabelIds.stream().map(labelId -> {
                Label label = new Label();
                label.setId(labelId);
                return label;
            }).collect(Collectors.toSet()));
            // 设置标签
            role.setRoleLabels(roleLabels);
        }

        // 处理之后的逻辑
        super.handleRoleLabels(role);
    }

    /**
     * 判断是否存在模板标签
     *
     * @param labels 标签
     * @return 是否存在模板标签 true 存在模板标签 false 不存在模板标签
     */
    private boolean hasTemplateLabel(List<Label> labels) {
        if (CollectionUtils.isEmpty(labels)) {
            return false;
        }

        // 判断是否包含模板标签
        return labels.stream().anyMatch(this::isTemplateLabel);
    }

    /**
     * 判断一个标签是否是模板标签
     *
     * @param label 标签对象
     * @return true 是模板标签 false 不是模板标签
     */
    private boolean isTemplateLabel(Label label) {
        // 包含以 _ROLE_TPL 结尾的标签，就认为是模板标签
        return label.getName().endsWith(Role.LABEL_ROLE_TPL);
    }
}
