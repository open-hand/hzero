package script.db

databaseChangeLog(logicalFilePath: 'script/db/hpfm_permission_rel.groovy') {
    changeSet(author: "hzero@hand-china.com", id: "2019-03-01-hpfm_permission_rel") {
        def weight = 1
        if(helper.isSqlServer()){
            weight = 2
        } else if(helper.isOracle()){
            weight = 3
        }
        if(helper.dbType().isSupportSequence()){
            createSequence(sequenceName: 'hpfm_permission_rel_s', startValue:"1")
        }
        createTable(tableName: "hpfm_permission_rel", remarks: "屏蔽范围规则关系") {
            column(name: "permission_rel_id", type: "bigint", autoIncrement: true ,   remarks: "表ID，主键，供其他表做外键")  {constraints(primaryKey: true)} 
            column(name: "range_id", type: "bigint",  remarks: "范围ID，hpfm_permission_range.range_id")  {constraints(nullable:"false")}  
            column(name: "rule_id", type: "bigint",  remarks: "屏蔽规则id，hpfm_permission_rule.rule_id")  {constraints(nullable:"false")}  
            column(name: "object_version_number", type: "bigint",   defaultValue:"1",   remarks: "行版本号，用来处理锁")  {constraints(nullable:"false")}  
            column(name: "creation_date", type: "datetime",   defaultValueComputed:"CURRENT_TIMESTAMP",   remarks: "")  {constraints(nullable:"false")}  
            column(name: "created_by", type: "bigint",   defaultValue:"-1",   remarks: "")  {constraints(nullable:"false")}  
            column(name: "last_updated_by", type: "bigint",   defaultValue:"-1",   remarks: "")  {constraints(nullable:"false")}  
            column(name: "last_update_date", type: "datetime",   defaultValueComputed:"CURRENT_TIMESTAMP",   remarks: "")  {constraints(nullable:"false")}  

        }

        addUniqueConstraint(columnNames:"range_id,rule_id",tableName:"hpfm_permission_rel",constraintName: "hpfm_permission_rel_u1")
    }

    changeSet(author: "hzero@hand-china.com", id: "2020-05-11-hpfm_permission_rel") {
        addColumn(tableName: 'hpfm_permission_rel') {
            column(name: "editable_flag", type: "tinyint", defaultValue:"1", remarks: "编辑标识")  {constraints(nullable:"false")}
        }
    }
    changeSet(author: "hzero@hand-china.com", id: "2020-06-11-hpfm_permission_rel") {
        addColumn(tableName: 'hpfm_permission_rel') {
            column(name: "tenant_id", type: "bigint", defaultValue: "0", remarks: "租户ID,hpfm_tenant.tenant_id") {
                constraints(nullable: "false")
            }
        }
    }
}