package script.db

databaseChangeLog(logicalFilePath: 'script/db/hmsg_wechat_official_tl.groovy') {
    changeSet(author: "hzero@hand-china.com", id: "2020-09-01-hmsg_wechat_official_tl") {
        def weight = 1
        if (helper.isSqlServer()) {
            weight = 2
        } else if (helper.isOracle()) {
            weight = 3
        }
        if (helper.dbType().isSupportSequence()) {
            createSequence(sequenceName: "hmsg_wechat_official_tl_s", startValue: "1")
        }
        createTable(tableName: "hmsg_wechat_official_tl", remarks: "") {
            column(name: "server_id", type: "bigint", remarks: "hmsg_wechat_official表ID") { constraints(nullable: "false") }
            column(name: "lang", type: "varchar(" + 16 * weight + ")", remarks: "语言", defaultValue: "") {
                constraints(nullable: "false")
            }
            column(name: "server_name", type: "varchar(" + 240 * weight + ")", remarks: "服务名称", defaultValue: "") {
                constraints(nullable: "false")
            }
            column(name: "tenant_id", type: "bigint", remarks: "租户ID") { constraints(nullable: "false") }
        }
    }
    changeSet(author: "hzero@hand-china.com", id: "2020-09-01-1-hmsg_wechat_official_tl") {
        addUniqueConstraint(columnNames: "server_id,lang", tableName: "hmsg_wechat_official_tl", constraintName: "hmsg_wechat_official_tl_u1")
    }
}

