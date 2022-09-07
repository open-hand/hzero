package script.db

databaseChangeLog(logicalFilePath: 'script/db/hiot_cloud_config_tl.groovy') {
    changeSet(author: "hzero@hand-china.com", id: "2020-09-03-hiot_cloud_config_tl") {
        def weight = 1
        if (helper.isSqlServer()) {
            weight = 2
        } else if (helper.isOracle()) {
            weight = 3
        }
        createTable(tableName: "hiot_cloud_config_tl", remarks: "云配置信息语言表") {
            column(name: "CONFIG_ID", type: "bigint", remarks: "表ID，主键") { constraints(nullable: "false") }
            column(name: "LANG", type: "varchar(" + 20 * weight + ")", remarks: "语言") { constraints(nullable: "false") }
            column(name: "CONFIG_NAME", type: "varchar(" + 20 * weight + ")", remarks: "配置文件名称，识别配置唯一标志") { constraints(nullable: "false") }
        }


        addUniqueConstraint(columnNames: "CONFIG_ID,LANG", tableName: "hiot_cloud_config_tl", constraintName: "hiot_cloud_config_tl_u1")
    }
}