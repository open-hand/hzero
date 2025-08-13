package script.db

databaseChangeLog(logicalFilePath: 'script/db/hpfm_entity_column.groovy') {
    changeSet(author: "xingxing.wu@hand-china.com ", id: "2019-07-24-hpfm_entity_column") {
        def weight = 1
        if(helper.isSqlServer()){
            weight = 2
        } else if(helper.isOracle()){
            weight = 3
        }
        if(helper.dbType().isSupportSequence()){
            createSequence(sequenceName: 'hpfm_entity_column_s', startValue:"1")
        }
        createTable(tableName: "hpfm_entity_column", remarks: "实体列") {
            column(name: "entity_column_id", type: "bigint", autoIncrement: true ,   remarks: "表ID，主键，供其他表做外键")  {constraints(primaryKey: true)} 
            column(name: "entity_table_id", type: "bigint",  remarks: "实体表ID")  {constraints(nullable:"false")}  
            column(name: "field_name", type: "varchar(" + 60 * weight + ")",  remarks: "字段名称")  {constraints(nullable:"false")}  
            column(name: "java_type", type: "varchar(" + 128 * weight + ")",  remarks: "字段Java类型")  {constraints(nullable:"false")}  
            column(name: "column_name", type: "varchar(" + 30 * weight + ")",  remarks: "列名称，必输仅存储数据库字段")  {constraints(nullable:"false")}  
            column(name: "column_comment", type: "longtext",  remarks: "列说明")   
            column(name: "jdbc_type", type: "varchar(" + 128 * weight + ")",  remarks: "JDBC类型")   
            column(name: "type_handler", type: "varchar(" + 128 * weight + ")",  remarks: "类型转换器")   
            column(name: "pk_id_flag", type: "tinyint",   defaultValue:"0",   remarks: "是否主键-@Id")  {constraints(nullable:"false")}  
            column(name: "pk_sequence_name", type: "varchar(" + 128 * weight + ")",  remarks: "序列名字-序列号生成器SequenceGenerator")   
            column(name: "pk_uuid_flag", type: "tinyint",   defaultValue:"0",   remarks: "主键生成方式是否UUID方式")  {constraints(nullable:"false")}  
            column(name: "pk_identity_flag", type: "tinyint",   defaultValue:"0",   remarks: "主键生成方式Identity方式")  {constraints(nullable:"false")}  
            column(name: "pk_generator", type: "varchar(" + 128 * weight + ")",  remarks: "主键生成器-GeneratedValue.generator/UUID/JDBC等")   
            column(name: "unique_flag", type: "tinyint",   defaultValue:"0",   remarks: "是否唯一列")  {constraints(nullable:"false")}  
            column(name: "transient_flag", type: "tinyint",   defaultValue:"0",   remarks: "是否非数据库字段")  {constraints(nullable:"false")}  
            column(name: "data_security_flag", type: "tinyint",   defaultValue:"0",   remarks: "是否敏感字段")  {constraints(nullable:"false")}  
            column(name: "blob_flag", type: "tinyint",   defaultValue:"0",   remarks: "是否Blob")  {constraints(nullable:"false")}  
            column(name: "lov_code", type: "varchar(" + 80 * weight + ")",  remarks: "LOV代码")   
            column(name: "multi_language_flag", type: "tinyint",   defaultValue:"0",   remarks: "是否多语言字段")  {constraints(nullable:"false")}  
            column(name: "remark", type: "longtext",  remarks: "备注说明")   
            column(name: "deleted_flag", type: "tinyint",   defaultValue:"0",   remarks: "删除标识，0-未删除，1-已删除")  {constraints(nullable:"false")}  
            column(name: "object_version_number", type: "bigint",   defaultValue:"1",   remarks: "行版本号，用来处理锁")  {constraints(nullable:"false")}  
            column(name: "creation_date", type: "datetime",   defaultValueComputed:"CURRENT_TIMESTAMP",   remarks: "")  {constraints(nullable:"false")}  
            column(name: "created_by", type: "bigint",   defaultValue:"-1",   remarks: "")  {constraints(nullable:"false")}  
            column(name: "last_updated_by", type: "bigint",   defaultValue:"-1",   remarks: "")  {constraints(nullable:"false")}  
            column(name: "last_update_date", type: "datetime",   defaultValueComputed:"CURRENT_TIMESTAMP",   remarks: "")  {constraints(nullable:"false")}  

        }

        addUniqueConstraint(columnNames:"entity_table_id,column_name",tableName:"hpfm_entity_column",constraintName: "hpfm_entity_column_u1")
    }
}