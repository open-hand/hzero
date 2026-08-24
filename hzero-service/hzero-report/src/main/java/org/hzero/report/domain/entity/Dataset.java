package org.hzero.report.domain.entity;

import com.fasterxml.jackson.annotation.JsonInclude;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import org.apache.commons.lang3.StringUtils;
import org.hzero.boot.platform.lov.annotation.LovValue;
import org.hzero.core.base.BaseConstants;
import org.hzero.core.util.Regexs;
import org.hzero.report.domain.repository.DatasetRepository;
import org.hzero.report.infra.constant.HrptConstants;
import org.hzero.report.infra.constant.HrptMessageConstants;
import org.hzero.report.infra.engine.SqlXmlParser;
import org.hzero.starter.keyencrypt.core.Encrypt;
import org.springframework.util.Assert;

import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.Transient;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;
import java.util.Objects;

import io.choerodon.mybatis.annotation.ModifyAudit;
import io.choerodon.mybatis.annotation.VersionAudit;
import io.choerodon.mybatis.domain.AuditDomain;

/**
 * 报表数据集
 *
 * @author xianzhi.chen@hand-china.com 2018-10-31 15:36:59
 */
@ApiModel("报表数据集")
@VersionAudit
@ModifyAudit
@JsonInclude(value = JsonInclude.Include.NON_NULL)
@Table(name = "hrpt_dataset")
public class Dataset extends AuditDomain {

    public static final String FIELD_DATASET_ID = "datasetId";
    public static final String FIELD_DATASET_CODE = "datasetCode";
    public static final String FIELD_DATASET_NAME = "datasetName";
    public static final String FIELD_DATASOURCE_CODE = "datasourceCode";
    public static final String FIELD_SQL_TEXT = "sqlText";
    public static final String FIELD_QUERY_PARAMS = "queryParams";
    public static final String FIELD_META_COLUMNS = "metaColumns";
    public static final String FIELD_SQL_TYPE = "sqlType";
    public static final String FIELD_ENABLED_FLAG = "enabledFlag";
    public static final String FIELD_REMARK = "remark";
    public static final String FIELD_TENANT_ID = "tenantId";


    //
    // 业务方法(按public protected private顺序排列)
    // ------------------------------------------------------------------------------

    /**
     * 被引用性校验
     *
     * @param datasetRepository 仓库
     * @return 是否被引用
     */
    public boolean existReference(DatasetRepository datasetRepository) {
        int cnt = datasetRepository.selectReferenceCount(datasetId);
        return cnt > 0;
    }

    /**
     * 校验
     *
     * @param datasetRepository 仓库
     */
    public void validateDataset(DatasetRepository datasetRepository) {
        // sql类型必须有数据源
        if (!Objects.equals(sqlType, HrptConstants.DataSetType.TYPE_A)) {
            Assert.isTrue(StringUtils.isNotBlank(datasourceCode), HrptMessageConstants.ERROR_NOSELECT_DATASOURCE);
        }
        Dataset ds = new Dataset().setTenantId(tenantId).setDatasetCode(datasetCode);
        Assert.isTrue(datasetRepository.selectCount(ds) <= 0, BaseConstants.ErrorCode.DATA_EXISTS);
    }

    /**
     * 获取sql类型
     *
     * @param sqlText sql文本
     * @return sql类型
     */
    public static String checkSqlType(String sqlText) {
        String sqlType = HrptConstants.DataSetType.TYPE_S;
        if (StringUtils.startsWith(sqlText, HrptConstants.XmlData.HTTP)) {
            sqlType = HrptConstants.DataSetType.TYPE_A;
        } else {
            try {
                if (SqlXmlParser.isSqlXml(sqlText)) {
                    sqlType = HrptConstants.DataSetType.TYPE_C;
                }
            } catch (Exception ignored) {
            }
        }
        return sqlType;
    }
    //
    // 数据库字段
    // ------------------------------------------------------------------------------


