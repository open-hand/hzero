package script.db

databaseChangeLog(logicalFilePath: 'script/db/hmnt_audit_data_line.groovy') {
    changeSet(author: "xingxing.wu@hand-china.com", id: "2019-08-17-hmnt_audit_data_line") {
        def weight = 1
        if(helper.isSqlServer()){
            weight = 2
        } else if(helper.isOracle()){
            weight = 3
        }
        if(helper.dbType().isSupportSequence()){
            createSequence(sequenceName: 'hmnt_audit_data_line_s', startValue:"1")
        }
        createTable(tableName: "hmnt_audit_data_line", remarks: "数据审计行") {
            column(name: "audit_data_line_id", type: "bigint", autoIncrement: true ,   remarks: "表ID，主键，供其他表做外键")  {constraints(primaryKey: true)} 
            column(name: "audit_data_id", type: "bigint",  remarks: "数据审计ID")  {constraints(nullable:"false")}  
            column(name: "audit_data_config_line_id", type: "bigint",  remarks: "数据审计配置行ID")  {constraints(nullable:"false")}  
            column(name: "tenant_id", type: "bigint",   defaultValue:"0",   remarks: "租户ID,hpfm_tenant.tenant_id")  {constraints(nullable:"false")}  
            column(name: "lang", type: "varchar(" + 30 * weight + ")",  remarks: "语言")  {constraints(nullable:"false")}  
            column(name: "field_code", type: "varchar(" + 30 * weight + ")",  remarks: "审计字段")  {constraints(nullable:"false")}  
            column(name: "field_value_old", type: "longtext",  remarks: "审计字段原值")   
            column(name: "field_value_new", type: "longtext",  remarks: "审计字段现值")   
            column(name: "remark", type: "longtext",  remarks: "备注说明")   
            column(name: "object_version_number", type: "bigint",   defaultValue:"1",   remarks: "行版本号，用来处理锁")  {constraints(nullable:"false")}  
            column(name: "creation_date", type: "datetime",   defaultValueComputed:"CURRENT_TIMESTAMP",   remarks: "")  {constraints(nullable:"false")}  
            column(name: "created_by", type: "bigint",   defaultValue:"-1",   remarks: "")  {constraints(nullable:"false")}  
            column(name: "last_updated_by", type: "bigint",   defaultValue:"-1",   remarks: "")  {constraints(nullable:"false")}  
            column(name: "last_update_date", type: "datetime",   defaultValueComputed:"CURRENT_TIMESTAMP",   remarks: "")  {constraints(nullable:"false")}  

        }
   createIndex(tableName: "hmnt_audit_data_line", indexName: "hmnt_audit_data_line_n1") {
            column(name: "field_code")
            column(name: "lang")
        }

    }
}