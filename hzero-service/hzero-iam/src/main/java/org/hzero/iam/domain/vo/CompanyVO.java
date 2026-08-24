package org.hzero.iam.domain.vo;

import java.util.Date;

import javax.persistence.Transient;

import org.hzero.starter.keyencrypt.core.Encrypt;

/**
 * CompanyVO
 *
 * @author bojiangzhou 2018/08/16
 */
public class CompanyVO {

    public static final String ENCRYPT_KEY = "hpfm_company";
    @Encrypt
    private Long companyId;
    private String companyName;
    private Date creationDate;
    @Transient
    private String companyNum;

    public Long getCompanyId() {
        return companyId;
    }

    public void setCompanyId(Long companyId) {
        this.companyId = companyId;
    }

    public String getCompanyName() {
        return companyName;
    }

    public void setCompanyName(String companyName) {
        this.companyName = companyName;
    }

    public Date getCreationDate() {
        return creationDate;
    }

    public void setCreationDate(Date creationDate) {
        this.creationDate = creationDate;
    }

    public String getCompanyNum() {
        return companyNum;
    }

    public void setCompanyNum(String companyNum) {
        this.companyNum = companyNum;
    }
}
