package script.db

databaseChangeLog(logicalFilePath: 'script/db/hres_rule.groovy') {
    changeSet(author: "hzero@hand-china.com", id: "2020-03-10-hres_rule") {
        def weight = 1
        if(helper.isSqlServer()){
            weight = 2
        } else if(helper.isOracle()){
            weight = 3
        }
        if(helper.dbType().isSupportSequence()){
            createSequence(sequenceName: 'hres_rule_s', startValue:"1")
        }
        createTable(tableName: "hres_rule", remarks: "规则") {
            column(name: "RULE_ID", type: "bigint", autoIncrement: true ,   remarks: "表ID，主键，供其他表做外键")  {constraints(primaryKey: true)} 
            column(name: "RULE_CODE", type: "varchar(" + 60 * weight + ")",  remarks: "规则编码")  {constraints(nullable:"false")}  
            column(name: "RULE_NAME", type: "varchar(" + 60 * weight + ")",  remarks: "规则名称")  {constraints(nullable:"false")}  
            column(name: "ENABLE_FLAG", type: "varchar(" + 1 * weight + ")",  remarks: "启用标识")  {constraints(nullable:"false")}  
            column(name: "FROZEN_FLAG", type: "varchar(" + 1 * weight + ")",  remarks: "冻结标识")  {constraints(nullable:"false")}  
            column(name: "TENANT_ID", type: "bigint",   defaultValue:"0",   remarks: "租户ID，hpfm_tenant.tenant_id")  {constraints(nullable:"false")}  
            column(name: "OBJECT_VERSION_NUMBER", type: "bigint",   defaultValue:"1",   remarks: "行版本号，用来处理锁")  {constraints(nullable:"false")}  
            column(name: "CREATION_DATE", type: "datetime",   defaultValueComputed:"CURRENT_TIMESTAMP",   remarks: "")  {constraints(nullable:"false")}  
            column(name: "CREATED_BY", type: "bigint",   defaultValue:"-1",   remarks: "")  {constraints(nullable:"false")}  
            column(name: "LAST_UPDATE_DATE", type: "datetime",   defaultValueComputed:"CURRENT_TIMESTAMP",   remarks: "")  {constraints(nullable:"false")}  
            column(name: "LAST_UPDATED_BY", type: "bigint",   defaultValue:"-1",   remarks: "")  {constraints(nullable:"false")}  

        }

        addUniqueConstraint(columnNames:"RULE_CODE",tableName:"hres_rule",constraintName: "HRES_RULE_U1")
    }
}