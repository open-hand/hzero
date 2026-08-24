package script.db

databaseChangeLog(logicalFilePath: 'script/db/hpfm_hr_sync_log.groovy') {
    changeSet(author: "minghui.qiu@hand-china.com", id: "2019-10-15-hpfm_hr_sync_log") {
        def weight = 1
        if(helper.isSqlServer()){
            weight = 2
        } else if(helper.isOracle()){
            weight = 3
        }
        if(helper.dbType().isSupportSequence()){
            createSequence(sequenceName: 'hpfm_hr_sync_log_s', startValue:"1")
        }
        createTable(tableName: "hpfm_hr_sync_log", remarks: "hr基础数据同步外部系统日志") {
            column(name: "sync_log_id", type: "bigint", autoIncrement: true ,   remarks: "主键")  {constraints(primaryKey: true)}
            column(name: "sync_id", type: "bigint",  remarks: "基础数据同步配置hpfm_hr_sync.sync_id")  {constraints(nullable:"false")}  
            column(name: "dept_status_code", type: "tinyint",   defaultValue:"0",   remarks: "部门同步状态")  {constraints(nullable:"false")}
            column(name: "emp_status_code", type: "tinyint",   defaultValue:"0",   remarks: "员工同步状态")  {constraints(nullable:"false")}  
            column(name: "log_content", type: "longtext",  remarks: "同步日志")   
            column(name: "tenant_id", type: "bigint",  remarks: "租户id")  {constraints(nullable:"false")} 
            column(name: "OBJECT_VERSION_NUMBER", type: "bigint",   defaultValue:"1",   remarks: "")   
            column(name: "CREATED_BY", type: "bigint ",   defaultValue:"0",   remarks: "")
            column(name: "CREATION_DATE", type: "datetime",   defaultValueComputed:"CURRENT_TIMESTAMP",   remarks: "")   
            column(name: "LAST_UPDATED_BY", type: "bigint",   defaultValue:"0",   remarks: "")
            column(name: "LAST_UPDATE_DATE", type: "datetime",   defaultValueComputed:"CURRENT_TIMESTAMP",   remarks: "")   

        }

        createIndex(tableName: "hpfm_hr_sync_log", indexName: "hpfm_hr_sync_log_n1") {
            column(name: "sync_id")
            column(name: "tenant_id")
        }

    }

    changeSet(author: "fanghan.liu@hand-china.com", id: "2020-04-20-hpfm_hr_sync_log") {
        def weight = 1
        if(helper.isSqlServer()){
            weight = 2
        } else if(helper.isOracle()){
            weight = 3
        }
        addColumn(tableName: 'hpfm_hr_sync_log') {
            column(name: "sync_direction", type: "varchar(" + 30 * weight + ")", defaultValue:"P", remarks: "同步方向，值集HPFM.HR_SYNC.DIRECTION")
        }
    }
}