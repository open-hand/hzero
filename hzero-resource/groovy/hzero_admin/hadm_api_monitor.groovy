package script.db

databaseChangeLog(logicalFilePath: 'script/db/hadm_api_monitor.groovy') {
    changeSet(author: "hzero@hand-china.com", id: "2019-12-30-hadm_api_monitor") {
        def weight = 1
        if(helper.isSqlServer()){
            weight = 2
        } else if(helper.isOracle()){
            weight = 3
        }
        if(helper.dbType().isSupportSequence()){
            createSequence(sequenceName: 'hadm_api_monitor_s', startValue:"1")
        }
        createTable(tableName: "hadm_api_monitor", remarks: "api监控") {
            column(name: "api_monitor_id", type: "bigint", autoIncrement: true ,   remarks: "表ID，主键，供其他表做外键")  {constraints(primaryKey: true)}
            column(name: "monitor_rule_id", type: "bigint",  remarks: "api埋点ID")  {constraints(nullable:"false")}
            column(name: "monitor_url", type: "varchar(" + 60 * weight + ")",  remarks: "监控url")  {constraints(nullable:"false")}
            column(name: "monitor_key", type: "varchar(" + 60 * weight + ")",  remarks: "监控key，目前指请求源ip地址")  {constraints(nullable:"false")}
            column(name: "max_statistics", type: "int",  remarks: "单位时间窗口的最大请求数"){constraints(nullable:"false")}
            column(name: "min_statistics", type: "int",  remarks: "单位时间窗口的最小请求数"){constraints(nullable:"false")}
            column(name: "sum_statistics", type: "int",  remarks: "总请求数"){constraints(nullable:"false")}
            column(name: "avg_statistics", type: "int",  remarks: "单位时间窗口的平均请求数"){constraints(nullable:"false")}
            column(name: "sum_failed_statistics", type: "int",  remarks: "总失败请求数"){constraints(nullable:"false")}
            column(name: "avg_failed_statistics", type: "int",  remarks: "单位时间窗口的平均失败请求数"){constraints(nullable:"false")}
            column(name: "start_date", type: "datetime",  remarks: "开始时间"){constraints(nullable:"false")}
            column(name: "end_date", type: "datetime",  remarks: "结束时间"){constraints(nullable:"false")}
            column(name: "object_version_number", type: "bigint",   defaultValue:"1",   remarks: "行版本号，用来处理锁")  {constraints(nullable:"false")}
            column(name: "creation_date", type: "datetime",   defaultValueComputed:"CURRENT_TIMESTAMP",   remarks: "")  {constraints(nullable:"false")}  
            column(name: "created_by", type: "bigint",   defaultValue:"-1",   remarks: "")  {constraints(nullable:"false")}  
            column(name: "last_updated_by", type: "bigint",   defaultValue:"-1",   remarks: "")  {constraints(nullable:"false")}  
            column(name: "last_update_date", type: "datetime",   defaultValueComputed:"CURRENT_TIMESTAMP",   remarks: "")  {constraints(nullable:"false")}  

        }

        addUniqueConstraint(columnNames:"monitor_rule_id,monitor_url,monitor_key",tableName:"hadm_api_monitor",constraintName: "hadm_api_monitor_u1")
    }

    changeSet(author: "hzero@hand-china.com", id: "2020-06-17-hadm_api_monitor"){
        def weight = 1
        if(helper.isSqlServer()){
            weight = 2
        } else if(helper.isOracle()){
            weight = 3
        }
        modifyDataType(tableName: "hadm_api_monitor", columnName: 'monitor_url', newDataType: "varchar(" + 240 * weight + ")")
        modifyDataType(tableName: "hadm_api_monitor", columnName: 'monitor_key', newDataType: "varchar(" + 120 * weight + ")")
    }
}