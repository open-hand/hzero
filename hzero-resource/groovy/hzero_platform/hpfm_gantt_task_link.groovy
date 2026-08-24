package script.db

databaseChangeLog(logicalFilePath: 'script/db/hpfm_gantt_task_link.groovy') {
    changeSet(author: "hzero@hand-china.com", id: "2020-04-21-hpfm_gantt_task_link") {
        def weight = 1
        if(helper.isSqlServer()){
            weight = 2
        } else if(helper.isOracle()){
            weight = 3
        }
        if(helper.dbType().isSupportSequence()){
            createSequence(sequenceName: 'hpfm_gantt_task_link_s', startValue:"1")
        }
        createTable(tableName: "hpfm_gantt_task_link", remarks: "甘特图任务关联") {
            column(name: "task_link_id", type: "bigint", autoIncrement: true ,   remarks: "表ID，主键，供其他表做外键")  {constraints(primaryKey: true)} 
            column(name: "gantt_id", type: "bigint",  remarks: "甘特图Id")  {constraints(nullable:"false")}  
            column(name: "task_from_id", type: "bigint",  remarks: "来源任务Id")  {constraints(nullable:"false")}  
            column(name: "task_target_id", type: "bigint",  remarks: "目标任务Id")  {constraints(nullable:"false")}  
            column(name: "link_type", type: "varchar(" + 1 * weight + ")",  remarks: "任务连接方式")  {constraints(nullable:"false")}  
            column(name: "tenant_id", type: "bigint",  remarks: "租户Id")  {constraints(nullable:"false")}  
            column(name: "object_version_number", type: "bigint",   defaultValue:"1",   remarks: "行版本号，用来处理锁")  {constraints(nullable:"false")}  
            column(name: "creation_date", type: "datetime",   defaultValueComputed:"CURRENT_TIMESTAMP",   remarks: "")  {constraints(nullable:"false")}  
            column(name: "created_by", type: "bigint",   defaultValue:"-1",   remarks: "")  {constraints(nullable:"false")}  
            column(name: "last_updated_by", type: "bigint",   defaultValue:"-1",   remarks: "")  {constraints(nullable:"false")}  
            column(name: "last_update_date", type: "datetime",   defaultValueComputed:"CURRENT_TIMESTAMP",   remarks: "")  {constraints(nullable:"false")}  

        }

        addUniqueConstraint(columnNames:"task_from_id,task_target_id,link_type",tableName:"hpfm_gantt_task_link",constraintName: "hpfm_gantt_task_link_u1")
    }
}