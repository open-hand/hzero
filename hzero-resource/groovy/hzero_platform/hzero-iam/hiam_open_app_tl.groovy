package script.db

databaseChangeLog(logicalFilePath: 'script/db/hiam_open_app_tl.groovy') {
    changeSet(author: "hzero@hand-china.com", id: "2020-08-31-hiam_open_app_tl") {
        def weight = 1
        if(helper.isSqlServer()){
            weight = 2
        } else if(helper.isOracle()){
            weight = 3
        }
        createTable(tableName: "hiam_open_app_tl", remarks: "三方应用多语言") {
            column(name: "open_app_id", type: "bigint",  remarks: "三方应用Id")  {constraints(nullable:"false")}  
            column(name: "lang", type: "varchar(" + 30 * weight + ")",  remarks: "语言")  {constraints(nullable:"false")}  
            column(name: "app_name", type: "varchar(" + 60 * weight + ")",  remarks: "应用名称")  {constraints(nullable:"false")}  
            column(name: "organization_id", type: "bigint",   defaultValue:"0",   remarks: "租户ID")  {constraints(nullable:"false")}  

        }

        addUniqueConstraint(columnNames:"open_app_id,lang",tableName:"hiam_open_app_tl",constraintName: "hiam_open_app_tl_u1")
    }
}
