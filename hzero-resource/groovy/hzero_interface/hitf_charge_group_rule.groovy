package script.db

databaseChangeLog(logicalFilePath: 'script/db/hitf_charge_group_rule.groovy') {
    changeSet(author: "minghui.qiu@hand-china.com ", id: "2020-02-14-hitf_charge_group_rule") {
        def weight = 1
        if (helper.isSqlServer()) {
            weight = 2
        } else if (helper.isOracle()) {
            weight = 3
        }
        if (helper.dbType().isSupportSequence()) {
            createSequence(sequenceName: 'hitf_charge_group_rule_s', startValue: "1")
        }
        createTable(tableName: "hitf_charge_group_rule", remarks: "组合计费计费规则配置表") {
            column(name: "group_rule_id", type: "bigint", autoIncrement: true ,   remarks: "表ID，主键")  {constraints(primaryKey: true)}
			column(name: "group_header_id", type: "bigint",  remarks: "组合计费头表ID")  {constraints(nullable:"false")}			
			column(name: "charge_rule_name", type: "varchar(" + 240 * weight + ")",  remarks: "计费规则名称")   {constraints(nullable:"false")}
			column(name: "charge_rule_code", type: "varchar(" + 60 * weight + ")",  remarks: "计费规则代码")   {constraints(nullable:"false")} 
			column(name: "settlement_period", type: "varchar(" + 30 * weight + ")",  remarks: "结算周期代码，值集：HITF.SETTLEMENT_PERIOD")   {constraints(nullable:"false")} 
			column(name: "payment_model", type: "varchar(" + 30 * weight + ")",  remarks: "付费模式代码") 
			column(name: "charge_method_code", type: "varchar(" + 30 * weight + ")",  remarks: "计费方式代码:总包/计量")
			column(name: "charge_uom_code", type: "varchar(" + 30 * weight + ")",  remarks: "计费单位")
			column(name: "start_date", type: "datetime",   remarks: "起始日期")  
			column(name: "end_date", type: "datetime",   remarks: "起始日期")			
			column(name: "charge_rule_id", type: "bigint",  remarks: "计费规则头ID")  {constraints(nullable:"false")}
            column(name: "tenant_id", type: "bigint",  remarks: "租户ID")  {constraints(nullable:"false")}  
            column(name: "object_version_number", type: "bigint",   defaultValue:"1",   remarks: "行版本号，用来处理锁")  {constraints(nullable:"false")}  
            column(name: "creation_date", type: "datetime",   defaultValueComputed:"CURRENT_TIMESTAMP",   remarks: "")  {constraints(nullable:"false")}  
            column(name: "created_by", type: "bigint",   defaultValue:"-1",   remarks: "")  {constraints(nullable:"false")}  
            column(name: "last_updated_by", type: "bigint",   defaultValue:"-1",   remarks: "")  {constraints(nullable:"false")}  
            column(name: "last_update_date", type: "datetime",   defaultValueComputed:"CURRENT_TIMESTAMP",   remarks: "")  {constraints(nullable:"false")}  
        }

        addUniqueConstraint(columnNames: "group_header_id,charge_rule_id", tableName: "hitf_charge_group_rule", constraintName: "hitf_charge_group_rule_u1")
        createIndex(tableName: "hitf_charge_group_rule", indexName: "hitf_charge_group_rule_n1") {
            column(name: "group_header_id")
        }
    }

    changeSet(author: "aaron.yi", id: "2020-04-23-hitf_charge_group_rule-patch") {
        def weight = 1
        if (helper.isSqlServer()) {
            weight = 2
        } else if (helper.isOracle()) {
            weight = 3
        }
        dropNotNullConstraint(tableName: 'hitf_charge_group_rule', columnName: 'settlement_period', columnDataType: "varchar(" + 30 * weight + ")")
    }

    changeSet(author: "aaron.yi", id: "2020-04-23-hitf_charge_group_rule-modify-start_date-drop") {
        dropColumn(tableName: 'hitf_charge_group_rule') {
            column(name: "start_date", type: "datetime")
        }
    }

    changeSet(author: "aaron.yi", id: "2020-04-23-hitf_charge_group_rule-modify-start_date-add") {
        addColumn(tableName: 'hitf_charge_group_rule') {
            column(name: "start_date", type: "date", remarks: "起始日期")
        }
    }

    changeSet(author: "aaron.yi", id: "2020-04-23-hitf_charge_group_rule-modify-end_date-drop") {
        dropColumn(tableName: 'hitf_charge_group_rule') {
            column(name: "end_date", type: "datetime")
        }
    }

    changeSet(author: "aaron.yi", id: "2020-04-23-hitf_charge_group_rule-modify-end_date-add") {
        addColumn(tableName: 'hitf_charge_group_rule') {
            column(name: "end_date", type: "date", remarks: "截止日期")
        }
    }
}