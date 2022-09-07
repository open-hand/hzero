package script.db

databaseChangeLog(logicalFilePath: 'script/db/hrpt_dataset.groovy') {
    changeSet(author: "hzero@hand-china.com", id: "2019-02-19-hrpt_dataset") {
        def weight = 1
        if(helper.isSqlServer()){
            weight = 2
        } else if(helper.isOracle()){
            weight = 3
        }
        if(helper.dbType().isSupportSequence()){
            createSequence(sequenceName: 'hrpt_dataset_s', startValue:"1")
        }
        createTable(tableName: "hrpt_dataset", remarks: "报表数据集") {
            column(name: "dataset_id", type: "bigint", autoIncrement: true ,   remarks: "表ID，主键，供其他表做外键")  {constraints(primaryKey: true)}
            column(name: "dataset_code", type: "varchar(" + 30 * weight + ")",  remarks: "数据集代码")  {constraints(nullable:"false")}
            column(name: "dataset_name", type: "varchar(" + 120 * weight + ")",  remarks: "数据集名称")  {constraints(nullable:"false")}
            column(name: "datasource_id", type: "bigint",  remarks: "数据源，hpfm_datasource.datasource_id")  {constraints(nullable:"false")}
            column(name: "sql_text", type: "longtext",  remarks: "报表SQL语句")  {constraints(nullable:"false")}
            column(name: "query_params", type: "longtext",  remarks: "查询条件列属性集合(JSON格式)")
            column(name: "meta_columns", type: "longtext",  remarks: "数据集列元数据(JSON格式)")
            column(name: "sql_type", type: "varchar(" + 30 * weight + ")",   defaultValue:"",   remarks: "SQL类型,独立值集：HRPT.DATASET_SQL_TYPE")  {constraints(nullable:"false")}
            column(name: "enabled_flag", type: "tinyint",   defaultValue:"1",   remarks: "启用标识")
            column(name: "remark", type: "longtext",  remarks: "备注说明")
            column(name: "tenant_id", type: "bigint",   defaultValue:"0",   remarks: "租户ID,hpfm_tenant.tenant_id")  {constraints(nullable:"false")}
            column(name: "object_version_number", type: "bigint",   defaultValue:"1",   remarks: "行版本号，用来处理锁")  {constraints(nullable:"false")}
            column(name: "creation_date", type: "datetime",   defaultValueComputed:"CURRENT_TIMESTAMP",   remarks: "")  {constraints(nullable:"false")}
            column(name: "created_by", type: "bigint",   defaultValue:"-1",   remarks: "")  {constraints(nullable:"false")}
            column(name: "last_updated_by", type: "bigint",   defaultValue:"-1",   remarks: "")  {constraints(nullable:"false")}
            column(name: "last_update_date", type: "datetime",   defaultValueComputed:"CURRENT_TIMESTAMP",   remarks: "")  {constraints(nullable:"false")}

        }
   createIndex(tableName: "hrpt_dataset", indexName: "hrpt_dataset_n1") {
            column(name: "dataset_code")
            column(name: "tenant_id")
        }

    }

    changeSet(author: "hzero@hand-china.com", id: "2019-05-08-hrpt_dataset") {
        def weight = 1
        if(helper.isSqlServer()){
            weight = 2
        } else if(helper.isOracle()){
            weight = 3
        }
        dropColumn(tableName: 'hrpt_dataset', columnName: 'datasource_id')
        addColumn(tableName: 'hrpt_dataset') {
            if (helper.isOracle()) {
                column(name: "datasource_code", type: "varchar(" + 30 * weight + ")", defaultValue: "", remarks: "数据源编码，hpfm_datasource.datasource_code") {
                    constraints(nullable: "false")
                }
            } else {
                column(name: "datasource_code", type: "varchar(" + 30 * weight + ")", remarks: "数据源编码，hpfm_datasource.datasource_code") {
                    constraints(nullable: "false")
                }
            }
        }
    }

    changeSet(author: "hzero@hand-china.com", id: "2019-12-03-hrpt_dataset") {
        def weight = 1
        if(helper.isSqlServer()){
            weight = 2
        } else if(helper.isOracle()){
            weight = 3
        }
        dropNotNullConstraint(tableName: "hrpt_dataset", columnName: "datasource_code", columnDataType: "varchar(" + 30 * weight + ")")
    }

    changeSet(author: "shuangfei.zhu@hand-china.com", id: "2020-01-03-hrpt_dataset") {
        dropIndex(tableName: 'hrpt_dataset', indexName: 'hrpt_dataset_n1')
        addUniqueConstraint(columnNames:"dataset_code,tenant_id", tableName:"hrpt_dataset", constraintName: "hrpt_dataset_u1")
    }
}