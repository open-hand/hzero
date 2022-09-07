package script.db

databaseChangeLog(logicalFilePath: 'script/db/hpfm_profile.groovy') {
    changeSet(author: "hzero@hand-china.com", id: "2019-03-01-hpfm_profile") {
        def weight = 1
        if(helper.isSqlServer()){
            weight = 2
        } else if(helper.isOracle()){
            weight = 3
        }
        if(helper.dbType().isSupportSequence()){
            createSequence(sequenceName: 'hpfm_profile_s', startValue:"1")
        }
        createTable(tableName: "hpfm_profile", remarks: "参数配置") {
            column(name: "profile_id", type: "bigint", autoIncrement: true ,   remarks: "")  {constraints(primaryKey: true)} 
            column(name: "profile_name", type: "varchar(" + 30 * weight + ")",  remarks: "配置名")  {constraints(nullable:"false")}  
            column(name: "description", type: "varchar(" + 240 * weight + ")",  remarks: "描述")   
            column(name: "profile_level", type: "varchar(" + 30 * weight + ")",  remarks: "应用层级,包括两种(平台级,租户级)")  {constraints(nullable:"false")}  
            column(name: "tenant_id", type: "bigint",   defaultValue:"0",   remarks: "租户id")  {constraints(nullable:"false")}  
            column(name: "object_version_number", type: "bigint",   defaultValue:"1",   remarks: "")   
            column(name: "created_by", type: "bigint",   defaultValue:"0",   remarks: "")   
            column(name: "creation_date", type: "datetime",   defaultValueComputed:"CURRENT_TIMESTAMP",   remarks: "")   
            column(name: "last_updated_by", type: "bigint",   defaultValue:"0",   remarks: "")   
            column(name: "last_update_date", type: "datetime",   defaultValueComputed:"CURRENT_TIMESTAMP",   remarks: "")   

        }

        addUniqueConstraint(columnNames:"profile_name,tenant_id",tableName:"hpfm_profile",constraintName: "hpfm_profile_u1")
    }
}