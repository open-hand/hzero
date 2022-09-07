package script.db

databaseChangeLog(logicalFilePath: 'script/db/hims_knowledge_category_tl.groovy') {
    changeSet(author: "hzero@hand-china.com", id: "2019-12-20-hims_knowledge_category_tl") {
        def weight = 1
        if(helper.isSqlServer()){
            weight = 2
        } else if(helper.isOracle()){
            weight = 3
        }
        createTable(tableName: "hims_knowledge_category_tl", remarks: "卡片多语言表") {
            column(name: "category_id", type: "bigint",  remarks: "hims_knowledge_category.category_id")  {constraints(nullable:"false")}
            column(name: "lang", type: "varchar(" + 30 * weight + ")",  remarks: "语言名称")  {constraints(nullable:"false")}
            column(name: "category_name", type: "varchar(" + 120 * weight + ")",  remarks: "知识类别名称")  {constraints(nullable:"false")}
        }

        addUniqueConstraint(columnNames:"category_id,lang",tableName:"hims_knowledge_category_tl",constraintName: "hims_knowledge_category_tl_u1")
    }
}