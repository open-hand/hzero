package script.db

databaseChangeLog(logicalFilePath: 'script/db/hpfm_template_tl.groovy') {
    changeSet(author: "hzero@hand-china.com", id: "2020-09-04-hpfm_template_tl") {
        def weight = 1
        if(helper.isSqlServer()){
            weight = 2
        } else if(helper.isOracle()){
            weight = 3
        }
        createTable(tableName: "hpfm_template_tl", remarks: "模板信息多语言") {
            column(name: "template_id", type: "bigint",  remarks: "模板Id")  {constraints(nullable:"false")}
            column(name: "lang", type: "varchar(" + 30 * weight + ")",  remarks: "语言")  {constraints(nullable:"false")}  
            column(name: "template_name", type: "varchar(" + 255 * weight + ")",  remarks: "模板名称")  {constraints(nullable:"false")}
            column(name: "tenant_id", type: "bigint",   defaultValue:"0",   remarks: "租户ID")  {constraints(nullable:"false")}  

        }

        addUniqueConstraint(columnNames:"template_id,lang",tableName:"hpfm_template_tl",constraintName: "hpfm_template_tl_u1")
    }
}
