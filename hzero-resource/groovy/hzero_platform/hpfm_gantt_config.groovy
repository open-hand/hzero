package script.db

databaseChangeLog(logicalFilePath: 'script/db/hpfm_gantt_config.groovy') {
    changeSet(author: "hzero@hand-china.com", id: "2020-03-19-hpfm_gantt_config") {
        def weight = 1
        if(helper.isSqlServer()){
            weight = 2
        } else if(helper.isOracle()){
            weight = 3
        }
        if(helper.dbType().isSupportSequence()){
            createSequence(sequenceName: 'hpfm_gantt_config_s', startValue:"1")
        }
        createTable(tableName: "hpfm_gantt_config", remarks: "甘特图") {
            column(name: "gantt_config_id", type: "bigint", autoIncrement: true ,   remarks: "甘特图配置id")  {constraints(primaryKey: true)}
            column(name: "gantt_id", type: "bigint",  remarks: "甘特图id")  {constraints(nullable:"false")}
            column(name: "config_code", type: "varchar(" + 30 * weight + ")",  remarks: "甘特图配置代码")  {constraints(nullable:"false")}
            column(name: "config_value", type: "varchar(" + 240 * weight + ")",  remarks: "甘特图配置值")
            column(name: "tenant_id", type: "bigint",   defaultValue:"0",   remarks: "租户id，hpfm_tenant.tenant_id")  {constraints(nullable:"false")}
            column(name: "object_version_number", type: "bigint",   defaultValue:"1",   remarks: "")  {constraints(nullable:"false")}
            column(name: "created_by", type: "bigint",   defaultValue:"-1",   remarks: "")  {constraints(nullable:"false")}  
            column(name: "creation_date", type: "datetime",   defaultValueComputed:"CURRENT_TIMESTAMP",   remarks: "")  {constraints(nullable:"false")}  
            column(name: "last_updated_by", type: "bigint",   defaultValue:"-1",   remarks: "")  {constraints(nullable:"false")}  
            column(name: "last_update_date", type: "datetime",   defaultValueComputed:"CURRENT_TIMESTAMP",   remarks: "")  {constraints(nullable:"false")}  

        }

        createIndex(tableName: "hpfm_gantt_config", indexName: "hpfm_gantt_config_n1") {
            column(name: "gantt_id")
            column(name: "tenant_id")
        }
        addUniqueConstraint(columnNames:"gantt_id,config_code",tableName:"hpfm_gantt_config",constraintName: "hpfm_gantt_config_u1")

    }
}