package script.db

databaseChangeLog(logicalFilePath: 'script/db/hres_variable.groovy') {
    changeSet(author: "hzero@hand-china.com", id: "2020-03-10-hres_variable") {
        def weight = 1
        if(helper.isSqlServer()){
            weight = 2
        } else if(helper.isOracle()){
            weight = 3
        }
        if(helper.dbType().isSupportSequence()){
            createSequence(sequenceName: 'hres_variable_s', startValue:"1")
        }
        createTable(tableName: "hres_variable", remarks: "规则变量") {
            column(name: "VARIABLE_ID", type: "bigint", autoIncrement: true ,   remarks: "表ID，主键，供其他表做外键")  {constraints(primaryKey: true)} 
            column(name: "RULE_CODE", type: "varchar(" + 60 * weight + ")",  remarks: "规则编码，HRES_RULE.RULE_CODE")  {constraints(nullable:"false")}  
            column(name: "FULL_NAME", type: "varchar(" + 120 * weight + ")",  remarks: "变量全名")  {constraints(nullable:"false")}  
            column(name: "NAME", type: "varchar(" + 60 * weight + ")",  remarks: "变量名称")  {constraints(nullable:"false")}  
            column(name: "FIELD_TYPE", type: "varchar(" + 60 * weight + ")",  remarks: "字段类型，值集：HRES.RULE.PARAMETER_TYPE")  {constraints(nullable:"false")}  
            column(name: "COMPONENT_TYPE", type: "varchar(" + 60 * weight + ")",  remarks: "组件类型")  {constraints(nullable:"false")}  
            column(name: "COMPONENT", type: "varchar(" + 30 * weight + ")",  remarks: "来源组件/组件名")   
            column(name: "TENANT_ID", type: "bigint",   defaultValue:"0",   remarks: "")  {constraints(nullable:"false")}  
            column(name: "OBJECT_VERSION_NUMBER", type: "bigint",   defaultValue:"1",   remarks: "行版本号，用来处理锁")  {constraints(nullable:"false")}  
            column(name: "CREATION_DATE", type: "datetime",   defaultValueComputed:"CURRENT_TIMESTAMP",   remarks: "")  {constraints(nullable:"false")}  
            column(name: "CREATED_BY", type: "bigint",   defaultValue:"-1",   remarks: "")  {constraints(nullable:"false")}  
            column(name: "LAST_UPDATE_DATE", type: "datetime",   defaultValueComputed:"CURRENT_TIMESTAMP",   remarks: "")  {constraints(nullable:"false")}  
            column(name: "LAST_UPDATED_BY", type: "bigint",   defaultValue:"-1",   remarks: "")  {constraints(nullable:"false")}  

        }

        addUniqueConstraint(columnNames:"RULE_CODE,FULL_NAME",tableName:"hres_variable",constraintName: "HRES_VARIABLE_U1")
    }
}