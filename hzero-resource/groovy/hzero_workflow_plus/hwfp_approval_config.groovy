package script.db

databaseChangeLog(logicalFilePath: 'script/db/hwfp_approval_config.groovy') {
    changeSet(author: "hzero", id: "2019-06-06-hwfp_approval_config") {
        def weight = 1
        if(helper.isSqlServer()){
            weight = 2
        } else if(helper.isOracle()){
            weight = 3
        }
        if(helper.dbType().isSupportSequence()){
            createSequence(sequenceName: 'hwfp_approval_config_s', startValue:"1")
        }
        createTable(tableName: "hwfp_approval_config", remarks: "审批配置") {
            column(name: "delegate_id", type: "bigint", autoIncrement: true ,   remarks: "表ID，主键，供其他表做外键")  {constraints(primaryKey: true)} 
            column(name: "employee_code", type: "varchar(" + 60 * weight + ")",  remarks: "配置人code")   
            column(name: "delegate_code", type: "varchar(" + 60 * weight + ")",  remarks: "转交给")   
            column(name: "delegate_start_date", type: "datetime",  remarks: "转交开始时间")   
            column(name: "delegate_end_date", type: "datetime",  remarks: "转交截止时间")   
            column(name: "auto_approval", type: "varchar(" + 10 * weight + ")",  remarks: "是否自动审批")   
            column(name: "approval_start_date", type: "datetime",  remarks: "自动审批开始时间")   
            column(name: "approval_end_date", type: "datetime",  remarks: "自动审批截止时间")   
            column(name: "auto_approval_limit", type: "varchar(" + 200 * weight + ")",  remarks: "自动审批时限")   
            column(name: "tenant_id", type: "bigint",   defaultValue:"0",   remarks: "租户ID")  {constraints(nullable:"false")}  
            column(name: "object_version_number", type: "bigint",   defaultValue:"1",   remarks: "行版本号，用来处理锁")  {constraints(nullable:"false")}  
            column(name: "creation_date", type: "datetime",   defaultValueComputed:"CURRENT_TIMESTAMP",   remarks: "")  {constraints(nullable:"false")}  
            column(name: "created_by", type: "bigint",   defaultValue:"-1",   remarks: "")  {constraints(nullable:"false")}  
            column(name: "last_updated_by", type: "bigint",   defaultValue:"-1",   remarks: "")  {constraints(nullable:"false")}  
            column(name: "last_update_date", type: "datetime",   defaultValueComputed:"CURRENT_TIMESTAMP",   remarks: "")  {constraints(nullable:"false")}  

        }

        addUniqueConstraint(columnNames:"employee_code,tenant_id",tableName:"hwfp_approval_config",constraintName: "hwfp_approval_config_u1")
    }
}