package script.db

databaseChangeLog(logicalFilePath: 'script/db/hdsc_pipeline_stream.groovy') {
    changeSet(author: "hzero@hand-china.com", id: "2020-09-01-hdsc_pipeline_stream") {
        def weight = 1
        if(helper.isSqlServer()){
            weight = 2
        } else if(helper.isOracle()){
            weight = 3
        }
        if(helper.dbType().isSupportSequence()){
            createSequence(sequenceName: 'hdsc_pipeline_stream_s', startValue:"1")
        }
        createTable(tableName: "hdsc_pipeline_stream", remarks: "数据流") {
            column(name: "pipeline_stream_id", type: "bigint", autoIncrement: true ,   remarks: "表ID，主键，供其他表做外键")  {constraints(primaryKey: true)}
            column(name: "pipeline_id", type: "bigint",  remarks: "数据流管道ID,关联hdsc_data_stream_pipeline.pipeline_id")  {constraints(nullable:"false")}
            column(name: "stream_id", type: "bigint",  remarks: "数据流ID,关联hdsc_data_stream.stream_id")  {constraints(nullable:"false")}
            column(name: "prev_ps_id", type: "bigint",  remarks: "前置数据流ID")  {constraints(nullable:"true")}
            column(name: "next_ps_id", type: "bigint",  remarks: "后置数据流ID")  {constraints(nullable:"true")}
            column(name: "tenant_id", type: "bigint",  defaultValue: "0",  remarks: "租户ID")  {constraints(nullable:"false")}
            column(name: "object_version_number", type: "bigint",   defaultValue:"1",   remarks: "行版本号，用来处理锁")  {constraints(nullable:"false")}
            column(name: "creation_date", type: "datetime",   defaultValueComputed:"CURRENT_TIMESTAMP",   remarks: "")  {constraints(nullable:"false")}  
            column(name: "created_by", type: "bigint",   defaultValue:"-1",   remarks: "")  {constraints(nullable:"false")}  
            column(name: "last_updated_by", type: "bigint",   defaultValue:"-1",   remarks: "")  {constraints(nullable:"false")}  
            column(name: "last_update_date", type: "datetime",   defaultValueComputed:"CURRENT_TIMESTAMP",   remarks: "")  {constraints(nullable:"false")}
        }
        createIndex(tableName: "hdsc_pipeline_stream", indexName: "hdsc_pipeline_stream_n1") {
            column(name: "pipeline_id")
        }
    }
}