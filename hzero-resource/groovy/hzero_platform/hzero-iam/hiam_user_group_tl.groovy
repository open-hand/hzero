package script.db

databaseChangeLog(logicalFilePath: 'script/db/hiam_user_group_tl.groovy') {
    changeSet(author: "hzero@hand-china.com", id: "2020-09-02-hiam_user_group_tl") {
        def weight = 1
        if (helper.isSqlServer()) {
            weight = 2
        } else if (helper.isOracle()) {
            weight = 3
        }
        if (helper.dbType().isSupportSequence()) {
            createSequence(sequenceName: "hiam_user_group_tl_s", startValue: "1")
        }
        createTable(tableName: "hiam_user_group_tl", remarks: "") {
            column(name: "user_group_id", type: "bigint", remarks: "hiam_user_group表ID") { constraints(nullable: "false") }
            column(name: "lang", type: "varchar(" + 16 * weight + ")", remarks: "语言", defaultValue: "") {
                constraints(nullable: "false")
            }
            column(name: "group_name", type: "varchar(" + 120 * weight + ")", remarks: "用户组名称", defaultValue: "") {
                constraints(nullable: "false")
            }
            column(name: "tenant_id", type: "bigint", remarks: "租户ID") { constraints(nullable: "false") }
        }
    }
    changeSet(author: "hzero@hand-china.com", id: "2020-09-02-1-hiam_user_group_tl") {
        addUniqueConstraint(columnNames: "user_group_id,lang", tableName: "hiam_user_group_tl", constraintName: "hiam_user_group_tl_u1")
    }
}

