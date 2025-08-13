package org.hzero.iam.domain.entity;

import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.validation.constraints.NotBlank;

import org.hibernate.validator.constraints.Length;
import org.hzero.starter.keyencrypt.core.Encrypt;

import com.fasterxml.jackson.annotation.JsonInclude;

import io.choerodon.mybatis.annotation.ModifyAudit;
import io.choerodon.mybatis.annotation.VersionAudit;
import io.choerodon.mybatis.domain.AuditDomain;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

/**
 * 单据类型定义SQLID
 *
 * @author qingsheng.chen@hand-china.com 2020-06-01 10:33:03
 */
@ApiModel("单据类型定义SQLID")
@VersionAudit
@ModifyAudit
@JsonInclude(value = JsonInclude.Include.NON_NULL)
@Table(name = "hiam_doc_type_sqlid")
public class DocTypeSqlid extends AuditDomain {
	public static final String ENCRYPT_KEY = "hiam_doc_type_sqlid";
	public static final String FIELD_DOC_TYPE_SQLID_ID = "docTypeSqlidId";
	public static final String FIELD_DOC_TYPE_ID = "docTypeId";
	public static final String FIELD_SQLID = "sqlid";

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
    private Long docTypeSqlidId;
    @ApiModelProperty(value = "单据类型ID，hiam_doc_type.doc_type_id",required = true)
	@Encrypt
    private Long docTypeId;
    @ApiModelProperty(value = "来源数据实体，Mapper ID",required = true)
    @NotBlank
	@Length(max = 240)
    private String sqlid;

	//
    // 非数据库字段
    // ------------------------------------------------------------------------------

    //
    // getter/setter
    // ------------------------------------------------------------------------------

    /**
     * @return 表ID，主键，供其他表做外键
     */
	public Long getDocTypeSqlidId() {
		return docTypeSqlidId;
	}

	public void setDocTypeSqlidId(Long docTypeSqlidId) {
		this.docTypeSqlidId = docTypeSqlidId;
	}
    /**
     * @return 单据类型ID，hiam_doc_type.doc_type_id
     */
	public Long getDocTypeId() {
		return docTypeId;
	}

	public DocTypeSqlid setDocTypeId(Long docTypeId) {
		this.docTypeId = docTypeId;
		return this;
	}

	/**
     * @return 来源数据实体，Mapper ID
     */
	public String getSqlid() {
		return sqlid;
	}

	public DocTypeSqlid setSqlid(String sqlid) {
		this.sqlid = sqlid;
		return this;
	}
}
