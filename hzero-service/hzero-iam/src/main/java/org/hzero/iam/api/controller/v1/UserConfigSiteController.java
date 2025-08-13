package org.hzero.iam.api.controller.v1;

import org.hzero.core.base.BaseController;
import org.hzero.core.util.Results;
import org.hzero.iam.config.SwaggerApiConfig;
import org.hzero.iam.domain.entity.UserConfig;
import org.hzero.iam.domain.repository.UserConfigRepository;
import org.hzero.starter.keyencrypt.core.Encrypt;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import io.choerodon.core.domain.Page;
import io.choerodon.core.iam.ResourceLevel;
import io.choerodon.mybatis.pagehelper.annotation.SortDefault;
import io.choerodon.mybatis.pagehelper.domain.PageRequest;
import io.choerodon.mybatis.pagehelper.domain.Sort;
import io.choerodon.swagger.annotation.Permission;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import springfox.documentation.annotations.ApiIgnore;

/**
 * 用户默认配置 管理 API
 *
 * @author zhiying.dong@hand-china.com 2018-09-14 10:46:53
 */
@Api(tags = SwaggerApiConfig.USER_CONFIG_SITE)
@RestController("userConfigSiteController.v1")
@RequestMapping("/v1/user-configs")
public class UserConfigSiteController extends BaseController {

    private UserConfigRepository userConfigRepository;

    @Autowired
    public UserConfigSiteController(UserConfigRepository userConfigRepository) {
        this.userConfigRepository = userConfigRepository;
    }

    @ApiOperation(value = "用户默认配置列表")
    @Permission(level = ResourceLevel.SITE)
    @GetMapping
    @ApiIgnore
    public ResponseEntity<Page<UserConfig>> listUserConfig(@Encrypt UserConfig userConfig,
                                                           @ApiIgnore @SortDefault(value = UserConfig.FIELD_USER_CONFIG_ID,
                                                                   direction = Sort.Direction.DESC) PageRequest pageRequest) {
        Page<UserConfig> list = userConfigRepository.pageAndSort(pageRequest, userConfig);
        return Results.success(list);
    }

    @ApiOperation(value = "用户默认配置明细")
    @Permission(level = ResourceLevel.SITE)
    @GetMapping("/userConfig")
    public ResponseEntity<UserConfig> detailUserConfig(
            @RequestParam("userId") @ApiParam("用户ID") @Encrypt Long userId,
            @RequestParam("tenantId") @ApiParam("租户ID") Long tenantId) {
        UserConfig userConfig = userConfigRepository.queryUserConfig(userId, tenantId);
        return Results.success(userConfig);
    }

    @ApiOperation(value = "创建用户默认配置")
    @Permission(level = ResourceLevel.SITE)
    @PostMapping
    public ResponseEntity<UserConfig> createUserConfig(@RequestBody @Encrypt UserConfig userConfig) {
        validObject(userConfig);
        userConfigRepository.createUserConfig(userConfig);
        return Results.success(userConfig);
    }

    @ApiOperation(value = "修改用户默认配置")
    @Permission(level = ResourceLevel.SITE)
    @PutMapping
    public ResponseEntity<UserConfig> updateUserConfig(@RequestBody @Encrypt UserConfig userConfig) {
        userConfigRepository.updateUserConfig(userConfig);
        return Results.success(userConfig);
    }

    @ApiOperation(value = "删除用户默认配置")
    @Permission(level = ResourceLevel.SITE)
    @DeleteMapping("/{userConfigId}")
    @ApiIgnore
    public ResponseEntity removeUserConfig(@PathVariable("userConfigId") @Encrypt Long userConfigId) {
        userConfigRepository.deleteByPrimaryKey(userConfigId);
        return Results.success();
    }

}
