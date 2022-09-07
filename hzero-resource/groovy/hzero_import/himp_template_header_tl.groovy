package script.db

databaseChangeLog(logicalFilePath: 'script/db/himp_template_header_tl.groovy') {
    changeSet(author: "hzero@hand-china.com", id: "2020-08-31-himp_template_header_tl") {
        def weight = 1
        if(helper.isSqlServer()){
            weight = 2
        } else if(helper.isOracle()){
            weight = 3
        }
        createTable(tableName: "himp_template_header_tl", remarks: "通用模板头多语言") {
            column(name: "id", type: "bigint",  remarks: "模板Id")  {constraints(nullable:"false")}  
            column(name: "lang", type: "varchar(" + 30 * weight + ")",  remarks: "语言")  {constraints(nullable:"false")}  
            column(name: "template_name", type: "varchar(" + 240 * weight + ")",  remarks: "模板名称")  {constraints(nullable:"false")}  
            column(name: "tenant_id", type: "bigint",   defaultValue:"0",   remarks: "租户ID")  {constraints(nullable:"false")}  

        }

        addUniqueConstraint(columnNames:"id,lang",tableName:"himp_template_header_tl",constraintName: "himp_template_header_tl_u1")
    }
}
