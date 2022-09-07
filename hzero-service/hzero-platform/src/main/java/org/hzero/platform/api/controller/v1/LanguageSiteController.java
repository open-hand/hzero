package org.hzero.platform.api.controller.v1;

import java.util.List;
import java.util.Optional;

import javax.validation.Valid;

import org.hzero.core.base.BaseController;
import org.hzero.core.util.Results;
import org.hzero.platform.app.service.HpfmLanguageService;
import org.hzero.platform.config.PlatformSwaggerApiConfig;
import org.hzero.platform.domain.entity.Language;
import org.hzero.starter.keyencrypt.core.Encrypt;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import springfox.documentation.annotations.ApiIgnore;

import io.choerodon.core.domain.Page;
import io.choerodon.core.exception.NotFoundException;
import io.choerodon.core.iam.ResourceLevel;
import io.choerodon.mybatis.pagehelper.annotation.SortDefault;
import io.choerodon.mybatis.pagehelper.domain.PageRequest;
import io.choerodon.mybatis.pagehelper.domain.Sort;
import io.choerodon.swagger.annotation.CustomPageRequest;
import io.choerodon.swagger.annotation.Permission;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;

/**
 * @author superlee
 */
@Api(tags = PlatformSwaggerApiConfig.LANGUAGE_SITE)
@RestController("languageSiteController.v1")
@RequestMapping("/v1/languages")
public class LanguageSiteController extends BaseController {

    private HpfmLanguageService languageService;

    public LanguageSiteController(HpfmLanguageService languageService) {
        this.languageService = languageService;
    }

    @ApiOperation(value = "分页查询Language")
    @Permission(level = ResourceLevel.SITE, permissionLogin = true)
    @GetMapping
    @CustomPageRequest
    public ResponseEntity<Page<Language>> pageLanguage(@RequestParam(required = false) String code,
                                                       @RequestParam(required = false) String name,
                                                       @ApiIgnore @SortDefault(value = "id", direction = Sort.Direction.ASC) PageRequest pageRequest) {
        return Results.success(languageService.pageLanguage(code, name, pageRequest));
    }

    @ApiOperation(value = "通过code查询Language")
    @Permission(level = ResourceLevel.SITE, permissionLogin = true)
    @GetMapping(value = "/code")
    public ResponseEntity<Language> queryLanguage(@RequestParam(name = "value") String code) {
        return Optional.ofNullable(languageService.queryLanguage(code)).map(Results::success).orElseThrow(NotFoundException::new);
    }

    @ApiOperation(value = "修改Language")
    @Permission(level = ResourceLevel.SITE)
    @PutMapping(value = "/{id}")
    public ResponseEntity<Language> updateLanguage(@PathVariable @Encrypt Long id,
                                                   @RequestBody @Valid Language language) {
        this.validObject(language);
        return Results.success(languageService.updateLanguage(language.setId(id)));
    }
}
