package org.hzero.iam.api.controller.v1;

import java.util.List;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import io.choerodon.core.oauth.DetailsHelper;
import io.choerodon.swagger.annotation.Permission;

import org.hzero.core.base.BaseController;
import org.hzero.core.util.Results;
import org.hzero.iam.config.SwaggerApiConfig;
import org.hzero.iam.domain.entity.UserOpenAccount;
import org.hzero.iam.domain.repository.UserOpenAccountRepository;
import org.hzero.mybatis.helper.SecurityTokenHelper;
import org.hzero.starter.keyencrypt.core.Encrypt;

/**
 * 用户第三方账号管理
 *
 * @author jiaxu.cui@hand-china.com 2018/9/29 16:45
 */
@Api(tags = SwaggerApiConfig.USER_OPEN_ACCOUNT)
@RestController("userOpenAccountController.v1")
@RequestMapping("/hzero/v1/user-open-account")
public class UserOpenAccountController extends BaseController {

    @Autowired
    private UserOpenAccountRepository userOpenAccountRepository;

    //用户Api
    //=======================================================

    @ApiOperation("查询用户第三方账号信息")
    @Permission(permissionLogin = true)
    @GetMapping
    public ResponseEntity<List<UserOpenAccount>> queryOpenAccountByUserId() {
        List<UserOpenAccount> userOpenAppList =
                userOpenAccountRepository.selectOpenAppAndBindUser(DetailsHelper.getUserDetails().getUsername());
        return Results.success(userOpenAppList);
    }

    @ApiOperation("用户解绑第三方账号")
    @Permission(permissionLogin = true)
    @PostMapping("/unbind")
    public ResponseEntity unbindOpenApp(@RequestBody @Encrypt UserOpenAccount userOpenAccount) {
        SecurityTokenHelper.validToken(userOpenAccount);
        userOpenAccountRepository.deleteByPrimaryKey(userOpenAccount);
        return Results.success();
    }

}
