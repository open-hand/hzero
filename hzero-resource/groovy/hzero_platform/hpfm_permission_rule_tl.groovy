package script.db

databaseChangeLog(logicalFilePath: 'script/db/hpfm_permission_rule_tl.groovy') {
    changeSet(author: "hzero@hand-china.com", id: "2020-09-02-hpfm_permission_rule_tl") {
        def weight = 1
        if (helper.isSqlServer()) {
            weight = 2
        } else if (helper.isOracle()) {
            weight = 3
        }
        if (helper.dbType().isSupportSequence()) {
            createSequence(sequenceName: "hpfm_permission_rule_tl_s", startValue: "1")
        }
        createTable(tableName: "hpfm_permission_rule_tl", remarks: "") {
            column(name: "rule_id", type: "bigint", remarks: "hpfm_permission_rule表ID") { constraints(nullable: "false") }
            column(name: "lang", type: "varchar(" + 16 * weight + ")", remarks: "语言", defaultValue: "") {
                constraints(nullable: "false")
            }
            column(name: "rule_name", type: "varchar(" + 120 * weight + ")", remarks: "屏蔽规则名称", defaultValue: "") {
                constraints(nullable: "false")
            }
            column(name: "tenant_id", type: "bigint", remarks: "租户ID") { constraints(nullable: "false") }
        }
    }
    changeSet(author: "hzero@hand-china.com", id: "2020-09-02-1-hpfm_permission_rule_tl") {
        addUniqueConstraint(columnNames: "rule_id,lang", tableName: "hpfm_permission_rule_tl", constraintName: "hpfm_permission_rule_tl_u1")
    }
}

