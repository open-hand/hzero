package script.db

databaseChangeLog(logicalFilePath: 'script/db/himp_template_target_tl.groovy') {
    changeSet(author: "hzero@hand-china.com", id: "2020-08-31-himp_template_target_tl") {
        def weight = 1
        if(helper.isSqlServer()){
            weight = 2
        } else if(helper.isOracle()){
            weight = 3
        }
        createTable(tableName: "himp_template_target_tl", remarks: "导入目标多语言") {
            column(name: "id", type: "bigint",  remarks: "模板目标Id")  {constraints(nullable:"false")}  
            column(name: "lang", type: "varchar(" + 30 * weight + ")",  remarks: "语言")  {constraints(nullable:"false")}  
            column(name: "sheet_name", type: "varchar(" + 30 * weight + ")",  remarks: "sheet页名称")  {constraints(nullable:"false")}  
            column(name: "tenant_id", type: "bigint",   defaultValue:"0",   remarks: "租户ID")  {constraints(nullable:"false")}  

        }

        addUniqueConstraint(columnNames:"id,lang",tableName:"himp_template_target_tl",constraintName: "himp_template_target_tl_u1")
    }
}
