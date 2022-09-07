package org.hzero.iam.api.dto;


import java.util.List;

import org.hzero.iam.domain.entity.Menu;
import org.hzero.starter.keyencrypt.core.Encrypt;

/**
 * @author jianbo.li
 * @date 2019/10/20 10:40
 */
public class AccessAuthDTO {

    @Encrypt
    private Long id;
    private String code;
    private String name;
    private String parentRoleName;

    private List<Menu> psList;

    @Override
    public String toString() {
        return "AccessAuthDTO{" +
                "id=" + id +
                ", code='" + code + '\'' +
                ", name='" + name + '\'' +
                ", psList=" + psList +
                '}';
    }

    public Long getId() {
        return id;
    }

    public String getCode() {
        return code;
    }

    public String getName() {
        return name;
    }


    public List<Menu> getPsList() {
        return psList;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public void setName(String name) {
        this.name = name;
    }


    public void setPsList(List<Menu> psList) {
        this.psList = psList;
    }

    public String getParentRoleName() {
        return parentRoleName;
    }

    public void setParentRoleName(String parentRoleName) {
        this.parentRoleName = parentRoleName;
    }
}
