package script.db

databaseChangeLog(logicalFilePath: 'script/db/hiot_property_type_tl.groovy') {
    changeSet(author: "hzero@hand-china.com", id: "2020-09-03-hiot_property_type_tl") {
        def weight = 1
        if (helper.isSqlServer()) {
            weight = 2
        } else if (helper.isOracle()) {
            weight = 3
        }

        createTable(tableName: "hiot_property_type_tl", remarks: "数据点类型多语言表") {
            column(name: "TYPE_ID", type: "bigint", remarks: "表ID，主键") { constraints(nullable: "false") }
            column(name: "LANG", type: "varchar(" + 20 * weight + ")", remarks: "语言") { constraints(nullable: "false") }
            column(name: "TYPE_NAME", type: "varchar(" + 45 * weight + ")", remarks: "名称") { constraints(nullable: "false") }
            column(name: "DESCRIPTION", type: "varchar(" + 255 * weight + ")", remarks: "说明")
        }


        addUniqueConstraint(columnNames: "TYPE_ID,LANG", tableName: "hiot_property_type_tl", constraintName: "hiot_property_type_tl_u1")
    }
}