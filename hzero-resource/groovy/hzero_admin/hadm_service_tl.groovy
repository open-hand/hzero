package script.db

databaseChangeLog(logicalFilePath: 'script/db/hadm_service_tl.groovy') {
    changeSet(author: "hzero@hand-china.com", id: "2020-01-04-hadm_service_tl") {
        def weight = 1
        if(helper.isSqlServer()){
            weight = 2
        } else if(helper.isOracle()){
            weight = 3
        }
        createTable(tableName: "hadm_service_tl", remarks: "") {
            column(name: "service_id", type: "bigint",  remarks: "")  {constraints(nullable:"false")}  
            column(name: "lang", type: "varchar(" + 16 * weight + ")",  remarks: "语言名称")  {constraints(nullable:"false")}  
            column(name: "service_name", type: "varchar(" + 90 * weight + ")",  remarks: "服务名称")  {constraints(nullable:"false")}  
        }

    }

    changeSet(author: "hzero@hand-china.com", id: "2020-12-15-hadm_service_tl") {
        addUniqueConstraint(columnNames: "service_id, lang", tableName: "hadm_service_tl", constraintName: "hadm_service_tl_u1")
    }
}