package script.db

databaseChangeLog(logicalFilePath: 'script/db/hevt_event_message.groovy') {
    changeSet(author: "jian.zhang02@hand-china.com", id: "2020-06-30-hevt_event_message") {
        def weight = 1
        if(helper.isSqlServer()){
            weight = 2
        } else if(helper.isOracle()){
            weight = 3
        }
        if(helper.dbType().isSupportSequence()){
            createSequence(sequenceName: 'hevt_event_message_s', startValue:"1")
        }
        createTable(tableName: "hevt_event_message", remarks: "事件消息") {
            column(name: "event_message_id", type: "bigint", autoIncrement: true ,   remarks: "")  {constraints(primaryKey: true)} 
            column(name: "tenant_id", type: "bigint",  remarks: "租户id")  {constraints(nullable:"false")}  
            column(name: "event_code", type: "varchar(" + 30 * weight + ")",  remarks: "事件代码")  {constraints(nullable:"false")}  
            column(name: "action", type: "varchar(" + 30 * weight + ")",  remarks: "动作")   
            column(name: "data", type: "longtext",  remarks: "数据")   
            column(name: "category", type: "varchar(" + 30 * weight + ")",  remarks: "事件类别 eg. PO/ASN/TRX")  {constraints(nullable:"false")}  
            column(name: "object_version_number", type: "bigint",   defaultValue:"1",   remarks: "行版本号，用来处理锁")  {constraints(nullable:"false")}  
            column(name: "creation_date", type: "datetime",   defaultValueComputed:"CURRENT_TIMESTAMP",   remarks: "")  {constraints(nullable:"false")}  
            column(name: "created_by", type: "bigint",   defaultValue:"-1",   remarks: "")  {constraints(nullable:"false")}  
            column(name: "last_updated_by", type: "bigint",   defaultValue:"-1",   remarks: "")  {constraints(nullable:"false")}  
            column(name: "last_update_date", type: "datetime",   defaultValueComputed:"CURRENT_TIMESTAMP",   remarks: "")  {constraints(nullable:"false")}  
            column(name: "source_message_id", type: "bigint",   defaultValue:"-1",   remarks: "来源消息ID，默认0为自身，消费类型消息时为相应发送消息的ID")  {constraints(nullable:"false")}  
            column(name: "process_type", type: "varchar(" + 30 * weight + ")",   defaultValue:"PRODUCE",   remarks: "处理类型，PRODUCE-发送，CONSUME-消费")  {constraints(nullable:"false")}  
            column(name: "process_host", type: "varchar(" + 80 * weight + ")",   defaultValue:"N/A",   remarks: "处理实例主机")  {constraints(nullable:"false")}  
            column(name: "process_status", type: "varchar(" + 30 * weight + ")",  remarks: "处理状态")   
            column(name: "process_time", type: "datetime",  remarks: "处理时间")   
            column(name: "process_remark", type: "varchar(" + 255 * weight + ")",  remarks: "处理消息")   
            column(name: "remark", type: "longtext",  remarks: "备注/原因/记录等")   

        }
   createIndex(tableName: "hevt_event_message", indexName: "opadm_event_message_n1") {
            column(name: "event_code")
            column(name: "tenant_id")
        }
   createIndex(tableName: "hevt_event_message", indexName: "opadm_event_message_n2") {
            column(name: "source_message_id")
        }

    }
}