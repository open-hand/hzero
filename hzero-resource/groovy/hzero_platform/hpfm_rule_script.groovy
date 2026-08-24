package script.db

databaseChangeLog(logicalFilePath: 'script/db/hpfm_rule_script.groovy') {
    changeSet(author: "hzero@hand-china.com", id: "2019-03-01-hpfm_rule_script") {
        def weight = 1
        if(helper.isSqlServer()){
            weight = 2
        } else if(helper.isOracle()){
            weight = 3
        }
        if(helper.dbType().isSupportSequence()){
            createSequence(sequenceName: 'hpfm_rule_script_s', startValue:"1")
        }
        createTable(tableName: "hpfm_rule_script", remarks: "") {
            column(name: "rule_script_id", type: "bigint", autoIncrement: true ,   remarks: "表ID，主键，供其他表做外键")  {constraints(primaryKey: true)} 
            column(name: "category", type: "varchar(" + 30 * weight + ")",  remarks: "脚本分类")  {constraints(nullable:"false")}  
            column(name: "server_name", type: "varchar(" + 60 * weight + ")",  remarks: "服务名称")  {constraints(nullable:"false")}  
            column(name: "script_code", type: "varchar(" + 30 * weight + ")",  remarks: "脚本编码")  {constraints(nullable:"false")}  
            column(name: "script_description", type: "varchar(" + 240 * weight + ")",  remarks: "脚本描述")  {constraints(nullable:"false")}  
            column(name: "script_type_code", type: "varchar(" + 20 * weight + ")",  remarks: "脚本类型，HPFM.RULE_SCRIPT_TYPE")  {constraints(nullable:"false")}  
            column(name: "script_content", type: "longtext",  remarks: "脚本内容")  {constraints(nullable:"false")}  
            column(name: "tenant_id", type: "bigint",   defaultValue:"0",   remarks: "租户ID,hpfm_tenant.tenant_id")  {constraints(nullable:"false")}  
            column(name: "enabled_flag", type: "tinyint",   defaultValue:"1",   remarks: "是否启用。1启用，0未启用")  {constraints(nullable:"false")}  
            column(name: "object_version_number", type: "bigint",   defaultValue:"1",   remarks: "行版本号，用来处理锁")  {constraints(nullable:"false")}  
            column(name: "creation_date", type: "datetime",   defaultValueComputed:"CURRENT_TIMESTAMP",   remarks: "")  {constraints(nullable:"false")}  
            column(name: "created_by", type: "bigint",   defaultValue:"-1",   remarks: "")  {constraints(nullable:"false")}  
            column(name: "last_updated_by", type: "bigint",   defaultValue:"-1",   remarks: "")  {constraints(nullable:"false")}  
            column(name: "last_update_date", type: "datetime",   defaultValueComputed:"CURRENT_TIMESTAMP",   remarks: "")  {constraints(nullable:"false")}  

        }
   createIndex(tableName: "hpfm_rule_script", indexName: "hpfm_rule_script_n1") {
            column(name: "category")
        }

        addUniqueConstraint(columnNames:"script_code,tenant_id",tableName:"hpfm_rule_script",constraintName: "hpfm_rule_script_u1")
    }
}