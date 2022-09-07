package script.db

databaseChangeLog(logicalFilePath: 'script/db/hsrh_index_field.groovy') {
    changeSet(author: "qi.liu02@hand-china.com", id: "2020-02-26-hsrh_index_field") {
        def weight = 1
        if(helper.isSqlServer()){
            weight = 2
        } else if(helper.isOracle()){
            weight = 3
        }
        if(helper.dbType().isSupportSequence()){
            createSequence(sequenceName: 'hsrh_index_field_s', startValue:"1")
        }
        createTable(tableName: "hsrh_index_field", remarks: "索引字段表") {
            column(name: "index_field_id", type: "bigint", autoIncrement: true ,   remarks: "表ID，主键")  {constraints(primaryKey: true)}
            column(name: "field_name", type: "varchar(" + 255 * weight + ")",  remarks: "字段名")  {constraints(nullable:"false")}  
            column(name: "field_type", type: "varchar(" + 255 * weight + ")",  remarks: "字段类型，值集：HSRH.FIELD_TYPE")  {constraints(nullable:"false")}  
            column(name: "analyzer_flag", type: "tinyint",   defaultValue:"0",   remarks: "是否分词。1分词，0不分词")  {constraints(nullable:"false")}
            column(name: "filed_analyzer_code", type: "varchar(" + 255 * weight + ")",  remarks: "分词器，值集：HSRH.FIELD_ANALYZER")   
            column(name: "index_id", type: "bigint",  remarks: "关联版本控制表hsrh_index.index_id")  {constraints(nullable:"false")}
            column(name: "pk_flag", type: "tinyint",   defaultValue:"0",   remarks: "是否主键")  {constraints(nullable:"false")}
            column(name: "tenant_id", type: "bigint",   defaultValue:"0",   remarks: "租户ID")  {constraints(nullable:"false")}
            column(name: "object_version_number", type: "bigint",   defaultValue:"1",   remarks: "行版本号，用来处理锁")  {constraints(nullable:"false")}
            column(name: "creation_date", type: "datetime",   defaultValueComputed:"CURRENT_TIMESTAMP",   remarks: "")  {constraints(nullable:"false")}  
            column(name: "created_by", type: "bigint",   defaultValue:"-1",   remarks: "")  {constraints(nullable:"false")}
            column(name: "last_updated_by", type: "bigint",   defaultValue:"-1",   remarks: "")  {constraints(nullable:"false")}
            column(name: "last_update_date", type: "datetime",   defaultValueComputed:"CURRENT_TIMESTAMP",   remarks: "")  {constraints(nullable:"false")}  

        }
   createIndex(tableName: "hsrh_index_field", indexName: "hsrh_index_field_n1") {
            column(name: "index_id")
        }

    }
}