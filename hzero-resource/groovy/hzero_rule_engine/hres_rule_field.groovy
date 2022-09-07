package script.db

databaseChangeLog(logicalFilePath: 'script/db/hres_rule_field.groovy') {
    changeSet(author: "hzero@hand-china.com", id: "2020-03-10-hres_rule_field") {
        def weight = 1
        if(helper.isSqlServer()){
            weight = 2
        } else if(helper.isOracle()){
            weight = 3
        }
        if(helper.dbType().isSupportSequence()){
            createSequence(sequenceName: 'hres_rule_field_s', startValue:"1")
        }
        createTable(tableName: "hres_rule_field", remarks: "规则组件字段") {
            column(name: "RULE_FIELD_ID", type: "bigint", autoIncrement: true ,   remarks: "表ID，主键，供其他表做外键")  {constraints(primaryKey: true)} 
            column(name: "RULE_CODE", type: "varchar(" + 60 * weight + ")",  remarks: "规则编码，HRES_RULE.RULE_CODE")  {constraints(nullable:"false")}  
            column(name: "RULE_COMPONENT_NAME", type: "varchar(" + 60 * weight + ")",  remarks: "规则组件名称")  {constraints(nullable:"false")}  
            column(name: "FIELD_NAME", type: "varchar(" + 60 * weight + ")",  remarks: "字段名称")  {constraints(nullable:"false")}  
            column(name: "FIELD_TYPE", type: "varchar(" + 60 * weight + ")",  remarks: "字段类型")  {constraints(nullable:"false")}  
            column(name: "EDIT_TYPE", type: "varchar(" + 60 * weight + ")",  remarks: "编辑类型")  {constraints(nullable:"false")}  
            column(name: "IS_REQUIRED", type: "varchar(" + 1 * weight + ")",  remarks: "是否必输")  {constraints(nullable:"false")}  
            column(name: "BUSINESS_MODEL", type: "varchar(" + 60 * weight + ")",  remarks: "业务模型值集视图编码")   
            column(name: "FORMULA", type: "longtext",  remarks: "公式")   
            column(name: "RULE_SCRIPT_CODE", type: "varchar(" + 60 * weight + ")",  remarks: "规则脚本编码")   
            column(name: "VALUE_FIELD", type: "varchar(" + 60 * weight + ")",  remarks: "值集的实际值")   
            column(name: "TENANT_ID", type: "bigint",   defaultValue:"0",   remarks: "租户ID，hpfm_tenant.tenant_id")  {constraints(nullable:"false")}  
            column(name: "OBJECT_VERSION_NUMBER", type: "bigint",   defaultValue:"1",   remarks: "行版本号，用来处理锁")  {constraints(nullable:"false")}  
            column(name: "CREATION_DATE", type: "datetime",   defaultValueComputed:"CURRENT_TIMESTAMP",   remarks: "")  {constraints(nullable:"false")}  
            column(name: "CREATED_BY", type: "bigint",   defaultValue:"-1",   remarks: "")  {constraints(nullable:"false")}  
            column(name: "LAST_UPDATE_DATE", type: "datetime",   defaultValueComputed:"CURRENT_TIMESTAMP",   remarks: "")  {constraints(nullable:"false")}  
            column(name: "LAST_UPDATED_BY", type: "bigint",   defaultValue:"-1",   remarks: "")  {constraints(nullable:"false")}  

        }

        addUniqueConstraint(columnNames:"RULE_CODE,RULE_COMPONENT_NAME,FIELD_NAME",tableName:"hres_rule_field",constraintName: "HRES_RULE_FIELD_U1")
    }
}