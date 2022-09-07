package script.db

databaseChangeLog(logicalFilePath: 'script/db/hivc_auth_result.groovy') {
    changeSet(author: "hzero@hand-china.com", id: "2020-05-26-hivc_auth_result") {
        def weight = 1
        if(helper.isSqlServer()){
            weight = 2
        } else if(helper.isOracle()){
            weight = 3
        }
        if(helper.dbType().isSupportSequence()){
            createSequence(sequenceName: 'hivc_auth_result_s', startValue:"1")
        }
        createTable(tableName: "hivc_auth_result", remarks: "发票认证结果") {
            column(name: "id", type: "bigint", autoIncrement: true ,   remarks: "表ID，主键")  {constraints(primaryKey: true)} 
            column(name: "buyer_no", type: "varchar(" + 30 * weight + ")",   defaultValue:"",   remarks: "纳税人识别号")  {constraints(nullable:"false")}  
            column(name: "batch_no", type: "varchar(" + 32 * weight + ")",   defaultValue:"",   remarks: "批次号")  {constraints(nullable:"false")}  
            column(name: "auth_state", type: "tinyint",   defaultValue:"0",   remarks: "当前企业实际认证状态")  {constraints(nullable:"false")}  
            column(name: "auth_period", type: "varchar(" + 30 * weight + ")",  remarks: "认证所属期")   
            column(name: "statistics_complete_time", type: "datetime",  remarks: "统计完成时间")   
            column(name: "statistics_result", type: "varchar(" + 480 * weight + ")",  remarks: "统计结果")   
            column(name: "confirm_complete_time", type: "datetime",  remarks: "确认完成时间")   
            column(name: "request_status", type: "int",   defaultValue:"0",   remarks: "请求状态")  {constraints(nullable:"false")}  
            column(name: "result_message", type: "varchar(" + 480 * weight + ")",   defaultValue:"",   remarks: "返回消息说明")  {constraints(nullable:"false")}  
            column(name: "tenant_id", type: "bigint",   defaultValue:"0",   remarks: "租户id")  {constraints(nullable:"false")}  
            column(name: "rewrite_flag", type: "tinyint",   defaultValue:"0",   remarks: "回写标志")  {constraints(nullable:"false")}  
            column(name: "object_version_number", type: "bigint",   defaultValue:"1",   remarks: "行版本号，用来处理锁")  {constraints(nullable:"false")}  
            column(name: "creation_date", type: "datetime",   defaultValueComputed:"CURRENT_TIMESTAMP",   remarks: "")  {constraints(nullable:"false")}  
            column(name: "created_by", type: "bigint",   defaultValue:"-1",   remarks: "")  {constraints(nullable:"false")}  
            column(name: "last_updated_by", type: "bigint",   defaultValue:"-1",   remarks: "")  {constraints(nullable:"false")}  
            column(name: "last_update_date", type: "datetime",   defaultValueComputed:"CURRENT_TIMESTAMP",   remarks: "")  {constraints(nullable:"false")}  

        }
   createIndex(tableName: "hivc_auth_result", indexName: "hivc_auth_result_n1") {
            column(name: "buyer_no")
        }

        addUniqueConstraint(columnNames:"batch_no",tableName:"hivc_auth_result",constraintName: "hivc_auth_result_u1")
    }
}