package script.db

databaseChangeLog(logicalFilePath: 'script/db/hnlp_template_data.groovy') {
    changeSet(author: "hzero@hand-china.com", id: "2019-06-21-hnlp_template_data") {
        def weight = 1
        if(helper.isSqlServer()){
            weight = 2
        } else if(helper.isOracle()){
            weight = 3
        }
        if(helper.dbType().isSupportSequence()){
            createSequence(sequenceName: 'hnlp_template_data_s', startValue:"1")
        }
        createTable(tableName: "hnlp_template_data", remarks: "模板配置") {
            column(name: "DATA_ID", type: "bigint", autoIncrement: true ,   remarks: "表ID")  {constraints(primaryKey: true)} 
            column(name: "TENANT_ID", type: "bigint",   defaultValue:"0",   remarks: "租户ID")  {constraints(nullable:"false")}  
            column(name: "TEMPLATE_ID", type: "bigint",  remarks: "")  {constraints(nullable:"false")}  
            column(name: "ACTUAL_TYPE", type: "varchar(" + 100 * weight + ")",  remarks: "实际类型，即提供给达观识别的类型")  {constraints(nullable:"false")}  
            column(name: "TYPE", type: "varchar(" + 100 * weight + ")",  remarks: "映射类型，即汇联易获取到的类型")   
            column(name: "TYPE_NUM", type: "bigint",  remarks: "类型的数量")   
            column(name: "IS_CUSTOM", type: "tinyint",  remarks: "是否需要做映射，标识识别后的结果是否做映射转换")   
            column(name: "object_version_number", type: "bigint",   defaultValue:"1",   remarks: "行版本号，用来处理锁")  {constraints(nullable:"false")}  
            column(name: "creation_date", type: "datetime",   defaultValueComputed:"CURRENT_TIMESTAMP",   remarks: "")  {constraints(nullable:"false")}  
            column(name: "created_by", type: "bigint",   defaultValue:"-1",   remarks: "")  {constraints(nullable:"false")}  
            column(name: "last_updated_by", type: "bigint",   defaultValue:"-1",   remarks: "")  {constraints(nullable:"false")}  
            column(name: "last_update_date", type: "datetime",   defaultValueComputed:"CURRENT_TIMESTAMP",   remarks: "")  {constraints(nullable:"false")}  

        }
   createIndex(tableName: "hnlp_template_data", indexName: "HNLP_TEMPLATE_DATA_n1") {
            column(name: "TEMPLATE_ID")
            column(name: "TENANT_ID")
        }

    }
}