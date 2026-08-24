package org.hzero.iam.api.controller.v1;

import java.util.List;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import io.choerodon.swagger.annotation.Permission;

import org.hzero.core.base.BaseController;
import org.hzero.core.util.Results;
import org.hzero.iam.app.service.UserDbdMenuService;
import org.hzero.iam.config.SwaggerApiConfig;
import org.hzero.iam.domain.entity.UserDbdMenu;
import org.hzero.starter.keyencrypt.core.Encrypt;

/**
 * 工作台用户级常用功能 管理 API
 *
 * @author zhixiang.huang@hand-china.com 2019-02-18 10:59:52
 */
@Api(tags = SwaggerApiConfig.USER_DBD_MENU)
@RestController("userDbdMenuSetController.v1")
@RequestMapping("/v1/user-dbd-menu")
public class UserDbdMenuController extends BaseController {

    @Autowired
    private UserDbdMenuService userDbdMenuService;

    @ApiOperation(value = "工作台用户级常用功能查询")
    @Permission(permissionLogin = true)
    @GetMapping()
    public ResponseEntity<List<UserDbdMenu>> queryFunction() {
        List<UserDbdMenu> dbdUserMenus = userDbdMenuService.queryMenu();
        return Results.success(dbdUserMenus);
    }

    @ApiOperation(value = "新增工作台用户级常用功能")
    @Permission(permissionLogin = true)
    @PostMapping()
    public ResponseEntity<Void> add(@RequestBody @Encrypt List<UserDbdMenu> dbdUserMenus) {
        userDbdMenuService.addMenu(dbdUserMenus);
        return Results.success();
    }

}
