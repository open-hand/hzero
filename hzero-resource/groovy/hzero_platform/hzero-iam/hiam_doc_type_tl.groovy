package script.db

databaseChangeLog(logicalFilePath: 'script/db/hiam_doc_type_tl.groovy') {
    changeSet(author: "hzero@hand-china.com", id: "2020-08-31-hiam_doc_type_tl") {
        def weight = 1
        if(helper.isSqlServer()){
            weight = 2
        } else if(helper.isOracle()){
            weight = 3
        }
        createTable(tableName: "hiam_doc_type_tl", remarks: "单据权限多语言") {
            column(name: "doc_type_id", type: "bigint",  remarks: "单据Id")  {constraints(nullable:"false")}  
            column(name: "lang", type: "varchar(" + 30 * weight + ")",  remarks: "语言")  {constraints(nullable:"false")}  
            column(name: "doc_type_name", type: "varchar(" + 240 * weight + ")",  remarks: "单据名称")  {constraints(nullable:"false")}  
            column(name: "tenant_id", type: "bigint",   defaultValue:"0",   remarks: "租户ID")  {constraints(nullable:"false")}  

        }

        addUniqueConstraint(columnNames:"doc_type_id,lang",tableName:"hiam_doc_type_tl",constraintName: "hiam_doc_type_tl_u1")
    }
}
