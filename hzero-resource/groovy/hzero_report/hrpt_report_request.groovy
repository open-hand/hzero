package script.db

databaseChangeLog(logicalFilePath: 'script/db/hrpt_report_request.groovy') {
    changeSet(author: "hzero@hand-china.com", id: "2019-02-19-hrpt_report_request") {
        def weight = 1
        if(helper.isSqlServer()){
            weight = 2
        } else if(helper.isOracle()){
            weight = 3
        }
        if(helper.dbType().isSupportSequence()){
            createSequence(sequenceName: 'hrpt_report_request_s', startValue:"1")
        }
        createTable(tableName: "hrpt_report_request", remarks: "报表请求") {
            column(name: "request_id", type: "bigint", autoIncrement: true ,   remarks: "表ID，主键，供其他表做外键")  {constraints(primaryKey: true)} 
            column(name: "report_id", type: "bigint",  remarks: "报表ID,hrpt_report.report_id")  {constraints(nullable:"false")}  
            column(name: "request_param", type: "longtext",  remarks: "请求参数")   
            column(name: "start_date", type: "datetime",  remarks: "开始时间")   
            column(name: "end_date", type: "datetime",  remarks: "结束时间")   
            column(name: "file_url", type: "varchar(" + 480 * weight + ")",  remarks: "报表输出")   
            column(name: "request_status", type: "varchar(" + 1 * weight + ")",  remarks: "运行状态，值集：HRPT.REQUEST_STATUS P:就绪 R:运行中 W:警告 E:错误")  {constraints(nullable:"false")}  
            column(name: "request_message", type: "varchar(" + 1200 * weight + ")",  remarks: "运行消息")   
            column(name: "tenant_id", type: "bigint",   defaultValue:"0",   remarks: "租户ID,hpfm_tenant.tenant_id")  {constraints(nullable:"false")}  
            column(name: "conc_request_id", type: "bigint",  remarks: "并发请求ID，hsdr_conc_request.request_id")   
            column(name: "object_version_number", type: "bigint",   defaultValue:"1",   remarks: "行版本号，用来处理锁")  {constraints(nullable:"false")}  
            column(name: "creation_date", type: "datetime",   defaultValueComputed:"CURRENT_TIMESTAMP",   remarks: "")  {constraints(nullable:"false")}  
            column(name: "created_by", type: "bigint",   defaultValue:"-1",   remarks: "")  {constraints(nullable:"false")}  
            column(name: "last_updated_by", type: "bigint",   defaultValue:"-1",   remarks: "")  {constraints(nullable:"false")}  
            column(name: "last_update_date", type: "datetime",   defaultValueComputed:"CURRENT_TIMESTAMP",   remarks: "")  {constraints(nullable:"false")}  

        }
   createIndex(tableName: "hrpt_report_request", indexName: "hrpt_report_request_n1") {
            column(name: "report_id")
        }

    }
}