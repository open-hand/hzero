package script.db

databaseChangeLog(logicalFilePath: 'script/db/hrpt_template.groovy') {
    changeSet(author: "hzero@hand-china.com", id: "2019-02-19-hrpt_template") {
        def weight = 1
        if(helper.isSqlServer()){
            weight = 2
        } else if(helper.isOracle()){
            weight = 3
        }
        if(helper.dbType().isSupportSequence()){
            createSequence(sequenceName: 'hrpt_template_s', startValue:"1")
        }
        createTable(tableName: "hrpt_template", remarks: "报表模板") {
            column(name: "template_id", type: "bigint", autoIncrement: true ,   remarks: "表ID，主键，供其他表做外键")  {constraints(primaryKey: true)} 
            column(name: "template_type_code", type: "varchar(" + 30 * weight + ")",  remarks: "模板类型，值集:HRPT.TEMPLATE_TYPE")  {constraints(nullable:"false")}  
            column(name: "template_code", type: "varchar(" + 60 * weight + ")",  remarks: "模板编码")  {constraints(nullable:"false")}  
            column(name: "template_name", type: "varchar(" + 240 * weight + ")",  remarks: "模板名称")  {constraints(nullable:"false")}  
            column(name: "enabled_flag", type: "tinyint",   defaultValue:"1",   remarks: "启用标识")  {constraints(nullable:"false")}  
            column(name: "remark", type: "longtext",  remarks: "备注说明")   
            column(name: "tenant_id", type: "bigint",   defaultValue:"0",   remarks: "租户ID,hpfm_tenant.tenant_id")  {constraints(nullable:"false")}  
            column(name: "object_version_number", type: "bigint",   defaultValue:"1",   remarks: "行版本号，用来处理锁")  {constraints(nullable:"false")}  
            column(name: "creation_date", type: "datetime",   defaultValueComputed:"CURRENT_TIMESTAMP",   remarks: "")  {constraints(nullable:"false")}  
            column(name: "created_by", type: "bigint",   defaultValue:"-1",   remarks: "")  {constraints(nullable:"false")}  
            column(name: "last_updated_by", type: "bigint",   defaultValue:"-1",   remarks: "")  {constraints(nullable:"false")}  
            column(name: "last_update_date", type: "datetime",   defaultValueComputed:"CURRENT_TIMESTAMP",   remarks: "")  {constraints(nullable:"false")}  

        }

        addUniqueConstraint(columnNames:"template_code,tenant_id",tableName:"hrpt_template",constraintName: "hrpt_template_u1")
    }
}