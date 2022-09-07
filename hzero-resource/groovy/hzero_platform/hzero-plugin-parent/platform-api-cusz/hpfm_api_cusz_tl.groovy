package script.db

databaseChangeLog(logicalFilePath: 'script/db/hpfm_api_cusz_tl.groovy') {
    changeSet(author: "hzero@hand-china.com", id: "2020-09-02-hpfm_api_cusz_tl") {
        def weight = 1
        if (helper.isSqlServer()) {
            weight = 2
        } else if (helper.isOracle()) {
            weight = 3
        }
        if (helper.dbType().isSupportSequence()) {
            createSequence(sequenceName: "hpfm_api_cusz_tl_s", startValue: "1")
        }
        createTable(tableName: "hpfm_api_cusz_tl", remarks: "") {
            column(name: "customize_id", type: "bigint", remarks: "hpfm_api_cusz表ID") { constraints(nullable: "false") }
            column(name: "lang", type: "varchar(" + 16 * weight + ")", remarks: "语言", defaultValue: "") {
                constraints(nullable: "false")
            }
            column(name: "customize_name", type: "varchar(" + 120 * weight + ")", remarks: "个性化名称", defaultValue: "") {
                constraints(nullable: "false")
            }
            column(name: "tenant_id", type: "bigint", remarks: "租户ID") { constraints(nullable: "false") }
        }
    }
    changeSet(author: "hzero@hand-china.com", id: "2020-09-02-1-hpfm_api_cusz_tl") {
        addUniqueConstraint(columnNames: "customize_id,lang", tableName: "hpfm_api_cusz_tl", constraintName: "hpfm_api_cusz_tl_u1")
    }
}

