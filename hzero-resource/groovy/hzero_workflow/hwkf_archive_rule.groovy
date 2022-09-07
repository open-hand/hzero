package script.db

databaseChangeLog(logicalFilePath: 'script/db/hwkf_archive_rule.groovy') {
    changeSet(author: "weisen.yang@hand-china.com", id: "2021-01-27-hwkf_archive_rule") {
        def weight = 1
        if (helper.isSqlServer()) {
            weight = 2
        } else if (helper.isOracle()) {
            weight = 3
        }
        if (helper.dbType().isSupportSequence()) {
            createSequence(sequenceName: 'hwkf_archive_rule_s', startValue: "1")
        }
        createTable(tableName: "hwkf_archive_rule", remarks: "流程归档规则表") {
            column(name: "ARCH_ID", type: "bigint", autoIncrement: true, remarks: "表ID，主键，供其他表做外键") { constraints(primaryKey: true) }
            column(name: "ARCH_CODE", type: "varchar(" + 30 * weight + ")", remarks: "归档规则编码") { constraints(nullable: "false") }
            column(name: "ARCH_NAME", type: "varchar(" + 80 * weight + ")", remarks: "归档规则名称")
            column(name: "TYPE_ID", type: "bigint", remarks: "分类ID")
            column(name: "FLOW_ID", type: "bigint", remarks: "流程定义ID")
            column(name: "RULE_TYPE", type: "varchar(" + 30 * weight + ")", remarks: "规则类型，INTERVAL(过期时间长度)/RANGE(过期时间范围)") { constraints(nullable: "false") }
            column(name: "TIMEOUT_FROM", type: "datetime", remarks: "过期时间从")
            column(name: "TIMEOUT_TO", type: "datetime", remarks: "过期时间至")
            column(name: "TIMEOUT_VALUE", type: "bigint", remarks: "过期时间")
            column(name: "TIMEOUT_UNIT", type: "varchar(" + 30 * weight + ")", remarks: "过期时间单位，YEAR(年)、MONTH(月)、DAY(天)")
            column(name: "ENABLED_FLAG", type: "tinyint", defaultValue: "1", remarks: "是否启用，0 未启用 / 1 启用") { constraints(nullable: "false") }
            column(name: "START_DATE", type: "datetime", remarks: "开始时间") { constraints(nullable: "false") }
            column(name: "END_DATE", type: "datetime", remarks: "结束时间")
            column(name: "TENANT_ID", type: "bigint", remarks: "租户ID") { constraints(nullable: "false") }
            column(name: "OBJECT_VERSION_NUMBER", type: "bigint", defaultValue: "1", remarks: "行版本号，用来处理锁") { constraints(nullable: "false") }
            column(name: "CREATION_DATE", type: "datetime", defaultValueComputed: "CURRENT_TIMESTAMP", remarks: "") { constraints(nullable: "false") }
            column(name: "CREATED_BY", type: "bigint", defaultValue: "-1", remarks: "") { constraints(nullable: "false") }
            column(name: "LAST_UPDATED_BY", type: "bigint", defaultValue: "-1", remarks: "") { constraints(nullable: "false") }
            column(name: "LAST_UPDATE_DATE", type: "datetime", defaultValueComputed: "CURRENT_TIMESTAMP", remarks: "") { constraints(nullable: "false") }
        }


        addUniqueConstraint(columnNames: "ARCH_CODE,TENANT_ID", tableName: "hwkf_archive_rule", constraintName: "hwkf_archive_rule_u1")
    }
}