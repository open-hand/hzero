package script.db

databaseChangeLog(logicalFilePath: 'script/db/hitf_model_config.groovy') {

    changeSet(author: "junlin.zhu@hand-china.com", id: "2020-03-12_hitf_model_config") {
        def weight = 1
        if (helper.isSqlServer()) {
            weight = 2
        } else if (helper.isOracle()) {
            weight = 3
        }
        if (helper.dbType().isSupportSequence()) {
            createSequence(sequenceName: 'hitf_model_config_S', startValue: "10001")
        }
        createTable(tableName: "hitf_model_config", remarks: "数据源模型配置(接口配置扩展)") {
            column(name: "model_id", type: "bigint", autoIncrement: "true", startWith: "10001", remarks: "表ID，主键，供其他表做外键") {
                constraints(nullable: "false", primaryKey: "true", primaryKeyName: "hitf_model_config_PK")
            }
            column(name: "interface_id", type: "bigint", remarks: "接口主键，hitf_interface.interface_id") {
                constraints(nullable: "false")
            }
            column(name: "ds_type", type: "varchar(" + 30 + ")", remarks: "数据源类型(值集：HPFM.DATABASE_TYPE)") {
                constraints(nullable: "false")
            }
            column(name: "datasource_id", type: "bigint", remarks: "数据源主键,hpfm_datasource.datasource_id") {
                constraints(nullable: "false")
            }
            column(name: "ds_purpose_code", type: "varchar(" + 30 + ")", remarks: "数据源用途") {
                constraints(nullable: "false")
            }
            column(name: "datasource_code", type: "varchar(" + 30 + ")", remarks: "数据源编码") {
                constraints(nullable: "false")
            }
            column(name: "ds_config_json", type: "clob", remarks: "数据源配置参数") {
                constraints(nullable: "false")
            }
            column(name: "expr_type", type: "varchar(" + 30 + ")", remarks: "表达式类型(DBO[DB OBJECT],SQL,值集码:HITF.SVC.EXPRESSION_TYPE") {
                constraints(nullable: "false")
            }
            column(name: "expr_content", type: "clob", remarks: "表达式内容(表，自定义sql)") {
                constraints(nullable: "false")
            }
            column(name: "remark", type: "varchar(" + 255 * weight + ")", remarks: "备注说明")
            column(name: "tenant_id", type: "bigint", defaultValue: "0", remarks: "租户") {
                constraints(nullable: "false")
            }
            column(name: "object_version_number", type: "bigint", defaultValue: "1", remarks: "行版本号，用来处理锁") {
                constraints(nullable: "false")
            }
            column(name: "created_by", type: "bigint", defaultValue: "-1")
            column(name: "creation_date", type: "datetime", defaultValueComputed: "CURRENT_TIMESTAMP")
            column(name: "last_updated_by", type: "bigint", defaultValue: "-1")
            column(name: "last_update_date", type: "datetime", defaultValueComputed: "CURRENT_TIMESTAMP")
        }
        addUniqueConstraint(columnNames: "interface_id,tenant_id", tableName: "hitf_model_config", constraintName: "hitf_model_config_U1")
    }
}


