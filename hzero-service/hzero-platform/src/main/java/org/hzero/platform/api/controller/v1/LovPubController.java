package org.hzero.platform.api.controller.v1;

import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.hzero.core.base.BaseConstants;
import org.hzero.core.util.Results;
import org.hzero.platform.api.dto.LovValueDTO;
import org.hzero.platform.app.service.LovService;
import org.hzero.platform.app.service.LovValueService;
import org.hzero.platform.app.service.LovViewHeaderService;
import org.hzero.platform.config.PlatformSwaggerApiConfig;
import org.hzero.platform.domain.entity.Lov;
import org.hzero.platform.domain.vo.LovViewVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import springfox.documentation.annotations.ApiIgnore;

import io.choerodon.core.exception.CommonException;
import io.choerodon.swagger.annotation.Permission;

import io.swagger.annotations.*;

/**
 * 值集公开API
 *
 * @author xiaoyu.zhao@hand-china.com 2020/09/08 19:38
 */
@Api(tags = PlatformSwaggerApiConfig.LOV_PUB)
@RestController("lovPubController.v1")
@RequestMapping("/v1/pub")
public class LovPubController {

    @Autowired
    private LovService lovService;
    @Autowired
    private LovValueService lovValueService;
    @Autowired
    private LovViewHeaderService lovViewHeaderService;

    @ApiOperation("获取值集信息")
    @Permission(permissionPublic = true)
    @GetMapping("/lovs/info")
    public ResponseEntity<Lov> queryLovInfo(
            @ApiParam(value = "值集代码", required = true) @RequestParam String lovCode,
            @ApiParam("租户ID") @RequestParam(required = false) Long tenantId,
            @ApiParam("语言") @RequestParam(required = false, defaultValue = "zh_CN") String lang) {
        return Results.success(lovService.queryLovInfo(lovCode, tenantId, lang, true));
    }

    @ApiOperation("获取值集值--公开接口")
    @Permission(permissionPublic = true)
    @GetMapping(path = "/lovs/value")
    public ResponseEntity<List<LovValueDTO>> queryLovValue(
            @ApiParam(value = "值集代码", required = true) @RequestParam String lovCode,
            @ApiParam("值tag") @RequestParam(required = false) String tag,
            @RequestParam(required = false) Long tenantId,
            @RequestParam(name = "lang", required = false, defaultValue = "zh_CN") String lang) {
        return Results.success(lovValueService.queryLovValue(lovCode, tenantId, tag, lang));
    }

    @ApiOperation("批量获取值集值--公开接口")
    @Permission(permissionPublic = true)
    @GetMapping(path = "/lovs/value/batch")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "queryMap", value = "批量查询条件,形式:code=返回key", paramType = "query", example = "CODE1=codeOne&CODE2=codeTwo", required = true),
            @ApiImplicitParam(name = "tenantId", value = "租户ID", paramType = "query")
    })
    public ResponseEntity<Map<String, List<LovValueDTO>>> batchQueryLovValueSite(
            @RequestParam Map<String, String> queryMap,
            @RequestParam(required = false) Long tenantId,
            @RequestParam(required = false, defaultValue = "zh_CN") String lang) {
        // 去除map中错误接收的语言内容
        queryMap.remove("lang");
        return Results.success(lovValueService.batchQueryLovValue(queryMap, tenantId, lang));
    }

    @ApiOperation("集成获取值集数据")
    @Permission(permissionPublic = true)
    @GetMapping("/lovs/data")
    public ResponseEntity<List<Map<String, Object>>> queryPubLovData(
            @ApiParam(value = "值集代码", required = true) @RequestParam String lovCode,
            @ApiParam("租户ID") @RequestParam(required = false) Long tenantId,
            @ApiParam("值tag") @RequestParam(required = false) String tag,
            @ApiParam("page") @RequestParam(required = false) Integer page,
            @ApiParam("size") @RequestParam(required = false) Integer size,
            @ApiParam("语言") @RequestParam(required = false, defaultValue = "zh_CN") String lang,
            @ApiIgnore @RequestParam Map<String, String> params
    ) {
        return Results.success(this.lovService.queryPubLovData(lovCode, tenantId, tag, page, size, params, lang, true));
    }

    /**
     * 获取值集视图信息
     *
     * @param viewCode 视图代码
     * @param tenantId 租户ID(可空)
     * @return 值集视图信息
     */
    @ApiOperation("获取值集视图信息--公开接口")
    @Permission(permissionPublic = true)
    @GetMapping("/lov-view/info")
    public ResponseEntity<LovViewVO> queryLovViewInfo(@ApiParam(value = "视图代码", required = true) @RequestParam("viewCode") String viewCode,
            @ApiParam("租户ID") @RequestParam(name = "tenantId", required = false) Long tenantId,
            @ApiParam("语言") @RequestParam(required = false, defaultValue = "zh_CN") String lang) {
        return viewCommonResult(lovViewHeaderService.queryLovViewInfo(viewCode, tenantId, lang, true));
    }

    @ApiOperation("获取值集SQL--公开接口")
    @Permission(permissionPublic = true)
    @GetMapping(path = "/lovs/sql")
    public ResponseEntity<String> queryLovSql(
            @ApiParam(value = "值集代码", required = true) @RequestParam String lovCode,
            @ApiParam("租户ID") @RequestParam(required = false) Long tenantId,
            @ApiParam("语言") @RequestParam(required = false, defaultValue = "zh_CN") String lang) {
        String sql = lovService.queryLovSql(lovCode, tenantId, lang, true);
        if (StringUtils.isEmpty(sql)) {
            return Results.success();
        }
        return Results.success(sql);
    }

    @ApiOperation("获取值集翻译SQL--公开接口")
    @Permission(permissionPublic = true)
    @GetMapping(path = "/lovs/translation-sql")
    public ResponseEntity<String> queryLovTranslationSql(
            @ApiParam(value = "值集代码", required = true) @RequestParam String lovCode,
            @ApiParam("租户ID") @RequestParam(required = false) Long tenantId,
            @ApiParam("语言") @RequestParam(required = false, defaultValue = "zh_CN") String lang) {
        String sql = lovService.queryLovTranslationSql(lovCode, tenantId, lang, true);
        if (StringUtils.isEmpty(sql)) {
            return Results.success();
        }
        return Results.success(sql);
    }

    /**
     * 通用返回值处理程序
     *
     * @param payload 载荷
     * @return ResponseEntity 返回值
     */
    private <T> ResponseEntity<T> viewCommonResult(T payload) {
        if (payload == null) {
            throw new CommonException(BaseConstants.ErrorCode.FORBIDDEN);
        } else {
            return Results.success(payload);
        }
    }

}
