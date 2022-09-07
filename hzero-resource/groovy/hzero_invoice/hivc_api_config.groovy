package script.db

databaseChangeLog(logicalFilePath: 'script/db/hivc_api_config.groovy') {
    changeSet(author: "hzero@hand-china.com", id: "2020-05-26-hivc_api_config") {
        def weight = 1
        if(helper.isSqlServer()){
            weight = 2
        } else if(helper.isOracle()){
            weight = 3
        }
        if(helper.dbType().isSupportSequence()){
            createSequence(sequenceName: 'hivc_api_config_s', startValue:"1")
        }
        createTable(tableName: "hivc_api_config", remarks: "发票接口配置") {
            column(name: "api_config_id", type: "bigint", autoIncrement: true ,   remarks: "")  {constraints(primaryKey: true)} 
            column(name: "api_type", type: "varchar(" + 60 * weight + ")",  remarks: "接口类型，值集HIVC.API_TYPE：发票查验CHECK/发票识别OCR")  {constraints(nullable:"false")}  
            column(name: "description", type: "varchar(" + 200 * weight + ")",  remarks: "接口描述")   
            column(name: "combine_flag", type: "tinyint",   defaultValue:"0",   remarks: "是否组合调用")  {constraints(nullable:"false")}  
            column(name: "default_flag", type: "tinyint",   defaultValue:"0",   remarks: "是否默认调用")  {constraints(nullable:"false")}  
            column(name: "tenant_id", type: "bigint",   defaultValue:"0",   remarks: "租户ID,hpfm_tenant.tenant_id")  {constraints(nullable:"false")}  
            column(name: "object_version_number", type: "bigint",   defaultValue:"1",   remarks: "行版本号，用来处理锁")  {constraints(nullable:"false")}  
            column(name: "creation_date", type: "datetime",   defaultValueComputed:"CURRENT_TIMESTAMP",   remarks: "")  {constraints(nullable:"false")}  
            column(name: "created_by", type: "bigint",   defaultValue:"-1",   remarks: "")  {constraints(nullable:"false")}  
            column(name: "last_updated_by", type: "bigint",   defaultValue:"-1",   remarks: "")  {constraints(nullable:"false")}  
            column(name: "last_update_date", type: "datetime",   defaultValueComputed:"CURRENT_TIMESTAMP",   remarks: "")  {constraints(nullable:"false")}  

        }

        addUniqueConstraint(columnNames:"api_type,tenant_id",tableName:"hivc_api_config",constraintName: "hivc_api_config_u1")
    }
}