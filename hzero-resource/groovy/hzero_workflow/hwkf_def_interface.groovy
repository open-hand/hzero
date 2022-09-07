package script.db

databaseChangeLog(logicalFilePath: 'script/db/hwkf_def_interface.groovy') {
    changeSet(author: "weisen.yang@hand-china.com", id: "2021-01-27-hwkf_def_interface") {
        def weight = 1
        if (helper.isSqlServer()) {
            weight = 2
        } else if (helper.isOracle()) {
            weight = 3
        }
        if (helper.dbType().isSupportSequence()) {
            createSequence(sequenceName: 'hwkf_def_interface_s', startValue: "1")
        }
        createTable(tableName: "hwkf_def_interface", remarks: "工作流接口定义表") {
            column(name: "INTERFACE_ID", type: "bigint", autoIncrement: true, remarks: "表ID，主键，供其他表做外键") { constraints(primaryKey: true) }
            column(name: "INTERFACE_CODE", type: "varchar(" + 40 * weight + ")", remarks: "接口编码") { constraints(nullable: "false") }
            column(name: "INTERFACE_NAME", type: "varchar(" + 240 * weight + ")", remarks: "接口名称") { constraints(nullable: "false") }
            column(name: "SERVICE_NAME", type: "varchar(" + 90 * weight + ")", remarks: "服务名称") { constraints(nullable: "false") }
            column(name: "PERMISSION_CODE", type: "varchar(" + 128 * weight + ")", remarks: "接口URL，iam_permission") { constraints(nullable: "false") }
            column(name: "ENABLED_FLAG", type: "tinyint", defaultValue: "1", remarks: "是否启用") { constraints(nullable: "false") }
            column(name: "METHOD", type: "varchar(" + 10 * weight + ")", remarks: "调用方式 GET/POST/PUT/DELETE") { constraints(nullable: "false") }
            column(name: "TENANT_ID", type: "bigint", remarks: "租户ID") { constraints(nullable: "false") }
            column(name: "OBJECT_VERSION_NUMBER", type: "bigint", defaultValue: "1", remarks: "行版本号，用来处理锁") { constraints(nullable: "false") }
            column(name: "CREATION_DATE", type: "datetime", defaultValueComputed: "CURRENT_TIMESTAMP", remarks: "") { constraints(nullable: "false") }
            column(name: "CREATED_BY", type: "bigint", remarks: "")
            column(name: "LAST_UPDATED_BY", type: "bigint", remarks: "")
            column(name: "LAST_UPDATE_DATE", type: "datetime", defaultValueComputed: "CURRENT_TIMESTAMP", remarks: "") { constraints(nullable: "false") }
            column(name: "PERMISSION_LEVEL", type: "varchar(" + 30 * weight + ")", remarks: "接口层级") { constraints(nullable: "false") }
            column(name: "CONTROLLER", type: "varchar(" + 240 * weight + ")", remarks: "接口swagger-controller（刷新接口参数用）")
            column(name: "OPERATION_ID", type: "varchar(" + 240 * weight + ")", remarks: "接口swagger-operationId（刷新接口参数用）")
        }


        addUniqueConstraint(columnNames: "INTERFACE_CODE,TENANT_ID", tableName: "hwkf_def_interface", constraintName: "hwkf_def_interface_u1")
    }
}