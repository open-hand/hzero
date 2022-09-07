package script.db

databaseChangeLog(logicalFilePath: 'script/db/hiot_ota_package_tl.groovy') {
    changeSet(author: "hzero@hand-china.com", id: "2020-09-03-hiot_ota_package_tl") {
        def weight = 1
        if (helper.isSqlServer()) {
            weight = 2
        } else if (helper.isOracle()) {
            weight = 3
        }
        createTable(tableName: "hiot_ota_package_tl", remarks: "ota基础信息多语言表") {
            column(name: "PACKAGE_ID", type: "bigint", remarks: "表ID，主键") { constraints(nullable: "false") }
            column(name: "LANG", type: "varchar(" + 20 * weight + ")", remarks: "语言") { constraints(nullable: "false") }
            column(name: "PACKAGE_NAME", type: "varchar(" + 64 * weight + ")", remarks: "任务名称") { constraints(nullable: "false") }
        }


        addUniqueConstraint(columnNames: "PACKAGE_ID,LANG", tableName: "hiot_ota_package_tl", constraintName: "hiot_ota_package_tl_u1")
    }
}