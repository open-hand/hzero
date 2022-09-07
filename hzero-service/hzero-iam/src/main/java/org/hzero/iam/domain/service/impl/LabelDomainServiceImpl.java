package org.hzero.iam.domain.service.impl;

import io.choerodon.core.domain.Page;
import io.choerodon.core.exception.CommonException;
import io.choerodon.mybatis.pagehelper.domain.PageRequest;
import org.hzero.boot.platform.lov.annotation.ProcessLovValue;
import org.hzero.core.base.BaseConstants;
import org.hzero.iam.domain.entity.Label;
import org.hzero.iam.domain.repository.LabelRepository;
import org.hzero.iam.domain.service.LabelDomainService;
import org.hzero.iam.infra.common.utils.AssertUtils;
import org.hzero.iam.infra.constant.HiamError;
import org.modelmapper.internal.util.Assert;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * 标签领域服务实现类
 *
 * @author xiaoyu.zhao@hand-china.com
 */
@Component
public class LabelDomainServiceImpl implements LabelDomainService {

    private final LabelRepository labelRepository;

    @Autowired
    public LabelDomainServiceImpl(LabelRepository labelRepository) {
        this.labelRepository = labelRepository;
    }

    @Override
    @ProcessLovValue
    public Page<Label> pageLabelList(PageRequest pageRequest, Label label) {
        return labelRepository.pageLabelList(pageRequest, label);
    }

    @Override
    @ProcessLovValue
    public Label getLabelDetail(Long id) {
        Label label = labelRepository.selectByPrimaryKey(id);
        Assert.notNull(label, BaseConstants.ErrorCode.DATA_NOT_EXISTS);
        return label;
    }

    @Override
    public Label createLabel(Label label) {
        Boolean flag = labelRepository.validLabelUnique(label);
        if (!flag) {
            throw new CommonException(HiamError.LABEL_EXIST);
        }
        labelRepository.insertSelective(label);
        return label;
    }

    @Override
    public Label updateLabel(Label label) {
        // 查询数据
        Label dbLabel = this.labelRepository.selectByPrimaryKey(label);
        AssertUtils.notNull(dbLabel, BaseConstants.ErrorCode.DATA_NOT_EXISTS);

        // preset_flag=1，不允许修改标签
        if (BaseConstants.Flag.YES.equals(dbLabel.getPresetFlag())) {
            // 内置标签禁止修改，标签名称: {0}
            throw new CommonException("hiam.error.label.preset.cannot.modify", dbLabel.getName());
        }
        // 更新标签，仅允许更新 启用状态、Tag、描述、页面可见标识
        this.labelRepository.updateOptional(label, Label.FIELD_ENABLED_FLAG, Label.FIELD_TAG,
                Label.FIELD_DESCRIPTION, Label.FIELD_VISIBLE_FLAG);
        return label;
    }

    @Override
    public List<Label> getLabelListByType(String type) {
        return labelRepository.getLabelListByType(type);
    }

    @Override
    public List<Label> getLabelListByTypeAndNames(String type, String[] labelName) {
        return labelRepository.getLabelListByTypeAndNames(type, labelName);
    }

    @Override
    public Set<String> getLabelListByTypeAndTag(String type, String tag) {
        Label label = new Label();
        label.setType(type);
        label.setTag(tag);
        return labelRepository.select(label).stream().map(Label::getName).collect(Collectors.toSet());
    }
}
