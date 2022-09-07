package script.db

databaseChangeLog(logicalFilePath: 'script/db/hpfm_entity_table.groovy') {
    changeSet(author: "xingxing.wu@hand-china.com ", id: "2019-07-24-hpfm_entity_table") {
        def weight = 1
        if(helper.isSqlServer()){
            weight = 2
        } else if(helper.isOracle()){
            weight = 3
        }
        if(helper.dbType().isSupportSequence()){
            createSequence(sequenceName: 'hpfm_entity_table_s', startValue:"1")
        }
        createTable(tableName: "hpfm_entity_table", remarks: "实体表") {
            column(name: "entity_table_id", type: "bigint", autoIncrement: true ,   remarks: "表ID，主键，供其他表做外键")  {constraints(primaryKey: true)} 
            column(name: "service_name", type: "varchar(" + 60 * weight + ")",  remarks: "服务名")  {constraints(nullable:"false")}  
            column(name: "entity_class", type: "varchar(" + 128 * weight + ")",  remarks: "实体类完全限定名")  {constraints(nullable:"false")}  
            column(name: "table_name", type: "varchar(" + 30 * weight + ")",  remarks: "表名称")  {constraints(nullable:"false")}  
            column(name: "table_catalog", type: "varchar(" + 30 * weight + ")",  remarks: "表目录")   
            column(name: "table_schema", type: "varchar(" + 30 * weight + ")",  remarks: "表模式")   
            column(name: "table_comment", type: "longtext",  remarks: "表说明")   
            column(name: "multi_language_flag", type: "tinyint",   defaultValue:"0",   remarks: "是否多语言表")  {constraints(nullable:"false")}  
            column(name: "multi_language_unique_flag", type: "tinyint",   defaultValue:"0",   remarks: "是否存在多语言字段作为唯一性字段")  {constraints(nullable:"false")}  
            column(name: "multi_language_table_name", type: "varchar(" + 30 * weight + ")",  remarks: "多语言表名称")   
            column(name: "remark", type: "longtext",  remarks: "备注说明")   
            column(name: "deleted_flag", type: "tinyint",   defaultValue:"0",   remarks: "删除标识，0-未删除，1-已删除")  {constraints(nullable:"false")}  
            column(name: "object_version_number", type: "bigint",   defaultValue:"1",   remarks: "行版本号，用来处理锁")  {constraints(nullable:"false")}  
            column(name: "creation_date", type: "datetime",   defaultValueComputed:"CURRENT_TIMESTAMP",   remarks: "")  {constraints(nullable:"false")}  
            column(name: "created_by", type: "bigint",   defaultValue:"-1",   remarks: "")  {constraints(nullable:"false")}  
            column(name: "last_updated_by", type: "bigint",   defaultValue:"-1",   remarks: "")  {constraints(nullable:"false")}  
            column(name: "last_update_date", type: "datetime",   defaultValueComputed:"CURRENT_TIMESTAMP",   remarks: "")  {constraints(nullable:"false")}  

        }

        addUniqueConstraint(columnNames:"service_name,table_name",tableName:"hpfm_entity_table",constraintName: "hpfm_entity_table_u1")
    }
}