package script.db

databaseChangeLog(logicalFilePath: 'script/db/hdsc_data_stream_pipeline.groovy') {
    changeSet(author: "hzero@hand-china.com", id: "2020-09-01-hdsc_data_stream_pipeline") {
        def weight = 1
        if(helper.isSqlServer()){
            weight = 2
        } else if(helper.isOracle()){
            weight = 3
        }
        if(helper.dbType().isSupportSequence()){
            createSequence(sequenceName: 'hdsc_data_stream_pipeline_s', startValue:"1")
        }
        createTable(tableName: "hdsc_data_stream_pipeline", remarks: "数据流") {
            column(name: "pipeline_id", type: "bigint", autoIncrement: true ,   remarks: "表ID，主键，供其他表做外键")  {constraints(primaryKey: true)}
            column(name: "pipeline_code", type: "varchar(" + 60 * weight + ")",  remarks: "数据流管道编码")  {constraints(nullable:"false")}
            column(name: "description", type: "varchar(" + 240 * weight + ")",  remarks: "描述")  {constraints(nullable:"true")}
            column(name: "enabled_flag", type: "tinyint",   defaultValue:"1",   remarks: "是否启用。1启用，0未启用")  {constraints(nullable:"false")}
            column(name: "tenant_id", type: "bigint",  defaultValue: "0", remarks: "租户ID")  {constraints(nullable:"false")}
            column(name: "object_version_number", type: "bigint",   defaultValue:"1",   remarks: "行版本号，用来处理锁")  {constraints(nullable:"false")}
            column(name: "creation_date", type: "datetime",   defaultValueComputed:"CURRENT_TIMESTAMP",   remarks: "")  {constraints(nullable:"false")}  
            column(name: "created_by", type: "bigint",   defaultValue:"-1",   remarks: "")  {constraints(nullable:"false")}  
            column(name: "last_updated_by", type: "bigint",   defaultValue:"-1",   remarks: "")  {constraints(nullable:"false")}  
            column(name: "last_update_date", type: "datetime",   defaultValueComputed:"CURRENT_TIMESTAMP",   remarks: "")  {constraints(nullable:"false")}
        }
        addUniqueConstraint(columnNames:"pipeline_code",tableName:"hdsc_data_stream_pipeline",constraintName: "hdsc_data_stream_pipeline_u1")
    }
}