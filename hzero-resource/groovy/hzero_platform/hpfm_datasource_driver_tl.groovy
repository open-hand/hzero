package script.db

databaseChangeLog(logicalFilePath: 'script/db/hpfm_datasource_driver_tl.groovy') {
    changeSet(author: "hzero@hand-china.com", id: "2020-08-31-hpfm_datasource_driver_tl") {
        def weight = 1
        if(helper.isSqlServer()){
            weight = 2
        } else if(helper.isOracle()){
            weight = 3
        }
        createTable(tableName: "hpfm_datasource_driver_tl", remarks: "数据源驱动多语言") {
            column(name: "driver_id", type: "bigint",  remarks: "驱动Id")  {constraints(nullable:"false")}  
            column(name: "lang", type: "varchar(" + 30 * weight + ")",  remarks: "语言")  {constraints(nullable:"false")}  
            column(name: "driver_name", type: "varchar(" + 255 * weight + ")",  remarks: "驱动名称")  {constraints(nullable:"false")}  
            column(name: "tenant_id", type: "bigint",   defaultValue:"0",   remarks: "租户ID")  {constraints(nullable:"false")}  

        }

        addUniqueConstraint(columnNames:"driver_id,lang",tableName:"hpfm_datasource_driver_tl",constraintName: "hpfm_datasource_driver_tl_u1")
    }
}
