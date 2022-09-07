package script.db

databaseChangeLog(logicalFilePath: 'script/db/hwkf_def_config.groovy') {
    changeSet(author: "weisen.yang@hand-china.com", id: "2021-01-27-hwkf_def_config") {
        def weight = 1
        if (helper.isSqlServer()) {
            weight = 2
        } else if (helper.isOracle()) {
            weight = 3
        }
        if (helper.dbType().isSupportSequence()) {
            createSequence(sequenceName: 'hwkf_def_config_s', startValue: "1")
        }
        createTable(tableName: "hwkf_def_config", remarks: "工作流全局配置") {
            column(name: "CONFIG_ID", type: "bigint", autoIncrement: true, remarks: "表ID，主键，供其他表做外键") { constraints(primaryKey: true) }
            column(name: "CONFIG_TYPE", type: "varchar(" + 30 * weight + ")", remarks: "配置类型 ACCOUNT/AUTO_APPROVE")
            column(name: "CONFIG_VALUE", type: "longtext", remarks: "配置值") { constraints(nullable: "false") }
            column(name: "TENANT_ID", type: "bigint", remarks: "租户ID") { constraints(nullable: "false") }
            column(name: "OBJECT_VERSION_NUMBER", type: "bigint", defaultValue: "1", remarks: "行版本号，用来处理锁") { constraints(nullable: "false") }
            column(name: "CREATION_DATE", type: "datetime", defaultValueComputed: "CURRENT_TIMESTAMP", remarks: "") { constraints(nullable: "false") }
            column(name: "CREATED_BY", type: "bigint", defaultValue: "-1", remarks: "") { constraints(nullable: "false") }
            column(name: "LAST_UPDATED_BY", type: "bigint", defaultValue: "-1", remarks: "") { constraints(nullable: "false") }
            column(name: "LAST_UPDATE_DATE", type: "datetime", defaultValueComputed: "CURRENT_TIMESTAMP", remarks: "") { constraints(nullable: "false") }
        }


    }
}