package script.db

databaseChangeLog(logicalFilePath: 'script/db/hsdr_concurrent_tl.groovy') {
    changeSet(author: "hzero@hand-china.com", id: "2020-08-31-hsdr_concurrent_tl") {
        def weight = 1
        if(helper.isSqlServer()){
            weight = 2
        } else if(helper.isOracle()){
            weight = 3
        }
        createTable(tableName: "hsdr_concurrent_tl", remarks: "并发程序多语言") {
            column(name: "concurrent_id", type: "bigint",  remarks: "并发程序Id")  {constraints(nullable:"false")}  
            column(name: "lang", type: "varchar(" + 30 * weight + ")",  remarks: "语言")  {constraints(nullable:"false")}  
            column(name: "conc_name", type: "varchar(" + 120 * weight + ")",  remarks: "并发程序名称")  {constraints(nullable:"false")}  
            column(name: "tenant_id", type: "bigint",   defaultValue:"0",   remarks: "租户ID")  {constraints(nullable:"false")}  

        }

        addUniqueConstraint(columnNames:"concurrent_id,lang",tableName:"hsdr_concurrent_tl",constraintName: "hsdr_concurrent_tl_u1")
    }
}
