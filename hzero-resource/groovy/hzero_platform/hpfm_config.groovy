package script.db

databaseChangeLog(logicalFilePath: 'script/db/hpfm_config.groovy') {
    changeSet(author: "hzero@hand-china.com", id: "2019-03-01-hpfm_config") {
        def weight = 1
        if(helper.isSqlServer()){
            weight = 2
        } else if(helper.isOracle()){
            weight = 3
        }
        if(helper.dbType().isSupportSequence()){
            createSequence(sequenceName: 'hpfm_config_s', startValue:"1")
        }
        createTable(tableName: "hpfm_config", remarks: "系统配置") {
            column(name: "config_id", type: "bigint", autoIncrement: true ,   remarks: "系统配置id")  {constraints(primaryKey: true)} 
            column(name: "tenant_id", type: "bigint",   defaultValue:"0",   remarks: "租户id，hpfm_tenant.tenant_id")  {constraints(nullable:"false")}  
            column(name: "config_code", type: "varchar(" + 30 * weight + ")",  remarks: "系统配置名")  {constraints(nullable:"false")}  
            column(name: "config_value", type: "varchar(" + 240 * weight + ")",  remarks: "系统配置值")  {constraints(nullable:"false")}  
            column(name: "category", type: "varchar(" + 30 * weight + ")",  remarks: "系统配置类型")  {constraints(nullable:"false")}  
            column(name: "object_version_number", type: "bigint",   defaultValue:"1",   remarks: "")  {constraints(nullable:"false")}  
            column(name: "created_by", type: "bigint",   defaultValue:"-1",   remarks: "")  {constraints(nullable:"false")}  
            column(name: "creation_date", type: "datetime",   defaultValueComputed:"CURRENT_TIMESTAMP",   remarks: "")  {constraints(nullable:"false")}  
            column(name: "last_updated_by", type: "bigint",   defaultValue:"-1",   remarks: "")  {constraints(nullable:"false")}  
            column(name: "last_update_date", type: "datetime",   defaultValueComputed:"CURRENT_TIMESTAMP",   remarks: "")  {constraints(nullable:"false")}  

        }

        addUniqueConstraint(columnNames:"config_code,tenant_id",tableName:"hpfm_config",constraintName: "hpfm_config_u1")
    }
}