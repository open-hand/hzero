package script.db

databaseChangeLog(logicalFilePath: 'script/db/hitf_interface_monitor.groovy') {
    changeSet(author: "xingxing.wu@hand-china.com ", id: "2019-06-27-hitf_interface_monitor") {
        def weight = 1
        if (helper.isSqlServer()) {
            weight = 2
        } else if (helper.isOracle()) {
            weight = 3
        }
        if (helper.dbType().isSupportSequence()) {
            createSequence(sequenceName: 'hitf_interface_monitor_s', startValue: "1")
        }
        createTable(tableName: "hitf_interface_monitor", remarks: "接口运维配置") {
            column(name: "interface_monitor_id", type: "bigint", autoIncrement: true ,   remarks: "表ID，主键")  {constraints(primaryKey: true)} 
            column(name: "interface_id", type: "bigint",  remarks: "接口ID")  {constraints(nullable:"false")}  
            column(name: "tenant_id", type: "bigint",  remarks: "租户ID")  {constraints(nullable:"false")}  
            column(name: "invoke_details_flag", type: "tinyint",   defaultValue:"0",   remarks: "是否记录调用详情")  {constraints(nullable:"false")}  
            column(name: "invoke_statistics_flag", type: "tinyint",   defaultValue:"0",   remarks: "是否启用统计调用次数，需排除健康检查")  {constraints(nullable:"false")}  
            column(name: "statistics_level", type: "varchar(" + 30 * weight + ")",  remarks: "统计维度，代码HITF.STATISTICS_LEVEL")   
            column(name: "statistics_period", type: "varchar(" + 30 * weight + ")",  remarks: "周期，代码HITF.STATISTICS_PERIOD")   
            column(name: "statistics_threshold", type: "int",  remarks: "统计阈值")   
            column(name: "statistics_grace", type: "int",  remarks: "宽免次数")   
            column(name: "statistics_exceed_action", type: "varchar(" + 30 * weight + ")",  remarks: "超阈值措施，代码HITF.EXCEED_THRESHOLD_ACTION")   
            column(name: "statistics_warning_before", type: "int",   defaultValue:"1",   remarks: "提前多少次预警")   
            column(name: "statistics_warning_sms_flag", type: "tinyint",   defaultValue:"1",   remarks: "短信预警")  {constraints(nullable:"false")}  
            column(name: "statistics_warning_email_flag", type: "tinyint",   defaultValue:"0",   remarks: "邮件预警")  {constraints(nullable:"false")}  
            column(name: "health_check_flag", type: "tinyint",   defaultValue:"0",   remarks: "是否启用健康检查")  {constraints(nullable:"false")}  
            column(name: "check_usecase_id", type: "bigint",  remarks: "健康检查所用用例ID")   
            column(name: "check_round_robin", type: "int",  remarks: "健康检查轮训周期（秒）")   
            column(name: "check_period", type: "int",  remarks: "健康检查统计周期（秒）")   
            column(name: "check_threshold", type: "int",  remarks: "健康检查异常阈值")   
            column(name: "check_warning_sms_flag", type: "tinyint",   defaultValue:"1",   remarks: "短信预警")  {constraints(nullable:"false")}  
            column(name: "check_warning_email_flag", type: "tinyint",   defaultValue:"0",   remarks: "邮件预警")  {constraints(nullable:"false")}  
            column(name: "check_warning_msg_tpl_code", type: "varchar(" + 80 * weight + ")",  remarks: "预警消息模板代码")   
            column(name: "check_warning_user_id", type: "bigint",  remarks: "预警目标用户")   
            column(name: "object_version_number", type: "bigint",   defaultValue:"1",   remarks: "行版本号，用来处理锁")  {constraints(nullable:"false")}  
            column(name: "creation_date", type: "datetime",   defaultValueComputed:"CURRENT_TIMESTAMP",   remarks: "")  {constraints(nullable:"false")}  
            column(name: "created_by", type: "bigint",   defaultValue:"-1",   remarks: "")  {constraints(nullable:"false")}  
            column(name: "last_updated_by", type: "bigint",   defaultValue:"-1",   remarks: "")  {constraints(nullable:"false")}  
            column(name: "last_update_date", type: "datetime",   defaultValueComputed:"CURRENT_TIMESTAMP",   remarks: "")  {constraints(nullable:"false")}  

        }

        addUniqueConstraint(columnNames: "interface_id,tenant_id", tableName: "hitf_interface_monitor", constraintName: "hitf_interface_monitor_u1")
    }

    changeSet(author: "aaron.yi", id: "2020-04-23-hitf_interface_monitor-modify-sms_flag-drop") {
        dropColumn(tableName: 'hitf_interface_monitor') {
            column(name: "check_warning_sms_flag", type: "tinyint")
        }
    }

    changeSet(author: "aaron.yi", id: "2020-04-23-hitf_interface_monitor-modify-sms_flag-add") {
        addColumn(tableName: 'hitf_interface_monitor') {
            column(name: "check_warning_sms_flag", type: "tinyint", defaultValue: "1", remarks: "短信预警")
        }
    }

    changeSet(author: "aaron.yi", id: "2020-04-23-hitf_interface_monitor-modify-email_flag-drop") {
        dropColumn(tableName: 'hitf_interface_monitor') {
            column(name: "check_warning_email_flag", type: "tinyint")
        }
    }

    changeSet(author: "aaron.yi", id: "2020-04-23-hitf_interface_monitor-modify-email_flag-add") {
        addColumn(tableName: 'hitf_interface_monitor') {
            column(name: "check_warning_email_flag", type: "tinyint", defaultValue: "0", remarks: "邮件预警")
        }
    }
    
    changeSet(author: "he.chen@hand-china.com", id: "2020-08-25-hitf_interface_monitor-add-column-alert_code") {
        def weight = 1
        if (helper.isSqlServer()) {
            weight = 2
        } else if (helper.isOracle()) {
            weight = 3
        }
        addColumn(tableName: 'hitf_interface_monitor') {
            column(name: "alert_code", type: "varchar(" + 120 * weight + ")", remarks: "告警代码")
        }
    }
}