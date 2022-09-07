package org.hzero.platform.api.controller.v1;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.hzero.core.base.BaseConstants;
import org.hzero.core.base.BaseController;
import org.hzero.core.util.Results;
import org.hzero.platform.api.dto.LovAggregateDTO;
import org.hzero.platform.api.dto.LovValueDTO;
import org.hzero.platform.app.service.LovService;
import org.hzero.platform.app.service.LovValueService;
import org.hzero.platform.config.PlatformSwaggerApiConfig;
import org.hzero.platform.domain.entity.Lov;
import org.hzero.platform.domain.entity.LovValue;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.util.Assert;
import org.springframework.web.bind.annotation.*;

import springfox.documentation.annotations.ApiIgnore;

import io.choerodon.core.domain.Page;
import io.choerodon.mybatis.pagehelper.annotation.SortDefault;
import io.choerodon.mybatis.pagehelper.domain.PageRequest;
import io.choerodon.swagger.annotation.CustomPageRequest;
import io.choerodon.swagger.annotation.Permission;

import io.swagger.annotations.*;

/**
 * 值集API v1
 *
 * @author gaokuo.dai@hand-china.com    2018年6月8日上午10:07:33
 */
@Api(tags = PlatformSwaggerApiConfig.LOV)
@RestController("lovController.v1")
@RequestMapping("/v1")
public class LovController extends BaseController {

    @Autowired
    private LovService lovService;
    @Autowired
    private LovValueService lovValueService;

    private static final Map<String, Object> EMPTY_OBJ = new HashMap<>();

    @ApiOperation("获取值集信息")
    @Permission(permissionLogin = true)
    @GetMapping("/{organizationId}/lovs/info")
    public ResponseEntity<Lov> queryLovInfo(
            @ApiParam(value = "值集代码", required = true) @RequestParam String lovCode,
            @ApiParam(value = "租户ID", required = true) @PathVariable("organizationId") Long organizationId,
            @ApiParam(value = "租户ID") @RequestParam(required = false) Long tenantId) {
        // 指定租户不等于当前租户，只允许访问 public 值集
        if (tenantId != null && !Objects.equals(tenantId, organizationId)) {
            return commonResult(this.lovService.queryLovInfo(lovCode, tenantId, null, !Objects.equals(tenantId, BaseConstants.DEFAULT_TENANT_ID)), false);
        } else {
            return commonResult(this.lovService.queryLovInfo(lovCode, organizationId, null), false);
        }
    }

    @ApiOperation("集成获取值集数据")
    @Permission(permissionLogin = true)
    @GetMapping("/{organizationId}/lovs/data")
    public ResponseEntity<List<Map<String, Object>>> queryLovData(
            @ApiParam(value = "值集代码", required = true) @RequestParam String lovCode,
            @ApiParam("租户ID") @PathVariable("organizationId") Long organizationId,
            @ApiParam("值tag") @RequestParam(required = false) String tag,
            @ApiParam("page") @RequestParam(required = false) Integer page,
            @ApiParam("size") @RequestParam(required = false) Integer size,
            @ApiIgnore @RequestParam Map<String, String> params,
            @ApiParam(value = "租户ID") @RequestParam(required = false) Long tenantId) {
        // 指定租户不等于当前租户，只允许访问 public 值集
        if (tenantId != null && !Objects.equals(tenantId, organizationId)) {
            return commonResult(this.lovService.queryLovData(lovCode, tenantId, tag, page, size, params, !Objects.equals(tenantId, BaseConstants.DEFAULT_TENANT_ID)), false);
        } else {
            return commonResult(this.lovService.queryLovData(lovCode, organizationId, tag, page, size, params), false);
        }
    }

    @ApiOperation("获取值集SQL")
    @Permission(permissionLogin = true)
    @GetMapping(path = "/{organizationId}/lovs/sql")
    public ResponseEntity<String> queryLovSql(
            @ApiParam(value = "值集代码", required = true) @RequestParam String lovCode,
            @ApiParam("租户ID") @PathVariable("organizationId") Long organizationId,
            @ApiParam(value = "租户ID") @RequestParam(required = false) Long tenantId) {
        String sql;
        if (tenantId != null && !Objects.equals(tenantId, organizationId)) {
            sql = this.lovService.queryLovSql(lovCode, tenantId, null, !Objects.equals(tenantId, BaseConstants.DEFAULT_TENANT_ID));
        } else {
            sql = this.lovService.queryLovSql(lovCode, organizationId,null);
        }
        if (StringUtils.isEmpty(sql)) {
            return Results.success();
        }
        return Results.success(sql);
    }

