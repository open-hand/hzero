package script.db

databaseChangeLog(logicalFilePath: 'script/db/hwfp_mail_template.groovy') {
    changeSet(author: "hzero@hand-china.com", id: "2019-11-07-hwfp_mail_template") {
        def weight = 1
        if(helper.isSqlServer()){
            weight = 2
        } else if(helper.isOracle()){
            weight = 3
        }
        if(helper.dbType().isSupportSequence()){
            createSequence(sequenceName: 'hwfp_mail_template_s', startValue:"1")
        }
        createTable(tableName: "hwfp_mail_template", remarks: "邮件审批模版") {
            column(name: "template_id", type: "bigint", autoIncrement: true ,   remarks: "表ID，主键，供其他表做外键")  {constraints(primaryKey: true)} 
            column(name: "template_code", type: "varchar(" + 60 * weight + ")",  remarks: "模版编码")  {constraints(nullable:"false")}  
            column(name: "template_name", type: "varchar(" + 60 * weight + ")",  remarks: "模版名称")  {constraints(nullable:"false")}  
            column(name: "template_content", type: "longtext",  remarks: "模版内容")   
            column(name: "tenant_id", type: "bigint",  remarks: "租户ID")  {constraints(nullable:"false")}  
            column(name: "enabled_flag", type: "tinyint",  remarks: "启用标识")  {constraints(nullable:"false")}  
            column(name: "document_id", type: "bigint",  remarks: "流程单据，hwfp_process_document.document_id")  {constraints(nullable:"false")}  
            column(name: "interface_id", type: "bigint",  remarks: "接口定义ID，hwfp_interface_definition.interface_id")   
            column(name: "template_remark", type: "longtext",  remarks: "模板备注")   
            column(name: "object_version_number", type: "bigint",   defaultValue:"1",   remarks: "行版本号，用来处理锁")  {constraints(nullable:"false")}  
            column(name: "creation_date", type: "datetime",   defaultValueComputed:"CURRENT_TIMESTAMP",   remarks: "")  {constraints(nullable:"false")}  
            column(name: "created_by", type: "bigint",   defaultValue:"-1",   remarks: "")  {constraints(nullable:"false")}  
            column(name: "last_updated_by", type: "bigint",   defaultValue:"-1",   remarks: "")  {constraints(nullable:"false")}  
            column(name: "last_update_date", type: "datetime",   defaultValueComputed:"CURRENT_TIMESTAMP",   remarks: "")  {constraints(nullable:"false")}  

        }

        addUniqueConstraint(columnNames:"template_code,tenant_id",tableName:"hwfp_mail_template",constraintName: "hwfp_mail_template_u1")
    }
}