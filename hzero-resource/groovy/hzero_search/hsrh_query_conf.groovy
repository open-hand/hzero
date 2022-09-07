package script.db

databaseChangeLog(logicalFilePath: 'script/db/hsrh_query_conf.groovy') {
    changeSet(author: "qi.liu02@hand-china.com", id: "2020-02-26-hsrh_query_conf") {
        def weight = 1
        if(helper.isSqlServer()){
            weight = 2
        } else if(helper.isOracle()){
            weight = 3
        }
        if(helper.dbType().isSupportSequence()){
            createSequence(sequenceName: 'hsrh_query_conf_s', startValue:"1")
        }
        createTable(tableName: "hsrh_query_conf", remarks: "查询配置表") {
            column(name: "config_id", type: "bigint", autoIncrement: true ,   remarks: "表ID，主键。全局ID")  {constraints(primaryKey: true)}
            column(name: "config_code", type: "varchar(" + 255 * weight + ")",  remarks: "查询配置code")  {constraints(nullable:"false")}  
            column(name: "query_json", type: "longtext",  remarks: "查询配置json")   
            column(name: "remark", type: "varchar(" + 255 * weight + ")",  remarks: "备注")   
            column(name: "index_id", type: "bigint",  remarks: "关联索引表hsrh_index.index_id")  {constraints(nullable:"false")}
            column(name: "tenant_id", type: "bigint",   defaultValue:"0",   remarks: "租户号")  {constraints(nullable:"false")}
            column(name: "enabled_flag", type: "tinyint",   defaultValue:"1",   remarks: "是否启用。1启用，0失效")  {constraints(nullable:"false")}
            column(name: "active_start_time", type: "datetime",  remarks: "生效开始时间")   
            column(name: "active_end_time", type: "datetime",  remarks: "生效结束时间")   
            column(name: "object_version_number", type: "bigint",   defaultValue:"1",   remarks: "行版本号，用来处理锁")  {constraints(nullable:"false")}
            column(name: "creation_date", type: "datetime",   defaultValueComputed:"CURRENT_TIMESTAMP",   remarks: "")  {constraints(nullable:"false")}  
            column(name: "created_by", type: "bigint",   defaultValue:"-1",   remarks: "")  {constraints(nullable:"false")}
            column(name: "last_updated_by", type: "bigint",   defaultValue:"-1",   remarks: "")  {constraints(nullable:"false")}
            column(name: "last_update_date", type: "datetime",   defaultValueComputed:"CURRENT_TIMESTAMP",   remarks: "")  {constraints(nullable:"false")}  

        }
   createIndex(tableName: "hsrh_query_conf", indexName: "hsrh_query_conf_n1") {
            column(name: "enabled_flag")
            column(name: "tenant_id")
        }
   createIndex(tableName: "hsrh_query_conf", indexName: "hsrh_query_conf_n2") {
            column(name: "index_id")
            column(name: "tenant_id")
        }

        addUniqueConstraint(columnNames:"config_code,tenant_id",tableName:"hsrh_query_conf",constraintName: "hsrh_query_conf_u1")
    }
}