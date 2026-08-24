package script.db

databaseChangeLog(logicalFilePath: 'script/db/hpfm_tenant_config.groovy') {
    changeSet(author: "hzero@hand-china.com", id: "2020-05-28-hpfm_tenant_config") {
        def weight = 1
        if(helper.isSqlServer()){
            weight = 2
        } else if(helper.isOracle()){
            weight = 3
        }
        if(helper.dbType().isSupportSequence()){
            createSequence(sequenceName: 'hpfm_tenant_config_s', startValue:"1")
        }
        createTable(tableName: "hpfm_tenant_config", remarks: "租户配置表") {
            column(name: "tenant_config_id", type: "bigint", autoIncrement: true ,   remarks: "表ID，主键，供其他表做外键")  {constraints(primaryKey: true)} 
            column(name: "tenant_id", type: "bigint",  remarks: "租户ID：hpfm_tenant.tenant_id")  {constraints(nullable:"false")}  
            column(name: "config_key", type: "varchar(" + 20 * weight + ")",  remarks: "配置键")  {constraints(nullable:"false")}  
            column(name: "config_value", type: "varchar(" + 1204 * weight + ")",  remarks: "配置值")   
            column(name: "object_version_number", type: "bigint",   defaultValue:"1",   remarks: "行版本号，用来处理锁")  {constraints(nullable:"false")}  
            column(name: "creation_date", type: "datetime",   defaultValueComputed:"CURRENT_TIMESTAMP",   remarks: "")  {constraints(nullable:"false")}  
            column(name: "created_by", type: "bigint",   defaultValue:"-1",   remarks: "")  {constraints(nullable:"false")}  
            column(name: "last_updated_by", type: "bigint",   defaultValue:"-1",   remarks: "")  {constraints(nullable:"false")}  
            column(name: "last_update_date", type: "datetime",   defaultValueComputed:"CURRENT_TIMESTAMP",   remarks: "")  {constraints(nullable:"false")}  

        }

        addUniqueConstraint(columnNames:"tenant_id,config_key",tableName:"hpfm_tenant_config",constraintName: "hpfm_tenant_config_u1")
    }
}