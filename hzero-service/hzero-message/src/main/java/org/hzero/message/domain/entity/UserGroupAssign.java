package org.hzero.message.domain.entity;

import com.fasterxml.jackson.annotation.JsonInclude;
import io.choerodon.mybatis.domain.AuditDomain;

/**
 * 用户组分配
 *
 * @author xiaoyu.zhao@hand-china.com 2019-01-15 16:06:11
 */

@JsonInclude(JsonInclude.Include.NON_NULL)
public class UserGroupAssign extends AuditDomain {

    private Long userGroupId;
  
    private Long tenantId;

  
    //
    // getter/setter
    // ------------------------------------------------------------------------------

 

    /**
     * @return 租户ID，hpfm_tenant.tenant_id
     */
    public Long getTenantId() {
        return tenantId;
    }

    public void setTenantId(Long tenantId) {
        this.tenantId = tenantId;
    }

	public Long getUserGroupId() {
		return userGroupId;
	}

	public void setUserGroupId(Long userGroupId) {
		this.userGroupId = userGroupId;
	}

}
