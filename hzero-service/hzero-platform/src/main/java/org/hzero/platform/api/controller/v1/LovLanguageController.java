package org.hzero.platform.api.controller.v1;

import java.util.List;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.hzero.core.base.BaseController;
import org.hzero.core.util.Results;
import org.hzero.platform.app.service.HpfmLanguageService;
import org.hzero.platform.config.PlatformSwaggerApiConfig;
import org.hzero.platform.domain.entity.Language;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import io.choerodon.swagger.annotation.Permission;

/**
 * @author superlee
 */
@Api(tags = PlatformSwaggerApiConfig.LANGUAGE)
@RestController("lovLanguageController.v1")
@RequestMapping("/v1/languages/lov")
public class LovLanguageController extends BaseController {

    private HpfmLanguageService languageService;

    public LovLanguageController(HpfmLanguageService languageService) {
        this.languageService = languageService;
    }

    @ApiOperation(value = "LOV 查询language列表")
    @Permission(permissionLogin = true)
    @GetMapping(value = "/list")
    public ResponseEntity<List<Language>> lovListLanguage() {
        return Results.success(languageService.listLanguage());
    }

}
