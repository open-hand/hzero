package script.db

databaseChangeLog(logicalFilePath: 'script/db/hevt_event_config.groovy') {
    changeSet(author: "jian.zhang02@hand-china.com", id: "2020-06-30-hevt_event_config") {
        def weight = 1
        if(helper.isSqlServer()){
            weight = 2
        } else if(helper.isOracle()){
            weight = 3
        }
        if(helper.dbType().isSupportSequence()){
            createSequence(sequenceName: 'hevt_event_config_s', startValue:"1")
        }
        createTable(tableName: "hevt_event_config", remarks: "事件配置") {
            column(name: "event_config_id", type: "bigint", autoIncrement: true ,   remarks: "")  {constraints(primaryKey: true)} 
            column(name: "tenant_id", type: "bigint",   defaultValue:"0",   remarks: "租户ID")  {constraints(nullable:"false")}  
            column(name: "source_type", type: "varchar(" + 30 * weight + ")",  remarks: "来源类型")  {constraints(nullable:"false")}  
            column(name: "source_id", type: "bigint",  remarks: "来源ID")  {constraints(nullable:"false")}  
            column(name: "config_code", type: "varchar(" + 255 * weight + ")",  remarks: "配置编码")  {constraints(nullable:"false")}  
            column(name: "config_value", type: "varchar(" + 255 * weight + ")",  remarks: "配置值")  {constraints(nullable:"false")}  
            column(name: "object_version_number", type: "bigint",   defaultValue:"1",   remarks: "行版本号，用来处理锁")  {constraints(nullable:"false")}  
            column(name: "creation_date", type: "datetime",   defaultValueComputed:"CURRENT_TIMESTAMP",   remarks: "")  {constraints(nullable:"false")}  
            column(name: "created_by", type: "bigint",   defaultValue:"-1",   remarks: "")  {constraints(nullable:"false")}  
            column(name: "last_updated_by", type: "bigint",   defaultValue:"-1",   remarks: "")  {constraints(nullable:"false")}  
            column(name: "last_update_date", type: "datetime",   defaultValueComputed:"CURRENT_TIMESTAMP",   remarks: "")  {constraints(nullable:"false")}  

        }

        addUniqueConstraint(columnNames:"config_code,source_id,source_type,tenant_id",tableName:"hevt_event_config",constraintName: "hevt_event_config_u1")
    }
}