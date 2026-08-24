package script.db

databaseChangeLog(logicalFilePath: 'script/db/hadm_api_monitor_rule.groovy') {
    changeSet(author: "hzero@hand-china.com", id: "2019-12-30-hadm_api_monitor_rule") {
        def weight = 1
        if(helper.isSqlServer()){
            weight = 2
        } else if(helper.isOracle()){
            weight = 3
        }
        if(helper.dbType().isSupportSequence()){
            createSequence(sequenceName: 'hadm_api_monitor_rule_s', startValue:"1")
        }
        createTable(tableName: "hadm_api_monitor_rule", remarks: "api埋点") {
            column(name: "monitor_rule_id", type: "bigint", autoIncrement: true ,   remarks: "表ID，主键，供其他表做外键")  {constraints(primaryKey: true)}
            column(name: "url_pattern", type: "varchar(" + 60 * weight + ")",  remarks: "匹配规则")  {constraints(nullable:"false")}
            column(name: "time_window_size", type: "int",  remarks: "时间窗口大小（单位秒）")  {constraints(nullable:"false")}
            column(name: "object_version_number", type: "bigint",   defaultValue:"1",   remarks: "行版本号，用来处理锁")  {constraints(nullable:"false")}
            column(name: "creation_date", type: "datetime",   defaultValueComputed:"CURRENT_TIMESTAMP",   remarks: "")  {constraints(nullable:"false")}  
            column(name: "created_by", type: "bigint",   defaultValue:"-1",   remarks: "")  {constraints(nullable:"false")}  
            column(name: "last_updated_by", type: "bigint",   defaultValue:"-1",   remarks: "")  {constraints(nullable:"false")}  
            column(name: "last_update_date", type: "datetime",   defaultValueComputed:"CURRENT_TIMESTAMP",   remarks: "")  {constraints(nullable:"false")}  

        }
        addUniqueConstraint(columnNames:"url_pattern",tableName:"hadm_api_monitor_rule",constraintName: "hadm_api_monitor_rule_u1")
    }
}