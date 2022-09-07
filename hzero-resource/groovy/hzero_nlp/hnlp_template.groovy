package script.db

databaseChangeLog(logicalFilePath: 'script/db/hnlp_template.groovy') {
    changeSet(author: "hzero@hand-china.com", id: "2019-06-21-hnlp_template") {
        def weight = 1
        if(helper.isSqlServer()){
            weight = 2
        } else if(helper.isOracle()){
            weight = 3
        }
        if(helper.dbType().isSupportSequence()){
            createSequence(sequenceName: 'hnlp_template_s', startValue:"1")
        }
        createTable(tableName: "hnlp_template", remarks: "模板") {
            column(name: "TEMPLATE_ID", type: "bigint", autoIncrement: true ,   remarks: "")  {constraints(primaryKey: true)} 
            column(name: "TENANT_ID", type: "bigint",   defaultValue:"0",   remarks: "租户ID")  {constraints(nullable:"false")}  
            column(name: "TEMPLATE_CODE", type: "varchar(" + 100 * weight + ")",  remarks: "模板编码")  {constraints(nullable:"false")}  
            column(name: "TEMPLATE_NAME", type: "varchar(" + 100 * weight + ")",  remarks: "模板名称")  {constraints(nullable:"false")}  
            column(name: "REPLACE_CHAR", type: "varchar(" + 300 * weight + ")",  remarks: "被替换为空的字符集合如a,bc")   
            column(name: "DESCRIPTION", type: "varchar(" + 600 * weight + ")",  remarks: "说明")   
            column(name: "ENABLED_FLAG", type: "tinyint",   defaultValue:"1",   remarks: "是否启用")  {constraints(nullable:"false")}  
            column(name: "object_version_number", type: "bigint",   defaultValue:"1",   remarks: "行版本号，用来处理锁")  {constraints(nullable:"false")}  
            column(name: "creation_date", type: "datetime",   defaultValueComputed:"CURRENT_TIMESTAMP",   remarks: "")  {constraints(nullable:"false")}  
            column(name: "created_by", type: "bigint",   defaultValue:"-1",   remarks: "")  {constraints(nullable:"false")}  
            column(name: "last_updated_by", type: "bigint",   defaultValue:"-1",   remarks: "")  {constraints(nullable:"false")}  
            column(name: "last_update_date", type: "datetime",   defaultValueComputed:"CURRENT_TIMESTAMP",   remarks: "")  {constraints(nullable:"false")}  
            column(name: "MAX_GRAM", type: "bigint",   defaultValue:"3",   remarks: "")  {constraints(nullable:"false")}  

        }

        addUniqueConstraint(columnNames:"TEMPLATE_CODE,TENANT_ID",tableName:"hnlp_template",constraintName: "HNLP_TEMPLATE_u1")
    }
}