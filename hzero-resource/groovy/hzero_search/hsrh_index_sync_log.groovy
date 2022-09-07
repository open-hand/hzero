package script.db

databaseChangeLog(logicalFilePath: 'script/db/hsrh_index_sync_log.groovy') {
    changeSet(author: "qi.liu02@hand-china.com", id: "2020-02-26-hsrh_index_sync_log") {
        def weight = 1
        if(helper.isSqlServer()){
            weight = 2
        } else if(helper.isOracle()){
            weight = 3
        }
        if(helper.dbType().isSupportSequence()){
            createSequence(sequenceName: 'hsrh_index_sync_log_s', startValue:"1")
        }
        createTable(tableName: "hsrh_index_sync_log", remarks: "同步日志表") {
            column(name: "sync_log_id", type: "bigint", autoIncrement: true ,   remarks: "表ID，主键。全局ID")  {constraints(primaryKey: true)}
            column(name: "sync_conf_id", type: "bigint",  remarks: "关联索引同步配置表hsrh_index_sync_conf.sync_conf_id")  {constraints(nullable:"false")}
            column(name: "sync_status_code", type: "varchar(" + 30 * weight + ")",  remarks: "同步状态，值集HSRH.SYNC_STATUS")  {constraints(nullable:"false")}  
            column(name: "sync_log", type: "longtext",  remarks: "同步日志")   
            column(name: "sync_data_url", type: "varchar(" + 255 * weight + ")",  remarks: "同步数据路径")   
            column(name: "sync_start_time", type: "datetime",  remarks: "同步开始时间")   
            column(name: "sync_end_time", type: "datetime",  remarks: "同步完成时间")   
            column(name: "index_id", type: "bigint",  remarks: "关联版本控制表hsrh_index.index_id")  {constraints(nullable:"false")}
            column(name: "tenant_id", type: "bigint",   defaultValue:"0",   remarks: "租户号")  {constraints(nullable:"false")}
            column(name: "object_version_number", type: "bigint",   defaultValue:"1",   remarks: "行版本号，用来处理锁")  {constraints(nullable:"false")}
            column(name: "creation_date", type: "datetime",   defaultValueComputed:"CURRENT_TIMESTAMP",   remarks: "")  {constraints(nullable:"false")}  
            column(name: "created_by", type: "bigint",   defaultValue:"-1",   remarks: "")  {constraints(nullable:"false")}
            column(name: "last_updated_by", type: "bigint",   defaultValue:"-1",   remarks: "")  {constraints(nullable:"false")}
            column(name: "last_update_date", type: "datetime",   defaultValueComputed:"CURRENT_TIMESTAMP",   remarks: "")  {constraints(nullable:"false")}  

        }
   createIndex(tableName: "hsrh_index_sync_log", indexName: "hsrh_index_sync_log_n1") {
            column(name: "index_id")
            column(name: "tenant_id")
        }
   createIndex(tableName: "hsrh_index_sync_log", indexName: "hsrh_index_sync_log_n2") {
            column(name: "sync_status_code")
            column(name: "tenant_id")
        }

    }
}