package script.db

databaseChangeLog(logicalFilePath: 'script/db/hrpt_report_template.groovy') {
    changeSet(author: "hzero@hand-china.com", id: "2019-02-19-hrpt_report_template") {
        def weight = 1
        if(helper.isSqlServer()){
            weight = 2
        } else if(helper.isOracle()){
            weight = 3
        }
        if(helper.dbType().isSupportSequence()){
            createSequence(sequenceName: 'hrpt_report_template_s', startValue:"1")
        }
        createTable(tableName: "hrpt_report_template", remarks: "报表模板关系") {
            column(name: "report_template_id", type: "bigint", autoIncrement: true ,   remarks: "表ID，主键，供其他表做外键")  {constraints(primaryKey: true)} 
            column(name: "report_id", type: "bigint",  remarks: "报表ID，hrpt_report.report_id")   
            column(name: "template_id", type: "bigint",  remarks: "报表模板ID，hrpt_template.template_id")   
            column(name: "default_flag", type: "tinyint",   defaultValue:"0",   remarks: "默认标识")  {constraints(nullable:"false")}  
            column(name: "tenant_id", type: "bigint",   defaultValue:"0",   remarks: "租户ID,hpfm_tenant.tenant_id")  {constraints(nullable:"false")}  
            column(name: "object_version_number", type: "bigint",   defaultValue:"1",   remarks: "行版本号，用来处理锁")  {constraints(nullable:"false")}  
            column(name: "creation_date", type: "datetime",   defaultValueComputed:"CURRENT_TIMESTAMP",   remarks: "")  {constraints(nullable:"false")}  
            column(name: "created_by", type: "bigint",   defaultValue:"-1",   remarks: "")  {constraints(nullable:"false")}  
            column(name: "last_updated_by", type: "bigint",   defaultValue:"-1",   remarks: "")  {constraints(nullable:"false")}  
            column(name: "last_update_date", type: "datetime",   defaultValueComputed:"CURRENT_TIMESTAMP",   remarks: "")  {constraints(nullable:"false")}  

        }

        addUniqueConstraint(columnNames:"report_id,template_id",tableName:"hrpt_report_template",constraintName: "hrpt_report_template_u1")
    }
}