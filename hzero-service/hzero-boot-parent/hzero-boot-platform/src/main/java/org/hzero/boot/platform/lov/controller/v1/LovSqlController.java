package org.hzero.boot.platform.lov.controller.v1;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import io.swagger.annotations.ApiOperation;
import org.hzero.boot.platform.lov.constant.LovConstants;
import org.hzero.boot.platform.lov.handler.LovSqlHandler;
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

/**
 * SQL值集服务内置Controller, 提供数据查询API
 *
 * @author gaokuo.dai@hand-china.com 2018年6月26日下午7:00:16
 */
@RestController("lovSqlController.v1")
public class LovSqlController extends BaseController {

    private final LovSqlHandler lovSqlHandler;

    private static final Logger logger = LoggerFactory.getLogger(LovSqlController.class);

    public LovSqlController(LovSqlHandler lovSqlHandler) {
        super();
        this.lovSqlHandler = lovSqlHandler;
    }

    @SuppressWarnings("rawtypes")
    @ApiOperation(value = "查询数据")
    @Permission(permissionLogin = true)
    @GetMapping("/v1/lovs/sql/data")
    public ResponseEntity getSqlLovData(
            @RequestParam Map<String, Object> queryParams,
            @RequestParam String lovCode,
            @RequestParam(defaultValue = BaseConstants.PAGE) int page,
            @RequestParam(defaultValue = BaseConstants.SIZE) int size) {
        try {
            return Results.success(this.lovSqlHandler.queryData(lovCode, BaseConstants.DEFAULT_TENANT_ID, queryParams, page, size));
        } catch (Exception e) {
            return this.handleException(e);
        }
    }

    @SuppressWarnings("rawtypes")
    @ApiOperation(value = "查询数据")
    @Permission(permissionLogin = true)
    @GetMapping("/v1/{organizationId}/lovs/translation-sql/data")
    public ResponseEntity getSqlTranslationData(
            @PathVariable("organizationId") Long organizationId,
            @RequestParam Map<String, Object> queryParams,
            @RequestParam List<String> params,
            @RequestParam String lovCode) {
        try {
            return Results.success(this.lovSqlHandler.queryTranslationData(lovCode, organizationId, queryParams, params));
        } catch (Exception e) {
            return this.handleException(e);
        }
    }

    @SuppressWarnings("rawtypes")
    @ApiOperation(value = "查询租户级数据")
    @Permission(permissionLogin = true)
    @GetMapping("/v1/{organizationId}/lovs/sql/data")
    public ResponseEntity getOrgSqlLovData(
            @PathVariable("organizationId") Long tenantId,
            @RequestParam Map<String, Object> queryParams,
            @RequestParam String lovCode,
            @RequestParam(defaultValue = BaseConstants.PAGE) int page,
            @RequestParam(defaultValue = BaseConstants.SIZE) int size
    ) {
        if (queryParams == null) {
            queryParams = new HashMap<>(1);
        }
        queryParams.put(LovConstants.Field.TENANT_ID, tenantId);
        try {
            return ResponseEntity.status(HttpStatus.OK).body(this.lovSqlHandler.queryData(lovCode, tenantId, queryParams, page, size));
        } catch (Exception e) {
            return this.handleException(e);
        }
    }

    @SuppressWarnings("rawtypes")
    @ApiOperation(value = "查询值集含义")
    @Permission(permissionLogin = true)
    @GetMapping("/v1/lovs/sql/meaning")
    public ResponseEntity getLovSqlMeaning(
            @RequestParam Map<String, Object> queryParams,
            @RequestParam String lovCode
    ) {
        if (queryParams == null) {
            queryParams = new HashMap<>(1);
        }
        queryParams.put(LovConstants.Field.TENANT_ID, BaseConstants.DEFAULT_TENANT_ID);
        try {
            return Results.success(this.lovSqlHandler.getLovSqlMeaning(lovCode, BaseConstants.DEFAULT_TENANT_ID, queryParams));
        } catch (Exception e) {
            return this.handleException(e);
        }
    }

    @SuppressWarnings("rawtypes")
    @ApiOperation(value = "查询租户级值集含义")
    @Permission(permissionLogin = true)
    @GetMapping("/v1/{organizationId}/lovs/sql/meaning")
    public ResponseEntity getOrgLovSqlMeaning(
            @PathVariable("organizationId") Long tenantId,
            @RequestParam Map<String, Object> queryParams,
            @RequestParam String lovCode
    ) {
        if (queryParams == null) {
            queryParams = new HashMap<>(1);
        }
        queryParams.put(LovConstants.Field.TENANT_ID, tenantId);
        try {
            return Results.success(this.lovSqlHandler.getLovSqlMeaning(lovCode, tenantId, queryParams));
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