    @ApiOperation("获取值集翻译SQL")
    @Permission(permissionLogin = true)
    @GetMapping(path = "/{organizationId}/lovs/translation-sql")
    public ResponseEntity<String> queryLovTranslationSql(
            @ApiParam(value = "值集代码", required = true) @RequestParam String lovCode,
            @ApiParam("租户ID") @PathVariable("organizationId") Long organizationId,
            @ApiParam(value = "租户ID") @RequestParam(required = false) Long tenantId) {
        String sql;
        if (tenantId != null && !Objects.equals(tenantId, organizationId)) {
            sql = this.lovService.queryLovTranslationSql(lovCode, tenantId, null, !Objects.equals(tenantId, BaseConstants.DEFAULT_TENANT_ID));
        } else {
            sql = this.lovService.queryLovTranslationSql(lovCode, organizationId, null);
        }
        if (StringUtils.isEmpty(sql)) {
            return Results.success();
        }
        return Results.success(sql);
    }

    @ApiOperation("获取值集值")
    @Permission(permissionLogin = true)
    @GetMapping(path = "/{organizationId}/lovs/value")
    public ResponseEntity<List<LovValueDTO>> queryLovValue(
            @ApiParam(value = "值集代码", required = true) @RequestParam String lovCode,
            @ApiParam("租户ID") @PathVariable("organizationId") Long organizationId,
            @ApiParam("值tag") @RequestParam(required = false) String tag,
            @RequestParam(required = false) Long tenantId) {
        if (tenantId != null) {
            organizationId = tenantId;
        }
        return commonResult(this.lovValueService.queryLovValue(lovCode, organizationId, tag), true);
    }

    @ApiOperation("分页获取值集值")
    @Permission(permissionLogin = true)
    @GetMapping("/{organizationId}/lovs/value/page")
    @CustomPageRequest
    public ResponseEntity<Page<LovValueDTO>> pageLovValue(@ApiParam("租户ID") @PathVariable("organizationId") Long organizationId,
                                                          LovValue lovValue,
                                                          @RequestParam(required = false) Long tenantId,
                                                          @ApiIgnore @SortDefault(value = LovValue.FIELD_LOV_CODE) PageRequest pageRequest) {
        Assert.notNull(lovValue, BaseConstants.ErrorCode.DATA_NOT_EXISTS);
        Assert.notNull(lovValue.getLovCode(), BaseConstants.ErrorCode.DATA_NOT_EXISTS);
        if (tenantId != null) {
            organizationId = tenantId;
        }
        lovValue.setTenantId(organizationId);
        return Results.success(this.lovValueService.pageLovValue(lovValue, pageRequest));
    }

