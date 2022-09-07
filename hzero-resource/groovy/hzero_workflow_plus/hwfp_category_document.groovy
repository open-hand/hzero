package script.db

databaseChangeLog(logicalFilePath: 'script/db/hwfp_category_document.groovy') {
    changeSet(author: "hzero", id: "2019-06-06-hwfp_category_document") {
        def weight = 1
        if(helper.isSqlServer()){
            weight = 2
        } else if(helper.isOracle()){
            weight = 3
        }
        if(helper.dbType().isSupportSequence()){
            createSequence(sequenceName: 'hwfp_category_document_s', startValue:"1")
        }
        createTable(tableName: "hwfp_category_document", remarks: "流程分类单据分配") {
            column(name: "category_document_id", type: "bigint", autoIncrement: true ,   remarks: "表ID，主键，供其他表做外键")  {constraints(primaryKey: true)} 
            column(name: "category_id", type: "bigint",  remarks: "流程分类ID,hwfl_process_category.category_id")  {constraints(nullable:"false")}  
            column(name: "document_id", type: "bigint",  remarks: "流程单据ID,hwfl_process_document.document_id")  {constraints(nullable:"false")}  
            column(name: "object_version_number", type: "bigint",   defaultValue:"1",   remarks: "行版本号，用来处理锁")  {constraints(nullable:"false")}  
            column(name: "creation_date", type: "datetime",   defaultValueComputed:"CURRENT_TIMESTAMP",   remarks: "")  {constraints(nullable:"false")}  
            column(name: "created_by", type: "bigint",   defaultValue:"-1",   remarks: "")  {constraints(nullable:"false")}  
            column(name: "last_updated_by", type: "bigint",   defaultValue:"-1",   remarks: "")  {constraints(nullable:"false")}  
            column(name: "last_update_date", type: "datetime",   defaultValueComputed:"CURRENT_TIMESTAMP",   remarks: "")  {constraints(nullable:"false")}  

        }

        addUniqueConstraint(columnNames:"category_id,document_id",tableName:"hwfp_category_document",constraintName: "hwfp_category_document_u1")
    }
}