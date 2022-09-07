package script.db

databaseChangeLog(logicalFilePath: 'script/db/hwkf_def_event.groovy') {
    changeSet(author: "weisen.yang@hand-china.com", id: "2021-01-27-hwkf_def_event") {
        def weight = 1
        if (helper.isSqlServer()) {
            weight = 2
        } else if (helper.isOracle()) {
            weight = 3
        }
        if (helper.dbType().isSupportSequence()) {
            createSequence(sequenceName: 'hwkf_def_event_s', startValue: "1")
        }
        createTable(tableName: "hwkf_def_event", remarks: "工作流事件") {
            column(name: "EVENT_ID", type: "bigint", autoIncrement: true, remarks: "表ID，主键，供其他表做外键") { constraints(primaryKey: true) }
            column(name: "TYPE_ID", type: "bigint", remarks: "工作流类型ID") { constraints(nullable: "false") }
            column(name: "EVENT_CODE", type: "varchar(" + 30 * weight + ")", remarks: "工作流事件CODE，来源于接口编码") { constraints(nullable: "false") }
            column(name: "EVENT_NAME", type: "varchar(" + 80 * weight + ")", remarks: "工作流事件名称，来源于接口名称") { constraints(nullable: "false") }
            column(name: "INTERFACE_ID", type: "bigint", remarks: "关联接口ID") { constraints(nullable: "false") }
            column(name: "SYNC_FLAG", type: "tinyint", defaultValue: "0", remarks: "0 同步 1 异步") { constraints(nullable: "false") }
            column(name: "ENABLED_FLAG", type: "tinyint", defaultValue: "1", remarks: "是否启用") { constraints(nullable: "false") }
            column(name: "TENANT_ID", type: "bigint", remarks: "租户ID") { constraints(nullable: "false") }
            column(name: "OBJECT_VERSION_NUMBER", type: "bigint", defaultValue: "1", remarks: "行版本号，用来处理锁") { constraints(nullable: "false") }
            column(name: "CREATION_DATE", type: "datetime", defaultValueComputed: "CURRENT_TIMESTAMP", remarks: "") { constraints(nullable: "false") }
            column(name: "CREATED_BY", type: "bigint", remarks: "")
            column(name: "LAST_UPDATED_BY", type: "bigint", remarks: "")
            column(name: "LAST_UPDATE_DATE", type: "datetime", defaultValueComputed: "CURRENT_TIMESTAMP", remarks: "") { constraints(nullable: "false") }
            column(name: "LAST_UPDATE_LOGIN", type: "int", defaultValue: "-1", remarks: "")
            column(name: "EVENT_TYPE", type: "varchar(" + 30 * weight + ")", remarks: "事件类型")
            column(name: "RECORD_SOURCE_TYPE", type: "varchar(" + 30 * weight + ")", defaultValue: "CUSTOMIZE", remarks: "记录来源：PREDEFINED(预定义)、CUSTOMIZE(自定义)")
        }


        addUniqueConstraint(columnNames: "TYPE_ID,EVENT_CODE", tableName: "hwkf_def_event", constraintName: "hwkf_def_event_u1")
    }
}