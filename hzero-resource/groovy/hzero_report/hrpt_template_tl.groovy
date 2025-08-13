package script.db

databaseChangeLog(logicalFilePath: 'script/db/hrpt_template_tl.groovy') {
    changeSet(author: "hzero@hand-china.com", id: "2020-08-31-hrpt_template_tl") {
        def weight = 1
        if(helper.isSqlServer()){
            weight = 2
        } else if(helper.isOracle()){
            weight = 3
        }
        createTable(tableName: "hrpt_template_tl", remarks: "报表模板多语言") {
            column(name: "template_id", type: "bigint",  remarks: "报表模板Id")  {constraints(nullable:"false")}  
            column(name: "lang", type: "varchar(" + 30 * weight + ")",  remarks: "语言")  {constraints(nullable:"false")}  
            column(name: "template_name", type: "varchar(" + 240 * weight + ")",  remarks: "报表模板名称")  {constraints(nullable:"false")}  
            column(name: "tenant_id", type: "bigint",   defaultValue:"0",   remarks: "租户ID")  {constraints(nullable:"false")}  

        }

        addUniqueConstraint(columnNames:"template_id,lang",tableName:"hrpt_template_tl",constraintName: "hrpt_template_tl_u1")
    }
}
