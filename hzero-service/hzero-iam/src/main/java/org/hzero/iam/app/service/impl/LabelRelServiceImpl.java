package org.hzero.iam.app.service.impl;

import org.hzero.iam.app.service.LabelRelService;
import org.hzero.iam.domain.entity.LabelRel;
import org.hzero.iam.domain.repository.LabelRelRepository;
import org.hzero.iam.infra.constant.LabelAssignType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Set;

/**
 * 标签关系表应用服务默认实现
 *
 * @author bo.he02@hand-china.com 2020-04-26 10:04:19
 */
@Service
public class LabelRelServiceImpl implements LabelRelService {
    @Autowired
    private LabelRelRepository labelRelRepository;

    @Override
    public List<LabelRel> selectViewLabelsByDataTypeAndDataId(String dataType, Long dataId) {
        return this.labelRelRepository.selectViewLabelsByDataTypeAndDataId(dataType, dataId);
    }

    @Override
    public List<LabelRel> addLabels(String type, Long dataId, String assignType, Set<Long> labelIds) {
        LabelAssignType labelAssignType = LabelAssignType.ofDefault(assignType);
        return this.labelRelRepository.addLabels(type, dataId, labelAssignType, labelIds);
    }

    @Override
    public void removeLabels(String type, Long dataId, Set<Long> labelIds) {
        this.labelRelRepository.removeLabels(type, dataId, labelIds);
    }
}
