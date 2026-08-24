package org.hzero.platform.api.controller.v1;

import java.util.List;

import org.hzero.boot.platform.lov.annotation.ProcessLovValue;
import org.hzero.core.base.BaseConstants;
import org.hzero.core.base.BaseController;
import org.hzero.core.util.Results;
import org.hzero.mybatis.helper.SecurityTokenHelper;
import org.hzero.platform.api.dto.ProfileDTO;
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
 * 配置维护profile controller
 *
 * @author yunxiang.zhou01 @hand-china.com 2018/06/05
 */
@Api(tags = PlatformSwaggerApiConfig.PROFILE_MANAGE_SITE)
@RestController("profileManagerSiteController.v1")
@RequestMapping("/v1")
public class ProfileManagerSiteController extends BaseController {

    @Autowired
    private ProfileService profileService;

    @Autowired
    private ProfileValueService profileValueService;

    @Autowired
    private ProfileRepository profileRepository;

    /**
     * 查询配置维护信息
     *
     * @param profile     配置维护
     * @param pageRequest 分页工具类
     * @return
     */
    @ApiOperation(value = "查询配置维护信息", notes = "查询配置维护信息")
    @GetMapping("/profiles")
    @CustomPageRequest
    @Permission(level = ResourceLevel.SITE)
    @ProcessLovValue(targetField = BaseConstants.FIELD_BODY)
    public ResponseEntity list(Profile profile, @ApiIgnore @SortDefault(value = Profile.PROFILE_ID,
            direction = Sort.Direction.ASC) PageRequest pageRequest) {
        List<ProfileDTO> profileList = profileRepository.selectProfileLike(pageRequest, profile);
        return Results.success(profileList);
    }

    /**
     * 根据profileId查询配置维护信息
     *
     * @param profileId 主键id
     * @return 统一返回结果
     */
    @ApiOperation(value = "根据profileId查询配置维护信息", notes = "根据profileId查询配置维护信息")
    @GetMapping("/profiles/{profileId}")
    @ApiImplicitParams({@ApiImplicitParam(name = "profileId", value = "配置维护ID", paramType = "path", required = true)})
    @Permission(level = ResourceLevel.SITE)
    public ResponseEntity query(@PathVariable @Encrypt Long profileId) {
        Profile profile = new Profile();
        profile.setProfileId(profileId);
        return Results.success(profileRepository.selectProfile(profile));
    }

    /**
     * 新增和更新配置维护
     *
     * @param profile 配置维护
     * @return 统一返回结果
     */
    @ApiOperation(value = "新增和更新配置维护", notes = "新增和更新配置维护")
    @PostMapping("/profiles")
    @Permission(level = ResourceLevel.SITE)
    public ResponseEntity insertOrUpdate(@RequestBody @Encrypt Profile profile) {
        SecurityTokenHelper.validTokenIgnoreInsert(profile);
        profile.injectTenantId();
        profile.setProfileLevel(FndConstants.Level.TENANT);
        validObject(profile);
        validList(profile.getProfileValueList());
        return Results.success(profileService.insertOrUpdate(profile));
    }

    /**
     * 删除当前头的所有配置维护
     *
     * @param profile 配置维护
     * @return 统一返回结果
     */
    @ApiOperation(value = "删除配置维护", notes = "删除配置维护")
    @DeleteMapping("/profiles")
    @Permission(level = ResourceLevel.SITE)
    public ResponseEntity delete(@RequestBody @Encrypt Profile profile) {
        SecurityTokenHelper.validTokenIgnoreInsert(profile);
        profileService.delete(profile.getProfileId(), BaseConstants.DEFAULT_TENANT_ID, true);
        return Results.success();
    }

    /**
     * 从数据库删除行，也从redis中删除
     *
     * @param profileValue 配置维护值
     * @return 统一返回结果
     */
    @ApiOperation(value = "配置维护行删除", notes = "配置维护行删除")
    @DeleteMapping("/profiles-value")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "profileValueId", value = "配置维护值ID", paramType = "path", required = true)})
    @Permission(level = ResourceLevel.SITE)
    public ResponseEntity deleteValue(@RequestBody @Encrypt ProfileValue profileValue) {
        SecurityTokenHelper.validTokenIgnoreInsert(profileValue);
        profileValueService.delete(profileValue.getProfileValueId(), null);
        return Results.success();
    }
}
