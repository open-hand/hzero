package script.db

databaseChangeLog(logicalFilePath: 'script/db/hivc_api_config_line.groovy') {
    changeSet(author: "hzero@hand-china.com", id: "2020-05-26-hivc_api_config_line") {
        def weight = 1
        if(helper.isSqlServer()){
            weight = 2
        } else if(helper.isOracle()){
            weight = 3
        }
        if(helper.dbType().isSupportSequence()){
            createSequence(sequenceName: 'hivc_api_config_line_s', startValue:"1")
        }
        createTable(tableName: "hivc_api_config_line", remarks: "发票接口配置行") {
            column(name: "api_config_line_id", type: "bigint", autoIncrement: true ,   remarks: "")  {constraints(primaryKey: true)} 
            column(name: "api_config_id", type: "bigint",  remarks: "发票接口配置hivc_api_config.api_config_id")   
            column(name: "order_seq", type: "int",  remarks: "调用顺序")  {constraints(nullable:"false")}  
            column(name: "server_code", type: "varchar(" + 30 * weight + ")",  remarks: "接口平台服务编码")  {constraints(nullable:"false")}  
            column(name: "interface_code", type: "varchar(" + 30 * weight + ")",  remarks: "接口平台接口编码")  {constraints(nullable:"false")}  
            column(name: "tenant_id", type: "bigint",   defaultValue:"0",   remarks: "租户ID,hpfm_tenant.tenant_id")  {constraints(nullable:"false")}  
            column(name: "object_version_number", type: "bigint",   defaultValue:"1",   remarks: "行版本号，用来处理锁")  {constraints(nullable:"false")}  
            column(name: "creation_date", type: "datetime",   defaultValueComputed:"CURRENT_TIMESTAMP",   remarks: "")  {constraints(nullable:"false")}  
            column(name: "created_by", type: "bigint",   defaultValue:"-1",   remarks: "")  {constraints(nullable:"false")}  
            column(name: "last_updated_by", type: "bigint",   defaultValue:"-1",   remarks: "")  {constraints(nullable:"false")}  
            column(name: "last_update_date", type: "datetime",   defaultValueComputed:"CURRENT_TIMESTAMP",   remarks: "")  {constraints(nullable:"false")}  

        }
   createIndex(tableName: "hivc_api_config_line", indexName: "hivc_api_config_line_n1") {
            column(name: "api_config_id")
        }

    }
}