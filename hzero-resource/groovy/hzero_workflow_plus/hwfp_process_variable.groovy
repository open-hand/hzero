package script.db

databaseChangeLog(logicalFilePath: 'script/db/hwfp_process_variable.groovy') {
    changeSet(author: "hzero", id: "2019-06-06-hwfp_process_variable") {
        def weight = 1
        if(helper.isSqlServer()){
            weight = 2
        } else if(helper.isOracle()){
            weight = 3
        }
        if(helper.dbType().isSupportSequence()){
            createSequence(sequenceName: 'hwfp_process_variable_s', startValue:"1")
        }
        createTable(tableName: "hwfp_process_variable", remarks: "流程变量定义") {
            column(name: "variable_id", type: "bigint", autoIncrement: true ,   remarks: "表ID，主键，供其他表做外键")  {constraints(primaryKey: true)} 
            column(name: "source_type", type: "varchar(" + 30 * weight + ")",  remarks: "数据来源，[DOCUMENT(单据),CATEGORY(流程分类)]")  {constraints(nullable:"false")}  
            column(name: "source_id", type: "bigint",  remarks: "数据来源ID,hwfp_process_document.document_id/hwfp_process_category.category_id")  {constraints(nullable:"false")}  
            column(name: "variable_name", type: "varchar(" + 30 * weight + ")",  remarks: "字段名称")  {constraints(nullable:"false")}  
            column(name: "variable_type", type: "varchar(" + 30 * weight + ")",  remarks: "字段类型，值集：HWFP.PROCESS.VARIABLE_TYPE")  {constraints(nullable:"false")}  
            column(name: "description", type: "varchar(" + 240 * weight + ")",  remarks: "字段描述")   
            column(name: "required_flag", type: "tinyint",  remarks: "是否必须[1(必需),0(非必需)]")  {constraints(nullable:"false")}  
            column(name: "object_version_number", type: "bigint",   defaultValue:"1",   remarks: "行版本号，用来处理锁")  {constraints(nullable:"false")}  
            column(name: "creation_date", type: "datetime",   defaultValueComputed:"CURRENT_TIMESTAMP",   remarks: "")  {constraints(nullable:"false")}  
            column(name: "created_by", type: "bigint",   defaultValue:"-1",   remarks: "")  {constraints(nullable:"false")}  
            column(name: "last_updated_by", type: "bigint",   defaultValue:"-1",   remarks: "")  {constraints(nullable:"false")}  
            column(name: "last_update_date", type: "datetime",   defaultValueComputed:"CURRENT_TIMESTAMP",   remarks: "")  {constraints(nullable:"false")}  

        }

        addUniqueConstraint(columnNames:"source_type,source_id,variable_name",tableName:"hwfp_process_variable",constraintName: "hwfp_process_variable_u1")
    }
}