package script.db

databaseChangeLog(logicalFilePath: 'script/db/halt_alert_rule_prometheus.groovy') {
    changeSet(author: "xingxing.wu@hand-china.com", id: "2020-06-09-halt_alert_rule_prometheus") {
        def weight = 1
        if(helper.isSqlServer()){
            weight = 2
        } else if(helper.isOracle()){
            weight = 3
        }
        if(helper.dbType().isSupportSequence()){
            createSequence(sequenceName: 'halt_alert_rule_prometheus_s', startValue:"1")
        }
        createTable(tableName: "halt_alert_rule_prometheus", remarks: "告警触发规则-Prometheus") {
            column(name: "alert_rule_prometheus_id", type: "bigint", autoIncrement: true ,   remarks: "表ID，主键")  {constraints(primaryKey: true)}
            column(name: "alert_source_id", type: "bigint",  remarks: "告警来源ID")  {constraints(nullable:"false")}
            column(name: "alert_id", type: "bigint",  remarks: "告警ID")  {constraints(nullable:"false")}
            column(name: "tenant_id", type: "bigint",   defaultValue:"0",   remarks: "租户ID")  {constraints(nullable:"false")}
            column(name: "for_duration", type: "int",   defaultValue:"0",   remarks: "持续时间间隔，N秒")  {constraints(nullable:"false")}
            column(name: "prom_ql", type: "longtext",  remarks: "PromQL表达式，用于检测触发条件")  {constraints(nullable:"false")}
            column(name: "object_version_number", type: "bigint",   defaultValue:"1",   remarks: "行版本号，用来处理锁")  {constraints(nullable:"false")}
            column(name: "creation_date", type: "datetime",   defaultValueComputed:"CURRENT_TIMESTAMP",   remarks: "")  {constraints(nullable:"false")}
            column(name: "created_by", type: "bigint",   defaultValue:"-1",   remarks: "")  {constraints(nullable:"false")}
            column(name: "last_updated_by", type: "bigint",   defaultValue:"-1",   remarks: "")  {constraints(nullable:"false")}
            column(name: "last_update_date", type: "datetime",   defaultValueComputed:"CURRENT_TIMESTAMP",   remarks: "")  {constraints(nullable:"false")}

        }
        createIndex(tableName: "halt_alert_rule_prometheus", indexName: "halt_alert_rule_prometheus_n1") {
            column(name: "alert_id")
            column(name: "tenant_id")
        }

    }
    changeSet(author: "hzero@hand-china.com", id: "2020-07-30-halt_alert_rule_prometheus") {
        def weight = 1
        if(helper.isSqlServer()){
            weight = 2
        } else if(helper.isOracle()){
            weight = 3
        }
        addColumn(tableName: 'halt_alert_rule_prometheus') {
            column(name: "duration_unit ", type: "varchar(" + 30 * weight + ")", defaultValue: "s", remarks: "持续时间单位，默认为s") {
                constraints(nullable: "false")
            }
        }
    }
    changeSet(author: "xingxing.wu@hand-china.com", id: "2020-10-23-halt_alert_rule_prometheus"){
        dropTable(tableName: "halt_alert_rule_prometheus")
    }
}