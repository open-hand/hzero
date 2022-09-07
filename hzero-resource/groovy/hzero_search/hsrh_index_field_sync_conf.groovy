package script.db

databaseChangeLog(logicalFilePath: 'script/db/hsrh_index_field_sync_conf.groovy') {
    changeSet(author: "qi.liu02@hand-china.com", id: "2020-02-26-hsrh_index_field_sync_conf") {
        def weight = 1
        if(helper.isSqlServer()){
            weight = 2
        } else if(helper.isOracle()){
            weight = 3
        }
        if(helper.dbType().isSupportSequence()){
            createSequence(sequenceName: 'hsrh_index_field_sync_conf_s', startValue:"1")
        }
        createTable(tableName: "hsrh_index_field_sync_conf", remarks: "索引字段同步配置表") {
            column(name: "field_sync_conf_id", type: "bigint", autoIncrement: true ,   remarks: "表ID，主键")  {constraints(primaryKey: true)}
            column(name: "index_field_id", type: "bigint",  remarks: "关联索引字段表hsrh_index_field.index_field_id")  {constraints(nullable:"false")}
            column(name: "field_mapping", type: "varchar(" + 255 * weight + ")",  remarks: "映射字段")  {constraints(nullable:"false")}  
            column(name: "sync_conf_id", type: "bigint",  remarks: "关联索引同步配置表hsrh_index_sync_conf.sync_conf_id")  {constraints(nullable:"false")}
            column(name: "tenant_id", type: "bigint",   defaultValue:"0",   remarks: "租户号")  {constraints(nullable:"false")}
            column(name: "object_version_number", type: "bigint",   defaultValue:"1",   remarks: "行版本号，用来处理锁")  {constraints(nullable:"false")}
            column(name: "creation_date", type: "datetime",   defaultValueComputed:"CURRENT_TIMESTAMP",   remarks: "")  {constraints(nullable:"false")}  
            column(name: "created_by", type: "bigint",   defaultValue:"-1",   remarks: "")  {constraints(nullable:"false")}
            column(name: "last_updated_by", type: "bigint",   defaultValue:"-1",   remarks: "")  {constraints(nullable:"false")}
            column(name: "last_update_date", type: "datetime",   defaultValueComputed:"CURRENT_TIMESTAMP",   remarks: "")  {constraints(nullable:"false")}  

        }
   createIndex(tableName: "hsrh_index_field_sync_conf", indexName: "hsrh_index_field_sync_conf_n1") {
            column(name: "index_field_id")
        }

    }
}