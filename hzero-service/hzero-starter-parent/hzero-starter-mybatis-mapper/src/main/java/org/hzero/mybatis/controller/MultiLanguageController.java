package org.hzero.mybatis.controller;

import java.util.List;
import java.util.Map;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.util.Assert;
import org.springframework.util.CollectionUtils;
import org.springframework.web.bind.annotation.*;

import io.choerodon.swagger.annotation.Permission;

import org.hzero.core.base.BaseController;
import org.hzero.core.util.Results;
import org.hzero.mybatis.domian.MultiLanguage;
import org.hzero.mybatis.service.MultiLanguageService;

/**
 * <p>
 * 多语言查询API
 * </p>
 *
 * @author qingsheng.chen 2018/9/16 星期日 14:41
 */
@Api(value = "多语言查询API", hidden = true)
@RestController("mybatis.multiLanguageController.v1")
@RequestMapping("/v1/hidden/multi-language")
public class MultiLanguageController extends BaseController {
    private MultiLanguageService multiLanguageService;

    @Autowired
    public MultiLanguageController(MultiLanguageService multiLanguageService) {
        this.multiLanguageService = multiLanguageService;
    }

    @ApiOperation("多语言字段查询")
    @PostMapping
    @Permission(permissionLogin = true)
    public ResponseEntity<List<MultiLanguage>> listMultiLanguageHidden(@RequestParam String className,
                                                                 @RequestParam String fieldName,
                                                                 @RequestBody Map<String, Object> pkValue) {
        Assert.isTrue(!CollectionUtils.isEmpty(pkValue), "pk Value must be not empty!");
        return Results.success(multiLanguageService.listMultiLanguage(className, fieldName, pkValue));
    }
}
