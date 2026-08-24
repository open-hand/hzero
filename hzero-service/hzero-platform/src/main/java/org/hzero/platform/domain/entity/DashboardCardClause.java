package org.hzero.platform.domain.entity;

import java.util.Objects;

import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.Transient;
import javax.validation.constraints.NotNull;

import com.fasterxml.jackson.annotation.JsonIgnore;
import org.hzero.mybatis.domian.Condition;
import org.hzero.mybatis.util.Sqls;
import org.hzero.platform.domain.repository.DashboardCardClauseRepository;
import org.hzero.platform.infra.constant.HpfmMsgCodeConstants;

import com.fasterxml.jackson.annotation.JsonInclude;

import io.choerodon.core.exception.CommonException;
import io.choerodon.mybatis.annotation.ModifyAudit;
import io.choerodon.mybatis.annotation.VersionAudit;
import io.choerodon.mybatis.domain.AuditDomain;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import org.hzero.starter.keyencrypt.core.Encrypt;

/**
 * 条目卡片关联配置管理
 *
 * @author xiaoyu.zhao@hand-china.com 2019-03-06 17:55:53
 */
@ApiModel("条目卡片关联配置管理")
@VersionAudit
@ModifyAudit
@Table(name = "hpfm_dashboard_card_clause")
@JsonInclude(JsonInclude.Include.NON_NULL)
public class DashboardCardClause extends AuditDomain {

    public static final String FIELD_ID = "id";
    public static final String FIELD_CARD_ID = "cardId";
    public static final String FIELD_CLAUSE_ID = "clauseId";


    public interface Insert{}

    //
    // 业务方法(按public protected private顺序排列)
    // ------------------------------------------------------------------------------

	/**
	 * 校验卡片条目关系数据是否重复
	 * FIX 处理不兼容字段，rank字段在mysql8中被设置为保留字，因此废弃掉，使用order_seq 替代
	 */
	public void checkCardClauseRepeat(DashboardCardClauseRepository repository) {
		int count = repository.checkRepeat(clauseId, cardId);
		if (count != 0) {
			throw new CommonException(HpfmMsgCodeConstants.ERROR_CARD_CLAUSE_REPEAT);
		}
	}

    //
    // 数据库字段
    // ------------------------------------------------------------------------------


    @ApiModelProperty("表Id, 主键")
    @Id
    @GeneratedValue
	@Encrypt
    private Long id;
    @ApiModelProperty(value = "卡片ID")
    @NotNull
	@Encrypt
    private Long cardId;
    @ApiModelProperty(value = "条目ID")
    @NotNull
	@Encrypt
    private Long clauseId;
	@ApiModelProperty(value = "条目排序字段")
	private Integer orderSeq;
	@NotNull(groups = Insert.class)
	private Long tenantId;
    @ApiModelProperty(value = "条目排序字段-已废弃")
	@Transient
	@JsonIgnore
	private Integer rank;

	//
    // 非数据库字段
    // ------------------------------------------------------------------------------
	/**
	 * 卡片编码
	 */
	@Transient
	private String code;

	/**
	 * 卡片名称
	 */
	@Transient
	private String name;

    //
    // getter/setter
    // ------------------------------------------------------------------------------


	public Long getTenantId() {
		return tenantId;
	}

	public DashboardCardClause setTenantId(Long tenantId) {
		this.tenantId = tenantId;
		return this;
	}

	public Integer getOrderSeq() {
		return orderSeq;
	}

	public void setOrderSeq(Integer orderSeq) {
		this.orderSeq = orderSeq;
	}

	@Deprecated
	public Integer getRank() {
		return rank;
	}
	@Deprecated
	public void setRank(Integer rank) {
		this.rank = rank;
	}

	/**
     * @return 
     */
	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}
    /**
     * @return 卡片ID
     */
	public Long getCardId() {
		return cardId;
	}

	public void setCardId(Long cardId) {
		this.cardId = cardId;
	}
    /**
     * @return 条目ID
     */
	public Long getClauseId() {
		return clauseId;
	}

	public void setClauseId(Long clauseId) {
		this.clauseId = clauseId;
	}

	public String getCode() {
		return code;
	}

	public void setCode(String code) {
		this.code = code;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	@Override
	public boolean equals(Object o) {
		if (this == o)
			return true;
		if (!(o instanceof DashboardCardClause))
			return false;
		DashboardCardClause that = (DashboardCardClause) o;
		return getCardId().equals(that.getCardId()) && getClauseId().equals(that.getClauseId());
	}

	@Override
	public int hashCode() {
		return Objects.hash(getCardId(), getClauseId());
	}
}
