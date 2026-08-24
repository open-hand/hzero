package org.hzero.platform.api.controller.v1;

import org.hzero.boot.platform.lov.annotation.ProcessLovValue;
import org.hzero.core.base.BaseConstants;
import org.hzero.core.base.BaseController;
import org.hzero.core.util.Results;
import org.hzero.mybatis.helper.SecurityTokenHelper;
import org.hzero.platform.app.service.ProfileService;
import org.hzero.platform.app.service.ProfileValueService;
import org.hzero.platform.config.PlatformSwaggerApiConfig;
import org.hzero.platform.domain.entity.Profile;
import org.hzero.platform.domain.entity.ProfileValue;
import org.hzero.platform.domain.repository.ProfileRepository;
import org.hzero.platform.infra.constant.FndConstants;
import org.hzero.starter.keyencrypt.core.Encrypt;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import springfox.documentation.annotations.ApiIgnore;

import io.choerodon.core.iam.ResourceLevel;
import io.choerodon.mybatis.pagehelper.annotation.SortDefault;
import io.choerodon.mybatis.pagehelper.domain.PageRequest;
import io.choerodon.mybatis.pagehelper.domain.Sort;
import io.choerodon.swagger.annotation.CustomPageRequest;
import io.choerodon.swagger.annotation.Permission;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;

/**
 * <p>
 * 配置维护租户级管理接口
 * </p>
 *
 * @author yunxiang.zhou01@hand-china.com 2018/06/27 11:48
 */
@Api(tags = PlatformSwaggerApiConfig.PROFILE_MANAGE)
@RestController("profileManagerController.v1")
@RequestMapping("/v1")
public class ProfileManagerController extends BaseController {

    @Autowired
    private ProfileRepository profileRepository;
    @Autowired
    private ProfileService profileService;
    @Autowired
    private ProfileValueService profileValueService;

    /**
     * 查询配置维护信息
     *
     * @param organizationId 租户id
     * @param profile 配置维护
     * @param pageRequest 分页工具类
     * @return 配置维护信息
     */
    @ApiOperation(value = "查询配置维护信息", notes = "查询配置维护信息")
    @GetMapping("/{organizationId}/profiles")
    @ApiImplicitParams({@ApiImplicitParam(name = "organizationId", value = "租户ID", paramType = "path", required = true)})
    @CustomPageRequest
    @Permission(level = ResourceLevel.ORGANIZATION)
    @ProcessLovValue(targetField = BaseConstants.FIELD_BODY)
    public ResponseEntity list(@PathVariable Long organizationId, Profile profile, @ApiIgnore @SortDefault(
                    value = Profile.PROFILE_ID, direction = Sort.Direction.ASC) PageRequest pageRequest) {
        profile.setTenantId(organizationId);
        return Results.success(profileRepository.selectTenantProfileLike(pageRequest, profile));
    }

    /**
     * 租户级根据profileId查询配置维护信息
     *
     * @param profileId 主键id
     * @return 统一返回结果
     */
    @ApiOperation(value = "租户级根据profileId查询配置维护信息", notes = "租户级根据profileId查询配置维护信息")
    @GetMapping("/{organizationId}/profiles/{profileId}")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "organizationId", value = "租户ID", paramType = "path", required = true),
            @ApiImplicitParam(name = "profileId", value = "配置维护ID", paramType = "path", required = true)})
    @Permission(level = ResourceLevel.ORGANIZATION)
    public ResponseEntity query(@PathVariable Long organizationId, @PathVariable @Encrypt Long profileId) {
        Profile profile = new Profile();
        profile.setProfileId(profileId);
        profile.setTenantId(organizationId);
        return Results.success(profileRepository.selectProfile(profile));
    }

    /**
     * 租户级新增和更新配置维护
     *
     * @param profile 配置维护
     * @return 统一返回结果
     */
    @ApiOperation(value = "租户级新增和更新配置维护", notes = "租户级新增和更新配置维护")
    @PostMapping("/{organizationId}/profiles")
    @ApiImplicitParams({@ApiImplicitParam(name = "organizationId", value = "租户ID", paramType = "path", required = true)})
    @Permission(level = ResourceLevel.ORGANIZATION)
    public ResponseEntity insertOrUpdate(@PathVariable Long organizationId, @RequestBody @Encrypt Profile profile) {
        profile.setTenantId(organizationId);
        profile.setProfileLevel(FndConstants.Level.TENANT);
        SecurityTokenHelper.validTokenIgnoreInsert(profile);
        validObject(profile);
        validList(profile.getProfileValueList());
        return Results.success(profileService.insertOrUpdate(profile));
    }

    /**
     * 租户级删除当前头的所有配置维护
     *
     * @param profile 配置维护
     * @return 统一返回结果
     */
    @ApiOperation(value = "租户级删除当前头的所有配置维护", notes = "租户级删除当前头的所有配置维护")
    @DeleteMapping("/{organizationId}/profiles")
    @ApiImplicitParams({@ApiImplicitParam(name = "organizationId", value = "租户ID", paramType = "path", required = true)})
    @Permission(level = ResourceLevel.ORGANIZATION)
    public ResponseEntity delete(@PathVariable Long organizationId, @RequestBody @Encrypt Profile profile) {
        SecurityTokenHelper.validTokenIgnoreInsert(profile);
        profileService.delete(profile.getProfileId(), organizationId, false);
        return Results.success();
    }

    /**
     * 租户级从数据库删除行，也从redis中删除
     *
     * @param profileValue 配置维护行
     * @return 统一返回结果
     */
    @ApiOperation(value = "租户级配置维护行删除", notes = "租户级配置维护行删除")
    @DeleteMapping("/{organizationId}/profiles-value")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "organizationId", value = "租户ID", paramType = "path", required = true),
            @ApiImplicitParam(name = "profileValueId", value = "配置维护值ID", paramType = "path", required = true)})
    @Permission(level = ResourceLevel.ORGANIZATION)
    public ResponseEntity deleteValue(@PathVariable Long organizationId, @RequestBody @Encrypt ProfileValue profileValue) {
        SecurityTokenHelper.validTokenIgnoreInsert(profileValue);
        profileValueService.delete(profileValue.getProfileValueId(), organizationId);
        return Results.success();
    }

    @ApiOperation(value = "按照层级逐级查询配置维护值")
    @GetMapping("/profiles/value")
    @ApiImplicitParams({@ApiImplicitParam(name = "tenantId", value = "租户ID", paramType = "query", required = true),
            @ApiImplicitParam(name = "profileName", value = "配置维护名称", paramType = "query", required = true),
            @ApiImplicitParam(name = "userId", value = "用户ID", paramType = "query", required = true),
            @ApiImplicitParam(name = "roleId", value = "角色ID", paramType = "query", required = true)})
    @Permission(permissionWithin = true)
    @ProcessLovValue(targetField = BaseConstants.FIELD_BODY)
    public ResponseEntity<String> getProfileValueByLevel(@RequestParam("tenantId") Long tenantId,
                    @RequestParam("profileName") String profileName, @RequestParam("userId") Long userId,
                    @RequestParam("roleId") Long roleId) {
        return Results.success(profileRepository.getProfileValueByLevel(tenantId, profileName, userId, roleId));
    }
}
