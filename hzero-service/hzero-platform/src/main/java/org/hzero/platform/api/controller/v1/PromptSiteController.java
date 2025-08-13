package org.hzero.platform.api.controller.v1;

import org.hzero.core.base.BaseController;
import org.hzero.core.util.Results;
import org.hzero.platform.config.PlatformSwaggerApiConfig;
import org.hzero.platform.domain.repository.PromptRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import io.choerodon.core.iam.ResourceLevel;
import io.choerodon.swagger.annotation.Permission;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;


/**
 * 租户级多语言描述获取
 *
 * @author yunxiang.zhou01@hand-china.com 2018-06-21 16:02:17
 */
@Api(tags = PlatformSwaggerApiConfig.PROMPT_SITE)
@RestController("promptSiteController.v1")
@RequestMapping("/v1")
public class PromptSiteController extends BaseController {

    @Autowired
    private PromptRepository promptRepository;

    /**
     * 租户级获取多语言标签描述
     *
     * @param organizationId 租户id
     * @param lang           语言
     * @param promptKey      多语言key
     * @return 统一返回结果
     * @author yunxiang.zhou01@hand-china.com 2018-06-25 11:27
     */
    @ApiOperation(value = "获取多语言描述")
    @Permission(permissionPublic = true, level = ResourceLevel.SITE)
    @GetMapping("/prompt/{lang}")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "organizationId", value = "租户ID", paramType = "query", required = true),
            @ApiImplicitParam(name = "lang", value = "语言", paramType = "path", required = true),
            @ApiImplicitParam(name = "promptKey", value = "语言KEY", paramType = "query", required = true)
    })
    public ResponseEntity getDescriptionByTenantId(Long organizationId,
                                                   @PathVariable String lang,
                                                   @RequestParam String[] promptKey) {
        return Results.success(promptRepository.getDescription(promptKey, lang, organizationId));
    }
}
