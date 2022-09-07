package script.db

databaseChangeLog(logicalFilePath: 'script/db/hwfp_process_document_tl.groovy') {
    changeSet(author: "hzero@hand-china.com", id: "2019-11-01-hwfp_process_document_tl") {
        def weight = 1
        if(helper.isSqlServer()){
            weight = 2
        } else if(helper.isOracle()){
            weight = 3
        }
        createTable(tableName: "hwfp_process_document_tl", remarks: "流程单据多语言") {
            column(name: "document_id", type: "bigint",  remarks: "表ID，主键，供其他表做外键")  {constraints(nullable:"false")}  
            column(name: "lang", type: "varchar(" + 30 * weight + ")",  remarks: "语言编码")  {constraints(nullable:"false")}  
            column(name: "description", type: "varchar(" + 240 * weight + ")",  remarks: "单据描述")   

        }

        addUniqueConstraint(columnNames:"document_id,lang",tableName:"hwfp_process_document_tl",constraintName: "hwfp_process_document_tl_u1")
    }
}