package script.db
databaseChangeLog(logicalFilePath: 'script/db/halt_alert_event_label.groovy') {
    def weight = 1
    if(helper.isSqlServer()){
    weight = 2
    } else if(helper.isOracle()){
    weight = 3
    }
    changeSet(author: "xingxing.wu@hand-china.com", id: "halt_alert_event_label-2020-11-24-version-4"){
        if(helper.dbType().isSupportSequence()){
            createSequence(sequenceName: 'halt_alert_event_label_s', startValue:"1")
        }
        createTable(tableName: "halt_alert_event_label", remarks: "告警事件标签") {
            column(name: "created_by", type: "bigint(20)",   defaultValue:"-1",   remarks: "")  {constraints(nullable:"false")}  
            column(name: "last_updated_by", type: "bigint(20)",   defaultValue:"-1",   remarks: "")  {constraints(nullable:"false")}  
            column(name: "creation_date", type: "datetime",   defaultValueComputed:"CURRENT_TIMESTAMP",   remarks: "")  {constraints(nullable:"false")}  
            column(name: "last_update_date", type: "datetime",   defaultValueComputed:"CURRENT_TIMESTAMP",   remarks: "")  {constraints(nullable:"false")}  
            column(name: "event_label_id", type: "bigint(20)", autoIncrement: true ,   remarks: "主键")  {constraints(primaryKey: true)} 
            column(name: "label_key", type: "varchar(" + 60* weight + ")",  remarks: "标签")  {constraints(nullable:"false")}  
            column(name: "label_value", type: "varchar(" + 255* weight + ")",  remarks: "标签值")  {constraints(nullable:"false")}  
            column(name: "alert_event_id", type: "bigint(20)",  remarks: "告警事件Id")  {constraints(nullable:"false")}  
            column(name: "tenant_id", type: "bigint(20)",  remarks: "租户")  {constraints(nullable:"false")}
            column(name: "object_version_number", type: "bigint(20)",   defaultValue:"1",   remarks: "行版本号，用来处理锁")  {constraints(nullable:"false")}
        }
       createIndex(tableName: "halt_alert_event_label", indexName: "halt_alert_event_label_n1") {
           column(name: "alert_event_id")
       }
    }
    changeSet(author: "guoqiang.lv@hand-china.com", id: "halt_alert_event_label-2021-01-20") {
        addColumn (tableName: "halt_alert_event_label"){
            column (name: "label_source", type: "varchar(" + 30 * weight + ")", remarks: "标签来源")
        }
    }
}
