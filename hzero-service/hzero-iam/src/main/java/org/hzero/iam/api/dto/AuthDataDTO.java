package org.hzero.iam.api.dto;

import org.hzero.starter.keyencrypt.core.Encrypt;

/**
 *
 * @author bojiangzhou 2020/06/15
 */
public class AuthDataDTO {

    @Encrypt
    private Long dataId;
    private String authorityTypeCode;

    public Long getDataId() {
        return dataId;
    }

    public void setDataId(Long dataId) {
        this.dataId = dataId;
    }

    public String getAuthorityTypeCode() {
        return authorityTypeCode;
    }

    public void setAuthorityTypeCode(String authorityTypeCode) {
        this.authorityTypeCode = authorityTypeCode;
    }
}
