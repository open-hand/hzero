package script.db

databaseChangeLog(logicalFilePath: 'script/db/hsrh_query_field_conf.groovy') {
    changeSet(author: "qi.liu02@hand-china.com", id: "2020-02-26-hsrh_query_field_conf") {
        def weight = 1
        if(helper.isSqlServer()){
            weight = 2
        } else if(helper.isOracle()){
            weight = 3
        }
        if(helper.dbType().isSupportSequence()){
            createSequence(sequenceName: 'hsrh_query_field_conf_s', startValue:"1")
        }
        createTable(tableName: "hsrh_query_field_conf", remarks: "查询配置字段表") {
            column(name: "field_config_id", type: "bigint", autoIncrement: true ,   remarks: "表ID，主键。全局ID")  {constraints(primaryKey: true)}
            column(name: "visible_flag", type: "tinyint",   defaultValue:"1",   remarks: "是否显示。1显示，0不显示")  {constraints(nullable:"false")}
            column(name: "sort_flag", type: "tinyint",   defaultValue:"0",   remarks: "是否排序。1排序，0不排序")  {constraints(nullable:"false")}
            column(name: "sort_direct", type: "tinyint",  remarks: "排序方向（0 正序/1 倒序）")
            column(name: "weight", type: "bigint",  remarks: "权重")  {constraints(nullable:"false")}
            column(name: "config_id", type: "bigint",   defaultValue:"1",   remarks: "关联查询配置表hsrh_query_config.config_id")  {constraints(nullable:"false")}
            column(name: "index_field_id", type: "bigint",  remarks: "关联字段表hsrh_index_field.index_field_id")  {constraints(nullable:"false")}
            column(name: "tenant_id", type: "bigint",   defaultValue:"0",   remarks: "租户号")  {constraints(nullable:"false")}
            column(name: "object_version_number", type: "bigint",   defaultValue:"1",   remarks: "行版本号，用来处理锁")  {constraints(nullable:"false")}
            column(name: "creation_date", type: "datetime",   defaultValueComputed:"CURRENT_TIMESTAMP",   remarks: "")  {constraints(nullable:"false")}  
            column(name: "created_by", type: "bigint",   defaultValue:"-1",   remarks: "")  {constraints(nullable:"false")}
            column(name: "last_updated_by", type: "bigint",   defaultValue:"-1",   remarks: "")  {constraints(nullable:"false")}
            column(name: "last_update_date", type: "datetime",   defaultValueComputed:"CURRENT_TIMESTAMP",   remarks: "")  {constraints(nullable:"false")}  

        }
   createIndex(tableName: "hsrh_query_field_conf", indexName: "hsrh_query_field_conf_n1") {
            column(name: "config_id")
            column(name: "tenant_id")
        }

    }
}