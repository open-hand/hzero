package script.db

databaseChangeLog(logicalFilePath: 'script/db/halt_alert_dataset.groovy') {
    changeSet(author: "xingxing.wu@hand-china.com", id: "2020-06-09-halt_alert_dataset") {
        def weight = 1
        if(helper.isSqlServer()){
            weight = 2
        } else if(helper.isOracle()){
            weight = 3
        }
        if(helper.dbType().isSupportSequence()){
            createSequence(sequenceName: 'halt_alert_dataset_s', startValue:"1")
        }
        createTable(tableName: "halt_alert_dataset", remarks: "告警数据集配置") {
            column(name: "alert_dataset_id", type: "bigint", autoIncrement: true ,   remarks: "表ID，主键")  {constraints(primaryKey: true)} 
            column(name: "alert_source_id", type: "bigint",  remarks: "告警来源ID")  {constraints(nullable:"false")}  
            column(name: "alert_id", type: "bigint",  remarks: "告警ID")  {constraints(nullable:"false")}  
            column(name: "tenant_id", type: "bigint",   defaultValue:"0",   remarks: "租户ID")  {constraints(nullable:"false")}  
            column(name: "job_id", type: "bigint",  remarks: "调度任务ID")   
            column(name: "dataset_code", type: "varchar(" + 60 * weight + ")",  remarks: "数据集代码，hrpt_dataset.dataset_code")  {constraints(nullable:"false")}  
            column(name: "start_time", type: "datetime",  remarks: "开始时间")   
            column(name: "end_time", type: "datetime",  remarks: "结束时间")   
            column(name: "interval_type", type: "varchar(" + 20 * weight + ")",  remarks: "间隔类型")   
            column(name: "interval_number", type: "bigint",   defaultValue:"1",   remarks: "间隔大小")   
            column(name: "interval_hour", type: "bigint",  remarks: "固定间隔-时")   
            column(name: "interval_minute", type: "bigint",  remarks: "固定间隔-分")   
            column(name: "interval_second", type: "bigint",  remarks: "固定间隔-秒")   
            column(name: "status", type: "varchar(" + 20 * weight + ")",   defaultValue:"CREATE",   remarks: "调度任务状态")  {constraints(nullable:"false")}  
            column(name: "error_msg", type: "longtext",  remarks: "启动失败信息")   
            column(name: "object_version_number", type: "bigint",   defaultValue:"1",   remarks: "行版本号，用来处理锁")  {constraints(nullable:"false")}  
            column(name: "creation_date", type: "datetime",   defaultValueComputed:"CURRENT_TIMESTAMP",   remarks: "")  {constraints(nullable:"false")}  
            column(name: "created_by", type: "bigint",   defaultValue:"-1",   remarks: "")  {constraints(nullable:"false")}  
            column(name: "last_updated_by", type: "bigint",   defaultValue:"-1",   remarks: "")  {constraints(nullable:"false")}  
            column(name: "last_update_date", type: "datetime",   defaultValueComputed:"CURRENT_TIMESTAMP",   remarks: "")  {constraints(nullable:"false")}  

        }

        addUniqueConstraint(columnNames:"dataset_code,alert_source_id,alert_id",tableName:"halt_alert_dataset",constraintName: "halt_alert_dataset_u1")
    }
    changeSet(author: "hzero@hand-china.com", id: "2020-07-30-halt_alert_dataset") {
        addColumn(tableName: 'halt_alert_dataset') {
            column(name: "latest_execute_time ", type: "datetime",  remarks: "上次执行时间")
        }
    }
}