package org.hzero.platform.api.controller.v1;

import com.fasterxml.jackson.core.JsonProcessingException;
import io.choerodon.swagger.annotation.Permission;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.hzero.core.base.BaseController;
import org.hzero.core.util.Results;
import org.hzero.mybatis.domian.MultiLanguage;
import org.hzero.platform.app.service.MultiLanguageService;
import org.hzero.platform.config.PlatformSwaggerApiConfig;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.io.UnsupportedEncodingException;
import java.util.List;

/**
 * <p>
 * 多语言查询
 * </p>
 *
 * @author qingsheng.chen 2018/9/16 星期日 15:55
 */
@Api(tags = PlatformSwaggerApiConfig.MULTI_LANGUAGE)
@RestController("multiLanguageController.v1")
@RequestMapping("/v1/multi-language")
public class MultiLanguageController extends BaseController {
    private MultiLanguageService multiLanguageService;

    @Autowired
    public MultiLanguageController(MultiLanguageService multiLanguageService) {
        this.multiLanguageService = multiLanguageService;
    }

    @ApiOperation("多语言字段查询")
    @GetMapping
    @Permission(permissionLogin = true)
    public ResponseEntity<List<MultiLanguage>> listMultiLanguage(@RequestParam(required = false) String _token, @RequestParam String fieldName) throws JsonProcessingException, UnsupportedEncodingException {
        return Results.success(multiLanguageService.responseListMultiLanguage(_token, fieldName));
    }
}
