package script.db

databaseChangeLog(logicalFilePath: 'script/db/hsdr_conc_executable.groovy') {
    changeSet(author: "shuangfei.zhu@hand-china.com", id: "2019-02-18-hsdr_conc_executable") {
        def weight = 1
        if(helper.isSqlServer()){
            weight = 2
        } else if(helper.isOracle()){
            weight = 3
        }
        if(helper.dbType().isSupportSequence()){
            createSequence(sequenceName: 'hsdr_conc_executable_s', startValue:"1")
        }
        createTable(tableName: "hsdr_conc_executable", remarks: "并发可执行") {
            column(name: "executable_id", type: "bigint", autoIncrement: true ,   remarks: "表ID，主键，供其他表做外键")  {constraints(primaryKey: true)} 
            column(name: "executable_code", type: "varchar(" + 30 * weight + ")",  remarks: "可执行代码")  {constraints(nullable:"false")}  
            column(name: "executable_name", type: "varchar(" + 120 * weight + ")",  remarks: "可执行名称")  {constraints(nullable:"false")}  
            column(name: "executable_desc", type: "varchar(" + 240 * weight + ")",  remarks: "可执行描述")   
            column(name: "executor_id", type: "bigint",  remarks: "执行器ID,hsdr_executor.executor_id")  {constraints(nullable:"false")}  
            column(name: "executor_strategy", type: "varchar(" + 30 * weight + ")",  remarks: "执行器策略，HSDR.EXECUTOR_STRATEGY")  {constraints(nullable:"false")}  
            column(name: "fail_strategy", type: "varchar(" + 30 * weight + ")",  remarks: "失败处理策略，HSDR.FAIL_STRATEGY")  {constraints(nullable:"false")}  
            column(name: "exe_type_code", type: "varchar(" + 30 * weight + ")",  remarks: "可执行类型，HSDR.GLUE_TYPE")  {constraints(nullable:"false")}  
            column(name: "job_handler", type: "varchar(" + 30 * weight + ")",  remarks: "jobHandler")   
            column(name: "enabled_flag", type: "tinyint",   defaultValue:"1",   remarks: "启用标识")  {constraints(nullable:"false")}  
            column(name: "tenant_id", type: "bigint",   defaultValue:"0",   remarks: "租户ID,hpfm_tenant.tenant_id")  {constraints(nullable:"false")}  
            column(name: "object_version_number", type: "bigint",   defaultValue:"1",   remarks: "行版本号，用来处理锁")  {constraints(nullable:"false")}  
            column(name: "creation_date", type: "datetime",   defaultValueComputed:"CURRENT_TIMESTAMP",   remarks: "")  {constraints(nullable:"false")}  
            column(name: "created_by", type: "bigint",   defaultValue:"-1",   remarks: "")  {constraints(nullable:"false")}  
            column(name: "last_updated_by", type: "bigint",   defaultValue:"-1",   remarks: "")  {constraints(nullable:"false")}  
            column(name: "last_update_date", type: "datetime",   defaultValueComputed:"CURRENT_TIMESTAMP",   remarks: "")  {constraints(nullable:"false")}  

        }

        addUniqueConstraint(columnNames:"executable_code,tenant_id",tableName:"hsdr_conc_executable",constraintName: "hsdr_conc_executable_u1")
    }

    changeSet(author: "hzero@hand-china.com", id: "2019-03-12-hsdr_conc_executable") {
        def weight = 1
        if(helper.isSqlServer()){
            weight = 2
        } else if(helper.isOracle()){
            weight = 3
        }
        addColumn(tableName: 'hsdr_conc_executable') {
            column(name: "strategy_param", type: "varchar(" + 240 * weight + ")", remarks: "策略参数")
        }
    }
}