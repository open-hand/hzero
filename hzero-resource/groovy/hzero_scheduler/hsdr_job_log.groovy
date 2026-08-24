package script.db

databaseChangeLog(logicalFilePath: 'script/db/hsdr_job_log.groovy') {
    changeSet(author: "shuangfei.zhu@hand-china.com", id: "2019-02-18-hsdr_job_log") {
        def weight = 1
        if(helper.isSqlServer()){
            weight = 2
        } else if(helper.isOracle()){
            weight = 3
        }
        if(helper.dbType().isSupportSequence()){
            createSequence(sequenceName: 'hsdr_job_log_s', startValue:"1")
        }
        createTable(tableName: "hsdr_job_log", remarks: "任务日志") {
            column(name: "log_id", type: "bigint", autoIncrement: true ,   remarks: "表ID，主键，供其他表做外键")  {constraints(primaryKey: true)} 
            column(name: "job_id", type: "bigint",  remarks: "任务ID,hsdr_job_info.job_id")  {constraints(nullable:"false")}  
            column(name: "tenant_id", type: "bigint",   defaultValue:"0",   remarks: "租户ID,hpfm_tenant.tenant_id")  {constraints(nullable:"false")}  
            column(name: "job_result", type: "varchar(" + 30 * weight + ")",  remarks: "任务调度结果")   
            column(name: "client_result", type: "varchar(" + 30 * weight + ")",  remarks: "客户端执行结果")   
            column(name: "executor_id", type: "bigint",  remarks: "执行器ID,hsdr_executor.executor_id")  {constraints(nullable:"false")}  
            column(name: "address", type: "varchar(" + 30 * weight + ")",  remarks: "任务执行地址")   
            column(name: "message_header", type: "varchar(" + 480 * weight + ")",  remarks: "错误信息简略")   
            column(name: "message", type: "longtext",  remarks: "错误信息")   
            column(name: "start_time", type: "datetime",  remarks: "任务开始时间")   
            column(name: "end_time", type: "datetime",  remarks: "任务结束时间")   
            column(name: "log_url", type: "varchar(" + 480 * weight + ")",  remarks: "日志文件url")   
            column(name: "object_version_number", type: "bigint",   defaultValue:"1",   remarks: "行版本号，用来处理锁")  {constraints(nullable:"false")}  
            column(name: "created_by", type: "bigint",   defaultValue:"0",   remarks: "")  {constraints(nullable:"false")}  
            column(name: "creation_date", type: "datetime",   defaultValueComputed:"CURRENT_TIMESTAMP",   remarks: "")  {constraints(nullable:"false")}  
            column(name: "last_updated_by", type: "bigint",   defaultValue:"0",   remarks: "")  {constraints(nullable:"false")}  
            column(name: "last_update_date", type: "datetime",   defaultValueComputed:"CURRENT_TIMESTAMP",   remarks: "")  {constraints(nullable:"false")}  

        }
   createIndex(tableName: "hsdr_job_log", indexName: "hsdr_job_log_n1") {
            column(name: "job_id")
        }
   createIndex(tableName: "hsdr_job_log", indexName: "hsdr_job_log_n2") {
            column(name: "executor_id")
        }

    }

    changeSet(author: "shuangfei.zhu@hand-china.com", id: "2019-05-24-hsdr_job_log") {
        addColumn(tableName: 'hsdr_job_log') {
            column(name: "log_message", type: "longtext", remarks: "日志信息")
        }
    }

    changeSet(author: "shuangfei.zhu@hand-china.com", id: "2020-09-03-hsdr_job_log") {
        def weight = 1
        if(helper.isSqlServer()){
            weight = 2
        } else if(helper.isOracle()){
            weight = 3
        }
        addColumn(tableName: 'hsdr_job_log') {
            column(name: "output_file", type: "varchar(" + 480 * weight + ")", remarks: "输出文件")
        }
    }
}