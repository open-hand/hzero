package org.hzero.platform.domain.entity;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;

import io.choerodon.core.exception.CommonException;
import javax.validation.constraints.NotBlank;
import org.hzero.boot.platform.lov.annotation.LovValue;

import com.fasterxml.jackson.annotation.JsonInclude;

import io.choerodon.mybatis.annotation.ModifyAudit;
import io.choerodon.mybatis.annotation.MultiLanguage;
import io.choerodon.mybatis.annotation.MultiLanguageField;
import io.choerodon.mybatis.annotation.VersionAudit;
import io.choerodon.mybatis.domain.AuditDomain;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import org.hzero.core.base.BaseConstants;
import org.hzero.core.util.Regexs;
import org.hzero.mybatis.common.query.Where;
import org.hzero.mybatis.domian.Condition;
import org.hzero.mybatis.util.Sqls;
import org.hzero.platform.domain.repository.DashboardClauseRepository;
import org.hzero.starter.keyencrypt.core.Encrypt;
import org.springframework.util.Assert;

import java.util.List;
import java.util.Objects;

/**
 * 工作台条目配置
 *
 * @author jun.gao@hand-china.com 2019-01-27 19:10:25
 * @author xiaoyu.zhao@hand-china.com
 */
@ApiModel("工作台条目配置")
@VersionAudit
@ModifyAudit
@MultiLanguage
@Table(name = "hpfm_dashboard_clause")
@JsonInclude(JsonInclude.Include.NON_NULL)
public class DashboardClause extends AuditDomain {

    public static final String FIELD_CLAUSE_ID = "clauseId";
    public static final String FIELD_CLAUSE_CODE = "clauseCode";
    public static final String FIELD_CLAUSE_NAME = "clauseName";
    public static final String FIELD_DATA_TENANT_LEVEL = "dataTenantLevel";
    public static final String FIELD_MENU_CODE = "menuCode";
    public static final String FIELD_ROUTE = "route";
    public static final String FIELD_STATS_EXPRESSION = "statsExpression";
    public static final String FIELD_DOC_REMARK_EXPRESSION = "docRemarkExpression";
    public static final String FIELD_ENABLED_FLAG = "enabledFlag";
    public static final String FIELD_REMARK = "remark";
    public static final String FIELD_TENANT_ID = "tenantId";


    //
    // 业务方法(按public protected private顺序排列)
    // ------------------------------------------------------------------------------

	/**
	 * 校验数据是否已经存在（通过clauseCode校验唯一性）
	 *
	 * @param dashboardClauseRepository 卡片条目资源库
	 */
	public void validate(DashboardClauseRepository dashboardClauseRepository) {
		int count = dashboardClauseRepository.selectCountByCondition(Condition.builder(DashboardClause.class)
				.andWhere(Sqls.custom()
						.andEqualTo(DashboardClause.FIELD_CLAUSE_CODE, clauseCode)
						.andEqualTo(DashboardClause.FIELD_TENANT_ID, tenantId)
				).build());
		if (count != 0) {
			// 数据重复
			throw new CommonException(BaseConstants.ErrorCode.DATA_EXISTS);
		}

	}

	/**
	 * 校验数据合法性
	 *
	 * @param incomingTenantId 传入的租户id
	 */
	public void judgeClauseValidity(Long incomingTenantId) {
		Assert.isTrue(Objects.equals(this.tenantId, incomingTenantId), BaseConstants.ErrorCode.DATA_INVALID);
	}

    //
    // 数据库字段
    // ------------------------------------------------------------------------------


