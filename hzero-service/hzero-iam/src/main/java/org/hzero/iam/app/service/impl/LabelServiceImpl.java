package org.hzero.iam.app.service.impl;

import org.hzero.core.base.BaseAppService;
import org.hzero.iam.app.service.LabelService;
import org.hzero.iam.domain.entity.Label;
import org.hzero.iam.domain.service.LabelDomainService;
import org.hzero.iam.infra.constant.ApiTypeTag;
import org.hzero.mybatis.helper.SecurityTokenHelper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

import io.choerodon.core.domain.Page;
import io.choerodon.mybatis.pagehelper.domain.PageRequest;

/**
 * 应用服务默认实现
 *
 * @author xiaoyu.zhao@hand-china.com 2020-02-25 14:22:16
 */
@Service
public class LabelServiceImpl extends BaseAppService implements LabelService {

    private final LabelDomainService labelDomainService;

    @Autowired
    public LabelServiceImpl(LabelDomainService labelDomainService) {
        this.labelDomainService = labelDomainService;
    }

    @Override
    public Page<Label> pageLabelList(PageRequest pageRequest, Label label) {
        return labelDomainService.pageLabelList(pageRequest, label);
    }

    @Override
    public Label getLabelDetail(Long id) {
        return labelDomainService.getLabelDetail(id);
    }

    @Override
    public Label createLabel(Label label) {
        validObject(label, Label.Insert.class);
        return labelDomainService.createLabel(label);
    }

    @Override
    public Label updateLabel(Label label) {
        SecurityTokenHelper.validToken(label);
        validObject(label, Label.Update.class);
        return labelDomainService.updateLabel(label);
    }

    @Override
    public List<Label> getLabelListByType(String type) {
        return labelDomainService.getLabelListByType(type);
    }

    @Override
    public String[] getApiTagSign() {
        return ApiTypeTag.getApiTagValues();
    }
}
