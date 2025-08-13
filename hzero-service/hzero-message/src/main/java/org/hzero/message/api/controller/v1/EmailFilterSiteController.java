package org.hzero.message.api.controller.v1;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import org.hzero.core.base.BaseController;
import org.hzero.core.util.Results;
import org.hzero.message.app.service.EmailFilterService;
import org.hzero.message.config.MessageSwaggerApiConfig;
import org.hzero.message.domain.entity.EmailFilter;
import org.hzero.message.domain.repository.EmailFilterRepository;
import org.hzero.mybatis.helper.SecurityTokenHelper;
import org.hzero.starter.keyencrypt.core.Encrypt;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import springfox.documentation.annotations.ApiIgnore;

import java.util.List;

import io.choerodon.core.domain.Page;
import io.choerodon.core.iam.ResourceLevel;
import io.choerodon.mybatis.pagehelper.annotation.SortDefault;
import io.choerodon.mybatis.pagehelper.domain.PageRequest;
import io.choerodon.mybatis.pagehelper.domain.Sort;
import io.choerodon.swagger.annotation.CustomPageRequest;
import io.choerodon.swagger.annotation.Permission;

/**
 * 邮箱账户黑白名单 管理 API
 *
 * @author shuangfei.zhu@hand-china.com 2019-06-04 14:23:17
 */
@Api(tags = MessageSwaggerApiConfig.EMAIL_FILTER_SITE)
@RestController("emailFilterSiteController.v1")
@RequestMapping("/v1/email-filters")
public class EmailFilterSiteController extends BaseController {

    @Autowired
    private EmailFilterRepository emailFilterRepository;
    @Autowired
    private EmailFilterService emailFilterService;

    @ApiOperation(value = "邮箱账户黑白名单列表")
    @Permission(level = ResourceLevel.SITE)
    @GetMapping
    @ApiImplicitParams({
            @ApiImplicitParam(name = "address", value = "地址", paramType = "query")
    })
    @CustomPageRequest
    public ResponseEntity<Page<EmailFilter>> pageEmailFilter(String address, @Encrypt @RequestParam Long serverId,
                                                             @ApiIgnore @SortDefault(value = EmailFilter.FIELD_EMAIL_FILTER_ID, direction = Sort.Direction.ASC) PageRequest pageRequest) {
        Page<EmailFilter> list = emailFilterRepository.pageEmailFilter(pageRequest, address, serverId);
        return Results.success(list);
    }

    @ApiOperation(value = "创建更新邮箱账户黑白名单")
    @Permission(level = ResourceLevel.SITE)
    @PostMapping
    public ResponseEntity<List<EmailFilter>> createOrUpdateFilter(@Encrypt @RequestBody List<EmailFilter> emailFilterList) {
        validObject(emailFilterList);
        SecurityTokenHelper.validTokenIgnoreInsert(emailFilterList);
        return Results.success(emailFilterService.createOrUpdateFilter(emailFilterList, null));
    }

    @ApiOperation(value = "删除邮箱账户黑白名单")
    @Permission(level = ResourceLevel.SITE)
    @DeleteMapping
    public ResponseEntity deleteEmailFilter(@Encrypt @RequestBody List<EmailFilter> emailFilterList) {
        SecurityTokenHelper.validTokenIgnoreInsert(emailFilterList);
        emailFilterService.deleteFilter(emailFilterList);
        return Results.success();
    }

}
