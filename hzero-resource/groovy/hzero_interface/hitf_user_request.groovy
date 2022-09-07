package script.db

databaseChangeLog(logicalFilePath: 'script/db/hitf_user_request.groovy') {
    changeSet(author: "minghui.qiu@hand-china.com ", id: "2020-02-14-hitf_user_request") {
        def weight = 1
        if (helper.isSqlServer()) {
            weight = 2
        } else if (helper.isOracle()) {
            weight = 3
        }
        if (helper.dbType().isSupportSequence()) {
            createSequence(sequenceName: 'hitf_user_request_s', startValue: "1")
        }
        createTable(tableName: "hitf_user_request", remarks: "用户请求信息") {
            column(name: "user_request_id", type: "bigint", autoIncrement: true ,   remarks: "表ID，主键")  {constraints(primaryKey: true)}
			column(name: "request_user_id", type: "bigint",  remarks: "请求用户ID")  {constraints(nullable:"false")}	
			column(name: "request_user_name", type: "varchar(" + 240 * weight + ")",  remarks: "请求用户名称")   {constraints(nullable:"false")}
			column(name: "request_tenant_id", type: "bigint",  remarks: "请求租户ID")  {constraints(nullable:"false")}
			column(name: "request_tenant_name", type: "varchar(" + 240 * weight + ")",  remarks: "请求租户名称")   {constraints(nullable:"false")} 
			column(name: "time_start", type: "datetime",   remarks: "开始日期")  		
			column(name: "request_count", type: "bigint",  remarks: "请求累计次数")  {constraints(nullable:"false")}
            column(name: "status_code", type: "varchar(" + 20 * weight + ")", defaultValue:"NEW", remarks: "状态")   {constraints(nullable:"false")}
            column(name: "object_version_number", type: "bigint",   defaultValue:"1",   remarks: "行版本号，用来处理锁")  {constraints(nullable:"false")}  
            column(name: "creation_date", type: "datetime",   defaultValueComputed:"CURRENT_TIMESTAMP",   remarks: "")  {constraints(nullable:"false")}  
            column(name: "created_by", type: "bigint",   defaultValue:"-1",   remarks: "")  {constraints(nullable:"false")}  
            column(name: "last_updated_by", type: "bigint",   defaultValue:"-1",   remarks: "")  {constraints(nullable:"false")}  
            column(name: "last_update_date", type: "datetime",   defaultValueComputed:"CURRENT_TIMESTAMP",   remarks: "")  {constraints(nullable:"false")}  
        }
    }

    changeSet(author: "aaron.yi", id: "2020-04-23-hitf_user_request-patch") {
        def weight = 1
        if (helper.isSqlServer()) {
            weight = 2
        } else if (helper.isOracle()) {
            weight = 3
        }

        dropColumn(tableName: "hitf_user_request", columnName: "request_user_id")
        dropColumn(tableName: "hitf_user_request", columnName: "request_user_name")
        dropColumn(tableName: "hitf_user_request", columnName: "request_tenant_id")
        dropColumn(tableName: "hitf_user_request", columnName: "request_tenant_name")
        dropColumn(tableName: "hitf_user_request", columnName: "time_start")
        dropColumn(tableName: "hitf_user_request", columnName: "request_count")

        addColumn(tableName: "hitf_user_request") {
            column(name: "server_available_id", type: "bigint", remarks: "表hitf_server_available主键") { constraints(nullable: "false") }
        }
        addColumn(tableName: "hitf_user_request") {
            column(name: "start_time", type: "datetime", remarks: "起始时间") { constraints(nullable: "false") }
        }
        addColumn(tableName: "hitf_user_request") {
            column(name: "end_time", type: "datetime", remarks: "截止时间")
        }
        addColumn(tableName: "hitf_user_request") {
            column(name: "request_count", type: "bigint", remarks: "已请求总量")
        }
        addColumn(tableName: "hitf_user_request") {
            column(name: "payment_time", type: "datetime", remarks: "结算时间")
        }
        addColumn(tableName: "hitf_user_request") {
            column(name: "tenant_id", type: "bigint", remarks: "租户ID") { constraints(nullable: "false") }
        }
        addColumn(tableName: "hitf_user_request") {
            column(name: "remark", type: "varchar(" + 360 * weight + ")", remarks: "备注")
        }

        createIndex(tableName: "hitf_user_request", indexName: "hitf_user_request_n1") {
            column(name: "server_available_id")
        }
    }

    changeSet(author: "aaron.yi", id: "2020-04-23-hitf_user_request-modify-status-drop") {
        def weight = 1
        if (helper.isSqlServer()) {
            weight = 2
        } else if (helper.isOracle()) {
            weight = 3
        }
        dropColumn(tableName: 'hitf_user_request') {
            column(name: "status_code", type: "varchar(" + 240 * weight + ")")
        }
    }

    changeSet(author: "aaron.yi", id: "2020-04-23-hitf_user_request-modify-status-add") {
        addColumn(tableName: 'hitf_user_request') {
            column(name: "status_code", type: "varchar(" + 30 + ")", remarks: "状态") { constraints(nullable: "false") }
        }
    }
}