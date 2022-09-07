package script.db

databaseChangeLog(logicalFilePath: 'script/db/hiot_thing_group_tl.groovy') {
    changeSet(author: "hzero@hand-china.com", id: "2020-09-03-hiot_thing_group_tl") {
        def weight = 1
        if (helper.isSqlServer()) {
            weight = 2
        } else if (helper.isOracle()) {
            weight = 3
        }

        createTable(tableName: "hiot_thing_group_tl", remarks: "设备分组信息多语言表") {
            column(name: "THING_GROUP_ID", type: "bigint", remarks: "表ID，主键") { constraints(nullable: "false") }
            column(name: "LANG", type: "varchar(" + 20 * weight + ")", remarks: "语言") { constraints(nullable: "false") }
            column(name: "NAME", type: "varchar(" + 60 * weight + ")", remarks: "名称") { constraints(nullable: "false") }
            column(name: "DESCRIPTION", type: "varchar(" + 255 * weight + ")", remarks: "说明")
        }


        addUniqueConstraint(columnNames: "THING_GROUP_ID,LANG", tableName: "hiot_thing_group_tl", constraintName: "hiot_project_tl_u1")
    }
}