package script.db

databaseChangeLog(logicalFilePath: 'script/db/hsdr_conc_request.groovy') {
    changeSet(author: "shuangfei.zhu@hand-china.com", id: "2019-02-18-hsdr_conc_request") {
        def weight = 1
        if(helper.isSqlServer()){
            weight = 2
        } else if(helper.isOracle()){
            weight = 3
        }
        if(helper.dbType().isSupportSequence()){
            createSequence(sequenceName: 'hsdr_conc_request_s', startValue:"1")
        }
        createTable(tableName: "hsdr_conc_request", remarks: "并发请求") {
            column(name: "request_id", type: "bigint", autoIncrement: true ,   remarks: "表ID，主键，供其他表做外键")  {constraints(primaryKey: true)} 
            column(name: "concurrent_id", type: "bigint",  remarks: "并发程序ID,hsdr_concurrent.concurrent_id")  {constraints(nullable:"false")}  
            column(name: "executable_id", type: "bigint",  remarks: "并发可执行ID,hsdr_conc_executable.executable_id")  {constraints(nullable:"false")}  
            column(name: "cycle_flag", type: "tinyint",   defaultValue:"0",   remarks: "周期性")  {constraints(nullable:"false")}  
            column(name: "interval_type", type: "varchar(" + 30 * weight + ")",  remarks: "间隔类型，HSDR.REQUEST.INTERVAL_TYPE(天/时/分/秒)")   
            column(name: "interval_number", type: "int",  remarks: "间隔大小")   
            column(name: "interval_hour", type: "int",  remarks: "固定间隔-时")   
            column(name: "interval_minute", type: "int",  remarks: "固定间隔-分")   
            column(name: "interval_second", type: "int",  remarks: "固定间隔-秒")   
            column(name: "cron", type: "varchar(" + 120 * weight + ")",  remarks: "CRON表达式")   
            column(name: "request_param", type: "longtext",  remarks: "请求参数")   
            column(name: "start_date", type: "datetime",  remarks: "开始时间")   
            column(name: "end_date", type: "datetime",  remarks: "结束时间/下次运行时间")   
            column(name: "executor_id", type: "bigint",  remarks: "执行器ID,hsdr_executor.executor_id")  {constraints(nullable:"false")}  
            column(name: "job_id", type: "bigint",  remarks: "任务ID，hsdr_job_info.job_id")   
            column(name: "tenant_id", type: "bigint",   defaultValue:"0",   remarks: "租户ID,hpfm_tenant.tenant_id")  {constraints(nullable:"false")}  
            column(name: "object_version_number", type: "bigint",   defaultValue:"1",   remarks: "行版本号，用来处理锁")  {constraints(nullable:"false")}  
            column(name: "creation_date", type: "datetime",   defaultValueComputed:"CURRENT_TIMESTAMP",   remarks: "")  {constraints(nullable:"false")}  
            column(name: "created_by", type: "bigint",   defaultValue:"-1",   remarks: "")  {constraints(nullable:"false")}  
            column(name: "last_updated_by", type: "bigint",   defaultValue:"-1",   remarks: "")  {constraints(nullable:"false")}  
            column(name: "last_update_date", type: "datetime",   defaultValueComputed:"CURRENT_TIMESTAMP",   remarks: "")  {constraints(nullable:"false")}  

        }
   createIndex(tableName: "hsdr_conc_request", indexName: "hsdr_conc_request_n1") {
            column(name: "concurrent_id")
            column(name: "tenant_id")
        }
   createIndex(tableName: "hsdr_conc_request", indexName: "hsdr_conc_request_n2") {
            column(name: "job_id")
        }

    }
}