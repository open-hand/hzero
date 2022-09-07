package org.hzero.platform.api.controller.v1;

import java.util.List;

import org.hzero.core.base.BaseController;
import org.hzero.core.util.Results;
import org.hzero.platform.app.service.DataGroupLineService;
import org.hzero.platform.config.PlatformSwaggerApiConfig;
import org.hzero.platform.domain.entity.DataGroupLine;
import org.hzero.platform.domain.repository.DataGroupLineRepository;
import org.hzero.starter.keyencrypt.core.Encrypt;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import springfox.documentation.annotations.ApiIgnore;

import io.choerodon.core.domain.Page;
import io.choerodon.core.iam.ResourceLevel;
import io.choerodon.mybatis.pagehelper.annotation.SortDefault;
import io.choerodon.mybatis.pagehelper.domain.PageRequest;
import io.choerodon.mybatis.pagehelper.domain.Sort;
import io.choerodon.swagger.annotation.Permission;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;

/**
 * 数据组行定义 管理 API
 *
 * @author jianbo.li@hand-china.com 2019-07-17 15:48:48
 */
@Api(tags = PlatformSwaggerApiConfig.DATA_GROUP_LINE)
@RestController("dataGroupLineController.v1")
@RequestMapping("/v1/{organizationId}/data-lines")
public class DataGroupLineController extends BaseController {

    @Autowired
    private DataGroupLineRepository dataGroupLineRepository;
    @Autowired
    private DataGroupLineService dataGroupLineService;

    @ApiOperation(value = "数据组行定义列表")
    @Permission(level = ResourceLevel.ORGANIZATION)
    @GetMapping("/{dataGroupId}")
    public ResponseEntity<Page<DataGroupLine>> list(@PathVariable("organizationId") Long tenantId,
                                                    @PathVariable("dataGroupId") @Encrypt Long dataGroupId,
                                                    @Encrypt DataGroupLine dataGroupLine,
                                                    @ApiIgnore @SortDefault(value = DataGroupLine.FIELD_GROUP_LINE_ID, direction = Sort.Direction.DESC) PageRequest pageRequest) {
        dataGroupLine.setTenantId(tenantId);
        dataGroupLine.setGroupId(dataGroupId);
        Page<DataGroupLine> list = dataGroupLineService.pageDataGroupLine(pageRequest, dataGroupLine);
        return Results.success(list);
    }

    @ApiOperation(value = "创建数据组行定义")
    @Permission(level = ResourceLevel.ORGANIZATION)
    @PostMapping("/{dataGroupId}")
    public ResponseEntity<List<DataGroupLine>> create(@PathVariable("organizationId") Long tenantId,
                                                      @PathVariable("dataGroupId") @Encrypt Long dataGroupId,
                                                      @RequestBody @Encrypt List<DataGroupLine> dataGroupLine) {
        dataGroupLine.forEach(item -> {
            item.setTenantId(tenantId);
            item.setGroupId(dataGroupId);
        });
        validList(dataGroupLine);
        dataGroupLineService.createDataGroupLine(dataGroupLine);
        return Results.success(dataGroupLine);
    }

    @ApiOperation(value = "删除数据组行定义")
    @Permission(level = ResourceLevel.ORGANIZATION)
    @DeleteMapping("/{dataGroupId}")
    public ResponseEntity<?> remove(@PathVariable("organizationId") Long tenantId,
                                    @PathVariable("dataGroupId") @Encrypt Long dataGroupId,
                                    @RequestBody @Encrypt List<DataGroupLine> dataGroupLineList) {

        dataGroupLineService.deleteDataGroupLine(dataGroupLineList);
        return Results.success();
    }

}
