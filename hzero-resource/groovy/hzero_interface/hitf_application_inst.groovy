package script.db

databaseChangeLog(logicalFilePath: 'script/db/hitf_application_inst.groovy') {
    changeSet(author: "jianbo.li@hand-china.com", id: "2019-09-05-hitf_application_inst") {
        def weight = 1
        if(helper.isSqlServer()){
            weight = 2
        } else if(helper.isOracle()){
            weight = 3
        }
        if(helper.dbType().isSupportSequence()){
            createSequence(sequenceName: 'hitf_application_inst_s', startValue:"1")
        }
        createTable(tableName: "hitf_application_inst", remarks: "应用产品实例配置") {
            column(name: "application_inst_id", type: "bigint", autoIncrement: true ,   remarks: "表ID，主键")  {constraints(primaryKey: true)} 
            column(name: "application_id", type: "bigint",  remarks: "应用产品ID")  {constraints(nullable:"false")}  
            column(name: "tenant_id", type: "bigint",   defaultValue:"0",   remarks: "租户ID")  {constraints(nullable:"false")}  
            column(name: "inst_interface_id", type: "bigint",  remarks: "接口实例ID")  {constraints(nullable:"false")}  
            column(name: "weight", type: "int",  remarks: "权重")   
            column(name: "order_seq", type: "int",  remarks: "排序号")   
            column(name: "mapping_class", type: "longtext",  remarks: "客户化映射源代码")   
            column(name: "enabled_flag", type: "tinyint",   defaultValue:"1",   remarks: "是否启用。1启用，0未启用")  {constraints(nullable:"false")}  
            column(name: "remark", type: "longtext",  remarks: "备注说明")   
            column(name: "object_version_number", type: "bigint",   defaultValue:"1",   remarks: "行版本号，用来处理锁")  {constraints(nullable:"false")}  
            column(name: "creation_date", type: "datetime",   defaultValueComputed:"CURRENT_TIMESTAMP",   remarks: "")  {constraints(nullable:"false")}  
            column(name: "created_by", type: "bigint",   defaultValue:"-1",   remarks: "")  {constraints(nullable:"false")}  
            column(name: "last_updated_by", type: "bigint",   defaultValue:"-1",   remarks: "")  {constraints(nullable:"false")}  
            column(name: "last_update_date", type: "datetime",   defaultValueComputed:"CURRENT_TIMESTAMP",   remarks: "")  {constraints(nullable:"false")}  

        }
   createIndex(tableName: "hitf_application_inst", indexName: "hitf_application_inst_n1") {
            column(name: "application_id")
            column(name: "tenant_id")
            column(name: "inst_interface_id")
        }

    }
}