package script.db

databaseChangeLog(logicalFilePath: 'script/db/hwfp_form_definition.groovy') {
    def weight = 1
    if(helper.isSqlServer()){
        weight = 2
    } else if(helper.isOracle()){
        weight = 3
    }
    changeSet(author: "hzero", id: "2019-06-06-hwfp_form_definition") {
        if(helper.dbType().isSupportSequence()){
            createSequence(sequenceName: 'hwfp_form_definition_s', startValue:"1")
        }
        createTable(tableName: "hwfp_form_definition", remarks: "表单定义") {
            column(name: "form_id", type: "bigint", autoIncrement: true ,   remarks: "表ID，主键，供其他表做外键")  {constraints(primaryKey: true)} 
            column(name: "document_id", type: "bigint",  remarks: "流程单据，hwfp_process_document.document_id")  {constraints(nullable:"false")}  
            column(name: "form_code", type: "varchar(" + 60 * weight + ")",  remarks: "表单编码")  {constraints(nullable:"false")}  
            column(name: "form_url", type: "varchar(" + 240 * weight + ")",  remarks: "表单url")  {constraints(nullable:"false")}  
            column(name: "description", type: "varchar(" + 240 * weight + ")",  remarks: "描述")  {constraints(nullable:"false")}  
            column(name: "enabled_flag", type: "tinyint",  remarks: "启用标识")  {constraints(nullable:"false")}  
            column(name: "invoke_flag", type: "tinyint",  remarks: "是否回调")   
            column(name: "tenant_id", type: "bigint",   defaultValue:"0",   remarks: "租户ID")  {constraints(nullable:"false")}  
            column(name: "object_version_number", type: "bigint",   defaultValue:"1",   remarks: "行版本号，用来处理锁")  {constraints(nullable:"false")}  
            column(name: "creation_date", type: "datetime",   defaultValueComputed:"CURRENT_TIMESTAMP",   remarks: "")  {constraints(nullable:"false")}  
            column(name: "created_by", type: "bigint",   defaultValue:"-1",   remarks: "")  {constraints(nullable:"false")}  
            column(name: "last_updated_by", type: "bigint",   defaultValue:"-1",   remarks: "")  {constraints(nullable:"false")}  
            column(name: "last_update_date", type: "datetime",   defaultValueComputed:"CURRENT_TIMESTAMP",   remarks: "")  {constraints(nullable:"false")}  

        }

        addUniqueConstraint(columnNames:"document_id,form_code",tableName:"hwfp_form_definition",constraintName: "hwfp_form_definition_u1")
    }

    changeSet(author: "peng.yu01@hand-china.com", id: "2020-03-03_hwfp_form_definition_add_mobile_form_key") {
        addColumn(tableName: 'hwfp_form_definition') {
            column(name: "mobile_form_url", type: "varchar(" + 240 * weight + ")", remarks: "移动端表单url") {
                constraints(nullable: "true")
            }
        }
    }

    changeSet(author: "peng.yu01@hand-china.com", id: "2020-03-09_hwfp_form_definition_modify-unique") {
        dropUniqueConstraint(tableName: "hwfp_form_definition", constraintName: "hwfp_form_definition_u1")
        addUniqueConstraint(tableName: "hwfp_form_definition", constraintName: "hwfp_form_definition_u1", columnNames: "document_id,form_code,tenant_id")
    }
}