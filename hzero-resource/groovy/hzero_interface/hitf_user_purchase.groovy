package script.db

databaseChangeLog(logicalFilePath: 'script/db/hitf_user_purchase.groovy') {
    changeSet(author: "minghui.qiu@hand-china.com ", id: "2020-02-18-hitf_user_purchase") {
        def weight = 1
        if (helper.isSqlServer()) {
            weight = 2
        } else if (helper.isOracle()) {
            weight = 3
        }
        if (helper.dbType().isSupportSequence()) {
            createSequence(sequenceName: 'hitf_user_purchase_s', startValue: "1")
        }
        createTable(tableName: "hitf_user_purchase", remarks: "用户购买记录信息") {
            column(name: "user_purchase_id", type: "bigint", autoIncrement: true, remarks: "表ID，主键") { constraints(primaryKey: true) }
            column(name: "source_type_code", type: "varchar(" + 30 + ")", remarks: "来源类型,值集:HITF.PURCHASE_TYPE") { constraints(nullable: "false") }
            column(name: "status_code", type: "varchar(" + 30 + ")", remarks: "状态")
            column(name: "source_id", type: "bigint", remarks: "来源ID") { constraints(nullable: "false") }
            column(name: "charge_rule_name", type: "varchar(" + 240 * weight + ")", remarks: "计费规则名称") { constraints(nullable: "false") }
            column(name: "charge_rule_code", type: "varchar(" + 60 + ")", remarks: "计费规则代码") { constraints(nullable: "false") }
            column(name: "charge_method_code", type: "varchar(" + 30 + ")", remarks: "计费方式代码:总包/计量")
            column(name: "payment_model", type: "varchar(" + 30 + ")", remarks: "付费模式代码") { constraints(nullable: "false") }
            column(name: "settlement_period", type: "varchar(" + 30 + ")", remarks: "结算周期代码，值集：HITF.SETTLEMENT_PERIOD")
            column(name: "total_count", type: "bigint", remarks: "总数量")
            column(name: "remain_count", type: "bigint", remarks: "余量")
            column(name: "charge_uom_code", type: "varchar(" + 30 + ")", remarks: "计费单位代码")
            column(name: "next_bill_time", type: "datetime", remarks: "下一个账单时间")
            column(name: "charge_rule_id", type: "bigint", remarks: "计费规则头ID") { constraints(nullable: "false") }
            column(name: "charge_rule_line_id", type: "bigint", remarks: "计费规则行ID")
            column(name: "tenant_id", type: "bigint", remarks: "租户ID") { constraints(nullable: "false") }
            column(name: "remark", type: "longtext", remarks: "备注说明")
            column(name: "object_version_number", type: "bigint", defaultValue: "1", remarks: "行版本号，用来处理锁") { constraints(nullable: "false") }
            column(name: "creation_date", type: "datetime", defaultValueComputed: "CURRENT_TIMESTAMP", remarks: "") { constraints(nullable: "false") }
            column(name: "created_by", type: "bigint", defaultValue: "-1", remarks: "") { constraints(nullable: "false") }
            column(name: "last_updated_by", type: "bigint", defaultValue: "-1", remarks: "") { constraints(nullable: "false") }
            column(name: "last_update_date", type: "datetime", defaultValueComputed: "CURRENT_TIMESTAMP", remarks: "") { constraints(nullable: "false") }
        }

        createIndex(tableName: "hitf_user_purchase", indexName: "hitf_user_purchase_n1") {
            column(name: "source_type_code")
            column(name: "source_id")
        }
    }
}