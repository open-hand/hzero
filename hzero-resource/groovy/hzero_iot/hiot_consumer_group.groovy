package script.db

databaseChangeLog(logicalFilePath: 'script/db/hiot_consumer_group.groovy') {
    changeSet(author: "hzero@hand-china.com", id: "2020-09-03-hiot_consumer_group") {
        def weight = 1
        if (helper.isSqlServer()) {
            weight = 2
        } else if (helper.isOracle()) {
            weight = 3
        }
        if (helper.dbType().isSupportSequence()) {
            createSequence(sequenceName: 'hiot_consumer_group_s', startValue: "1")
        }
        createTable(tableName: "hiot_consumer_group", remarks: "消费者组表") {
            column(name: "CONSUMER_GROUP_ID", type: "bigint", autoIncrement: true, remarks: "表ID，主键") { constraints(primaryKey: true) }
            column(name: "CONFIG_ID", type: "bigint", remarks: "云账号配置ID")
            column(name: "CODE", type: "varchar(" + 100 * weight + ")", remarks: "消费者编码") { constraints(nullable: "false") }
            column(name: "NAME", type: "varchar(" + 100 * weight + ")", remarks: "消费者名称，来源云平台，云平台没有，自己定义") { constraints(nullable: "false") }
            column(name: "GUID", type: "varchar(" + 100 * weight + ")", remarks: "云平台上的消费者ID") { constraints(nullable: "false") }
            column(name: "DESCRIPTION", type: "varchar(" + 255 * weight + ")", remarks: "说明")
            column(name: "ENABLE_LISTENER_FLAG", type: "tinyint", defaultValue: "0", remarks: "是否启用了监听(0:未启用，1：启用)") { constraints(nullable: "false") }
            column(name: "TENANT_ID", type: "bigint", defaultValue: "0", remarks: "租户ID") { constraints(nullable: "false") }
            column(name: "OBJECT_VERSION_NUMBER", type: "bigint", defaultValue: "1", remarks: "行版本号，用来处理锁") { constraints(nullable: "false") }
            column(name: "creation_date", type: "datetime", defaultValueComputed: "CURRENT_TIMESTAMP", remarks: "") { constraints(nullable: "false") }
            column(name: "created_by", type: "bigint", defaultValue: "-1", remarks: "") { constraints(nullable: "false") }
            column(name: "last_updated_by", type: "bigint", defaultValue: "-1", remarks: "") { constraints(nullable: "false") }
            column(name: "last_update_date", type: "datetime", defaultValueComputed: "CURRENT_TIMESTAMP", remarks: "") { constraints(nullable: "false") }
        }


        addUniqueConstraint(columnNames: "GUID,TENANT_ID", tableName: "hiot_consumer_group", constraintName: "hiot_guid_u1")
    }
}