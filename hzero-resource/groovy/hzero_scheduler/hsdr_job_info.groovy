package script.db

databaseChangeLog(logicalFilePath: 'script/db/hsdr_job_info.groovy') {
    changeSet(author: "shuangfei.zhu@hand-china.com", id: "2019-02-18-hsdr_job_info") {
        def weight = 1
        if(helper.isSqlServer()){
            weight = 2
        } else if(helper.isOracle()){
            weight = 3
        }
        if(helper.dbType().isSupportSequence()){
            createSequence(sequenceName: 'hsdr_job_info_s', startValue:"1")
        }
        createTable(tableName: "hsdr_job_info", remarks: "调度任务") {
            column(name: "job_id", type: "bigint", autoIncrement: true ,   remarks: "表ID，主键，供其他表做外键")  {constraints(primaryKey: true)} 
            column(name: "executor_id", type: "bigint",  remarks: "执行器ID,hsdr_executor.executor_id")  {constraints(nullable:"false")}  
            column(name: "job_code", type: "varchar(" + 30 * weight + ")",  remarks: "任务编码")  {constraints(nullable:"false")}  
            column(name: "job_cron", type: "varchar(" + 60 * weight + ")",  remarks: "任务执行corn")   
            column(name: "job_name", type: "varchar(" + 120 * weight + ")",  remarks: "任务名称")  {constraints(nullable:"false")}  
            column(name: "description", type: "varchar(" + 240 * weight + ")",  remarks: "任务描述")   
            column(name: "job_param", type: "longtext",  remarks: "执行器任务参数")   
            column(name: "alarm_email", type: "varchar(" + 240 * weight + ")",  remarks: "报警邮件")   
            column(name: "executor_strategy", type: "varchar(" + 30 * weight + ")",  remarks: "执行器策略，HSDR.EXECUTOR_STRATEGY")  {constraints(nullable:"false")}  
            column(name: "fail_strategy", type: "varchar(" + 30 * weight + ")",  remarks: "失败处理策略，HSDR.FAIL_STRATEGY")  {constraints(nullable:"false")}  
            column(name: "glue_type", type: "varchar(" + 30 * weight + ")",  remarks: "任务类型，HSDR.GLUE_TYPE")  {constraints(nullable:"false")}  
            column(name: "job_handler", type: "varchar(" + 30 * weight + ")",  remarks: "jobHandler")   
            column(name: "cycle_flag", type: "tinyint",   defaultValue:"0",   remarks: "周期性")  {constraints(nullable:"false")}  
            column(name: "remark", type: "varchar(" + 120 * weight + ")",  remarks: "备注")   
            column(name: "start_date", type: "datetime",  remarks: "有效时间从")   
            column(name: "end_date", type: "datetime",  remarks: "有效时间至")   
            column(name: "tenant_id", type: "bigint",   defaultValue:"0",   remarks: "租户ID,hpfm_tenant.tenant_id")  {constraints(nullable:"false")}  
            column(name: "parent_id", type: "bigint",  remarks: "父任务ID")   
            column(name: "object_version_number", type: "bigint",   defaultValue:"1",   remarks: "行版本号，用来处理锁")  {constraints(nullable:"false")}  
            column(name: "created_by", type: "bigint",   defaultValue:"0",   remarks: "")  {constraints(nullable:"false")}  
            column(name: "creation_date", type: "datetime",   defaultValueComputed:"CURRENT_TIMESTAMP",   remarks: "")  {constraints(nullable:"false")}  
            column(name: "last_updated_by", type: "bigint",   defaultValue:"0",   remarks: "")  {constraints(nullable:"false")}  
            column(name: "last_update_date", type: "datetime",   defaultValueComputed:"CURRENT_TIMESTAMP",   remarks: "")  {constraints(nullable:"false")}  

        }
   createIndex(tableName: "hsdr_job_info", indexName: "hsdr_job_info_n1") {
            column(name: "job_code")
            column(name: "tenant_id")
        }

        addUniqueConstraint(columnNames:"job_name",tableName:"hsdr_job_info",constraintName: "hsdr_job_info_u1")
    }

    changeSet(author: "hzero@hand-china.com", id: "2019-02-27-hsdr_job_info") {
        dropUniqueConstraint(tableName: 'hsdr_job_info', constraintName: 'hsdr_job_info_u1')
        dropColumn(tableName: 'hsdr_job_info', columnName: 'job_name')
        dropColumn(tableName: 'hsdr_job_info', columnName: 'remark')
    }

    changeSet(author: "hzero@hand-china.com", id: "2019-03-12-hsdr_job_info") {
        def weight = 1
        if(helper.isSqlServer()){
            weight = 2
        } else if(helper.isOracle()){
            weight = 3
        }
        addColumn(tableName: 'hsdr_job_info') {
            column(name: "strategy_param", type: "varchar(" + 240 * weight + ")", remarks: "策略参数")
        }
    }

    changeSet(author: "hzero@hand-china.com", id: "2019-05-31-hsdr_job_info") {
        addColumn(tableName: 'hsdr_job_info') {
            column(name: "serial", type: "tinyint",   remarks: "串行标识")
        }
    }

    changeSet(author: "shuangfei.zhu@hand-china.com", id: "2019-08-20-hsdr_job_info") {
        def weight = 1
        if(helper.isSqlServer()){
            weight = 2
        } else if(helper.isOracle()){
            weight = 3
        }
        addColumn(tableName: 'hsdr_job_info') {
            column(name: "expand_param", type: "varchar(" + 480 * weight + ")", remarks: "扩展属性")
        }
        addColumn(tableName: 'hsdr_job_info') {
            column(name: "init_flag", type: "tinyint", defaultValue:"0", remarks: "初始化标识")  {constraints(nullable:"false")}
        }
    }
}