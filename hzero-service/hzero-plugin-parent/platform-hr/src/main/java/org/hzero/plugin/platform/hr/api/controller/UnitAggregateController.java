package org.hzero.plugin.platform.hr.api.controller;

import java.util.List;

import org.hzero.core.base.BaseController;
import org.hzero.plugin.platform.hr.api.dto.UnitPositionDTO;
import org.hzero.plugin.platform.hr.app.service.PositionService;
import org.hzero.plugin.platform.hr.config.EnablePlatformHrPlugin;
import org.hzero.starter.keyencrypt.core.Encrypt;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import io.choerodon.core.iam.ResourceLevel;
import io.choerodon.swagger.annotation.Permission;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;

/**
 * <p>
 * 组织架构公司部门岗位聚合表Controller
 * </p>
 *
 * @author qingsheng.chen 2018/6/28 星期四 14:49
 */
@Api(tags = EnablePlatformHrPlugin.UNIT_AGGREGATE)
@RestController("unitAggregateController.v1")
@RequestMapping("/v1/{organizationId}/unit-aggregate")
public class UnitAggregateController extends BaseController {
    private PositionService positionService;

    @Autowired
    public UnitAggregateController(PositionService positionService) {
        this.positionService = positionService;
    }

    @ApiOperation("查询组织架构公司部门岗位聚合树")
    @GetMapping("/{employeeId}")
    @Permission(level = ResourceLevel.ORGANIZATION)
    public ResponseEntity<List<UnitPositionDTO>> treeUnitPosition(@PathVariable long organizationId,
                                                                  @PathVariable @Encrypt long employeeId,
                                                                  @RequestParam(required = false) String type,
                                                                  @RequestParam(required = false) String name) {
        return ResponseEntity.ok(positionService.treeUnitPosition(organizationId, employeeId, type, name));
    }
}
