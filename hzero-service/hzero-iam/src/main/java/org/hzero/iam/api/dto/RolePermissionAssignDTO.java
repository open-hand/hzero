package org.hzero.iam.api.dto;

import java.util.Arrays;
import java.util.Collections;
import java.util.Set;
import java.util.stream.Collectors;

import org.apache.commons.lang3.StringUtils;

import org.hzero.core.base.BaseConstants;

/**
 *
 * @author bojiangzhou 2020/07/10
 */
public class RolePermissionAssignDTO {

    /**
     * 角色路径，多个用逗号隔开
     */
    private String roleLevelPaths;
    /**
     * 菜单权限集，多个用逗号隔开
     */
    private String menuPermissionSetCodes;

    public Set<String> getRoleLevelPaths() {
        if (StringUtils.isBlank(roleLevelPaths)) {
            return Collections.emptySet();
        }
        return Arrays.stream(roleLevelPaths.split(BaseConstants.Symbol.COMMA))
                .map(String::trim).collect(Collectors.toSet());
    }

    public void setRoleLevelPaths(String roleLevelPaths) {
        this.roleLevelPaths = roleLevelPaths;
    }

    public Set<String> getMenuPermissionSetCodes() {
        if (StringUtils.isBlank(menuPermissionSetCodes)) {
            return Collections.emptySet();
        }
        return Arrays.stream(menuPermissionSetCodes.split(BaseConstants.Symbol.COMMA))
                .map(String::trim).collect(Collectors.toSet());
    }

    public void setMenuPermissionSetCodes(String menuPermissionSetCodes) {
        this.menuPermissionSetCodes = menuPermissionSetCodes;
    }
}
