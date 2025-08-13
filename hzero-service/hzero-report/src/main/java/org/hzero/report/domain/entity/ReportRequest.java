package org.hzero.report.domain.entity;

import com.fasterxml.jackson.annotation.JsonInclude;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import org.hzero.boot.platform.lov.annotation.LovValue;
import org.hzero.starter.keyencrypt.core.Encrypt;

import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.Transient;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.util.Date;

import io.choerodon.mybatis.annotation.ModifyAudit;
import io.choerodon.mybatis.domain.AuditDomain;

/**
 * 报表请求
 *
 * @author xianzhi.chen@hand-china.com 2019-01-25 14:21:02
 */
@ApiModel("报表请求")
@ModifyAudit
@JsonInclude(value = JsonInclude.Include.NON_NULL)
@Table(name = "hrpt_report_request")
public class ReportRequest extends AuditDomain {

    public static final String FIELD_REQUEST_ID = "requestId";
    public static final String FIELD_REPORT_ID = "reportId";
    public static final String FIELD_REQUEST_PARAM = "requestParam";
    public static final String FIELD_START_DATE = "startDate";
    public static final String FIELD_END_DATE = "endDate";
    public static final String FIELD_FILE_URL = "fileUrl";
    public static final String FIELD_REQUEST_STATUS = "requestStatus";
    public static final String FIELD_REQUEST_MESSAGE = "requestMessage";
    public static final String FIELD_TENANT_ID = "tenantId";
    public static final String FIELD_CONC_REQUEST_ID = "concRequestId";

    //
    // 业务方法(按public protected private顺序排列)
    // ------------------------------------------------------------------------------

    //
    // 数据库字段
    // ------------------------------------------------------------------------------

    @ApiModelProperty("表ID，主键，供其他表做外键")
    @Id
    @GeneratedValue
    @Encrypt
    private Long requestId;
    @ApiModelProperty(value = "报表ID,hrpt_report.report_id")
    @NotNull
    @Encrypt
    private Long reportId;
    @ApiModelProperty(value = "请求参数")
    private String requestParam;
    @ApiModelProperty(value = "开始时间")
    private Date startDate;
    @ApiModelProperty(value = "结束时间")
    private Date endDate;
    @ApiModelProperty(value = "报表输出")
    @Size(max = 240)
    private String fileUrl;
    @ApiModelProperty(value = "运行状态，值集：HRPT.REQUEST_STATUS P:就绪 R:运行中 F:已完成 W:警告 E:错误")
    @NotBlank
    @Size(max = 1)
    @LovValue(value = "HRPT.REQUEST_STATUS", meaningField = "requestStatusMeaning")
    private String requestStatus;
    @ApiModelProperty(value = "运行消息")
    @Size(max = 1200)
    private String requestMessage;
    @ApiModelProperty(value = "租户ID,hpfm_tenant.tenant_id")
    @NotNull
    private Long tenantId;
    @ApiModelProperty(value = "并发请求ID，hsdr_conc_request.request_id")
    private Long concRequestId;
    @ApiModelProperty(hidden = true)
    private Long objectVersionNumber;

    //
    // 非数据库字段
    // ------------------------------------------------------------------------------

    @ApiModelProperty(value = "报表编码")
    @Transient
    private String reportCode;
    @ApiModelProperty(value = "报表名称")
    @Transient
    private String reportName;
    @ApiModelProperty(value = "租户名称")
    @Transient
    private String tenantName;
    @ApiModelProperty(value = "提交者")
    @Transient
    private String requester;
    @ApiModelProperty(value = "请求状态")
    @Transient
    private String requestStatusMeaning;

    //
    // getter/setter
    // ------------------------------------------------------------------------------


    public Long getRequestId() {
        return requestId;
    }

    public ReportRequest setRequestId(Long requestId) {
        this.requestId = requestId;
        return this;
    }

    public Long getReportId() {
        return reportId;
    }

    public ReportRequest setReportId(Long reportId) {
        this.reportId = reportId;
        return this;
    }

    public String getRequestParam() {
        return requestParam;
    }

    public ReportRequest setRequestParam(String requestParam) {
        this.requestParam = requestParam;
        return this;
    }

    public Date getStartDate() {
        return startDate;
    }

    public ReportRequest setStartDate(Date startDate) {
        this.startDate = startDate;
        return this;
    }

    public Date getEndDate() {
        return endDate;
    }

    public ReportRequest setEndDate(Date endDate) {
        this.endDate = endDate;
        return this;
    }

    public String getFileUrl() {
        return fileUrl;
    }

    public ReportRequest setFileUrl(String fileUrl) {
        this.fileUrl = fileUrl;
        return this;
    }

    public String getRequestStatus() {
        return requestStatus;
    }

    public ReportRequest setRequestStatus(String requestStatus) {
        this.requestStatus = requestStatus;
        return this;
    }

    public String getRequestMessage() {
        return requestMessage;
    }

    public ReportRequest setRequestMessage(String requestMessage) {
        this.requestMessage = requestMessage;
        return this;
    }

    public Long getTenantId() {
        return tenantId;
    }

    public ReportRequest setTenantId(Long tenantId) {
        this.tenantId = tenantId;
        return this;
    }

    public Long getConcRequestId() {
        return concRequestId;
    }

    public ReportRequest setConcRequestId(Long concRequestId) {
        this.concRequestId = concRequestId;
        return this;
    }

    @Override
    public Long getObjectVersionNumber() {
        return objectVersionNumber;
    }

    @Override
    public void setObjectVersionNumber(Long objectVersionNumber) {
        this.objectVersionNumber = objectVersionNumber;
    }

    public String getReportCode() {
        return reportCode;
    }

    public ReportRequest setReportCode(String reportCode) {
        this.reportCode = reportCode;
        return this;
    }

    public String getReportName() {
        return reportName;
    }

    public ReportRequest setReportName(String reportName) {
        this.reportName = reportName;
        return this;
    }

    public String getTenantName() {
        return tenantName;
    }

    public ReportRequest setTenantName(String tenantName) {
        this.tenantName = tenantName;
        return this;
    }

    public String getRequester() {
        return requester;
    }

    public ReportRequest setRequester(String requester) {
        this.requester = requester;
        return this;
    }

    public String getRequestStatusMeaning() {
        return requestStatusMeaning;
    }

    public ReportRequest setRequestStatusMeaning(String requestStatusMeaning) {
        this.requestStatusMeaning = requestStatusMeaning;
        return this;
    }
}
