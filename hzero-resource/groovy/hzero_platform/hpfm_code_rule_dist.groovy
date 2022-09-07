package script.db

databaseChangeLog(logicalFilePath: 'script/db/hpfm_code_rule_dist.groovy') {
    changeSet(author: "hzero@hand-china.com", id: "2019-03-01-hpfm_code_rule_dist") {
        def weight = 1
        if(helper.isSqlServer()){
            weight = 2
        } else if(helper.isOracle()){
            weight = 3
        }
        if(helper.dbType().isSupportSequence()){
            createSequence(sequenceName: 'hpfm_code_rule_dist_s', startValue:"1")
        }
        createTable(tableName: "hpfm_code_rule_dist", remarks: "编码规则分配") {
            column(name: "rule_dist_id", type: "bigint", autoIncrement: true ,   remarks: "编码规则分配id ")  {constraints(primaryKey: true)} 
            column(name: "rule_id", type: "bigint",  remarks: "")  {constraints(nullable:"false")}  
            column(name: "level_code", type: "varchar(" + 30 * weight + ")",  remarks: "租户级下的应用层级")  {constraints(nullable:"false")}  
            column(name: "level_value", type: "varchar(" + 30 * weight + ")",  remarks: "应用层级值")   
            column(name: "used_flag", type: "tinyint",   defaultValue:"0",   remarks: "")  {constraints(nullable:"false")}  
            column(name: "enabled_flag", type: "tinyint",   defaultValue:"1",   remarks: "是否启用 ")  {constraints(nullable:"false")}  
            column(name: "description", type: "varchar(" + 240 * weight + ")",  remarks: "描述")   
            column(name: "object_version_number", type: "bigint",   defaultValue:"1",   remarks: "")  {constraints(nullable:"false")}  
            column(name: "created_by", type: "bigint",   defaultValue:"-1",   remarks: "创建人  ")  {constraints(nullable:"false")}  
            column(name: "creation_date", type: "datetime",   defaultValueComputed:"CURRENT_TIMESTAMP",   remarks: "创建日期 ")  {constraints(nullable:"false")}  
            column(name: "last_updated_by", type: "bigint",   defaultValue:"-1",   remarks: "最后更新人  ")  {constraints(nullable:"false")}  
            column(name: "last_update_date", type: "datetime",   defaultValueComputed:"CURRENT_TIMESTAMP",   remarks: "最后更新日期 ")  {constraints(nullable:"false")}  

        }

        addUniqueConstraint(columnNames:"rule_id,level_code,level_value",tableName:"hpfm_code_rule_dist",constraintName: "hpfm_code_rule_dist_u1")
    }
    changeSet(author: "hzero@hand-china.com", id: "2020-06-11-hpfm_code_rule_dist") {
        addColumn(tableName: 'hpfm_code_rule_dist') {
            column(name: "tenant_id", type: "bigint", defaultValue: "0", remarks: "租户ID,hpfm_tenant.tenant_id") {
                constraints(nullable: "false")
            }
        }
    }
}