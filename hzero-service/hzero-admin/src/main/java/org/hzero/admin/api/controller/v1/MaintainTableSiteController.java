package org.hzero.admin.api.controller.v1;

import io.choerodon.core.domain.Page;
import io.choerodon.core.iam.ResourceLevel;
import io.choerodon.mybatis.pagehelper.annotation.PageableDefault;
import io.choerodon.mybatis.pagehelper.domain.PageRequest;
import io.choerodon.swagger.annotation.CustomPageRequest;
import io.choerodon.swagger.annotation.Permission;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.hzero.admin.api.dto.Report;
import org.hzero.admin.app.service.MaintainTableService;
import org.hzero.admin.config.SwaggerApiConfig;
import org.hzero.admin.domain.entity.MaintainTable;
import org.hzero.core.util.Results;
import org.hzero.starter.keyencrypt.core.Encrypt;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
import springfox.documentation.annotations.ApiIgnore;

import javax.servlet.http.HttpServletResponse;
import java.util.List;

/**
 * @author XCXCXCXCX
 * @date 2020/6/1 11:48 上午
 */
@Api(tags = SwaggerApiConfig.MAINTAIN_TABLE_SITE)
@RestController("maintainTableSiteController.v1")
@RequestMapping(value = "/v1/maintain-tables")
public class MaintainTableSiteController {

    @Autowired
    private MaintainTableService maintainTableService;

    @ApiOperation("分页查询运维表")
    @Permission(level = ResourceLevel.SITE)
    @CustomPageRequest
    @GetMapping
    public ResponseEntity<Page<MaintainTable>> page(
            @Encrypt @RequestParam(value = "maintainId", required = false) Long maintainId,
            @RequestParam(value = "serviceCode", required = false) String serviceCode,
            @RequestParam(value = "tableName", required = false) String tableName,
            @ApiIgnore @PageableDefault PageRequest pageRequest) {
        return Results.success(this.maintainTableService.page(maintainId, serviceCode, tableName, pageRequest));
    }

    @ApiOperation("新增运维表")
    @Permission(level = ResourceLevel.SITE)
    @PostMapping
    public ResponseEntity<?> create(@Encrypt @RequestBody MaintainTable maintainTable) {
        this.maintainTableService.insert(maintainTable);
        return Results.success();
    }

    @ApiOperation("更新运维表")
    @Permission(level = ResourceLevel.SITE)
    @PutMapping
    public ResponseEntity<?> update(@Encrypt @RequestBody MaintainTable maintainTable) {
        this.maintainTableService.updateByPrimaryKey(maintainTable);
        return Results.success();
    }

    @ApiOperation("批量更新运维表")
    @Permission(level = ResourceLevel.SITE)
    @PutMapping("/batch")
    public ResponseEntity<?> batchUpdate(@Encrypt @RequestBody List<MaintainTable> maintainTables) {
        this.maintainTableService.batchUpdate(maintainTables);
        return Results.success();
    }

    @ApiOperation("删除运维表")
    @Permission(level = ResourceLevel.SITE)
    @DeleteMapping
    public ResponseEntity<?> remove(@Encrypt @RequestParam("maintainId") Long maintainId,
                                    @Encrypt @RequestParam("maintainTableId") Long maintainTableId) {
        this.maintainTableService.deleteByPrimaryKey(maintainId, maintainTableId);
        return Results.success();
    }

    @ApiOperation("启用运维")
    @Permission(level = ResourceLevel.SITE)
    @PostMapping("/enable")
    public ResponseEntity<Report> enable(@Encrypt @RequestParam("maintainId") Long maintainId,
                                         @RequestParam("serviceCode") String serviceCode) {
        return Results.success(this.maintainTableService.enable(maintainId, serviceCode));
    }

    @ApiOperation("禁用运维")
    @Permission(level = ResourceLevel.SITE)
    @PostMapping("/disable")
    public ResponseEntity<Report> disable(@Encrypt @RequestParam("maintainId") Long maintainId,
                                          @RequestParam("serviceCode") String serviceCode) {
        return Results.success(this.maintainTableService.disable(maintainId, serviceCode));
    }

    @ApiOperation("下载导入运维表模版")
    @Permission(level = ResourceLevel.SITE)
    @PostMapping("/download-template")
    public ResponseEntity<?> downloadTemplate(HttpServletResponse response) {
        this.maintainTableService.downloadTemplate(response);
        return Results.success();
    }

    @ApiOperation("导入运维表数据")
    @Permission(level = ResourceLevel.SITE)
    @PostMapping("/import-data")
    public ResponseEntity<?> importData(@Encrypt @RequestParam("maintainId") Long maintainId, @RequestParam("importData") MultipartFile multipartFile) {
        this.maintainTableService.importData(maintainId, multipartFile);
        return Results.success();
    }

}
