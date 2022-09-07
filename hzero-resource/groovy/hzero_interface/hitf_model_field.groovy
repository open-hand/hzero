package script.db

databaseChangeLog(logicalFilePath: 'script/db/hitf_model_field.groovy') {
    changeSet(author: "junlin.zhu@hand-china.com", id: "2020-03-11_hitf_model_field") {
        def weight = 1
        if (helper.isSqlServer()) {
            weight = 2
        } else if (helper.isOracle()) {
            weight = 3
        }
        if (helper.dbType().isSupportSequence()) {
            createSequence(sequenceName: 'hitf_model_field_S', startValue: "10001")
        }
        createTable(tableName: "hitf_model_field", remarks: "模型字段") {
            column(name: "field_id", type: "bigint", autoIncrement: "true", startWith: "10001", remarks: "表ID，主键，供其他表做外键") {
                constraints(nullable: "false", primaryKey: "true", primaryKeyName: "hitf_model_field_PK")
            }
            column(name: "model_id", type: "bigint", remarks: "数据源模型配置,hitf_model_config.model_id") {
                constraints(nullable: "false")
            }
            column(name: "field_name", type: "varchar(" + 100 + ")", remarks: "字段名") {
                constraints(nullable: "false")
            }
            column(name: "field_type", type: "varchar(" + 30 + ")", remarks: "字段类型,值集：HITF.SVC.COL_TYPE") {
                constraints(nullable: "false")
            }
            column(name: "field_desc", type: "varchar(" + 255 * weight + ")", remarks: "字段描述")
            column(name: "field_expr", type: "varchar(" + 255 * weight + ")", remarks: "字段表达式")
            column(name: "seq_num", type: "bigint", defaultValue: "1", remarks: "字段排序号") {
                constraints(nullable: "false")
            }
            column(name: "privacy_level", type: "Tinyint", defaultValue: "0", remarks: "密级(0无密,1绝密,2脱敏),HITF.SVC.PRIVACY_LEVEL") {
                constraints(nullable: "false")
            }
            column(name: "table_alias", type: "varchar(" + 30 + ")", remarks: "表别名")
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
        addUniqueConstraint(columnNames: "model_id,field_name,tenant_id", tableName: "hitf_model_field", constraintName: "hitf_model_field_U1")
    }
}