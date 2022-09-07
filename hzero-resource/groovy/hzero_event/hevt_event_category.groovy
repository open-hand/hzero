package script.db

databaseChangeLog(logicalFilePath: 'script/db/hevt_event_category.groovy') {
    changeSet(author: "jian.zhang02@hand-china.com", id: "2020-06-30-hevt_event_category") {
        def weight = 1
        if(helper.isSqlServer()){
            weight = 2
        } else if(helper.isOracle()){
            weight = 3
        }
        if(helper.dbType().isSupportSequence()){
            createSequence(sequenceName: 'hevt_event_category_s', startValue:"1")
        }
        createTable(tableName: "hevt_event_category", remarks: "事件类型") {
            column(name: "tenant_id", type: "bigint",  remarks: "租户id")  {constraints(nullable:"false")}  
            column(name: "category_code", type: "varchar(" + 30 * weight + ")",  remarks: "事件类别 eg. PO/ASN/TRX")  {constraints(nullable:"false")}  
            column(name: "category_name", type: "varchar(" + 360 * weight + ")",  remarks: "事件类别描述")   
            column(name: "level_code", type: "varchar(" + 30 * weight + ")",  remarks: "层级，HEVT.EVENT_LEVEL_CODE,GLOBAL/TENANT")  {constraints(nullable:"false")}
            column(name: "enabled_flag", type: "tinyint",   defaultValue:"0",   remarks: "启用标识")  {constraints(nullable:"false")}  
            column(name: "object_version_number", type: "bigint",   defaultValue:"1",   remarks: "行版本号，用来处理锁")  {constraints(nullable:"false")}  
            column(name: "creation_date", type: "datetime",   defaultValueComputed:"CURRENT_TIMESTAMP",   remarks: "")  {constraints(nullable:"false")}  
            column(name: "created_by", type: "bigint",   defaultValue:"-1",   remarks: "")  {constraints(nullable:"false")}  
            column(name: "last_updated_by", type: "bigint",   defaultValue:"-1",   remarks: "")  {constraints(nullable:"false")}  
            column(name: "last_update_date", type: "datetime",   defaultValueComputed:"CURRENT_TIMESTAMP",   remarks: "")  {constraints(nullable:"false")}  
            column(name: "category_id", type: "bigint", autoIncrement: true ,   remarks: "")  {constraints(primaryKey: true)} 

        }

        addUniqueConstraint(columnNames:"category_code,tenant_id",tableName:"hevt_event_category",constraintName: "opadm_event_category_u1")
    }
}