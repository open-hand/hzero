package script.db

databaseChangeLog(logicalFilePath: 'script/db/horc_dm_target.groovy') {
    changeSet(author: "xiaolong.li@hand-china.com", id: "2020-07-28_horc_dm_target") {
        def weight = 1
        if (helper.isSqlServer()) {
            weight = 2
        } else if (helper.isOracle()) {
            weight = 3
        }
        if (helper.dbType().isSupportSequence()) {
            createSequence(sequenceName: 'horc_dm_target_S', startValue: "10001")
        }
        createTable(tableName: "horc_dm_target", remarks: "数据映射值映射目标") {
            column(name: "mapping_target_id", type: "bigint", autoIncrement: "true", startWith: "10001", remarks: "表ID，主键，供其他表做外键") {
                constraints(nullable: "false", primaryKey: "true", primaryKeyName: "horc_dm_target_PK")
            }
            column(name: "cast_line_id", type: "bigint", remarks: "数据映射配置行表ID") {
                constraints(nullable: "false")
            }
            column(name: "mapping_field", type: "varchar(80)", remarks: "映射字段")
            column(name: "field_type", type: "varchar(30)", defaultValue: "STRING", remarks: "映射字段类型，HORC.MAPPING_FIELD_TYPE") {
                constraints(nullable: "false")
            }
            column(name: "target_value", type: "varchar(" + 240 * weight + ")", remarks: "映射后的值") {
                constraints(nullable: "false")
            }
            column(name: "conjunction", type: "varchar(30)", defaultValue: "AND", remarks: "映射来源值连接符（AND且/OR或）") {
                constraints(nullable: "false")
            }
            column(name: "condition_json", type: "clob", remarks: "映射来源条件组织JSON")
            column(name: "evaluate_expression", type: "clob", remarks: "映射来源条件计算表达式")
            column(name: "status_code", type: "varchar(30)", defaultValue: "NEW", remarks: "状态") {
                constraints(nullable: "false")
            }
            column(name: "version", type: "decimal(8,4)", defaultValue: "0", remarks: "版本") {
                constraints(nullable: "false")
            }
            column(name: "from_version", type: "decimal(8,4)", remarks: "来源版本")
            column(name: "tenant_id", type: "bigint", defaultValue: "0", remarks: "租户ID") {
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
        addUniqueConstraint(columnNames: "cast_line_id,target_value", tableName: "horc_dm_target", constraintName: "horc_dm_target_U1")
    }
}

