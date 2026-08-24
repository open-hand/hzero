package script.db

databaseChangeLog(logicalFilePath: 'script/db/hpfm_code_rule.groovy') {
    changeSet(author: "hzero@hand-china.com", id: "2019-03-01-hpfm_code_rule") {
        def weight = 1
        if(helper.isSqlServer()){
            weight = 2
        } else if(helper.isOracle()){
            weight = 3
        }
        if(helper.dbType().isSupportSequence()){
            createSequence(sequenceName: 'hpfm_code_rule_s', startValue:"1")
        }
        createTable(tableName: "hpfm_code_rule", remarks: "编码规则头") {
            column(name: "rule_id", type: "bigint", autoIncrement: true ,   remarks: "编码规则id ")  {constraints(primaryKey: true)} 
            column(name: "rule_code", type: "varchar(" + 30 * weight + ")",  remarks: "编码规则code")  {constraints(nullable:"false")}  
            column(name: "rule_name", type: "varchar(" + 60 * weight + ")",  remarks: "编码规则名")  {constraints(nullable:"false")}  
            column(name: "rule_level", type: "varchar(" + 30 * weight + ")",  remarks: "应用层级，有全局级和租户级")  {constraints(nullable:"false")}  
            column(name: "tenant_id", type: "bigint",   defaultValue:"0",   remarks: "当应用层级为租户级时，此字段必输，值为租户id")  {constraints(nullable:"false")}  
            column(name: "description", type: "varchar(" + 240 * weight + ")",  remarks: "描述")   
            column(name: "object_version_number", type: "bigint",   defaultValue:"1",   remarks: "")  {constraints(nullable:"false")}  
            column(name: "created_by", type: "bigint",   defaultValue:"-1",   remarks: "创建人 ")  {constraints(nullable:"false")}  
            column(name: "creation_date", type: "datetime",   defaultValueComputed:"CURRENT_TIMESTAMP",   remarks: "创建日期 ")  {constraints(nullable:"false")}  
            column(name: "last_updated_by", type: "bigint",   defaultValue:"-1",   remarks: "最后更新人  ")  {constraints(nullable:"false")}  
            column(name: "last_update_date", type: "datetime",   defaultValueComputed:"CURRENT_TIMESTAMP",   remarks: "最后更新日期 ")  {constraints(nullable:"false")}  

        }

        addUniqueConstraint(columnNames:"rule_code,tenant_id",tableName:"hpfm_code_rule",constraintName: "hpfm_code_rule_u1")
    }
}