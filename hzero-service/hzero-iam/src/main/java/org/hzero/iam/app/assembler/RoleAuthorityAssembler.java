package org.hzero.iam.app.assembler;

import org.hzero.iam.api.dto.RoleAuthorityDTO;
import org.hzero.iam.domain.entity.RoleAuthority;
import org.springframework.beans.BeanUtils;

/**
 * @author mingke.yan@hand-china.com
 * @version 1.0
 * @date 2018/8/7
 */
public class RoleAuthorityAssembler {

    private RoleAuthorityAssembler() {
    }

    public static RoleAuthority translateRoleAuthority(RoleAuthorityDTO roleAuthorityDTO){
        RoleAuthority roleAuthority = new RoleAuthority();
        BeanUtils.copyProperties(roleAuthorityDTO,roleAuthority);
        return roleAuthority;
    }

}