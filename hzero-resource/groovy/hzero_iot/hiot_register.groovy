package script.db

databaseChangeLog(logicalFilePath: 'script/db/hiot_register.groovy') {
    changeSet(author: "hzero@hand-china.com", id: "2020-09-03-hiot_register") {
        def weight = 1
        if (helper.isSqlServer()) {
            weight = 2
        } else if (helper.isOracle()) {
            weight = 3
        }
        if (helper.dbType().isSupportSequence()) {
            createSequence(sequenceName: 'hiot_register_s', startValue: "1")
        }
        createTable(tableName: "hiot_register", remarks: "注册信息") {
            column(name: "REGISTER_ID", type: "bigint", autoIncrement: true, remarks: "表ID，主键") { constraints(primaryKey: true) }
            column(name: "ITEM_TYPE", type: "tinyint", remarks: "项类型, 0-设备模型,1-设备,2-网关") { constraints(nullable: "false") }
            column(name: "ITEM_ID", type: "bigint", remarks: "ITEM_TYPE=0,ITEM_ID=设备模型ID,hiot_thing_model.MODEL_ID;ITEM_TYPE=1,ITEM_ID=设备ID,hiot_thing.THING_ID;ITEM_TYPE=2,ITEM_ID=网关ID,hiot_gateway.GATEWAY_ID") { constraints(nullable: "false") }
            column(name: "PLATFORM", type: "varchar(" + 30 * weight + ")", remarks: "取自快码，HIOT.CLOUD_PLATFORM") { constraints(nullable: "false") }
            column(name: "CLOUD_KEY", type: "varchar(" + 100 * weight + ")", remarks: "注册到云之后云返回的标志，用于与云做映射，如设备模板的schemaId等")
            column(name: "ADDITION", type: "text", remarks: "额外信息，如需要记录额外的信息，用json格式存储，如云平台返回的一些关键报文，无需存储则放空")
            column(name: "GROUP_KEY", type: "varchar(" + 100 * weight + ")", remarks: "权限组的标识,以网关为单位")
            column(name: "GROUP_ADDITION", type: "text", remarks: "权限组额外信息")
            column(name: "TENANT_ID", type: "bigint", remarks: "租户ID") { constraints(nullable: "false") }
            column(name: "OBJECT_VERSION_NUMBER", type: "bigint", defaultValue: "1", remarks: "") { constraints(nullable: "false") }
            column(name: "creation_date", type: "datetime", defaultValueComputed: "CURRENT_TIMESTAMP", remarks: "") { constraints(nullable: "false") }
            column(name: "created_by", type: "bigint", defaultValue: "-1", remarks: "") { constraints(nullable: "false") }
            column(name: "last_updated_by", type: "bigint", defaultValue: "-1", remarks: "") { constraints(nullable: "false") }
            column(name: "last_update_date", type: "datetime", defaultValueComputed: "CURRENT_TIMESTAMP", remarks: "") { constraints(nullable: "false") }
        }


        addUniqueConstraint(columnNames: "ITEM_TYPE,ITEM_ID", tableName: "hiot_register", constraintName: "hiot_register_u1")
    }
}