    @ApiModelProperty("表ID，主键，供其他表做外键")
    @Id
    @GeneratedValue
    @Encrypt
    private Long datasetId;
    @ApiModelProperty(value = "数据集代码")
    @Pattern(regexp = Regexs.CODE_UPPER)
    @NotBlank
    private String datasetCode;
    @ApiModelProperty(value = "数据集名称")
    @NotBlank
    private String datasetName;
    @ApiModelProperty(value = "数据源编码，hpfm_datasource.datasource_code")
    private String datasourceCode;
    @ApiModelProperty(value = "报表SQL语句")
    @NotBlank
    private String sqlText;
    @ApiModelProperty(value = "查询条件列属性集合(JSON格式)")
    private String queryParams;
    @ApiModelProperty(value = "数据集列元数据(JSON格式)")
    private String metaColumns;
    @ApiModelProperty(value = "SQL类型")
    @NotBlank
    @LovValue("HRPT.DATASET_SQL_TYPE")
    private String sqlType;
    @ApiModelProperty(value = "启用标识")
    private Integer enabledFlag;
    @ApiModelProperty(value = "备注说明")
    private String remark;
    @ApiModelProperty(value = "租户ID,hpfm_tenant.tenant_id")
    @NotNull
    private Long tenantId;

    //
    // 非数据库字段
    // ------------------------------------------------------------------------------

    @Transient
    private String sqlTypeMeaning;
    @Transient
    private String datasourceName;
    @Transient
    private String tenantName;

    //
    // getter/setter
    // ------------------------------------------------------------------------------

    public Long getDatasetId() {
        return datasetId;
    }

    public Dataset setDatasetId(Long datasetId) {
        this.datasetId = datasetId;
        return this;
    }

    public String getDatasetCode() {
        return datasetCode;
    }

    public Dataset setDatasetCode(String datasetCode) {
        this.datasetCode = datasetCode;
        return this;
    }

    public String getDatasetName() {
        return datasetName;
    }

    public Dataset setDatasetName(String datasetName) {
        this.datasetName = datasetName;
        return this;
    }

    public String getDatasourceCode() {
        return datasourceCode;
    }

    public Dataset setDatasourceCode(String datasourceCode) {
        this.datasourceCode = datasourceCode;
        return this;
    }

    public String getSqlText() {
        return sqlText;
    }

    public Dataset setSqlText(String sqlText) {
        this.sqlText = sqlText;
        return this;
    }

    public String getQueryParams() {
        return queryParams;
    }

    public Dataset setQueryParams(String queryParams) {
        this.queryParams = queryParams;
        return this;
    }

    public String getMetaColumns() {
        return metaColumns;
    }

    public Dataset setMetaColumns(String metaColumns) {
        this.metaColumns = metaColumns;
        return this;
    }

    public String getSqlType() {
        return sqlType;
    }

    public Dataset setSqlType(String sqlType) {
        this.sqlType = sqlType;
        return this;
    }

    public Integer getEnabledFlag() {
        return enabledFlag;
    }

    public Dataset setEnabledFlag(Integer enabledFlag) {
        this.enabledFlag = enabledFlag;
        return this;
    }

    public String getRemark() {
        return remark;
    }

    public Dataset setRemark(String remark) {
        this.remark = remark;
        return this;
    }

    public Long getTenantId() {
        return tenantId;
    }

    public Dataset setTenantId(Long tenantId) {
        this.tenantId = tenantId;
        return this;
    }

    public String getSqlTypeMeaning() {
        return sqlTypeMeaning;
    }

    public Dataset setSqlTypeMeaning(String sqlTypeMeaning) {
        this.sqlTypeMeaning = sqlTypeMeaning;
        return this;
    }

    public String getDatasourceName() {
        return datasourceName;
    }

    public Dataset setDatasourceName(String datasourceName) {
        this.datasourceName = datasourceName;
        return this;
    }

    public String getTenantName() {
        return tenantName;
    }

    public Dataset setTenantName(String tenantName) {
        this.tenantName = tenantName;
        return this;
    }
}
