package org.hzero.platform.api.controller.v1;

import java.util.List;
import java.util.Optional;
import javax.validation.Valid;

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
import org.hzero.core.base.BaseController;
import org.hzero.core.util.Results;
import org.hzero.platform.app.service.HpfmLanguageService;
import org.hzero.platform.config.PlatformSwaggerApiConfig;
import org.hzero.platform.domain.entity.Language;
import org.hzero.starter.keyencrypt.core.Encrypt;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import springfox.documentation.annotations.ApiIgnore;

/**
 * @author superlee
 */
@Api(tags = PlatformSwaggerApiConfig.LANGUAGE)
@RestController("languageController.v1")
@RequestMapping("/v1")
public class LanguageController extends BaseController {

    private HpfmLanguageService languageService;

    public LanguageController(HpfmLanguageService languageService) {
        this.languageService = languageService;
    }

    @ApiOperation(value = "查询language列表")
    @Permission(permissionLogin = true)
    @GetMapping(value = "/languages/list")
    public ResponseEntity<List<Language>> listLanguage() {
        return Results.success(languageService.listLanguage());
    }

    @ApiOperation(value = "分页查询Language")
    @Permission(level = ResourceLevel.ORGANIZATION)
    @GetMapping("/{organizationId}/languages")
    @CustomPageRequest
    public ResponseEntity<Page<Language>> pageLanguage(@RequestParam(required = false) String code,
                                                       @RequestParam(required = false) String name,
                                                       @ApiIgnore @SortDefault(value = "id", direction = Sort.Direction.ASC) PageRequest pageRequest) {
        return Results.success(languageService.pageLanguage(code, name, pageRequest));
    }

    @ApiOperation(value = "通过code查询Language")
    @Permission(level = ResourceLevel.ORGANIZATION)
    @GetMapping(value = "/{organizationId}/languages/code")
    public ResponseEntity<Language> queryLanguage(@RequestParam(name = "value") String code) {
        return Optional.ofNullable(languageService.queryLanguage(code)).map(Results::success).orElseThrow(NotFoundException::new);
    }

    @ApiOperation(value = "修改Language")
    @Permission(level = ResourceLevel.ORGANIZATION)
    @PutMapping(value = "/{organizationId}/languages/{id}")
    public ResponseEntity<Language> updateLanguage(@PathVariable @Encrypt Long id,
                                                   @RequestBody @Valid Language language) {
        this.validObject(language);
        return Results.success(languageService.updateLanguage(language.setId(id)));
    }
}
