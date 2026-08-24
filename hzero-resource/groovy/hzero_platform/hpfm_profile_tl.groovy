package script.db

databaseChangeLog(logicalFilePath: 'script/db/hpfm_profile_tl.groovy') {
    changeSet(author: "hzero@hand-china.com", id: "2020-09-02-hpfm_profile_tl") {
        def weight = 1
        if (helper.isSqlServer()) {
            weight = 2
        } else if (helper.isOracle()) {
            weight = 3
        }
        if (helper.dbType().isSupportSequence()) {
            createSequence(sequenceName: "hpfm_profile_tl_s", startValue: "1")
        }
        createTable(tableName: "hpfm_profile_tl", remarks: "") {
            column(name: "profile_id", type: "bigint", remarks: "hpfm_profile表ID") { constraints(nullable: "false") }
            column(name: "lang", type: "varchar(" + 16 * weight + ")", remarks: "语言", defaultValue: "") {
                constraints(nullable: "false")
            }
            column(name: "description", type: "varchar(" + 240 * weight + ")", remarks: "")
            column(name: "tenant_id", type: "bigint", remarks: "租户ID") { constraints(nullable: "false") }
        }
    }
    changeSet(author: "hzero@hand-china.com", id: "2020-09-02-1-hpfm_profile_tl") {
        addUniqueConstraint(columnNames: "profile_id,lang", tableName: "hpfm_profile_tl", constraintName: "hpfm_profile_tl_u1")
    }
}