    @ApiModelProperty("表ID，主键，供其他表做外键")
    @Id
    @GeneratedValue
	@Where
	@Encrypt
    private Long clauseId;
    @ApiModelProperty(value = "条目代码")
    @NotBlank
	@Where
	@Pattern(regexp = Regexs.CODE)
    private String clauseCode;
    @ApiModelProperty(value = "条目名称")
    @NotBlank
	@MultiLanguageField
    private String clauseName;
    @ApiModelProperty(value = "数据租户级别HPFM.DATA_TENANT_LEVEL(SITE/平台级|TENANT/租户级)")
    @NotBlank
	@LovValue(value = "HPFM.DATA_TENANT_LEVEL",meaningField = "dataTenantLevelMeaning")
    private String dataTenantLevel;
    @ApiModelProperty(value = "跳转至功能")
    private String menuCode;
    @ApiModelProperty(value = "路由")
    private String route;
    @ApiModelProperty(value = "数据匹配表达式")
    @NotBlank
    private String statsExpression;
    @ApiModelProperty(value = "单据标题/备注计算表达式")
    private String docRemarkExpression;
    @ApiModelProperty(value = "启用标识")
    @NotNull
    private Integer enabledFlag;
    @ApiModelProperty(value = "备注说明")
    private String remark;
    @NotNull
	@ApiModelProperty("租户ID hpfm_tenant.tenant_id")
	@MultiLanguageField
	private Long tenantId;

	//
    // 非数据库字段
    // ------------------------------------------------------------------------------

	@Transient
	private String dataTenantLevelMeaning;
	@Transient
	private List<DashboardCardClause> dashboardCardClauseList;

    //
    // getter/setter
    // ------------------------------------------------------------------------------

    /**
     * @return 表ID，主键，供其他表做外键
     */
	public Long getClauseId() {
		return clauseId;
	}

	public void setClauseId(Long clauseId) {
		this.clauseId = clauseId;
	}
    /**
     * @return 条目代码
     */
	public String getClauseCode() {
		return clauseCode;
	}

	public void setClauseCode(String clauseCode) {
		this.clauseCode = clauseCode;
	}
    /**
     * @return 条目名称
     */
	public String getClauseName() {
		return clauseName;
	}

	public void setClauseName(String clauseName) {
		this.clauseName = clauseName;
	}
    /**
     * @return 数据租户级别HPFM.DATA_TENANT_LEVEL(SITE/平台级|TENANT/租户级)
     */
	public String getDataTenantLevel() {
		return dataTenantLevel;
	}

	public void setDataTenantLevel(String dataTenantLevel) {
		this.dataTenantLevel = dataTenantLevel;
	}
    /**
     * @return 跳转至功能
     */
	public String getMenuCode() {
		return menuCode;
	}

	public void setMenuCode(String menuCode) {
		this.menuCode = menuCode;
	}
    /**
     * @return 路由
     */
	public String getRoute() {
		return route;
	}

	public void setRoute(String route) {
		this.route = route;
	}
    /**
     * @return 数据匹配表达式
     */
	public String getStatsExpression() {
		return statsExpression;
	}

	public void setStatsExpression(String statsExpression) {
		this.statsExpression = statsExpression;
	}
    /**
     * @return 单据标题/备注计算表达式
     */
	public String getDocRemarkExpression() {
		return docRemarkExpression;
	}

	public void setDocRemarkExpression(String docRemarkExpression) {
		this.docRemarkExpression = docRemarkExpression;
	}
    /**
     * @return 启用标识
     */
	public Integer getEnabledFlag() {
		return enabledFlag;
	}

	public void setEnabledFlag(Integer enabledFlag) {
		this.enabledFlag = enabledFlag;
	}
    /**
     * @return 备注说明
     */
	public String getRemark() {
		return remark;
	}

	public void setRemark(String remark) {
		this.remark = remark;
	}

	public String getDataTenantLevelMeaning() {
		return dataTenantLevelMeaning;
	}

	public void setDataTenantLevelMeaning(String dataTenantLevelMeaning) {
		this.dataTenantLevelMeaning = dataTenantLevelMeaning;
	}

	public List<DashboardCardClause> getDashboardCardClauseList() {
		return dashboardCardClauseList;
	}

	public void setDashboardCardClauseList(List<DashboardCardClause> dashboardCardClauseList) {
		this.dashboardCardClauseList = dashboardCardClauseList;
	}

	public Long getTenantId() {
		return tenantId;
	}

	public void setTenantId(Long tenantId) {
		this.tenantId = tenantId;
	}

}
