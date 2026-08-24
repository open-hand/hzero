package org.hzero.iam.api.dto;

import org.hzero.starter.keyencrypt.core.Encrypt;

/**
 * 合作伙伴DTO
 *
 * @author jinliang 2018/12/08 15:41
 */
public class UserAuthorityDataDTO {
    public static final String FIELD_DATA_ID = "dataId";

    @Encrypt
    private Long dataId;
    private String dataName;
    private String dataCode;

    public Long getDataId() {
        return dataId;
    }

    public void setDataId(Long dataId) {
        this.dataId = dataId;
    }

    public String getDataName() {
        return dataName;
    }

    public void setDataName(String dataName) {
        this.dataName = dataName;
    }

    public String getDataCode() {
        return dataCode;
    }

    public void setDataCode(String dataCode) {
        this.dataCode = dataCode;
    }
}
