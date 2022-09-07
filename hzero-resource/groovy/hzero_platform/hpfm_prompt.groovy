package script.db

databaseChangeLog(logicalFilePath: 'script/db/hpfm_prompt.groovy') {
    changeSet(author: "hzero@hand-china.com", id: "2019-03-01-hpfm_prompt") {
        def weight = 1
        if(helper.isSqlServer()){
            weight = 2
        } else if(helper.isOracle()){
            weight = 3
        }
        if(helper.dbType().isSupportSequence()){
            createSequence(sequenceName: 'hpfm_prompt_s', startValue:"1")
        }
        createTable(tableName: "hpfm_prompt", remarks: "多语言配置") {
            column(name: "prompt_id", type: "bigint", autoIncrement: true ,   remarks: "表ID，主键，供其他表做外键")  {constraints(primaryKey: true)} 
            column(name: "tenant_id", type: "bigint",   defaultValue:"0",   remarks: "租户id，hpfm_tenant.tenant_id")  {constraints(nullable:"false")}  
            column(name: "prompt_key", type: "varchar(" + 60 * weight + ")",  remarks: "多语言key")  {constraints(nullable:"false")}  
            column(name: "prompt_code", type: "varchar(" + 240 * weight + ")",  remarks: "多语言code")  {constraints(nullable:"false")}  
            column(name: "lang", type: "varchar(" + 30 * weight + ")",  remarks: "语言")  {constraints(nullable:"false")}  
            column(name: "description", type: "varchar(" + 750 * weight + ")",  remarks: "描述")  {constraints(nullable:"false")}  
            column(name: "object_version_number", type: "bigint",   defaultValue:"1",   remarks: "行版本号，用来处理锁")  {constraints(nullable:"false")}  
            column(name: "creation_date", type: "datetime",   defaultValueComputed:"CURRENT_TIMESTAMP",   remarks: "")  {constraints(nullable:"false")}  
            column(name: "created_by", type: "bigint",   defaultValue:"-1",   remarks: "")  {constraints(nullable:"false")}  
            column(name: "last_updated_by", type: "bigint",   defaultValue:"-1",   remarks: "")  {constraints(nullable:"false")}  
            column(name: "last_update_date", type: "datetime",   defaultValueComputed:"CURRENT_TIMESTAMP",   remarks: "")  {constraints(nullable:"false")}  

        }

        addUniqueConstraint(columnNames:"prompt_key,prompt_code,lang,tenant_id",tableName:"hpfm_prompt",constraintName: "hpfm_prompt_u1")
    }
}