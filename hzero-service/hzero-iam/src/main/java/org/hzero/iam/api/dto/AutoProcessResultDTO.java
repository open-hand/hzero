package org.hzero.iam.api.dto;

import org.hzero.iam.api.eventhandler.CompanyConstants;
import org.hzero.iam.infra.constant.Constants;

/**
 * <p>
 * 描述
 * </p>
 *
 * @author allen 2018/7/11
 */
public class AutoProcessResultDTO {
    /**
     * INIT_APPROVE-初审/COMPANY_REGISTER-组织注册/ROLE_ASSIGN-角色及数据权限分配
     */
    private String processType;
    /**
     * SUCCESS/FAILED
     */
    private String processStatus;
    /**
     * JSON, 不同类型需约定最终结果对象, 例如, ROLE_ASSIGN类型, 此处为空
     */
    private String processResult;
    private String sourceKey;
    /**
     * SRM/HPFM/HIAM/...
     */
    private String sourceCode;
    private String processMessage;

    private String companyNum;

    /**
     * 无参构造函数
     */
    public AutoProcessResultDTO() {

    }
    /**
     * 构造函数
     * @param companyNum
     */
    public AutoProcessResultDTO(String companyNum, String sourceKey, String sourceCode) {
        setProcessType(CompanyConstants.CompanyApprovalProcessType.ROLE_ASSIGN);
        setSourceKey(sourceKey);
        setSourceCode(sourceCode);
        setCompanyNum(companyNum);
        setProcessStatus(CompanyConstants.CompanyApprovalProcessStatus.SUCCESS);
        setProcessMessage("success!");
    }

    public String getProcessType() {
        return processType;
    }

    public void setProcessType(String processType) {
        this.processType = processType;
    }

    public String getProcessStatus() {
        return processStatus;
    }

    public void setProcessStatus(String processStatus) {
        this.processStatus = processStatus;
    }

    public String getProcessResult() {
        return processResult;
    }

    public void setProcessResult(String processResult) {
        this.processResult = processResult;
    }

    public String getSourceCode() {
        return sourceCode;
    }

    public void setSourceCode(String sourceCode) {
        this.sourceCode = sourceCode;
    }

    public String getProcessMessage() {
        return processMessage;
    }

    public void setProcessMessage(String processMessage) {
        this.processMessage = processMessage;
    }

    public String getCompanyNum() {
        return companyNum;
    }

    public void setCompanyNum(String companyNum) {
        this.companyNum = companyNum;
    }

    public String getSourceKey() {
        return sourceKey;
    }

    public void setSourceKey(String sourceKey) {
        this.sourceKey = sourceKey;
    }
}
