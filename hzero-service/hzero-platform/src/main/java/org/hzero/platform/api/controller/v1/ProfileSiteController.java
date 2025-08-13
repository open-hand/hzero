package org.hzero.platform.api.controller.v1;

import org.hzero.core.base.BaseConstants;
import org.hzero.core.base.BaseController;
import org.hzero.core.util.Results;
import org.hzero.platform.config.PlatformSwaggerApiConfig;
import org.hzero.platform.domain.service.ProfileValueDomainService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import io.choerodon.core.iam.ResourceLevel;
import io.choerodon.swagger.annotation.Permission;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;


/**
 * 配置维护profile controller
 *
 * @author yunxiang.zhou01 @hand-china.com 2018/06/05
 */
@Api(tags = PlatformSwaggerApiConfig.PROFILE_SITE)
@RestController("profileSiteController.v1")
@RequestMapping("/v1")
public class ProfileSiteController extends BaseController {

    @Autowired
    private ProfileValueDomainService profileValueDomainService;

    /**
     * 根据配置维护值名得到当前用户最低层次的配置维护值 默认 租户级 > 角色级 > 用户级
     * FIX 2019-04-19 新增平台级查询配置维护缓存接口，移除原有通过配置维护名获取配置维护值接口
     *
     * @param profileName 配置维护名
     * @param userId      用户id
     * @param roleId      角色id
     * @return 配置维护值
     */
    @ApiOperation(value = "根据配置维护名得到配置维护值", notes = "根据配置维护名得到配置维护值")
    @GetMapping("/profile-value")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "profileName", value = "配置维护名", paramType = "query", required = true),
            @ApiImplicitParam(name = "userId", value = "用户ID", paramType = "query", required = false),
            @ApiImplicitParam(name = "roleId", value = "角色ID", paramType = "query", required = false)})
    @Permission(level = ResourceLevel.SITE)
    public ResponseEntity<String> getProfileValueByProfileName(String profileName,
            @RequestParam(required = false) Long userId,
            @RequestParam(required = false) Long roleId) {
        return Results.success(profileValueDomainService.getProfileValueByProfileName(BaseConstants.DEFAULT_TENANT_ID, profileName, userId, roleId));
    }
}
