package org.hzero.iam.api.dto;

/**
 * 公司、业务实体、库存组织DTO
 *
 * @author liang.jin@hand-china.com 2018/08/02 20:54
 */
public class CompanyOuInvorgNodeDTO {
    public static final String INVORG = "INV_ORGANIZATION";
    public static final String OU = "OU";
    public static final String COMPANY = "COMPANY";


    private Long companyId;
    private String companyNum;
    private String companyName;
    private Integer comCheckedFlag;
    private Long ouId;
    private String ouCode;
    private String ouName;
    private Integer ouCheckedFlag;
    private Long organizationId;
    private String organizationCode;
    private String organizationName;
    private Integer orgCheckedFlag;

    private String typeCode;
    private Long secGrpDclLineId;
    private Integer shieldFlag;

    public Long getCompanyId() {
        return companyId;
    }

    public void setCompanyId(Long companyId) {
        this.companyId = companyId;
    }

    public String getCompanyNum() {
        return companyNum;
    }

    public void setCompanyNum(String companyNum) {
        this.companyNum = companyNum;
    }

    public String getCompanyName() {
        return companyName;
    }

    public void setCompanyName(String companyName) {
        this.companyName = companyName;
    }

    public Integer getComCheckedFlag() {
        return comCheckedFlag;
    }

    public void setComCheckedFlag(Integer comCheckedFlag) {
        this.comCheckedFlag = comCheckedFlag;
    }

    public Long getOuId() {
        return ouId;
    }

    public void setOuId(Long ouId) {
        this.ouId = ouId;
    }

    public String getOuCode() {
        return ouCode;
    }

    public void setOuCode(String ouCode) {
        this.ouCode = ouCode;
    }

    public String getOuName() {
        return ouName;
    }

    public void setOuName(String ouName) {
        this.ouName = ouName;
    }

    public Integer getOuCheckedFlag() {
        return ouCheckedFlag;
    }

    public void setOuCheckedFlag(Integer ouCheckedFlag) {
        this.ouCheckedFlag = ouCheckedFlag;
    }

    public Long getOrganizationId() {
        return organizationId;
    }

    public void setOrganizationId(Long organizationId) {
        this.organizationId = organizationId;
    }

    public String getOrganizationCode() {
        return organizationCode;
    }

    public void setOrganizationCode(String organizationCode) {
        this.organizationCode = organizationCode;
    }

    public String getOrganizationName() {
        return organizationName;
    }

    public void setOrganizationName(String organizationName) {
        this.organizationName = organizationName;
    }

    public Integer getOrgCheckedFlag() {
        return orgCheckedFlag;
    }

    public void setOrgCheckedFlag(Integer orgCheckedFlag) {
        this.orgCheckedFlag = orgCheckedFlag;
    }

    public String getTypeCode() {
        return typeCode;
    }

    public void setTypeCode(String typeCode) {
        this.typeCode = typeCode;
    }

    public Long getSecGrpDclLineId() {
        return secGrpDclLineId;
    }

    public void setSecGrpDclLineId(Long secGrpDclLineId) {
        this.secGrpDclLineId = secGrpDclLineId;
    }

    public Integer getShieldFlag() {
        return shieldFlag;
    }

    public void setShieldFlag(Integer shieldFlag) {
        this.shieldFlag = shieldFlag;
    }
}
