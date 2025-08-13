package script.db

databaseChangeLog(logicalFilePath: 'script/db/hsdr_executor_tl.groovy') {
    changeSet(author: "hzero@hand-china.com", id: "2020-08-31-hsdr_executor_tl") {
        def weight = 1
        if(helper.isSqlServer()){
            weight = 2
        } else if(helper.isOracle()){
            weight = 3
        }
        createTable(tableName: "hsdr_executor_tl", remarks: "执行器多语言") {
            column(name: "executor_id", type: "bigint",  remarks: "执行器Id")  {constraints(nullable:"false")}  
            column(name: "lang", type: "varchar(" + 30 * weight + ")",  remarks: "语言")  {constraints(nullable:"false")}  
            column(name: "executor_name", type: "varchar(" + 120 * weight + ")",  remarks: "执行器名称")  {constraints(nullable:"false")}  
            column(name: "tenant_id", type: "bigint",   defaultValue:"0",   remarks: "租户ID")  {constraints(nullable:"false")}  

        }

        addUniqueConstraint(columnNames:"executor_id,lang",tableName:"hsdr_executor_tl",constraintName: "hsdr_executor_tl_u1")
    }
}
