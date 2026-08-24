package org.hzero.iam.domain.vo;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import org.hzero.iam.domain.entity.DomainAssign;
import org.springframework.beans.BeanUtils;

/**
 * 租户公司缓存VO（域名配置缓存使用）
 *
 * @author xiaoyu.zhao@hand-china.com 2020/09/03 10:26
 */
public class DomainAssignCacheVO implements Serializable {
    private static final long serialVersionUID = -6625864894934365970L;
    private Long tenantId;
    private Long companyId;

    public static DomainAssignCacheVO from(DomainAssign domainAssign) {
        DomainAssignCacheVO copy = new DomainAssignCacheVO();
        BeanUtils.copyProperties(domainAssign, copy);
        return copy;
    }

    public static List<DomainAssignCacheVO> from(List<DomainAssign> domainAssigns) {
        List<DomainAssignCacheVO> resultList = new ArrayList<>();
        for (DomainAssign domainAssign : domainAssigns) {
            DomainAssignCacheVO copy = new DomainAssignCacheVO();
            BeanUtils.copyProperties(domainAssign, copy);
            resultList.add(copy);
        }
        return resultList;
    }

    public Long getTenantId() {
        return tenantId;
    }

    public void setTenantId(Long tenantId) {
        this.tenantId = tenantId;
    }

    public Long getCompanyId() {
        return companyId;
    }

    public void setCompanyId(Long companyId) {
        this.companyId = companyId;
    }

    @Override
    public String toString() {
        return "DomainAssignCacheVO{" + "tenantId=" + tenantId + ", companyId=" + companyId + '}';
    }
}
