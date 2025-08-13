package script.db

databaseChangeLog(logicalFilePath: 'script/db/hsdr_conc_executable_tl.groovy') {
    changeSet(author: "hzero@hand-china.com", id: "2020-08-31-hsdr_conc_executable_tl") {
        def weight = 1
        if(helper.isSqlServer()){
            weight = 2
        } else if(helper.isOracle()){
            weight = 3
        }
        createTable(tableName: "hsdr_conc_executable_tl", remarks: "可执行定义多语言") {
            column(name: "executable_id", type: "bigint",  remarks: "可执行定义Id")  {constraints(nullable:"false")}  
            column(name: "lang", type: "varchar(" + 30 * weight + ")",  remarks: "语言")  {constraints(nullable:"false")}  
            column(name: "executable_name", type: "varchar(" + 120 * weight + ")",  remarks: "可执行定义名称")  {constraints(nullable:"false")}  
            column(name: "tenant_id", type: "bigint",   defaultValue:"0",   remarks: "租户ID")  {constraints(nullable:"false")}  

        }

        addUniqueConstraint(columnNames:"executable_id,lang",tableName:"hsdr_conc_executable_tl",constraintName: "hsdr_conc_executable_tl_u1")
    }
}
