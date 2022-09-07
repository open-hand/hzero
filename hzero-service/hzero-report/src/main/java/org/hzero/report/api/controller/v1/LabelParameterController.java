package org.hzero.report.api.controller.v1;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.hzero.core.base.BaseController;
import org.hzero.core.util.Results;
import org.hzero.mybatis.helper.SecurityTokenHelper;
import org.hzero.report.config.ReportSwaggerApiConfig;
import org.hzero.report.domain.entity.LabelParameter;
import org.hzero.report.domain.repository.LabelParameterRepository;
import org.hzero.starter.keyencrypt.core.Encrypt;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import io.choerodon.core.iam.ResourceLevel;
import io.choerodon.swagger.annotation.Permission;

/**
 * 标签参数 管理 API
 *
 * @author fanghan.liu@hand-china.com 2019-11-27 10:35:39
 */
@Api(tags = ReportSwaggerApiConfig.LABEL_PARAMETER)
@RestController("labelParameterController.v1")
@RequestMapping("/v1/{organizationId}/label-parameters")
public class LabelParameterController extends BaseController {

    @Autowired
    private LabelParameterRepository labelParameterRepository;

    @ApiOperation(value = "删除标签参数")
    @Permission(level = ResourceLevel.ORGANIZATION)
    @DeleteMapping
    public ResponseEntity remove(@PathVariable("organizationId") Long organizationId, @Encrypt @RequestBody LabelParameter labelParameter) {
        labelParameter.setTenantId(organizationId);
        SecurityTokenHelper.validToken(labelParameter);
        labelParameterRepository.deleteByPrimaryKey(labelParameter.getLabelParameterId());
        return Results.success();
    }

}
