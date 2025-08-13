package org.hzero.platform.domain.entity;

import java.util.Objects;

import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.Transient;
import javax.validation.constraints.NotNull;

import com.fasterxml.jackson.annotation.JsonIgnore;
import org.hibernate.validator.constraints.Range;
import org.hzero.boot.platform.lov.annotation.LovValue;
import org.hzero.mybatis.domian.Condition;
import org.hzero.mybatis.util.Sqls;
import org.hzero.platform.domain.repository.DashboardRoleCardRepository;
import org.hzero.platform.infra.constant.Constants;
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
 * 角色卡片表
 *
 * @author xiaoyu.zhao@hand-china.com 2019-01-24 19:58:17
 */
@ApiModel("角色卡片表")
@VersionAudit
@ModifyAudit
@Table(name = "hpfm_dashboard_role_card")
@JsonInclude(JsonInclude.Include.NON_NULL)
public class DashboardRoleCard extends AuditDomain {

    public static final String FIELD_ID = "id";
    public static final String FIELD_ROLE_ID = "roleId";
    public static final String FIELD_CARD_ID = "cardId";
    public static final String FIELD_X = "x";
    public static final String FIELD_Y = "y";
    public static final String FIELD_DEFAULT_DISPLAY_FLAG = "defaultDisplayFlag";


    //
    // 业务方法(按public protected private顺序排列)
    // ------------------------------------------------------------------------------

	/**
	 * 校验cardId和roleId是否在数据库中已经存在
	 *
	 * @param roleCardRepository 角色卡片资源库
	 */
	public void validate(DashboardRoleCardRepository roleCardRepository) {
		int count = roleCardRepository.selectCountByCondition(Condition.builder(DashboardRoleCard.class)
				.andWhere(Sqls.custom()
						.andEqualTo(DashboardRoleCard.FIELD_ROLE_ID, roleId)
						.andEqualTo(DashboardRoleCard.FIELD_CARD_ID, cardId))
				.build());
		if (count != 0) {
			throw new CommonException(HpfmMsgCodeConstants.DASHBOARD_ROLE_CARD_EXISTS);
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
    @ApiModelProperty(value = "角色id")
    @NotNull
	@Encrypt
    private Long roleId;
    @ApiModelProperty(value = "卡片id")
    @NotNull
	@Encrypt
    private Long cardId;
    @ApiModelProperty(value = "x轴")
    @NotNull
    private Integer x;
    @ApiModelProperty(value = "y轴")
    @NotNull
    private Integer y;
    @ApiModelProperty(value = "是否默认显示")
    @NotNull
	@Range(max = 1)
    private Integer defaultDisplayFlag;

	//
    // 非数据库字段
    // ------------------------------------------------------------------------------

	@Transient
	private String code;
	@Transient
	private String name;
	@Transient
	@LovValue(lovCode = "HPFM.DASHBOARD_CARD.TYPE", meaningField = "catalogMeaning")
	private String catalogType;
	@Transient
	private Integer w;
	@Transient
	private Integer h;
	@Transient
	private String catalogMeaning;
	@Transient
	private Long tenantId;
	@Transient
	@JsonIgnore
	private String cardParams;
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
     * @return 角色id
     */
	public Long getRoleId() {
		return roleId;
	}

	public void setRoleId(Long roleId) {
		this.roleId = roleId;
	}
    /**
     * @return 卡片id
     */
	public Long getCardId() {
		return cardId;
	}

	public void setCardId(Long cardId) {
		this.cardId = cardId;
	}
    /**
     * @return x轴
     */
	public Integer getX() {
		return x;
	}

	public void setX(Integer x) {
		this.x = x;
	}
    /**
     * @return y轴
     */
	public Integer getY() {
		return y;
	}

	public void setY(Integer y) {
		this.y = y;
	}
    /**
     * @return 是否默认显示
     */
	public Integer getDefaultDisplayFlag() {
		return defaultDisplayFlag;
	}

	public void setDefaultDisplayFlag(Integer defaultDisplayFlag) {
		this.defaultDisplayFlag = defaultDisplayFlag;
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

	public String getCatalogType() {
		return catalogType;
	}

	public void setCatalogType(String catalogType) {
		this.catalogType = catalogType;
	}

	public Integer getW() {
		return w;
	}

	public void setW(Integer w) {
		this.w = w;
	}

	public Integer getH() {
		return h;
	}

	public void setH(Integer h) {
		this.h = h;
	}

	public String getCatalogMeaning() {
		return catalogMeaning;
	}

	public void setCatalogMeaning(String catalogMeaning) {
		this.catalogMeaning = catalogMeaning;
	}

	public Long getTenantId() {
		return tenantId;
	}

	public void setTenantId(Long tenantId) {
		this.tenantId = tenantId;
	}

	@Override
	public boolean equals(Object o) {
		if (this == o)
			return true;
		if (!(o instanceof DashboardRoleCard))
			return false;
		DashboardRoleCard that = (DashboardRoleCard) o;
		return Objects.equals(getRoleId(), that.getRoleId()) && Objects.equals(getCardId(), that.getCardId());
	}

	@Override
	public int hashCode() {
		return Objects.hash(getRoleId(), getCardId());
	}

	public DashboardRoleCard(Long roleId, Long cardId, Integer x, Integer y, Integer defaultDisplayFlag) {
		this.roleId = roleId;
		this.cardId = cardId;
		this.x = x;
		this.y = y;
		this.defaultDisplayFlag = defaultDisplayFlag;
	}

	public DashboardRoleCard(Long roleId, Long cardId, Integer x, Integer y, Integer defaultDisplayFlag, Long tenantId) {
		this.roleId = roleId;
		this.cardId = cardId;
		this.x = x;
		this.y = y;
		this.defaultDisplayFlag = defaultDisplayFlag;
		this.tenantId = tenantId;
	}

	public DashboardRoleCard() {
	}

	public String getCardParams() {
		return cardParams;
	}

	public void setCardParams(String cardParams) {
		this.cardParams = cardParams;
	}
}