    @ApiOperation("批量获取值集值")
    @Permission(permissionLogin = true)
    @GetMapping(path = "/{organizationId}/lovs/value/batch")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "queryMap", value = "批量查询条件,形式:返回key=code", paramType = "query", example = "codeOne=CODE1&codeTwo=CODE2", required = true),
            @ApiImplicitParam(name = "tenantId", value = "租户ID", paramType = "query")
    })
    public ResponseEntity<Map<String, List<LovValueDTO>>> batchQueryLovValue(
            @RequestParam Map<String, String> queryMap,
            @PathVariable("organizationId") Long organizationId,
            @RequestParam(required = false) Long tenantId) {
        if (tenantId != null) {
            organizationId = tenantId;
        }
        return commonResult(this.lovValueService.batchQueryLovValue(queryMap, organizationId, null), false);
    }

    @ApiOperation("根据父值集值获取子值集值")
    @Permission(permissionLogin = true)
    @GetMapping(path = "/{organizationId}/lovs/value/parent-value")
    public ResponseEntity<List<LovValueDTO>> queryLovValueByParentValue(
            @ApiParam(value = "值集代码", required = true) @RequestParam String lovCode,
            @ApiParam("租户ID") @PathVariable("organizationId") Long organizationId,
            @RequestParam(required = false) Long tenantId,
            @ApiParam(value = "父值集值", required = true) @RequestParam String parentValue) {
        if (tenantId != null) {
            organizationId = tenantId;
        }
        return commonResult(this.lovValueService.queryLovValueByParentValue(lovCode, parentValue, organizationId), true);
    }

    @ApiOperation("根据Tag获取值集值")
    @Permission(permissionLogin = true)
    @GetMapping(path = "/{organizationId}/lovs/value/tag")
    public ResponseEntity<List<LovValueDTO>> queryLovValueByTag(
            @ApiParam(value = "值集代码", required = true) @RequestParam String lovCode,
            @ApiParam("租户ID") @PathVariable("organizationId") Long organizationId,
            @RequestParam(required = false) Long tenantId,
            @ApiParam(value = "tag值", required = true) @RequestParam String tag) {
        if (tenantId != null) {
            organizationId = tenantId;
        }
        return commonResult(this.lovValueService.queryLovValueByTag(lovCode, tag, organizationId), true);
    }

    @ApiOperation("获得父子值集树")
    @Permission(permissionLogin = true)
    @GetMapping(path = "/{organizationId}/lovs/value/tree")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "queryMap", value = "父子查询条件,形式:code=父子顺序", paramType = "query", example = "LEVEL_TOP_CODE=1&LEVEL_MEDIUM_CODE=2&LEVEL_BOTTOM_CODE=3", required = true),
            @ApiImplicitParam(name = "tenantId", value = "租户ID", paramType = "query")
    })
    public ResponseEntity<List<LovValueDTO>> queryLovValueTree(
            @RequestParam Map<String, String> queryMap,
            @PathVariable("organizationId") Long organizationId,
            @RequestParam(required = false) Long tenantId) {
        if (tenantId != null) {
            organizationId = tenantId;
        }
        return commonResult(this.lovValueService.queryLovValueTree(queryMap, organizationId), true);
    }

    @ApiOperation("获取值集信息")
    @Permission(permissionLogin = true)
    @GetMapping("/lovs/info")
    public ResponseEntity<Lov> queryLovInfoSite(
            @ApiParam(value = "值集代码", required = true) @RequestParam String lovCode,
            @ApiParam("租户ID") @RequestParam(required = false) Long tenantId) {
        return commonResult(this.lovService.queryLovInfo(lovCode, tenantId, null), false);
    }

    @ApiOperation("集成获取值集数据")
    @Permission(permissionLogin = true)
    @GetMapping("/lovs/data")
    public ResponseEntity<List<Map<String, Object>>> queryLovDataSite(
            @ApiParam(value = "值集代码", required = true) @RequestParam String lovCode,
            @ApiParam("租户ID") @RequestParam(required = false) Long tenantId,
            @ApiParam("值tag") @RequestParam(required = false) String tag,
            @ApiParam("page") @RequestParam(required = false) Integer page,
            @ApiParam("size") @RequestParam(required = false) Integer size,
            @ApiIgnore @RequestParam Map<String, String> params
    ) {
        return commonResult(this.lovService.queryLovData(lovCode, tenantId, tag, page, size, params), false);
    }

    @ApiOperation("获取值集SQL")
    @Permission(permissionLogin = true)
    @GetMapping(path = "/lovs/sql")
    public ResponseEntity<String> queryLovSqlSite(
            @ApiParam(value = "值集代码", required = true) @RequestParam String lovCode,
            @ApiParam("租户ID") @RequestParam(required = false) Long tenantId) {
        String sql = this.lovService.queryLovSql(lovCode, tenantId, null);
        if (StringUtils.isEmpty(sql)) {
            return Results.success();
        }
        return Results.success(sql);
    }

    @ApiOperation("获取值集翻译SQL")
    @Permission(permissionLogin = true)
    @GetMapping(path = "/lovs/translation-sql")
    public ResponseEntity<String> queryLovTranslationSqlSite(
            @ApiParam(value = "值集代码", required = true) @RequestParam String lovCode,
            @ApiParam("租户ID") @RequestParam(required = false) Long tenantId) {
        String sql = this.lovService.queryLovTranslationSql(lovCode, tenantId, null);
        if (StringUtils.isEmpty(sql)) {
            return Results.success();
        }
        return Results.success(sql);
    }

    @ApiOperation("获取值集值")
    @Permission(permissionLogin = true)
    @GetMapping(path = "/lovs/value")
    public ResponseEntity<List<LovValueDTO>> queryLovValueSite(
            @ApiParam(value = "值集代码", required = true) @RequestParam String lovCode,
            @ApiParam("租户ID") @RequestParam(required = false) Long tenantId,
            @ApiParam("值tag") @RequestParam(required = false) String tag,
            @RequestParam(name = "lang", required = false) String lang) {
        return commonResult(this.lovValueService.queryLovValue(lovCode, tenantId, tag, lang), true);
    }

    @ApiOperation("分页获取值集值")
    @Permission(permissionLogin = true)
    @GetMapping("/lovs/value/page")
    @CustomPageRequest
    public ResponseEntity<Page<LovValueDTO>> pageLovValueSite(LovValue lovValue,
                                                              @ApiIgnore @SortDefault(value = LovValue.FIELD_LOV_CODE) PageRequest pageRequest) {
        Assert.notNull(lovValue, BaseConstants.ErrorCode.DATA_NOT_EXISTS);
        Assert.notNull(lovValue.getLovCode(), BaseConstants.ErrorCode.DATA_NOT_EXISTS);
        return Results.success(this.lovValueService.pageLovValue(lovValue, pageRequest));
    }

    @ApiOperation("批量获取值集值")
    @Permission(permissionLogin = true)
    @GetMapping(path = "/lovs/value/batch")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "queryMap", value = "批量查询条件,形式:code=返回key", paramType = "query", example = "CODE1=codeOne&CODE2=codeTwo", required = true),
            @ApiImplicitParam(name = "tenantId", value = "租户ID", paramType = "query")
    })
    public ResponseEntity<Map<String, List<LovValueDTO>>> batchQueryLovValueSite(
            @RequestParam Map<String, String> queryMap,
            @RequestParam(required = false) Long tenantId) {
        return commonResult(this.lovValueService.batchQueryLovValue(queryMap, tenantId, null), false);
    }

    @ApiOperation("根据父值集值获取子值集值")
    @Permission(permissionLogin = true)
    @GetMapping(path = "/lovs/value/parent-value")
    public ResponseEntity<List<LovValueDTO>> queryLovValueByParentValueSite(
            @ApiParam(value = "值集代码", required = true) @RequestParam String lovCode,
            @ApiParam("租户ID") @RequestParam(required = false) Long tenantId,
            @ApiParam(value = "父值集值", required = true) @RequestParam String parentValue) {
        return commonResult(this.lovValueService.queryLovValueByParentValue(lovCode, parentValue, tenantId), true);
    }

    @ApiOperation("根据Tag获取值集值")
    @Permission(permissionLogin = true)
    @GetMapping(path = "/lovs/value/tag")
    public ResponseEntity<List<LovValueDTO>> queryLovValueByTagSite(
            @ApiParam(value = "值集代码", required = true) @RequestParam String lovCode,
            @ApiParam("租户ID") @RequestParam(required = false) Long tenantId,
            @ApiParam(value = "tag值", required = true) @RequestParam String tag) {
        return commonResult(this.lovValueService.queryLovValueByTag(lovCode, tag, tenantId), true);
    }

    @ApiOperation("获得父子值集树")
    @Permission(permissionLogin = true)
    @GetMapping(path = "/lovs/value/tree")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "queryMap", value = "父子查询条件,形式:code=父子顺序", paramType = "query", example = "LEVEL_TOP_CODE=1&LEVEL_MEDIUM_CODE=2&LEVEL_BOTTOM_CODE=3", required = true),
            @ApiImplicitParam(name = "tenantId", value = "租户ID", paramType = "query")
    })
    public ResponseEntity<List<LovValueDTO>> queryLovValueTreeSite(
            @RequestParam Map<String, String> queryMap,
            @RequestParam(required = false) Long tenantId) {
        return commonResult(this.lovValueService.queryLovValueTree(queryMap, tenantId), true);
    }

    @ApiOperation("聚合获取值集头行数据")
    @Permission(permissionLogin = true)
    @GetMapping(path = "/lovs/aggregate")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "lovCode", value = "值集编码", paramType = "query", required = true),
            @ApiImplicitParam(name = "tenantId", value = "租户ID", paramType = "query", defaultValue = "0"),
            @ApiImplicitParam(name = "lang", value = "语言", paramType = "query"),
            @ApiImplicitParam(name = "tag", value = "标记，仅独立值集时可作为查询条件传递", paramType = "query")
    })
    public ResponseEntity<LovAggregateDTO> queryLovAggregateLovValues(@RequestParam("lovCode") String lovCode,
            @RequestParam(name = "tenantId", required = false) Long tenantId,
            @RequestParam(name = "lang", required = false) String lang,
            @RequestParam(name = "tag", required = false) String tag) {
        return commonResult(this.lovService.queryLovAggregateLovValues(lovCode, tenantId, lang, tag), true);
    }

    /**
     * 通用返回值处理程序
     *
     * @param payload      返回值
     * @param isCollection 返回值是否为集合
     * @return ResponseEntity
     */
    @SuppressWarnings("unchecked")
    private <T> ResponseEntity<T> commonResult(T payload, boolean isCollection) {
        if (payload == null) {
            if (isCollection) {
                return (ResponseEntity<T>) Results.success(CollectionUtils.EMPTY_COLLECTION);
            } else {
                return (ResponseEntity<T>) Results.success(EMPTY_OBJ);
            }
        } else {
            return Results.success(payload);
        }
    }

}
