package org.hzero.boot.platform.lov.controller.v1;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.hzero.boot.platform.lov.constant.LovConstants;
import org.hzero.boot.platform.lov.handler.LovPubSqlHandler;
import org.hzero.core.base.BaseConstants;
import org.hzero.core.base.BaseController;
import org.hzero.core.util.Results;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import io.choerodon.core.exception.CommonException;
import io.choerodon.swagger.annotation.Permission;

import io.swagger.annotations.ApiOperation;

/**
 * SQL值集服务内置Controller, 提供数据查询API
 *
 * @author gaokuo.dai@hand-china.com 2018年6月26日下午7:00:16
 */
@RestController("lovPubSqlController.v1")
public class LovPubSqlController extends BaseController {

    private final LovPubSqlHandler lovPubSqlHandler;

    private static final Logger logger = LoggerFactory.getLogger(LovPubSqlController.class);

    public LovPubSqlController(LovPubSqlHandler lovPubSqlHandler) {
        super();
        this.lovPubSqlHandler = lovPubSqlHandler;
    }

    @ApiOperation(value = "查询数据")
    @Permission(permissionPublic = true)
    @GetMapping("/v1/pub/lovs/sql/data")
    public ResponseEntity getSqlLovData(
            @RequestParam Map<String, Object> queryParams,
            @RequestParam(name = "lovCode") String lovCode,
            @RequestParam(name = "lang", required = false, defaultValue = "zh_CN") String lang,
            @RequestParam(defaultValue = BaseConstants.PAGE) int page,
            @RequestParam(defaultValue = BaseConstants.SIZE) int size) {
        try {
            return Results.success(this.lovPubSqlHandler.queryPubData(lovCode, lang, BaseConstants.DEFAULT_TENANT_ID, queryParams, page, size));
        } catch (Exception e) {
            return this.handleException(e);
        }
    }

    @ApiOperation(value = "查询数据")
    @Permission(permissionPublic = true)
    @GetMapping("/v1/pub/{organizationId}/lovs/translation-sql/data")
    public ResponseEntity getSqlTranslationData(
            @PathVariable("organizationId") Long organizationId,
            @RequestParam List<String> params,
            @RequestParam(name = "lovCode") String lovCode,
            @RequestParam(name = "lang", required = false, defaultValue = "zh_CN") String lang) {
        try {
            return Results.success(this.lovPubSqlHandler.queryPubTranslationData(lovCode, lang, organizationId, params));
        } catch (Exception e) {
            return this.handleException(e);
        }
    }

    @ApiOperation(value = "查询租户级数据")
    @Permission(permissionPublic = true)
    @GetMapping("/v1/pub/{organizationId}/lovs/sql/data")
    public ResponseEntity getOrgSqlLovData(
            @PathVariable("organizationId") Long tenantId,
            @RequestParam Map<String, Object> queryParams,
            @RequestParam(name = "lovCode") String lovCode,
            @RequestParam(name = "lang", required = false, defaultValue = "zh_CN") String lang,
            @RequestParam(defaultValue = BaseConstants.PAGE) int page,
            @RequestParam(defaultValue = BaseConstants.SIZE) int size
    ) {
        if (queryParams == null) {
            queryParams = new HashMap<>(1);
        }
        queryParams.put(LovConstants.Field.TENANT_ID, tenantId);
        try {
            return ResponseEntity.status(HttpStatus.OK).body(this.lovPubSqlHandler.queryPubData(lovCode, lang, tenantId, queryParams, page, size));
        } catch (Exception e) {
            return this.handleException(e);
        }
    }

    @ApiOperation(value = "查询值集含义")
    @Permission(permissionPublic = true)
    @GetMapping("/v1/pub/lovs/sql/meaning")
    public ResponseEntity getLovSqlMeaning(
            @RequestParam Map<String, Object> queryParams,
            @RequestParam(name = "lovCode") String lovCode,
            @RequestParam(name = "lang", required = false, defaultValue = "zh_CN") String lang
    ) {
        if (queryParams == null) {
            queryParams = new HashMap<>(1);
        }
        queryParams.put(LovConstants.Field.TENANT_ID, BaseConstants.DEFAULT_TENANT_ID);
        try {
            return Results.success(this.lovPubSqlHandler.getPubLovSqlMeaning(lovCode, lang, BaseConstants.DEFAULT_TENANT_ID, queryParams));
        } catch (Exception e) {
            return this.handleException(e);
        }
    }

    @ApiOperation(value = "查询租户级值集含义")
    @Permission(permissionPublic = true)
    @GetMapping("/v1/pub/{organizationId}/lovs/sql/meaning")
    public ResponseEntity getOrgLovSqlMeaning(
            @PathVariable("organizationId") Long tenantId,
            @RequestParam Map<String, Object> queryParams,
            @RequestParam String lovCode,
            @RequestParam(name = "lang", required = false, defaultValue = "zh_CN") String lang
    ) {
        if (queryParams == null) {
            queryParams = new HashMap<>(1);
        }
        queryParams.put(LovConstants.Field.TENANT_ID, tenantId);
        try {
            return Results.success(lovPubSqlHandler.getPubLovSqlMeaning(lovCode, lang, tenantId, queryParams));
        } catch (Exception e) {
            return this.handleException(e);
        }
    }

    /**
     * 处理异常
     *
     * @param e 异常
     * @return 包装后的异常返回值
     */
    private ResponseEntity<?> handleException(Exception e) {
        Map<String, Object> result = new HashMap<>(2);
        result.put(LovConstants.Field.FAIL, true);
        if (e != null) {
            logger.error(e.getMessage(), e);
            if (e instanceof CommonException) {
                CommonException ce = (CommonException) e;
                result.put(LovConstants.Field.MESSAGE, String.format(ce.getMessage(), ce.getParameters()));
            } else {
                result.put(LovConstants.Field.MESSAGE, LovConstants.ErrorMessage.ERROR_INVALIDE_SQL);
            }
        }
        return ResponseEntity.status(HttpStatus.OK).body(result);
    }

}
