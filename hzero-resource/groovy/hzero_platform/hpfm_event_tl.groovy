package script.db

databaseChangeLog(logicalFilePath: 'script/db/hpfm_event_tl.groovy') {
    changeSet(author: "hzero@hand-china.com", id: "2020-09-04-hpfm_event_tl") {
        def weight = 1
        if(helper.isSqlServer()){
            weight = 2
        } else if(helper.isOracle()){
            weight = 3
        }
        createTable(tableName: "hpfm_event_tl", remarks: "事件多语言") {
            column(name: "event_id", type: "bigint",  remarks: "事件Id")  {constraints(nullable:"false")}
            column(name: "lang", type: "varchar(" + 30 * weight + ")",  remarks: "语言")  {constraints(nullable:"false")}  
            column(name: "event_description", type: "varchar(" + 240 * weight + ")",  remarks: "描述说明")  {constraints(nullable:"false")}
            column(name: "tenant_id", type: "bigint",   defaultValue:"0",   remarks: "租户ID")  {constraints(nullable:"false")}  

        }

        addUniqueConstraint(columnNames:"event_id,lang",tableName:"hpfm_event_tl",constraintName: "hpfm_event_tl_u1")
    }
}
