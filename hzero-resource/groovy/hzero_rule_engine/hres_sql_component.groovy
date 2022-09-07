package script.db

databaseChangeLog(logicalFilePath: 'script/db/hres_sql_component.groovy') {
    changeSet(author: "hzero@hand-china.com", id: "2020-03-10-hres_sql_component") {
        def weight = 1
        if(helper.isSqlServer()){
            weight = 2
        } else if(helper.isOracle()){
            weight = 3
        }
        if(helper.dbType().isSupportSequence()){
            createSequence(sequenceName: 'hres_sql_component_s', startValue:"1")
        }
        createTable(tableName: "hres_sql_component", remarks: "SQL组件") {
            column(name: "SQL_COMPONENT_ID", type: "bigint", autoIncrement: true ,   remarks: "表ID，主键，供其他表做外键")  {constraints(primaryKey: true)} 
            column(name: "RULE_CODE", type: "varchar(" + 60 * weight + ")",  remarks: "规则编码，HRES_RULE.RULE_CODE")  {constraints(nullable:"false")}  
            column(name: "SQL_NAME", type: "varchar(" + 60 * weight + ")",  remarks: "SQL名称")  {constraints(nullable:"false")}  
            column(name: "SQL_TEXT", type: "longtext",  remarks: "SQL内容")  {constraints(nullable:"false")}  
            column(name: "DATA_SOURCE_CODE", type: "varchar(" + 60 * weight + ")",  remarks: "数据源编码")  {constraints(nullable:"false")}  
            column(name: "TENANT_ID", type: "bigint",   defaultValue:"0",   remarks: "租户ID，hpfm_tenant.tenant_id")  {constraints(nullable:"false")}  
            column(name: "OBJECT_VERSION_NUMBER", type: "bigint",   defaultValue:"1",   remarks: "行版本号，用来处理锁")  {constraints(nullable:"false")}  
            column(name: "CREATION_DATE", type: "datetime",   defaultValueComputed:"CURRENT_TIMESTAMP",   remarks: "")  {constraints(nullable:"false")}  
            column(name: "CREATED_BY", type: "bigint",   defaultValue:"-1",   remarks: "")  {constraints(nullable:"false")}  
            column(name: "LAST_UPDATE_DATE", type: "datetime",   defaultValueComputed:"CURRENT_TIMESTAMP",   remarks: "")  {constraints(nullable:"false")}  
            column(name: "LAST_UPDATED_BY", type: "bigint",   defaultValue:"-1",   remarks: "")  {constraints(nullable:"false")}  

        }

        addUniqueConstraint(columnNames:"RULE_CODE,SQL_NAME",tableName:"hres_sql_component",constraintName: "HRES_SQL_COMPONENT_U1")
    }
}