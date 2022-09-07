package script.db

databaseChangeLog(logicalFilePath: 'script/db/hevt_event_source.groovy') {
    changeSet(author: "jian.zhang02@hand-china.com", id: "2020-06-30-hevt_event_source") {
        def weight = 1
        if(helper.isSqlServer()){
            weight = 2
        } else if(helper.isOracle()){
            weight = 3
        }
        if(helper.dbType().isSupportSequence()){
            createSequence(sequenceName: 'hevt_event_source_s', startValue:"1")
        }
        createTable(tableName: "hevt_event_source", remarks: "事件源") {
            column(name: "event_source_id", type: "bigint", autoIncrement: true ,   remarks: "")  {constraints(primaryKey: true)} 
            column(name: "tenant_id", type: "bigint",   defaultValue:"0",   remarks: "租户ID")  {constraints(nullable:"false")}  
            column(name: "event_source_code", type: "varchar(" + 60 * weight + ")",  remarks: "事件源编码")  {constraints(nullable:"false")}  
            column(name: "event_source_name", type: "varchar(" + 60 * weight + ")",  remarks: "事件源名称")  {constraints(nullable:"false")}  
            column(name: "event_source_type", type: "varchar(" + 30 * weight + ")",  remarks: "事件源类型")  {constraints(nullable:"false")}  
            column(name: "service_address", type: "varchar(" + 512 * weight + ")",  remarks: "服务地址")  {constraints(nullable:"false")}  
            column(name: "enabled_flag", type: "tinyint",   defaultValue:"1",   remarks: "是否启用。1启用，0未启用")  {constraints(nullable:"false")}  
            column(name: "object_version_number", type: "bigint",   defaultValue:"1",   remarks: "行版本号，用来处理锁")  {constraints(nullable:"false")}  
            column(name: "creation_date", type: "datetime",   defaultValueComputed:"CURRENT_TIMESTAMP",   remarks: "")  {constraints(nullable:"false")}  
            column(name: "created_by", type: "bigint",   defaultValue:"-1",   remarks: "")  {constraints(nullable:"false")}  
            column(name: "last_updated_by", type: "bigint",   defaultValue:"-1",   remarks: "")  {constraints(nullable:"false")}  
            column(name: "last_update_date", type: "datetime",   defaultValueComputed:"CURRENT_TIMESTAMP",   remarks: "")  {constraints(nullable:"false")}  
            column(name: "username", type: "varchar(" + 255 * weight + ")",  remarks: "用户名")   
            column(name: "password", type: "varchar(" + 255 * weight + ")",  remarks: "密码")   

        }

        addUniqueConstraint(columnNames:"event_source_code,tenant_id",tableName:"hevt_event_source",constraintName: "hevent_event_source_u1")
    }

    changeSet(author: 'jian.zhang02@hand-china.com', id: '2020-09-08-password-rename') {
        def weight = 1
        if(helper.isSqlServer()){
            weight = 2
        } else if(helper.isOracle()){
            weight = 3
        }
        renameColumn(columnDataType: "varchar(" + 255 * weight + ")", newColumnName: "user_password", oldColumnName: "password", remarks: "密码", tableName: 'hevt_event_source')
    }
}