package org.hzero.platform.api.controller.v1;

import io.choerodon.core.iam.ResourceLevel;
import io.choerodon.swagger.annotation.Permission;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.hzero.core.util.Results;
import org.hzero.platform.api.dto.DataHierarchyDTO;
import org.hzero.platform.api.dto.DataHierarchyDisplayStyleDTO;
import org.hzero.platform.app.service.DataHierarchySwitchService;
import org.hzero.platform.config.PlatformSwaggerApiConfig;
import org.hzero.starter.keyencrypt.core.IEncryptionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * 数据层级配置 切换   API
 *
 * @author qingsheng.chen@hand-china.com
 */
@Api(tags = PlatformSwaggerApiConfig.DATA_HIERARCHY_SWITCH)
@RestController("dataHierarchySwitchController.v1")
@RequestMapping("/v1/{organizationId}/data-hierarchies/switch")
public class DataHierarchySwitchController {
    private DataHierarchySwitchService dataHierarchySwitchService;
    private IEncryptionService encryptionService;

    @Autowired
    public DataHierarchySwitchController(DataHierarchySwitchService dataHierarchySwitchService,
                                         IEncryptionService encryptionService) {
        this.dataHierarchySwitchService = dataHierarchySwitchService;
        this.encryptionService = encryptionService;
    }

    @ApiOperation("树形查询当前用户的数据层级值")
    @Permission(permissionLogin = true, level = ResourceLevel.ORGANIZATION)
    @GetMapping("/value")
    public ResponseEntity<List<DataHierarchyDTO>> treeValue(@ApiParam(value = "租户ID", required = true) @PathVariable Long organizationId) {
        return Results.success(dataHierarchySwitchService.treeDataHierarchyValue(organizationId));
    }

    @ApiOperation("按照显示风格分组查询数据层级值")
    @Permission(permissionLogin = true, level = ResourceLevel.ORGANIZATION)
    @GetMapping("/display-style/value")
    public ResponseEntity<DataHierarchyDisplayStyleDTO> displayStyleValue(@ApiParam(value = "租户ID", required = true) @PathVariable Long organizationId) {
        return Results.success(dataHierarchySwitchService.displayStyleDataHierarchyValue(organizationId));
    }

    @ApiOperation("查询指定当前用户的数据层级值")
    @Permission(permissionLogin = true, level = ResourceLevel.ORGANIZATION)
    @GetMapping("/value/details")
    public ResponseEntity<DataHierarchyDTO> queryValueDetail(@ApiParam(value = "租户ID", required = true) @PathVariable Long organizationId,
                                                             @RequestParam String dataHierarchyCode) {
        return Results.success(dataHierarchySwitchService.queryDataHierarchyValue(organizationId, dataHierarchyCode));
    }

    @ApiOperation("查询当前用户对应数据层级编码的数据层级值")
    @Permission(permissionLogin = true, level = ResourceLevel.ORGANIZATION)
    @GetMapping
    public ResponseEntity<Map<String, Object>> queryValue(@ApiParam("数据层级编码列表，不指定编码返回所有值") @RequestParam(required = false) Set<String> dataHierarchyCodeList) {
        return Results.success(dataHierarchySwitchService.queryDataHierarchyValue(dataHierarchyCodeList));
    }

    @ApiOperation("切换当前用户对应数据层级编码的数据层级值")
    @Permission(permissionLogin = true, level = ResourceLevel.ORGANIZATION)
    @PutMapping
    public ResponseEntity<Void> saveValue(@ApiParam(value = "租户ID", required = true) @PathVariable Long organizationId,
                                          @ApiParam(value = "数据层级编码", required = true) @RequestParam String dataHierarchyCode,
                                          @ApiParam(value = "数据层级值", required = true) @RequestParam String dataHierarchyValue,
                                          @ApiParam(value = "数据层级值展示值", required = true) @RequestParam String dataHierarchyMeaning) {
        dataHierarchySwitchService.saveDataHierarchyValue(organizationId,
                dataHierarchyCode,
                encryptionService.isCipher(dataHierarchyValue) ? encryptionService.decrypt(dataHierarchyValue, "") : dataHierarchyValue,
                dataHierarchyMeaning);
        return Results.success();
    }
}
