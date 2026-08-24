package script.db

databaseChangeLog(logicalFilePath: 'script/db/hpfm_gantt_task.groovy') {
    changeSet(author: "hzero@hand-china.com", id: "2020-04-21-hpfm_gantt_task") {
        def weight = 1
        if(helper.isSqlServer()){
            weight = 2
        } else if(helper.isOracle()){
            weight = 3
        }
        if(helper.dbType().isSupportSequence()){
            createSequence(sequenceName: 'hpfm_gantt_task_s', startValue:"1")
        }
        createTable(tableName: "hpfm_gantt_task", remarks: "甘特图任务管理") {
            column(name: "task_id", type: "bigint", autoIncrement: true ,   remarks: "任务Id")  {constraints(primaryKey: true)} 
            column(name: "gantt_id", type: "bigint",  remarks: "甘特图Id,hpfm_gantt.gantt_id")  {constraints(nullable:"false")}  
            column(name: "task_name", type: "varchar(" + 255 * weight + ")",  remarks: "任务名称")  {constraints(nullable:"false")}  
            column(name: "start_date", type: "datetime",  remarks: "任务开始时间")  {constraints(nullable:"false")}  
            column(name: "duration", type: "int",  remarks: "持续时间，若采用起始时间的模式则无需该内容")   
            column(name: "end_date", type: "datetime",  remarks: "任务结束时间，若采用持续时间的模式则无需该内容")   
            column(name: "parent_id", type: "bigint",  remarks: "父任务Id")   
            column(name: "order_seq", type: "int",  remarks: "排序号")   
            column(name: "progress", type: "decimal(10,2)",  remarks: "任务进度")   
            column(name: "priority", type: "varchar(" + 10 * weight + ")",  remarks: "任务优先级")   
            column(name: "task_render", type: "varchar(" + 30 * weight + ")",  remarks: "任务渲染方式，分拆分任务和普通任务两种")   
            column(name: "tenant_id", type: "bigint",  remarks: "租户Id")  {constraints(nullable:"false")}  
            column(name: "object_version_number", type: "bigint",   defaultValue:"1",   remarks: "行版本号，用来处理锁")  {constraints(nullable:"false")}  
            column(name: "creation_date", type: "datetime",   defaultValueComputed:"CURRENT_TIMESTAMP",   remarks: "")  {constraints(nullable:"false")}  
            column(name: "created_by", type: "bigint",   defaultValue:"-1",   remarks: "")  {constraints(nullable:"false")}  
            column(name: "last_updated_by", type: "bigint",   defaultValue:"-1",   remarks: "")  {constraints(nullable:"false")}  
            column(name: "last_update_date", type: "datetime",   defaultValueComputed:"CURRENT_TIMESTAMP",   remarks: "")  {constraints(nullable:"false")}  

        }
   createIndex(tableName: "hpfm_gantt_task", indexName: "hpfm_gantt_task_n1") {
            column(name: "gantt_id")
            column(name: "tenant_id")
        }

    }
}