package script.db

databaseChangeLog(logicalFilePath: 'script/db/hiot_gateway_tl.groovy') {
    changeSet(author: "hzero@hand-china.com", id: "2020-09-03-hiot_gateway_tl") {
        def weight = 1
        if (helper.isSqlServer()) {
            weight = 2
        } else if (helper.isOracle()) {
            weight = 3
        }
        createTable(tableName: "hiot_gateway_tl", remarks: "网关语言表") {
            column(name: "GATEWAY_ID", type: "bigint", remarks: "表ID，主键") { constraints(nullable: "false") }
            column(name: "LANG", type: "varchar(" + 20 * weight + ")", remarks: "语言") { constraints(nullable: "false") }
            column(name: "GATEWAY_NAME", type: "varchar(" + 45 * weight + ")", remarks: "名称") { constraints(nullable: "false") }
            column(name: "DESCRIPTION", type: "varchar(" + 100 * weight + ")", remarks: "说明")
        }


        addUniqueConstraint(columnNames: "GATEWAY_ID,LANG", tableName: "hiot_gateway_tl", constraintName: "hiot_gateway_tl_u1")
    }
}