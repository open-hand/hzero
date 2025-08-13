package script.db

databaseChangeLog(logicalFilePath: 'script/db/hpfm_rule_script_tl.groovy') {
    changeSet(author: "hzero@hand-china.com", id: "2020-09-02-hpfm_rule_script_tl") {
        def weight = 1
        if (helper.isSqlServer()) {
            weight = 2
        } else if (helper.isOracle()) {
            weight = 3
        }
        if (helper.dbType().isSupportSequence()) {
            createSequence(sequenceName: "hpfm_rule_script_tl_s", startValue: "1")
        }
        createTable(tableName: "hpfm_rule_script_tl", remarks: "") {
            column(name: "rule_script_id", type: "bigint", remarks: "hpfm_rule_script表ID") {
                constraints(nullable: "false")
            }
            column(name: "lang", type: "varchar(" + 16 * weight + ")", remarks: "语言", defaultValue: "") {
                constraints(nullable: "false")
            }
            column(name: "script_description", type: "varchar(" + 240 * weight + ")", remarks: "规则描述", defaultValue: "") {
                constraints(nullable: "false")
            }
            column(name: "tenant_id", type: "bigint", remarks: "租户ID") { constraints(nullable: "false") }
        }
    }
    changeSet(author: "hzero@hand-china.com", id: "2020-09-02-1-hpfm_rule_script_tl") {
        addUniqueConstraint(columnNames: "rule_script_id,lang", tableName: "hpfm_rule_script_tl", constraintName: "hpfm_rule_script_tl_u1")
    }
}

