package script.db

databaseChangeLog(logicalFilePath: 'script/db/hpfm_config_tl.groovy') {
    changeSet(author: "hzero@hand-china.com", id: "2020-08-31-hpfm_config_tl") {
        def weight = 1
        if(helper.isSqlServer()){
            weight = 2
        } else if(helper.isOracle()){
            weight = 3
        }
        createTable(tableName: "hpfm_config_tl", remarks: "系统配置多语言") {
            column(name: "config_id", type: "bigint",  remarks: "配置Id")  {constraints(nullable:"false")}  
            column(name: "lang", type: "varchar(" + 30 * weight + ")",  remarks: "语言")  {constraints(nullable:"false")}  
            column(name: "config_value", type: "varchar(" + 240 * weight + ")",  remarks: "配置值")  {constraints(nullable:"false")}  
            column(name: "tenant_id", type: "bigint",   defaultValue:"0",   remarks: "租户ID")  {constraints(nullable:"false")}  

        }

        addUniqueConstraint(columnNames:"config_id,lang",tableName:"hpfm_config_tl",constraintName: "hpfm_config_tl_u1")
    }
}
