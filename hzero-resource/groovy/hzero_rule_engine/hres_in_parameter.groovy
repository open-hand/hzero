package script.db

databaseChangeLog(logicalFilePath: 'script/db/hres_in_parameter.groovy') {
    changeSet(author: "hzero@hand-china.com", id: "2020-03-10-hres_in_parameter") {
        def weight = 1
        if(helper.isSqlServer()){
            weight = 2
        } else if(helper.isOracle()){
            weight = 3
        }
        if(helper.dbType().isSupportSequence()){
            createSequence(sequenceName: 'hres_in_parameter_s', startValue:"1")
        }
        createTable(tableName: "hres_in_parameter", remarks: "规则入参") {
            column(name: "IN_PARAMETER_ID", type: "bigint", autoIncrement: true ,   remarks: "表ID，主键，供其他表做外键")  {constraints(primaryKey: true)} 
            column(name: "RULE_CODE", type: "varchar(" + 60 * weight + ")",  remarks: "规则编码，HRES_RULE.RULE_CODE")  {constraints(nullable:"false")}  
            column(name: "FIELD_CODE", type: "varchar(" + 240 * weight + ")",  remarks: "字段编码")  {constraints(nullable:"false")}  
            column(name: "FIELD_NAME", type: "varchar(" + 60 * weight + ")",  remarks: "字段名称")  {constraints(nullable:"false")}  
            column(name: "FIELD_TYPE", type: "varchar(" + 60 * weight + ")",  remarks: "字段类型，值集：HRES.RULE.PARAMETER_TYPE")  {constraints(nullable:"false")}  
            column(name: "MASK_CODE", type: "varchar(" + 240 * weight + ")",  remarks: "格式掩码")   
            column(name: "IS_REQUIRED", type: "varchar(" + 1 * weight + ")",  remarks: "必输标识")  {constraints(nullable:"false")}  
            column(name: "TENANT_ID", type: "bigint",   defaultValue:"0",   remarks: "租户ID，hpfm_tenant.tenant_id")  {constraints(nullable:"false")}  
            column(name: "OBJECT_VERSION_NUMBER", type: "bigint",   defaultValue:"1",   remarks: "行版本号，用来处理锁")  {constraints(nullable:"false")}  
            column(name: "CREATION_DATE", type: "datetime",   defaultValueComputed:"CURRENT_TIMESTAMP",   remarks: "")  {constraints(nullable:"false")}  
            column(name: "CREATED_BY", type: "bigint",   defaultValue:"-1",   remarks: "")  {constraints(nullable:"false")}  
            column(name: "LAST_UPDATE_DATE", type: "datetime",   defaultValueComputed:"CURRENT_TIMESTAMP",   remarks: "")  {constraints(nullable:"false")}  
            column(name: "LAST_UPDATED_BY", type: "bigint",   defaultValue:"-1",   remarks: "")  {constraints(nullable:"false")}  

        }

        addUniqueConstraint(columnNames:"RULE_CODE,FIELD_CODE",tableName:"hres_in_parameter",constraintName: "HRES_IN_PARAMETER_U1")
    }
}