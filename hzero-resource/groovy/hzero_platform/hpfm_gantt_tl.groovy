package script.db

databaseChangeLog(logicalFilePath: 'script/db/hpfm_gantt_tl.groovy') {
    changeSet(author: "hzero@hand-china.com", id: "2020-08-31-hpfm_gantt_tl") {
        def weight = 1
        if(helper.isSqlServer()){
            weight = 2
        } else if(helper.isOracle()){
            weight = 3
        }
        createTable(tableName: "hpfm_gantt_tl", remarks: "甘特图多语言") {
            column(name: "gantt_id", type: "bigint",  remarks: "甘特图Id")  {constraints(nullable:"false")}  
            column(name: "lang", type: "varchar(" + 30 * weight + ")",  remarks: "语言")  {constraints(nullable:"false")}  
            column(name: "gantt_name", type: "varchar(" + 240 * weight + ")",  remarks: "甘特图名称")  {constraints(nullable:"false")}  
            column(name: "tenant_id", type: "bigint",   defaultValue:"0",   remarks: "租户ID")  {constraints(nullable:"false")}  

        }

        addUniqueConstraint(columnNames:"gantt_id,lang",tableName:"hpfm_gantt_tl",constraintName: "hpfm_gantt_tl_u1")
    }
}
