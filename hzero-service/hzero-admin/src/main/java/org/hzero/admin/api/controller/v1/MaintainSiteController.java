package org.hzero.admin.api.controller.v1;

import io.choerodon.core.domain.Page;
import io.choerodon.core.iam.ResourceLevel;
import io.choerodon.mybatis.pagehelper.annotation.PageableDefault;
import io.choerodon.mybatis.pagehelper.annotation.SortDefault;
import io.choerodon.mybatis.pagehelper.domain.PageRequest;
import io.choerodon.mybatis.pagehelper.domain.Sort;
import io.choerodon.swagger.annotation.CustomPageRequest;
import io.choerodon.swagger.annotation.Permission;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.hzero.admin.app.service.MaintainService;
import org.hzero.admin.config.SwaggerApiConfig;
import org.hzero.admin.domain.entity.Maintain;
import org.hzero.boot.platform.lov.annotation.ProcessLovValue;
import org.hzero.core.util.Results;
import org.hzero.starter.keyencrypt.core.Encrypt;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import springfox.documentation.annotations.ApiIgnore;

import java.util.List;

/**
 * @author XCXCXCXCX
 * @date 2020/6/1 11:48 上午
 */
@Api(tags = SwaggerApiConfig.MAINTAIN_SITE)
@RestController("maintainSiteController.v1")
@RequestMapping(value = "/v1/maintains")
public class MaintainSiteController {

    @Autowired
    private MaintainService maintainService;

    @ApiOperation("分页查询运维规则")
    @Permission(level = ResourceLevel.SITE)
    @CustomPageRequest
    @GetMapping
    @ProcessLovValue(targetField = {"body"})
    public ResponseEntity<Page<Maintain>> page(
            @RequestParam(value = "maintainVersion", required = false) String maintainVersion,
            @RequestParam(value = "state", required = false) String state,
            @ApiIgnore @PageableDefault @SortDefault(sort = Maintain.FIELD_MAINTAIN_ID, direction = Sort.Direction.DESC) PageRequest pageRequest) {
        return Results.success(this.maintainService.page(pageRequest, new Maintain().setMaintainVersion(maintainVersion).setState(state)));
    }

    @ApiOperation("查询运维规则明细")
    @Permission(level = ResourceLevel.SITE)
    @GetMapping("/{maintainId}")
    @ProcessLovValue(targetField = {"body"})
    public ResponseEntity<Maintain> detail(@Encrypt @PathVariable("maintainId") Long maintainId) {
        return Results.success(this.maintainService.selectByPrimaryKey(maintainId));
    }

    @ApiOperation("新增运维规则")
    @Permission(level = ResourceLevel.SITE)
    @PostMapping
    public ResponseEntity<Maintain> create(@Encrypt @RequestBody Maintain maintain) {
        this.maintainService.insertSelective(maintain);
        return Results.success(maintain);
    }

    @ApiOperation("更新运维规则")
    @Permission(level = ResourceLevel.SITE)
    @PutMapping
    public ResponseEntity<Maintain> update(@Encrypt @RequestBody Maintain maintain) {
        this.maintainService.updateByPrimaryKeySelective(maintain);
        return Results.success(maintain);
    }

    @ApiOperation("删除运维规则")
    @Permission(level = ResourceLevel.SITE)
    @DeleteMapping
    public ResponseEntity<Maintain> remove(@Encrypt @RequestParam("maintainId") Long maintainId) {
        this.maintainService.deleteByPrimaryKey(maintainId);
        return Results.success();
    }

    @ApiOperation("更新运维状态")
    @Permission(level = ResourceLevel.SITE)
    @PostMapping("/state")
    public ResponseEntity<?> updateState(@Encrypt @RequestParam("maintainId") Long maintainId,
                                         @RequestParam("from") String from,
                                         @RequestParam("to") String to) {
        this.maintainService.updateState(maintainId, from, to);
        return Results.success();
    }

    @ApiOperation("获取运维服务名列表")
    @Permission(level = ResourceLevel.SITE)
    @GetMapping("/services")
    public ResponseEntity<List<String>> getServices(@Encrypt @RequestParam("maintainId") Long maintainId) {
        return Results.success(this.maintainService.getServices(maintainId));
    }
}
