package script.table

databaseChangeLog(logicalFilePath: 'script/db/hchg_rule_line.groovy') {
    changeSet(author: "junlin.zhu@hand-china.com", id: "2020-02-14_hchg_rule_line") {
        def weight = 1
        if (helper.isSqlServer()) {
            weight = 2
        } else if (helper.isOracle()) {
            weight = 3
        }
        if (helper.dbType().isSupportSequence()) {
            createSequence(sequenceName: 'hchg_rule_line_S', startValue: "10001")
        }
        createTable(tableName: "hchg_rule_line", remarks: '计费规则行表') {
            column(name: "rule_line_id", type: "bigint", autoIncrement: "true", startWith: "10001", remarks: "表ID，主键，供其他表做外键") {
                constraints(nullable: "false", primaryKey: "true", primaryKeyName: "hchg_rule_line_PK")
            }
            column(name: "rule_header_id", type: "bigint", remarks: "计费规则头ID，hchg_rule_header表主键") {
                constraints(nullable: "false")
            }
            column(name: "seq_number", type: "Int", remarks: "序号") {
                constraints(nullable: "false")
            }
            column(name: "greater_than", type: "decimal(20,2)", remarks: "大于")
            column(name: "less_and_equals", type: "decimal(20,2)", remarks: "小于等于")
            column(name: "constant_value", type: "decimal(20,2)", remarks: "固定数值")
            column(name: "price", type: "decimal(20,2)", remarks: "价格")
            column(name: "remark", type: "varchar(" + 240 * weight + ")", remarks: "备注说明")
            column(name: "tenant_id", type: "bigint", defaultValue: "0", remarks: "租户ID") {
                constraints(nullable: "false")
            }
            column(name: "object_version_number", type: "bigint", defaultValue: "1", remarks: "行版本号，用来处理锁") {
                constraints(nullable: "false")
            }
            column(name: "created_by", type: "bigint", defaultValue: "0") {
                constraints(nullable: "false")
            }
            column(name: "creation_date", type: "datetime", defaultValueComputed: "CURRENT_TIMESTAMP") {
                constraints(nullable: "false")
            }
            column(name: "last_updated_by", type: "bigint", defaultValue: "0") {
                constraints(nullable: "false")
            }
            column(name: "last_update_date", type: "datetime", defaultValueComputed: "CURRENT_TIMESTAMP") {
                constraints(nullable: "false")
            }
        }

        addUniqueConstraint(columnNames: "rule_header_id,seq_number", tableName: "hchg_rule_line", constraintName: "hchg_rule_line_U1")

        createIndex(tableName: "hchg_rule_line", indexName: "hchg_rule_line_N1") {
            column(name: "rule_header_id")
        }

    }


}