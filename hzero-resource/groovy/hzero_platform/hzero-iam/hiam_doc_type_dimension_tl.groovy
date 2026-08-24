package script.db

databaseChangeLog(logicalFilePath: 'script/db/hiam_doc_type_dimension_tl.groovy') {
    changeSet(author: "hzero@hand-china.com", id: "2020-08-31-hiam_doc_type_dimension_tl") {
        def weight = 1
        if(helper.isSqlServer()){
            weight = 2
        } else if(helper.isOracle()){
            weight = 3
        }
        createTable(tableName: "hiam_doc_type_dimension_tl", remarks: "单据类型维度多语言") {
            column(name: "dimension_id", type: "bigint",  remarks: "维度Id")  {constraints(nullable:"false")}  
            column(name: "lang", type: "varchar(" + 30 * weight + ")",  remarks: "语言")  {constraints(nullable:"false")}  
            column(name: "dimension_name", type: "varchar(" + 60 * weight + ")",  remarks: "维度名称")  {constraints(nullable:"false")}  
            column(name: "tenant_id", type: "bigint",   defaultValue:"0",   remarks: "租户ID")  {constraints(nullable:"false")}  

        }

        addUniqueConstraint(columnNames:"dimension_id,lang",tableName:"hiam_doc_type_dimension_tl",constraintName: "hiam_doc_type_dimension_tl_u1")
    }
}
