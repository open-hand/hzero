package script.db

databaseChangeLog(logicalFilePath: 'script/db/halt_alert_rule_log.groovy') {
    changeSet(author: "xingxing.wu@hand-china.com", id: "2020-06-09-halt_alert_rule_log") {
        def weight = 1
        if(helper.isSqlServer()){
            weight = 2
        } else if(helper.isOracle()){
            weight = 3
        }
        if(helper.dbType().isSupportSequence()){
            createSequence(sequenceName: 'halt_alert_rule_log_s', startValue:"1")
        }
        createTable(tableName: "halt_alert_rule_log", remarks: "告警出发规则-日志") {
            column(name: "alert_rule_log_id", type: "bigint", autoIncrement: true ,   remarks: "表ID，主键")  {constraints(primaryKey: true)} 
            column(name: "alert_source_id", type: "bigint",  remarks: "告警来源ID")  {constraints(nullable:"false")}  
            column(name: "alert_id", type: "bigint",  remarks: "告警ID")  {constraints(nullable:"false")}  
            column(name: "tenant_id", type: "bigint",   defaultValue:"0",   remarks: "租户ID")  {constraints(nullable:"false")}  
            column(name: "log_alert_type", type: "varchar(" + 30 * weight + ")",  remarks: "触发规则类型，HALT.ALERT_LOG_ALERT_TYPE")  {constraints(nullable:"false")}  
            column(name: "timeframe", type: "int",  remarks: "时间窗口分钟")  {constraints(nullable:"false")}  
            column(name: "index_pattern", type: "varchar(" + 255 * weight + ")",  remarks: "索引模式，HALT.ALERT_LOG_INDEX_PATTERN")  {constraints(nullable:"false")}  
            column(name: "timestamp_field", type: "varchar(" + 255 * weight + ")",  remarks: "索引中时间字段，用于排序等")   
            column(name: "timestamp_type", type: "varchar(" + 30 * weight + ")",  remarks: "时间格式化类型，HALT.ALERT_LOG_TIMESTAMP")   
            column(name: "timestamp_format", type: "varchar(" + 80 * weight + ")",  remarks: "自定义时间格式化类型")   
            column(name: "compare_key", type: "varchar(" + 255 * weight + ")",  remarks: "索引中比较字段名，HALT.ALERT_LOG_INDEX_FIELD")   
            column(name: "compare_value_list", type: "varchar(" + 255 * weight + ")",  remarks: "黑白名单列表，逗号分隔的字符串")   
            column(name: "ignore_null", type: "tinyint",   defaultValue:"1",   remarks: "是否忽略无比较字段的情况")  {constraints(nullable:"false")}  
            column(name: "num_events", type: "int",  remarks: "超过频次")   
            column(name: "spike_height", type: "int",  remarks: "环比上一个时间窗口差异倍数")   
            column(name: "spike_type", type: "varchar(" + 30 * weight + ")",  remarks: "环比类型，HALT.ALERT_LOG_SPIKE_TYPE")   
            column(name: "threshold_ref", type: "int",  remarks: "上一个时间窗口至少出现N次")   
            column(name: "threshold_cur", type: "int",  remarks: "本次时间窗口至少出现N次")   
            column(name: "filter_dsl", type: "longtext",  remarks: "过滤匹配条件，ElasticSearch DSL表达式")   
            column(name: "object_version_number", type: "bigint",   defaultValue:"1",   remarks: "行版本号，用来处理锁")  {constraints(nullable:"false")}  
            column(name: "creation_date", type: "datetime",   defaultValueComputed:"CURRENT_TIMESTAMP",   remarks: "")  {constraints(nullable:"false")}  
            column(name: "created_by", type: "bigint",   defaultValue:"-1",   remarks: "")  {constraints(nullable:"false")}  
            column(name: "last_updated_by", type: "bigint",   defaultValue:"-1",   remarks: "")  {constraints(nullable:"false")}  
            column(name: "last_update_date", type: "datetime",   defaultValueComputed:"CURRENT_TIMESTAMP",   remarks: "")  {constraints(nullable:"false")}  

        }

        addUniqueConstraint(columnNames:"alert_id,tenant_id",tableName:"halt_alert_rule_log",constraintName: "halt_alert_rule_log_u1")
    }
    changeSet(author: "xingxing.wu@hand-china.com", id: "2020-10-23-halt_alert_rule_log"){
        dropTable(tableName: "halt_alert_rule_log")
    }
}