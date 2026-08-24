package script.db

databaseChangeLog(logicalFilePath: 'script/db/himp_template_line_tl.groovy') {
    changeSet(author: "hzero@hand-china.com", id: "2020-03-26-himp_template_line_tl") {
        def weight = 1
        if(helper.isSqlServer()){
            weight = 2
        } else if(helper.isOracle()){
            weight = 3
        }
        createTable(tableName: "himp_template_line_tl", remarks: "通用模板行多语言") {
            column(name: "id", type: "bigint",  remarks: "行Id，himp_template_line.id")  {constraints(nullable:"false")}  
            column(name: "lang", type: "varchar(" + 30 * weight + ")",  remarks: "语言")  {constraints(nullable:"false")}  
            column(name: "column_name", type: "varchar(" + 60 * weight + ")",  remarks: "列名称")  {constraints(nullable:"false")}  

        }

        addUniqueConstraint(columnNames:"id,lang",tableName:"himp_template_line_tl",constraintName: "himp_template_line_tl_u1")
    }
}