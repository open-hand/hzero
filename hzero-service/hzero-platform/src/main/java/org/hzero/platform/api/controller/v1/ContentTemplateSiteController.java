package org.hzero.platform.api.controller.v1;

import io.choerodon.core.iam.ResourceLevel;
import io.choerodon.mybatis.pagehelper.annotation.SortDefault;
import io.choerodon.mybatis.pagehelper.domain.PageRequest;
import io.choerodon.mybatis.pagehelper.domain.Sort;
import io.choerodon.swagger.annotation.Permission;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.hzero.core.base.BaseConstants;
import org.hzero.core.base.BaseController;
import org.hzero.core.util.Results;
import org.hzero.mybatis.helper.SecurityTokenHelper;
import org.hzero.platform.app.service.ContentTemplateService;
import org.hzero.platform.config.PlatformSwaggerApiConfig;
import org.hzero.platform.domain.entity.ContentTemplate;
import org.hzero.platform.domain.repository.ContentTemplateRepository;
import org.hzero.starter.keyencrypt.core.Encrypt;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import springfox.documentation.annotations.ApiIgnore;

/**
 * 内容模板
 *
 * @author xiaoyu.zhao@hand-china.com 2019/06/26 10:53
 */
@Api(tags = PlatformSwaggerApiConfig.CONTENT_TEMPLATE_SITE)
@RestController("contentTemplateSiteController.v1")
@RequestMapping("/v1/content-templates")
public class ContentTemplateSiteController extends BaseController {

    @Autowired
    private ContentTemplateService templateService;
    @Autowired
    private ContentTemplateRepository templateRepository;

    @ApiOperation(value = "门户模板列表")
    @Permission(level = ResourceLevel.SITE)
    @GetMapping
    public ResponseEntity list(ContentTemplate template, @ApiIgnore @SortDefault(value = ContentTemplate.FIELD_TEMPLATE_ID,
            direction = Sort.Direction.DESC) PageRequest pageRequest) {
        template.setSiteQueryFlag(BaseConstants.Flag.YES);
        return Results.success(templateRepository.selectTemplates(pageRequest, template));
    }

    @ApiOperation(value = "创建门户模板")
    @Permission(level = ResourceLevel.SITE)
    @PostMapping
    public ResponseEntity create(@RequestBody ContentTemplate template) {
        validObject(template);
        return Results.success(templateService.insertTemplates(template));
    }

    @ApiOperation(value = "修改门户模板")
    @Permission(level = ResourceLevel.SITE)
    @PutMapping
    public ResponseEntity update(@RequestBody @Encrypt ContentTemplate template) {
        validObject(template);
        SecurityTokenHelper.validTokenIgnoreInsert(template);
        return Results.success(templateService.updateTemplate(template));
    }

    @ApiOperation(value = "删除门户模板")
    @Permission(level = ResourceLevel.SITE)
    @DeleteMapping
    public ResponseEntity remove(@RequestBody @Encrypt ContentTemplate template) {
        SecurityTokenHelper.validTokenIgnoreInsert(template);
        templateService.removeTemplate(template);
        return Results.success();
    }

}
