package org.hzero.platform.domain.entity;

import java.util.Date;
import java.util.Objects;

import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.Transient;
import javax.validation.constraints.NotNull;

import org.hzero.core.base.BaseConstants;
import org.hzero.mybatis.domian.Condition;
import org.hzero.mybatis.util.Sqls;
import org.hzero.platform.domain.repository.DashboardTenantCardRepository;
import org.hzero.platform.infra.constant.HpfmMsgCodeConstants;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;

import io.choerodon.core.exception.CommonException;
import io.choerodon.mybatis.annotation.ModifyAudit;
import io.choerodon.mybatis.annotation.VersionAudit;
import io.choerodon.mybatis.domain.AuditDomain;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import org.hzero.starter.keyencrypt.core.Encrypt;

/**
 * 租户卡片分配
 *
 * @author xiaoyu.zhao@hand-china.com 2019-01-24 09:30:19
 */
@ApiModel("租户卡片分配")
@VersionAudit
@ModifyAudit
@Table(name = "hpfm_dashboard_tenant_card")
@JsonInclude(JsonInclude.Include.NON_NULL)
public class DashboardTenantCard extends AuditDomain {

    public static final String FIELD_ID = "id";
    public static final String FIELD_TENANT_ID = "tenantId";
    public static final String FIELD_CARD_ID = "cardId";


    //
    // 业务方法(按public protected private顺序排列)
    // ------------------------------------------------------------------------------

	/**
	 * 校验数据是否已经存在
	 */
	public void validate(DashboardTenantCardRepository tenantCardRepository) {
		int count = tenantCardRepository.selectCountByCondition(Condition.builder(DashboardTenantCard.class)
				.andWhere(Sqls.custom()
						.andEqualTo(FIELD_CARD_ID, cardId)
						.andEqualTo(FIELD_TENANT_ID, tenantId)
				)
				.build());
		if (count != 0) {
			throw new CommonException(BaseConstants.ErrorCode.DATA_EXISTS);
		}
	}

    //
    // 数据库字段
    // ------------------------------------------------------------------------------

    @ApiModelProperty("表ID，主键，供其他表做外键")
    @Id
    @GeneratedValue
	@Encrypt
    private Long id;
    @ApiModelProperty(value = "租户表hpfm_tenant.tenant_id")
    @NotNull
    private Long tenantId;
    @ApiModelProperty(value = "卡片表hpfm_dashboard_card.id")
    @NotNull
	@Encrypt
    private Long cardId;

	//
    // 非数据库字段
    // ------------------------------------------------------------------------------

	@Transient
	@ApiModelProperty("租户名称")
	private String tenantName;
	@Transient
	@ApiModelProperty("租户编码")
	private String tenantNum;
	@Transient
	@ApiModelProperty("起始时间-筛选注册时间使用")
	@JsonIgnore
	private Date beginDate;
	@Transient
	@JsonIgnore
	@ApiModelProperty("结束时间-筛选注册时间使用")
	private Date endDate;


    //
    // getter/setter
    // ------------------------------------------------------------------------------

    /**
     * @return 表ID，主键，供其他表做外键
     */
	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}
    /**
     * @return 租户表hpfm_tenant.tenant_id
     */
	public Long getTenantId() {
		return tenantId;
	}

	public void setTenantId(Long tenantId) {
		this.tenantId = tenantId;
	}
    /**
     * @return 卡片表hpfm_dashboard_card.id
     */
	public Long getCardId() {
		return cardId;
	}

	public void setCardId(Long cardId) {
		this.cardId = cardId;
	}

	public String getTenantName() {
		return tenantName;
	}

	public void setTenantName(String tenantName) {
		this.tenantName = tenantName;
	}

	public String getTenantNum() {
		return tenantNum;
	}

	public void setTenantNum(String tenantNum) {
		this.tenantNum = tenantNum;
	}

	public Date getBeginDate() {
		return beginDate;
	}

	public void setBeginDate(Date beginDate) {
		this.beginDate = beginDate;
	}

	public Date getEndDate() {
		return endDate;
	}

	public void setEndDate(Date endDate) {
		this.endDate = endDate;
	}

	@Override
	public boolean equals(Object o) {
		if (this == o)
			return true;
		if (!(o instanceof DashboardTenantCard))
			return false;
		DashboardTenantCard that = (DashboardTenantCard) o;
		return getTenantId().equals(that.getTenantId()) && getCardId().equals(that.getCardId());
	}

	@Override
	public int hashCode() {
		return Objects.hash(getTenantId(), getCardId());
	}
}
