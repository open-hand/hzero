package script.db

databaseChangeLog(logicalFilePath: 'script/db/hrpt_label_template.groovy') {
    changeSet(author: "hzero@hand-china.com", id: "2019-12-18-hrpt_label_template") {
        def weight = 1
        if(helper.isSqlServer()){
            weight = 2
        } else if(helper.isOracle()){
            weight = 3
        }
        if(helper.dbType().isSupportSequence()){
            createSequence(sequenceName: 'hrpt_label_template_s', startValue:"1")
        }
        createTable(tableName: "hrpt_label_template", remarks: "标签模板") {
            column(name: "label_template_id", type: "bigint", autoIncrement: true ,   remarks: "表ID，主键，供其他表做外键")  {constraints(primaryKey: true)} 
            column(name: "template_code", type: "varchar(" + 30 * weight + ")",  remarks: "模板代码")  {constraints(nullable:"false")}  
            column(name: "template_name", type: "varchar(" + 60 * weight + ")",  remarks: "模板名称")  {constraints(nullable:"false")}  
            column(name: "template_width", type: "int",  remarks: "模板宽度")  {constraints(nullable:"false")}  
            column(name: "template_high", type: "int",  remarks: "模板高度")  {constraints(nullable:"false")}  
            column(name: "dataset_id", type: "bigint",  remarks: "数据集，hrpt_dataset.dataset_id")  {constraints(nullable:"false")}  
            column(name: "template_content", type: "longtext",  remarks: "标签模板内容")   
            column(name: "enabled_flag", type: "tinyint",   defaultValue:"1",   remarks: "启用标识")  {constraints(nullable:"false")}  
            column(name: "tenant_id", type: "bigint",   defaultValue:"0",   remarks: "租户ID,hpfm_tenant.tenant_id")  {constraints(nullable:"false")}  
            column(name: "object_version_number", type: "bigint",   defaultValue:"1",   remarks: "行版本号，用来处理锁")  {constraints(nullable:"false")}  
            column(name: "creation_date", type: "datetime",   defaultValueComputed:"CURRENT_TIMESTAMP",   remarks: "")  {constraints(nullable:"false")}  
            column(name: "created_by", type: "bigint",   defaultValue:"-1",   remarks: "")  {constraints(nullable:"false")}  
            column(name: "last_updated_by", type: "bigint",   defaultValue:"-1",   remarks: "")  {constraints(nullable:"false")}  
            column(name: "last_update_date", type: "datetime",   defaultValueComputed:"CURRENT_TIMESTAMP",   remarks: "")  {constraints(nullable:"false")}  

        }

        addUniqueConstraint(columnNames:"template_code,tenant_id",tableName:"hrpt_label_template",constraintName: "hrpt_label_template_u1")
    }
}