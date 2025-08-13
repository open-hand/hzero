package script.db

databaseChangeLog(logicalFilePath: 'script/db/hrpt_label_parameter.groovy') {
    changeSet(author: "hzero@hand-china.com", id: "2019-12-18-hrpt_label_parameter") {
        def weight = 1
        if(helper.isSqlServer()){
            weight = 2
        } else if(helper.isOracle()){
            weight = 3
        }
        if(helper.dbType().isSupportSequence()){
            createSequence(sequenceName: 'hrpt_label_parameter_s', startValue:"1")
        }
        createTable(tableName: "hrpt_label_parameter", remarks: "标签参数") {
            column(name: "label_parameter_id", type: "bigint", autoIncrement: true ,   remarks: "表ID，主键，供其他表做外键")  {constraints(primaryKey: true)} 
            column(name: "label_template_id", type: "bigint",  remarks: "模板ID，hrpt_label_template.label_template_id")  {constraints(nullable:"false")}  
            column(name: "parameter_code", type: "varchar(" + 60 * weight + ")",  remarks: "参数代码")  {constraints(nullable:"false")}  
            column(name: "parameter_name", type: "varchar(" + 240 * weight + ")",  remarks: "参数名称")  {constraints(nullable:"false")}  
            column(name: "param_type_code", type: "varchar(" + 60 * weight + ")",  remarks: "参数类型，值集:HRPT.LABEL_PARAM_TYPE")  {constraints(nullable:"false")}  
            column(name: "text_length", type: "int",  remarks: "文字长度")   
            column(name: "max_rows", type: "int",  remarks: "最大行数")   
            column(name: "image_url", type: "varchar(" + 480 * weight + ")",  remarks: "图片地址")   
            column(name: "bar_code_type", type: "varchar(" + 30 * weight + ")",  remarks: "条码类型，值集:HRPT.LABEL_BARCODE_TYPE")   
            column(name: "character_encoding", type: "varchar(" + 60 * weight + ")",   defaultValue:"utf-8",   remarks: "字符编码")  {constraints(nullable:"false")}  
            column(name: "tenant_id", type: "bigint",   defaultValue:"0",   remarks: "租户ID,hpfm_tenant.tenant_id")  {constraints(nullable:"false")}  
            column(name: "object_version_number", type: "bigint",   defaultValue:"1",   remarks: "行版本号，用来处理锁")  {constraints(nullable:"false")}  
            column(name: "creation_date", type: "datetime",   defaultValueComputed:"CURRENT_TIMESTAMP",   remarks: "")  {constraints(nullable:"false")}  
            column(name: "created_by", type: "bigint",   defaultValue:"-1",   remarks: "")  {constraints(nullable:"false")}  
            column(name: "last_updated_by", type: "bigint",   defaultValue:"-1",   remarks: "")  {constraints(nullable:"false")}  
            column(name: "last_update_date", type: "datetime",   defaultValueComputed:"CURRENT_TIMESTAMP",   remarks: "")  {constraints(nullable:"false")}  

        }

        addUniqueConstraint(columnNames:"parameter_code,label_template_id",tableName:"hrpt_label_parameter",constraintName: "hrpt_label_parameter_u1")
    }
}