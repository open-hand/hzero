package org.hzero.iam.api.controller.v1;

import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

import org.hzero.common.HZeroService;
import org.hzero.core.base.BaseConstants;
import org.hzero.iam.api.dto.ModelPublishPayloadDTO;
import org.hzero.iam.api.dto.RolePermissionWithDTO;
import org.hzero.iam.config.SwaggerApiConfig;
import org.hzero.iam.domain.entity.Menu;
import org.hzero.iam.domain.entity.MenuPermission;
import org.hzero.iam.domain.entity.RolePermission;
import org.hzero.iam.domain.repository.MenuPermissionRepository;
import org.hzero.iam.domain.repository.MenuRepository;
import org.hzero.iam.domain.repository.RolePermissionRepository;
import org.hzero.iam.infra.constant.Constants;
import org.hzero.iam.infra.constant.HiamMenuType;
import org.hzero.iam.infra.constant.HiamResourceLevel;
import org.hzero.mybatis.domian.Condition;
import org.hzero.mybatis.util.Sqls;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.util.Assert;
import org.springframework.web.bind.annotation.*;

import io.choerodon.core.exception.CommonException;
import io.choerodon.core.iam.ResourceLevel;
import io.choerodon.swagger.annotation.Permission;

import io.swagger.annotations.Api;

/**
 * 对接 low code 模型发布
 *
 * @author qingsheng.chen@hand-china.com
 */
@Api(tags = SwaggerApiConfig.LOW_CODE_MODE_PUBLISH)
@RestController("modelPublishController.v1")
@RequestMapping("/v1/{organizationId}/low-code/model/publish")
public class ModelPublishController {
    private final RolePermissionRepository rolePermissionRepository;
    private final MenuRepository menuRepository;
    private final MenuPermissionRepository menuPermissionRepository;

    @Value("${hzero.iam.low-code.permission-set-code:hzero.hlcd.model.ps.auto-ge}")
    private String lowCodeModelPermissionSetCode;
    @Value("${hzero.iam.low-code.permission-code:.view-organization.queryView}")
    private String lowCodeModelPermissionCode;

    public ModelPublishController(RolePermissionRepository rolePermissionRepository,
                                  MenuRepository menuRepository,
                                  MenuPermissionRepository menuPermissionRepository) {
        this.rolePermissionRepository = rolePermissionRepository;
        this.menuRepository = menuRepository;
        this.menuPermissionRepository = menuPermissionRepository;
    }

    @PostMapping
    @Permission(level = ResourceLevel.ORGANIZATION, permissionWithin = true)
    public ModelPublishPayloadDTO processModelPublishRole(@PathVariable long organizationId,
                                                          @RequestBody ModelPublishPayloadDTO payload) {
        long permissionSetId = lowCodeModelPermission();
        List<ModelPublishPayloadDTO.Role> roles = payload.getRoles();
        if (roles != null && !roles.isEmpty()) {
            Map<Long, RolePermissionWithDTO> rolePermissionWithMap = rolePermissionRepository.selectRoleWithPermission(
                    roles.stream()
                            .map(ModelPublishPayloadDTO.Role::getId)
                            .collect(Collectors.toList()),
                    permissionSetId,
                    HiamMenuType.PS.name())
                    .stream()
                    .collect(Collectors.toMap(RolePermissionWithDTO::getRoleId, Function.identity()));
            for (ModelPublishPayloadDTO.Role rolePayload : roles) {
                if (rolePayload.getId() == null) {
                    throw new CommonException("Automatic generation of roles is not supported.");
                } else {
                    RolePermissionWithDTO role = rolePermissionWithMap.get(rolePayload.getId());
                    Assert.notNull(role, "Role does not exist [" + rolePayload.getId() + "].");
                    if (!role.hasPermission()) {
                        rolePermissionRepository.insert(new RolePermission()
                                .setRoleId(rolePayload.getId())
                                .setPermissionSetId(permissionSetId)
                                .setType(HiamMenuType.PS.name())
                                .setTenantId(role.getTenantId())
                                .setCreateFlag(Constants.YesNoFlag.YES)
                                .setInheritFlag(Constants.YesNoFlag.NO));
                    }
                    rolePayload.setCode(role.getRoleCode());
                }
            }
        }
        return payload;
    }

    private long lowCodeModelPermission() {
        // 查询权限集是否存在
        List<Menu> menus = menuRepository.selectByCondition(Condition.builder(Menu.class)
                .andWhere(Sqls.custom()
                        .andEqualTo(Menu.FIELD_CODE, lowCodeModelPermissionSetCode)
                        .andEqualTo(Menu.FIELD_LEVEL, HiamResourceLevel.ORGANIZATION.value())
                        .andEqualTo(Menu.FIELD_TENANT_ID, BaseConstants.DEFAULT_TENANT_ID))
                .build());
        // 如果存在直接返回权限集ID
        return menus.stream().findFirst().orElseGet(() -> {
            // 如果不存在则新增
            Menu lowCodeModelPermissionSet = new Menu();
            lowCodeModelPermissionSet.setCode(lowCodeModelPermissionSetCode);
            lowCodeModelPermissionSet.setName("Automatically generated by the low code model publish");
            lowCodeModelPermissionSet.setLevel(HiamResourceLevel.ORGANIZATION.value());
            lowCodeModelPermissionSet.setParentId(0L);
            lowCodeModelPermissionSet.setType(HiamMenuType.PS.value());
            lowCodeModelPermissionSet.setDefault(BaseConstants.Flag.YES);
            lowCodeModelPermissionSet.setCustomFlag(BaseConstants.Flag.NO);
            lowCodeModelPermissionSet.setTenantId(BaseConstants.DEFAULT_TENANT_ID);
            lowCodeModelPermissionSet.setLevelPath(lowCodeModelPermissionSetCode);
            lowCodeModelPermissionSet.setVirtualFlag(BaseConstants.Flag.NO);
            lowCodeModelPermissionSet.setEnabledFlag(BaseConstants.Flag.YES);
            // 创建权限集
            menuRepository.insert(lowCodeModelPermissionSet);
            // 分配权限
            MenuPermission menuPermission = new MenuPermission();
            menuPermission.setMenuId(lowCodeModelPermissionSet.getId());
            menuPermission.setTenantId(lowCodeModelPermissionSet.getTenantId());
            menuPermission.setPermissionCode(HZeroService.getRealName(HZeroService.LowLowCode.NAME) + lowCodeModelPermissionCode);
            menuPermissionRepository.insert(menuPermission);
            return lowCodeModelPermissionSet;
        }).getId();
    }
}
