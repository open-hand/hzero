package script.db

databaseChangeLog(logicalFilePath: 'script/db/hsrh_index.groovy') {
    changeSet(author: "qi.liu02@hand-china.com", id: "2020-02-26-hsrh_index") {
        def weight = 1
        if(helper.isSqlServer()){
            weight = 2
        } else if(helper.isOracle()){
            weight = 3
        }
        if(helper.dbType().isSupportSequence()){
            createSequence(sequenceName: 'hsrh_index_s', startValue:"1")
        }
        createTable(tableName: "hsrh_index", remarks: "索引表") {
            column(name: "index_id", type: "bigint", autoIncrement: true ,   remarks: "表ID，主键")  {constraints(primaryKey: true)}
            column(name: "index_code", type: "varchar(" + 255 * weight + ")",  remarks: "表名")  {constraints(nullable:"false")}  
            column(name: "remark", type: "varchar(" + 255 * weight + ")",  remarks: "备注")   
            column(name: "shards", type: "tinyint",   defaultValue:"1",   remarks: "分片数量")
            column(name: "replicas", type: "tinyint",   defaultValue:"0",   remarks: "副本数量")
            column(name: "enabled_flag", type: "tinyint",  remarks: "1 启用，0 禁用 （0 代表当前索引可修改删除） 判断优先级高")  {constraints(nullable:"false")}
            column(name: "tenant_id", type: "bigint",  remarks: "租户ID")  {constraints(nullable:"false")}
            column(name: "object_version_number", type: "bigint",   defaultValue:"1",   remarks: "行版本号，用来处理锁")  {constraints(nullable:"false")}
            column(name: "creation_date", type: "datetime",   defaultValueComputed:"CURRENT_TIMESTAMP",   remarks: "")  {constraints(nullable:"false")}  
            column(name: "created_by", type: "bigint",   defaultValue:"-1",   remarks: "")  {constraints(nullable:"false")}
            column(name: "last_updated_by", type: "bigint",   defaultValue:"-1",   remarks: "")  {constraints(nullable:"false")}
            column(name: "last_update_date", type: "datetime",   defaultValueComputed:"CURRENT_TIMESTAMP",   remarks: "")  {constraints(nullable:"false")}  

        }
   createIndex(tableName: "hsrh_index", indexName: "hsrh_index_n1") {
            column(name: "enabled_flag")
            column(name: "tenant_id")
        }

        addUniqueConstraint(columnNames:"index_code,index_id,tenant_id",tableName:"hsrh_index",constraintName: "hsrh_index_u1")
    }
}