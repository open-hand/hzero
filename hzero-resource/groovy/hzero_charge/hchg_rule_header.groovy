package script.table

databaseChangeLog(logicalFilePath: 'script/db/hchg_rule_header.groovy') {
    changeSet(author: "junlin.zhu@hand-china.com", id: "2020-02-14_hchg_rule_header") {
        def weight = 1
        if (helper.isSqlServer()) {
            weight = 2
        } else if (helper.isOracle()) {
            weight = 3
        }
        if (helper.dbType().isSupportSequence()) {
            createSequence(sequenceName: 'hchg_rule_header_S', startValue: "10001")
        }
        createTable(tableName: "hchg_rule_header", remarks: '计费规则头表') {
            column(name: "rule_header_id", type: "bigint", autoIncrement: "true", startWith: "10001", remarks: "表ID，主键，供其他表做外键") {
                constraints(nullable: "false", primaryKey: "true", primaryKeyName: "hchg_rule_header_PK")
            }
            column(name: "rule_num", type: "varchar(" + 60 * weight + ")", remarks: "规则代码") {
                constraints(nullable: "false")
            }
            column(name: "rule_name", type: "varchar(" + 240 * weight + ")", remarks: "规则名称") {
                constraints(nullable: "false")
            }
            column(name: "status_code", type: "varchar(" + 10 * weight + ")", defaultValue: "NEW", remarks: "状态代码，编码：HCHG.RULE.STATUS") {
                constraints(nullable: "false")
            }
            column(name: "method_code", type: "varchar(" + 10 * weight + ")", remarks: "计费方式代码，编码：HCHG.RULE.CHARGE_METHOD") {
                constraints(nullable: "false")
            }
            column(name: "type_code", type: "varchar(" + 30 * weight + ")", remarks: "计费类型代码，编码：HCHG.RULE.CHARGE_TYPE") {
                constraints(nullable: "false")
            }
            column(name: "unit_code", type: "varchar(" + 10 * weight + ")", remarks: "计量单位代码，编码：HCHG.RULE.CHARGE_UNIT") {
                constraints(nullable: "false")
            }
            column(name: "prepay_amount", type: "decimal(20,2)", remarks: "预付金额")
            column(name: "start_date", type: "Date", remarks: "生效日期") {
                constraints(nullable: "false")
            }
            column(name: "end_date", type: "Date", remarks: "失效日期")
            column(name: "payment_model_code", type: "varchar(" + 10 * weight + ")", remarks: "付费模式，编码：HCHG.RULE.PAYMENT_MODEL") {
                constraints(nullable: "false")
            }
            column(name: "calculate_engine", type: "varchar(" + 120 * weight + ")", remarks: "计费引擎")
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

        addUniqueConstraint(columnNames: "rule_num", tableName: "hchg_rule_header", constraintName: "hchg_rule_header_U1")

        addUniqueConstraint(columnNames: "rule_name", tableName: "hchg_rule_header", constraintName: "hchg_rule_header_U2")

        createIndex(tableName: "hchg_rule_header", indexName: "hchg_rule_header_N1") {
            column(name: "start_date")
        }

        createIndex(tableName: "hchg_rule_header", indexName: "hchg_rule_header_N2") {
            column(name: "end_date")
        }

    }

}