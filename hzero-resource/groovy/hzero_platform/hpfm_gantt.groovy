package script.db

databaseChangeLog(logicalFilePath: 'script/db/hpfm_gantt.groovy') {
    changeSet(author: "hzero@hand-china.com", id: "2020-03-19-hpfm_gantt") {
        def weight = 1
        if(helper.isSqlServer()){
            weight = 2
        } else if(helper.isOracle()){
            weight = 3
        }
        if(helper.dbType().isSupportSequence()){
            createSequence(sequenceName: 'hpfm_gantt_s', startValue:"1")
        }
        createTable(tableName: "hpfm_gantt", remarks: "甘特图") {
            column(name: "gantt_id", type: "bigint", autoIncrement: true ,   remarks: "甘特图id")  {constraints(primaryKey: true)}
            column(name: "gantt_code", type: "varchar(" + 30 * weight + ")",  remarks: "甘特图代码")  {constraints(nullable:"false")}
            column(name: "gantt_name", type: "varchar(" + 240 * weight + ")",  remarks: "甘特图名称")  {constraints(nullable:"false")}
            column(name: "remark", type: "longtext",  remarks: "备注")
            column(name: "enabled_flag", type: "tinyint",   defaultValue:"1",   remarks: "是否启用。1启用，0未启用")  {constraints(nullable:"false")}
            column(name: "tenant_id", type: "bigint",   defaultValue:"0",   remarks: "租户id，hpfm_tenant.tenant_id")  {constraints(nullable:"false")}
            column(name: "object_version_number", type: "bigint",   defaultValue:"1",   remarks: "")  {constraints(nullable:"false")}
            column(name: "created_by", type: "bigint",   defaultValue:"-1",   remarks: "")  {constraints(nullable:"false")}  
            column(name: "creation_date", type: "datetime",   defaultValueComputed:"CURRENT_TIMESTAMP",   remarks: "")  {constraints(nullable:"false")}  
            column(name: "last_updated_by", type: "bigint",   defaultValue:"-1",   remarks: "")  {constraints(nullable:"false")}  
            column(name: "last_update_date", type: "datetime",   defaultValueComputed:"CURRENT_TIMESTAMP",   remarks: "")  {constraints(nullable:"false")}  

        }

        addUniqueConstraint(columnNames:"gantt_code,tenant_id",tableName:"hpfm_gantt",constraintName: "hpfm_gantt_u1")
    }
}
