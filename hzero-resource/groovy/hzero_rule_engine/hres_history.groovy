package script.db

databaseChangeLog(logicalFilePath: 'script/db/hres_history.groovy') {
    changeSet(author: "hzero@hand-china.com", id: "2020-03-10-hres_history") {
        def weight = 1
        if(helper.isSqlServer()){
            weight = 2
        } else if(helper.isOracle()){
            weight = 3
        }
        if(helper.dbType().isSupportSequence()){
            createSequence(sequenceName: 'hres_history_s', startValue:"1")
        }
        createTable(tableName: "hres_history", remarks: "执行历史") {
            column(name: "HISTORY_ID", type: "bigint", autoIncrement: true ,   remarks: "表ID，主键，供其他表做外键")  {constraints(primaryKey: true)} 
            column(name: "RULE_CODE", type: "varchar(" + 60 * weight + ")",  remarks: "规则编码，HRES_RULE.RULE_CODE")  {constraints(nullable:"false")}  
            column(name: "RULE_NAME", type: "varchar(" + 120 * weight + ")",  remarks: "规则名称")   
            column(name: "IN_PARAMETER", type: "longtext",  remarks: "入参JSON")   
            column(name: "OUT_PARAMETER", type: "longtext",  remarks: "出参JSON")   
            column(name: "VARIABLE", type: "longtext",  remarks: "变量JSON")   
            column(name: "STATUS", type: "varchar(" + 30 * weight + ")",  remarks: "状态")   
            column(name: "ERROR_PROCESS_SEQUENCE", type: "bigint",  remarks: "报错流程节点")   
            column(name: "MESSAGE", type: "longtext",  remarks: "执行错误信息")   
            column(name: "START_DATE_TIME", type: "date",  remarks: "执行开始日期")   
            column(name: "END_DATE_TIME", type: "date",  remarks: "执行结束日期")   
            column(name: "UUID", type: "varchar(" + 60 * weight + ")",  remarks: "执行ID")   
            column(name: "TENANT_ID", type: "bigint",   defaultValue:"0",   remarks: "租户ID，hpfm_tenant.tenant_id")  {constraints(nullable:"false")}  
            column(name: "OBJECT_VERSION_NUMBER", type: "bigint",   defaultValue:"1",   remarks: "行版本号，用来处理锁")  {constraints(nullable:"false")}  
            column(name: "CREATION_DATE", type: "datetime",   defaultValueComputed:"CURRENT_TIMESTAMP",   remarks: "")  {constraints(nullable:"false")}  
            column(name: "CREATED_BY", type: "bigint",   defaultValue:"-1",   remarks: "")  {constraints(nullable:"false")}  
            column(name: "LAST_UPDATE_DATE", type: "datetime",   defaultValueComputed:"CURRENT_TIMESTAMP",   remarks: "")  {constraints(nullable:"false")}  
            column(name: "LAST_UPDATED_BY", type: "bigint",   defaultValue:"-1",   remarks: "")  {constraints(nullable:"false")}  

        }

        addUniqueConstraint(columnNames:"UUID",tableName:"hres_history",constraintName: "HRES_HISTORY_U1")
    }
}