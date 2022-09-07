package script.db

databaseChangeLog(logicalFilePath: 'script/db/hivc_tick_result_line.groovy') {
    changeSet(author: "hzero@hand-china.com", id: "2020-05-26-hivc_tick_result_line") {
        def weight = 1
        if(helper.isSqlServer()){
            weight = 2
        } else if(helper.isOracle()){
            weight = 3
        }
        if(helper.dbType().isSupportSequence()){
            createSequence(sequenceName: 'hivc_tick_result_line_s', startValue:"1")
        }
        createTable(tableName: "hivc_tick_result_line", remarks: "发票勾选结果行") {
            column(name: "id", type: "bigint", autoIncrement: true ,   remarks: "表ID，主键")  {constraints(primaryKey: true)} 
            column(name: "result_id", type: "bigint",  remarks: "hivc_tick_result 主键")  {constraints(nullable:"false")}  
            column(name: "invoice_no", type: "varchar(" + 100 * weight + ")",   defaultValue:"",   remarks: "发票号码")  {constraints(nullable:"false")}  
            column(name: "invoice_code", type: "varchar(" + 100 * weight + ")",   defaultValue:"",   remarks: "发票号码")  {constraints(nullable:"false")}  
            column(name: "invoice_date", type: "date", remarks: "开票日期")  {constraints(nullable:"false")}
            column(name: "tick_flag", type: "tinyint",  remarks: "勾选标志")   
            column(name: "effective_tax", type: "decimal(20,2)",  remarks: "有效税额")
            column(name: "request_status", type: "int",  remarks: "请求状态")   
            column(name: "request_message", type: "varchar(" + 240 * weight + ")",   defaultValue:"",   remarks: "请求结果消息")  {constraints(nullable:"false")}  
            column(name: "rewrite_flag", type: "tinyint",   defaultValue:"0",   remarks: "回写标志")  {constraints(nullable:"false")}  
            column(name: "tenant_id", type: "bigint",   defaultValue:"0",   remarks: "租户id")   
            column(name: "object_version_number", type: "bigint",   defaultValue:"1",   remarks: "行版本号，用来处理锁")  {constraints(nullable:"false")}  
            column(name: "creation_date", type: "datetime",   defaultValueComputed:"CURRENT_TIMESTAMP",   remarks: "")  {constraints(nullable:"false")}  
            column(name: "created_by", type: "bigint",   defaultValue:"-1",   remarks: "")  {constraints(nullable:"false")}  
            column(name: "last_updated_by", type: "bigint",   defaultValue:"-1",   remarks: "")  {constraints(nullable:"false")}  
            column(name: "last_update_date", type: "datetime",   defaultValueComputed:"CURRENT_TIMESTAMP",   remarks: "")  {constraints(nullable:"false")}  

        }

    }
}