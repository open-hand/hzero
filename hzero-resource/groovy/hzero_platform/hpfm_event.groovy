package script.db

databaseChangeLog(logicalFilePath: 'script/db/hpfm_event.groovy') {
    changeSet(author: "hzero@hand-china.com", id: "2019-03-01-hpfm_event") {
        def weight = 1
        if(helper.isSqlServer()){
            weight = 2
        } else if(helper.isOracle()){
            weight = 3
        }
        if(helper.dbType().isSupportSequence()){
            createSequence(sequenceName: 'hpfm_event_s', startValue:"1")
        }
        createTable(tableName: "hpfm_event", remarks: "事件表") {
            column(name: "event_id", type: "bigint", autoIncrement: true ,   remarks: "主键ID ")  {constraints(primaryKey: true)} 
            column(name: "event_code", type: "varchar(" + 32 * weight + ")",  remarks: "事件编码")  {constraints(nullable:"false")}  
            column(name: "enabled_flag", type: "tinyint",   defaultValue:"1",   remarks: "是否启用，1：启用；0禁用；默认1；")  {constraints(nullable:"false")}  
            column(name: "event_description", type: "varchar(" + 255 * weight + ")",  remarks: "说明")  {constraints(nullable:"false")}  
            column(name: "object_version_number", type: "bigint",   defaultValue:"1",   remarks: "行版本号，用来处理锁")  {constraints(nullable:"false")}  
            column(name: "creation_date", type: "datetime",   defaultValueComputed:"CURRENT_TIMESTAMP",   remarks: "")  {constraints(nullable:"false")}  
            column(name: "created_by", type: "bigint",   defaultValue:"0",   remarks: "")  {constraints(nullable:"false")}  
            column(name: "last_update_date", type: "datetime",   defaultValueComputed:"CURRENT_TIMESTAMP",   remarks: "")  {constraints(nullable:"false")}  
            column(name: "last_updated_by", type: "bigint",   defaultValue:"0",   remarks: "")  {constraints(nullable:"false")}  

        }

        addUniqueConstraint(columnNames:"event_code",tableName:"hpfm_event",constraintName: "hpfm_event_u1")
    }
    changeSet(author: "xiaoyu.zhao@hand-china.com", id: "2019-05-31-hpfm_event") {
        addColumn(tableName: 'hpfm_event') {
            column(name: "tenant_id", type: "bigint", defaultValue: "0", remarks: "租户ID,hpfm_tenant.tenant_id") {
                constraints(nullable: "false")
            }
        }
        dropUniqueConstraint(tableName: "hpfm_event", constraintName: "hpfm_event_u1")
        addUniqueConstraint(columnNames: "event_code,tenant_id", tableName: "hpfm_event", constraintName: "hpfm_event_u1")
    }
}