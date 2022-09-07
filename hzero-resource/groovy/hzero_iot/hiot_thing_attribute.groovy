package script.db

databaseChangeLog(logicalFilePath: 'script/db/hiot_thing_attribute.groovy') {
    changeSet(author: "hzero@hand-china.com", id: "2020-09-03-hiot_thing_attribute") {
        def weight = 1
        if (helper.isSqlServer()) {
            weight = 2
        } else if (helper.isOracle()) {
            weight = 3
        }
        if (helper.dbType().isSupportSequence()) {
            createSequence(sequenceName: 'hiot_thing_attribute_s', startValue: "1")
        }
        createTable(tableName: "hiot_thing_attribute", remarks: "设备属性表") {
            column(name: "thing_attribute_id", type: "bigint", autoIncrement: true, remarks: "表ID，主键") { constraints(primaryKey: true) }
            column(name: "thing_id", type: "bigint", remarks: "设备id") { constraints(nullable: "false") }
            column(name: "equipment", type: "varchar(" + 100 * weight + ")", remarks: "设备型号")
            column(name: "manufacturer", type: "varchar(" + 100 * weight + ")", remarks: "厂家名称")
            column(name: "buying_time", type: "datetime", remarks: "购买时间")
            column(name: "longitude", type: "decimal(10,7)", remarks: "经度")
            column(name: "latitude", type: "decimal(10,7)", remarks: "纬度")
            column(name: "addition_info", type: "longtext", remarks: "额外信息")
            column(name: "tenant_id", type: "bigint", remarks: "租户id") { constraints(nullable: "false") }
            column(name: "object_version_number", type: "bigint", defaultValue: "1", remarks: "行版本号，用来处理锁") { constraints(nullable: "false") }
            column(name: "creation_date", type: "datetime", defaultValueComputed: "CURRENT_TIMESTAMP", remarks: "创建时间")
            column(name: "created_by", type: "bigint", remarks: "创建人")
            column(name: "last_updated_by", type: "bigint", remarks: "最后更新人")
            column(name: "last_update_date", type: "datetime", defaultValueComputed: "CURRENT_TIMESTAMP", remarks: "最后一次更新时间")
        }


        addUniqueConstraint(columnNames: "tenant_id,thing_id", tableName: "hiot_thing_attribute", constraintName: "hiot_thing_attribute_u1")
    }
}