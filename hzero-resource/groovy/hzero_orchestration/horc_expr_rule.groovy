package script.db

databaseChangeLog(logicalFilePath: 'script/db/horc_expr_rule.groovy') {
    changeSet(author: "xiaolong.li@hand-china.com", id: "2020-07-28_horc_expr_rule") {
        def weight = 1
        if (helper.isSqlServer()) {
            weight = 2
        } else if (helper.isOracle()) {
            weight = 3
        }
        if (helper.dbType().isSupportSequence()) {
            createSequence(sequenceName: 'horc_expr_rule_S', startValue: "10001")
        }
        createTable(tableName: "horc_expr_rule", remarks: "数据映射公式规则") {
            column(name: "expr_rule_id", type: "bigint", autoIncrement: "true", startWith: "10001", remarks: "表ID，主键，供其他表做外键") {
                constraints(nullable: "false", primaryKey: "true", primaryKeyName: "horc_expr_rule_PK")
            }
            column(name: "cast_line_id", type: "bigint", remarks: "数据映射配置行表ID")
            column(name: "expr_field_source_type", type: "varchar(30)", remarks: "公式字段来源类型") {
                constraints(nullable: "false")
            }
            column(name: "expr_field_source_value", type: "varchar(" + 240 * weight + ")", remarks: "公式字段来源取值") {
                constraints(nullable: "false")
            }
            column(name: "order_seq", type: "int", remarks: "排序号")
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
        addUniqueConstraint(columnNames: "cast_line_id,expr_field_source_type,expr_field_source_value", tableName: "horc_expr_rule", constraintName: "horc_expr_rule_U1")
    }
}