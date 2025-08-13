package org.hzero.platform.api.controller.v1;

import io.choerodon.swagger.annotation.Permission;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.hzero.core.util.Results;
import org.hzero.platform.app.service.CommonService;
import org.hzero.platform.config.PlatformSwaggerApiConfig;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;

/**
 * 通用接口
 *
 * @author xiaoyu.zhao@hand-china.com 2019/04/23 20:23
 */
@Api(tags = PlatformSwaggerApiConfig.COMMON)
@RestController("commonController.v1")
@RequestMapping("/v1/common")
public class CommonController {

    @Autowired
    private CommonService commonService;

    /**
     * 查询所有国际冠码
     *
     * @return 冠码
     */
    @ApiOperation(value = "查询所有国际冠码")
    @Permission(permissionPublic = true)
    @GetMapping("/idd-list")
    public ResponseEntity<List<Map<String, String>>> listIDD() {
        return Results.success(commonService.listIDD());
    }

}
