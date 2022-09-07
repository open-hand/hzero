package script.db

databaseChangeLog(logicalFilePath: 'script/db/hwfp_process_category_tl.groovy') {
    changeSet(author: "hzero@hand-china.com", id: "2019-11-01-hwfp_process_category_tl") {
        def weight = 1
        if(helper.isSqlServer()){
            weight = 2
        } else if(helper.isOracle()){
            weight = 3
        }
        createTable(tableName: "hwfp_process_category_tl", remarks: "流程分类多语言") {
            column(name: "category_id", type: "bigint",  remarks: "表ID，主键，供其他表做外键")  {constraints(nullable:"false")}  
            column(name: "lang", type: "varchar(" + 30 * weight + ")",  remarks: "语言编码")  {constraints(nullable:"false")}  
            column(name: "description", type: "varchar(" + 240 * weight + ")",  remarks: "流程分类描述")   

        }

        addUniqueConstraint(columnNames:"category_id,lang",tableName:"hwfp_process_category_tl",constraintName: "hwfp_process_category_tl_u1")
    }
}