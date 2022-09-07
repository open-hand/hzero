package script.db

databaseChangeLog(logicalFilePath: 'script/db/hwkf_def_automatic_process.groovy') {
    changeSet(author: "weisen.yang@hand-china.com", id: "2021-01-27-hwkf_def_automatic_process") {
        def weight = 1
        if (helper.isSqlServer()) {
            weight = 2
        } else if (helper.isOracle()) {
            weight = 3
        }
        if (helper.dbType().isSupportSequence()) {
            createSequence(sequenceName: 'hwkf_def_automatic_process_s', startValue: "1")
        }
        createTable(tableName: "hwkf_def_automatic_process", remarks: "工作流自动处理表") {
            column(name: "AUTOMATIC_ID", type: "bigint", autoIncrement: true, remarks: "表ID，主键，供其他表做外键") { constraints(primaryKey: true) }
            column(name: "FLOW_CODE", type: "varchar(" + 255 * weight + ")", remarks: "HWKF_DEF_WORKFLOW表code") { constraints(nullable: "false") }
            column(name: "PROCESS_CONDITION", type: "varchar(" + 30 * weight + ")", remarks: "处理条件:FIXED_PERIOD(固定期间)、TIME_OUT(超时时间)") { constraints(nullable: "false") }
            column(name: "PROCESS_RULE", type: "varchar(" + 30 * weight + ")", remarks: "处理动作：AUTO_DELEGATE(自动转交)、AUTO_APPROVE(自动同意)") { constraints(nullable: "false") }
            column(name: "PROCESS_START_DATE", type: "datetime", remarks: "开始日期")
            column(name: "PROCESS_END_DATE", type: "datetime", remarks: "结束日期")
            column(name: "PROCESS_OWNER", type: "varchar(" + 30 * weight + ")", remarks: "当前用户员工编码/用户Id") { constraints(nullable: "false") }
            column(name: "USER_ID", type: "bigint", remarks: "当前用户Id") { constraints(nullable: "false") }
            column(name: "DELEGATE_NUM", type: "varchar(" + 30 * weight + ")", remarks: "转交的员工编码/用户Id")
            column(name: "TIMEOUT_VALUE", type: "bigint", remarks: "超时时间")
            column(name: "TIMEOUT_UNIT", type: "varchar(" + 30 * weight + ")", remarks: "超时单位：HOUR(小时)、DAY(天)、WEEK(周)")
            column(name: "PROCESS_REMARK", type: "varchar(" + 1000 * weight + ")", remarks: "处理意见")
            column(name: "ENABLED_FLAG", type: "tinyint", remarks: "禁用启用标示") { constraints(nullable: "false") }
            column(name: "TENANT_ID", type: "bigint", remarks: "租户ID") { constraints(nullable: "false") }
            column(name: "OBJECT_VERSION_NUMBER", type: "bigint", defaultValue: "1", remarks: "行版本号，用来处理锁") { constraints(nullable: "false") }
            column(name: "CREATION_DATE", type: "datetime", defaultValueComputed: "CURRENT_TIMESTAMP", remarks: "") { constraints(nullable: "false") }
            column(name: "CREATED_BY", type: "bigint", remarks: "")
            column(name: "LAST_UPDATED_BY", type: "bigint", remarks: "")
            column(name: "LAST_UPDATE_DATE", type: "datetime", defaultValueComputed: "CURRENT_TIMESTAMP", remarks: "") { constraints(nullable: "false") }
        }

        createIndex(tableName: "hwkf_def_automatic_process", indexName: "HWKF_DEF_AUTOMATIC_PROCESS_N1") {
            column(name: "FLOW_CODE")
        }

        addUniqueConstraint(columnNames: "FLOW_CODE,USER_ID,TENANT_ID", tableName: "hwkf_def_automatic_process", constraintName: "hwkf_def_automatic_process_u1")
    }
}