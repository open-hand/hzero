package script.db

databaseChangeLog(logicalFilePath: 'script/db/hpfm_datasource_tl.groovy') {
    changeSet(author: "hzero@hand-china.com", id: "2020-08-31-hpfm_datasource_tl") {
        def weight = 1
        if(helper.isSqlServer()){
            weight = 2
        } else if(helper.isOracle()){
            weight = 3
        }
        createTable(tableName: "hpfm_datasource_tl", remarks: "数据源多语言") {
            column(name: "datasource_id", type: "bigint",  remarks: "数据源Id")  {constraints(nullable:"false")}  
            column(name: "lang", type: "varchar(" + 30 * weight + ")",  remarks: "语言")  {constraints(nullable:"false")}  
            column(name: "description", type: "varchar(" + 600 * weight + ")",  remarks: "数据源说明")  {constraints(nullable:"false")}  
            column(name: "tenant_id", type: "bigint",   defaultValue:"0",   remarks: "租户ID")  {constraints(nullable:"false")}  

        }

        addUniqueConstraint(columnNames:"datasource_id,lang",tableName:"hpfm_datasource_tl",constraintName: "hpfm_datasource_tl_u1")
    }
}
