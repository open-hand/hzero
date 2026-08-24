package org.hzero.iam.api.controller.v1;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import io.choerodon.core.iam.ResourceLevel;
import io.choerodon.swagger.annotation.Permission;

import org.hzero.core.base.BaseController;
import org.hzero.core.util.Results;
import org.hzero.iam.app.service.PasswordPolicyService;
import org.hzero.iam.config.SwaggerApiConfig;
import org.hzero.iam.domain.entity.PasswordPolicy;
import org.hzero.iam.domain.repository.PasswordPolicyRepository;
import org.hzero.iam.domain.vo.PasswordPolicyVO;
import org.hzero.mybatis.helper.SecurityTokenHelper;

/**
 * 密码策略接口
 *
 * @author bojiangzhou 2019/08/05
 */
@Api(tags = SwaggerApiConfig.PASSWORD_POLICY)
@RestController
@RequestMapping("/v1/{organizationId}/password-policies")
public class PasswordPolicyController extends BaseController {

    private final PasswordPolicyService passwordPolicyService;
    private final PasswordPolicyRepository passwordPolicyRepository;

    public PasswordPolicyController(PasswordPolicyService passwordPolicyService,
                                    PasswordPolicyRepository passwordPolicyRepository) {
        this.passwordPolicyService = passwordPolicyService;
        this.passwordPolicyRepository = passwordPolicyRepository;
    }


    @Permission(level = ResourceLevel.ORGANIZATION)
    @ApiOperation(value = "查询组织的密码策略")
    @GetMapping
    public ResponseEntity<PasswordPolicy> queryByOrganizationId(@PathVariable Long organizationId) {
        return Results.success(passwordPolicyRepository.selectTenantPasswordPolicy(organizationId));
    }

    @Permission(level = ResourceLevel.ORGANIZATION)
    @ApiOperation(value = "修改组织的密码策略")
    @PutMapping
    public ResponseEntity<Void> update(@PathVariable Long organizationId, @RequestBody PasswordPolicy passwordPolicy) {
        passwordPolicy.setOrganizationId(organizationId);
        this.validObject(passwordPolicy);
        if (passwordPolicy.getId() == null) {
            passwordPolicyService.createPasswordPolicy(organizationId, passwordPolicy);
        } else {
            SecurityTokenHelper.validToken(passwordPolicy);
            passwordPolicyService.updatePasswordPolicy(organizationId, passwordPolicy);
        }
        return Results.success();
    }

    @Permission(permissionPublic = true)
    @ApiOperation(value = "查询密码策略")
    @GetMapping("/query")
    public ResponseEntity<PasswordPolicyVO> queryPasswordPolicy(@PathVariable Long organizationId) {
        return Results.success(passwordPolicyRepository.queryPasswordPolicy(organizationId));
    }

}
