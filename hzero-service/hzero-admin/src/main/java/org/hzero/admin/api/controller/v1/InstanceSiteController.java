package org.hzero.admin.api.controller.v1;

import org.hzero.admin.api.dto.InstanceDTO;
import org.hzero.admin.api.dto.InstanceDetailDTO;
import org.hzero.admin.api.dto.condition.InstanceQueryDTO;
import org.hzero.admin.app.service.InstanceService;
import org.hzero.admin.config.SwaggerApiConfig;
import org.hzero.admin.domain.repository.InstanceRepository;
import org.hzero.core.util.Results;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import springfox.documentation.annotations.ApiIgnore;

import io.choerodon.core.domain.Page;
import io.choerodon.core.iam.ResourceLevel;
import io.choerodon.mybatis.pagehelper.annotation.PageableDefault;
import io.choerodon.mybatis.pagehelper.domain.PageRequest;
import io.choerodon.swagger.annotation.CustomPageRequest;
import io.choerodon.swagger.annotation.Permission;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;

/**
 * @author flyleft
 */
@Api(tags = SwaggerApiConfig.INSTANCE_SITE)
@RestController("instanceSiteController.v1")
@RequestMapping(value = "/v1/instances")
public class InstanceSiteController {
    /**
     * 实例服务对象
     */
    private final InstanceService instanceService;
    private final InstanceRepository instanceRepository;

    @Autowired
    public InstanceSiteController(InstanceService instanceService,
                                  InstanceRepository instanceRepository) {
        this.instanceService = instanceService;
        this.instanceRepository = instanceRepository;
    }

    @ApiOperation("查询实例列表")
    @Permission(level = ResourceLevel.SITE)
    @CustomPageRequest
    @GetMapping
    public ResponseEntity<Page<InstanceDTO>> list(
            InstanceQueryDTO instanceQueryDTO,
            @ApiIgnore @PageableDefault(size = Integer.MAX_VALUE, sort = "instanceId") PageRequest pageRequest) {
        return Results.success(this.instanceService.listByOptions(instanceQueryDTO.getService(), instanceQueryDTO, pageRequest));
    }

    /**
     * 值集调用
     */
    @ApiOperation("查询服务列表")
    @Permission(permissionLogin = true)
    @GetMapping("/service")
    public ResponseEntity<Page<InstanceDTO>> listService(String service, String version,
                                                         @ApiIgnore @PageableDefault(size = Integer.MAX_VALUE, sort = "instanceId") PageRequest pageRequest) {
        return Results.success(this.instanceRepository.listByOptions(service, version, pageRequest));
    }

    /**
     * 查询实例详情
     *
     * @param instanceId 实例ID
     * @return 实例详情
     */
    @ApiOperation("查询实例详情")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "instanceId", value = "实例ID", required = true, paramType = "path")
    })
    @Permission(level = ResourceLevel.SITE)
    @GetMapping(value = "/{instanceId:.*}")
    public ResponseEntity<InstanceDetailDTO> query(@PathVariable("instanceId") String instanceId) {
        return Results.success(this.instanceService.query(instanceId));
    }
}