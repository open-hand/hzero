package org.hzero.message.domain.entity;

import com.fasterxml.jackson.annotation.JsonInclude;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import org.hzero.starter.keyencrypt.core.Encrypt;

import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;

import io.choerodon.mybatis.annotation.ModifyAudit;
import io.choerodon.mybatis.annotation.VersionAudit;
import io.choerodon.mybatis.domain.AuditDomain;

/**
 * 公告具体内容
 *
 * @author runbai.chen@hand-china.com 2018-08-02 15:23:14
 */
@ApiModel("公告具体内容")
@VersionAudit
@ModifyAudit
@Table(name = "hmsg_notice_content")
@JsonInclude(JsonInclude.Include.NON_NULL)
public class NoticeContent extends AuditDomain {

    public static final String FIELD_NOTICE_CONTENT_ID = "noticeContentId";
    public static final String FIELD_NOTICE_ID = "noticeId";
    public static final String FIELD_NOTICE_BODY = "noticeBody";
    public static final String FIELD_TENANT_ID = "tenantId";
    public static final String FIELD_RECEIVE_TENANT_ID = "receiveTenantId";
    public static final String FIELD_RECV_USER_GROUP_ID = "recvUserGroupId";
    public static final String FIELD_RECEIVE_USER_ID = "receiveUserId";
    public static final String FIELD_RECV_TENANT_NAME = "recvTenantName";
    public static final String FIELD_RECV_GROUP_NAME = "recvGroupName";
    public static final String FIELD_RECV_USER_NAME = "receiveUserName";

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
    private Long noticeContentId;
    @ApiModelProperty(value = "公告头ID")
    @NotNull
	@Encrypt
	private Long noticeId;
    private String noticeBody;
    @ApiModelProperty(value = "租户ID")
    @NotNull
    private Long tenantId;

	public Long getNoticeContentId() {
		return noticeContentId;
	}

	public NoticeContent setNoticeContentId(Long noticeContentId) {
		this.noticeContentId = noticeContentId;
		return this;
	}

	public Long getNoticeId() {
		return noticeId;
	}

	public NoticeContent setNoticeId(Long noticeId) {
		this.noticeId = noticeId;
		return this;
	}

	public String getNoticeBody() {
		return noticeBody;
	}

	public NoticeContent setNoticeBody(String noticeBody) {
		this.noticeBody = noticeBody;
		return this;
	}

	public Long getTenantId() {
		return tenantId;
	}

	public NoticeContent setTenantId(Long tenantId) {
		this.tenantId = tenantId;
		return this;
	}
}
