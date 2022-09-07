package org.hzero.platform.api.dto;

import java.util.List;

import org.hzero.mybatis.domian.SecurityToken;
import org.hzero.platform.domain.entity.Profile;

import com.fasterxml.jackson.annotation.JsonInclude;

/**
 * @author yunxiang.zhou01@hand-china.com 2018/06/05
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ProfileDTO extends Profile {

    private String tenantName;

    private List<ProfileValueDTO> profileValueDTOList;

    public String getTenantName() {
        return tenantName;
    }

    public void setTenantName(String tenantName) {
        this.tenantName = tenantName;
    }

    public List<ProfileValueDTO> getProfileValueDTOList() {
        return profileValueDTOList;
    }

    public void setProfileValueDTOList(List<ProfileValueDTO> profileValueDTOList) {
        this.profileValueDTOList = profileValueDTOList;
    }

    @Override
    public Class<? extends SecurityToken> associateEntityClass() {
        return (Class<? extends SecurityToken>) this.getClass().getSuperclass();
    }
}
