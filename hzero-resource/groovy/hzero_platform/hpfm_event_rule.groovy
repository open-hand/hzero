package script.db

databaseChangeLog(logicalFilePath: 'script/db/hpfm_event_rule.groovy') {
    changeSet(author: "hzero@hand-china.com", id: "2019-03-01-hpfm_event_rule") {
        def weight = 1
        if(helper.isSqlServer()){
            weight = 2
        } else if(helper.isOracle()){
            weight = 3
        }
        if(helper.dbType().isSupportSequence()){
            createSequence(sequenceName: 'hpfm_event_rule_s', startValue:"1")
        }
        createTable(tableName: "hpfm_event_rule", remarks: "事件规则表 ") {
            column(name: "event_rule_id", type: "bigint", autoIncrement: true ,   remarks: "主键ID ")  {constraints(primaryKey: true)} 
            column(name: "event_id", type: "bigint",  remarks: "事件ID")  {constraints(nullable:"false")}  
            column(name: "sync_flag", type: "tinyint",   defaultValue:"1",   remarks: "是否同步调用 1：同步；0异步；默认1；")  {constraints(nullable:"false")}  
            column(name: "call_type", type: "char(1)",  remarks: "调用类型 M：方法；A：API；")  {constraints(nullable:"false")}  
            column(name: "bean_name", type: "varchar(" + 240 * weight + ")",  remarks: "事件处理类名称")   
            column(name: "method_name", type: "varchar(" + 240 * weight + ")",  remarks: "事件处理类方法名称")   
            column(name: "api_url", type: "varchar(" + 480 * weight + ")",  remarks: "API地址")   
            column(name: "api_method", type: "varchar(" + 240 * weight + ")",  remarks: "API方法")   
            column(name: "order_seq", type: "int",   defaultValue:"1",   remarks: "调用顺序")  {constraints(nullable:"false")}  
            column(name: "matching_rule", type: "varchar(" + 500 * weight + ")",  remarks: "匹配规则，OGNL表达式")  {constraints(nullable:"false")}  
            column(name: "result_flag", type: "tinyint",   defaultValue:"1",   remarks: "同步调用是否返回结果 1：返回；0：不反悔；默认1；")  {constraints(nullable:"false")}  
            column(name: "enabled_flag", type: "tinyint",   defaultValue:"1",   remarks: "是否启用 1：启用；0禁用；默认1；")  {constraints(nullable:"false")}  
            column(name: "rule_description", type: "varchar(" + 255 * weight + ")",  remarks: "说明")   
            column(name: "object_version_number", type: "bigint",   defaultValue:"1",   remarks: "行版本号，用来处理锁")  {constraints(nullable:"false")}  
            column(name: "creation_date", type: "datetime",   defaultValueComputed:"CURRENT_TIMESTAMP",   remarks: "")  {constraints(nullable:"false")}  
            column(name: "created_by", type: "bigint",   defaultValue:"0",   remarks: "")  {constraints(nullable:"false")}  
            column(name: "last_update_date", type: "datetime",   defaultValueComputed:"CURRENT_TIMESTAMP",   remarks: "")  {constraints(nullable:"false")}  
            column(name: "last_updated_by", type: "bigint",   defaultValue:"0",   remarks: "")  {constraints(nullable:"false")}  

        }
   createIndex(tableName: "hpfm_event_rule", indexName: "hpfm_fnd_event_rule_n1") {
            column(name: "event_id")
        }

    }
    changeSet(author: "hzero@hand-china.com", id: "2020-06-11-hpfm_event_rule") {
        addColumn(tableName: 'hpfm_event_rule') {
            column(name: "tenant_id", type: "bigint", defaultValue: "0", remarks: "租户ID,hpfm_tenant.tenant_id") {
                constraints(nullable: "false")
            }
        }
    }

    changeSet(author: "hzero@hand-china.com", id: "2020-09-04-hpfm_event_rule") {
        def weight = 1
        if(helper.isSqlServer()){
            weight = 2
        } else if(helper.isOracle()){
            weight = 3
        }
        addColumn(tableName: 'hpfm_event_rule') {
            column(name: "server_code", type: "varchar(" + 30 * weight + ")", remarks: "WebHook服务编码，表hmsg_webhook_server.server_code")
        }
        addColumn(tableName: 'hpfm_event_rule') {
            column(name: "message_code", type: "varchar(" + 60 * weight + ")", remarks: "消息模板编码，表hmsg_message_template.template_code")
        }
    }

}