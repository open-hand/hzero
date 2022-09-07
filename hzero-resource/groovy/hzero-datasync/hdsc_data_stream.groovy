package script.db

databaseChangeLog(logicalFilePath: 'script/db/hdsc_data_stream.groovy') {
    changeSet(author: "hzero@hand-china.com", id: "2020-09-01-hdsc_data_stream") {
        def weight = 1
        if(helper.isSqlServer()){
            weight = 2
        } else if(helper.isOracle()){
            weight = 3
        }
        if(helper.dbType().isSupportSequence()){
            createSequence(sequenceName: 'hdsc_data_stream_s', startValue:"1")
        }
        createTable(tableName: "hdsc_data_stream", remarks: "数据流") {
            column(name: "stream_id", type: "bigint", autoIncrement: true ,   remarks: "表ID，主键，供其他表做外键")  {constraints(primaryKey: true)}
            column(name: "stream_code", type: "varchar(" + 60 * weight + ")",  remarks: "数据流编码")  {constraints(nullable:"false")}
            column(name: "stream_purpose_code", type: "varchar(" + 60 * weight + ")",  remarks: "数据流用途编码")  {constraints(nullable:"false")}
            column(name: "stream_type_code", type: "varchar(" + 60 * weight + ")",  remarks: "数据流类型编码")  {constraints(nullable:"false")}
            column(name: "config_value", type: "longtext", remarks: "配置项") {constraints(nullable:"true")}
            column(name: "tenant_id", type: "bigint",  defaultValue: "0", remarks: "租户ID")  {constraints(nullable:"false")}
            column(name: "object_version_number", type: "bigint",   defaultValue:"1",   remarks: "行版本号，用来处理锁")  {constraints(nullable:"false")}
            column(name: "creation_date", type: "datetime",   defaultValueComputed:"CURRENT_TIMESTAMP",   remarks: "")  {constraints(nullable:"false")}  
            column(name: "created_by", type: "bigint",   defaultValue:"-1",   remarks: "")  {constraints(nullable:"false")}  
            column(name: "last_updated_by", type: "bigint",   defaultValue:"-1",   remarks: "")  {constraints(nullable:"false")}  
            column(name: "last_update_date", type: "datetime",   defaultValueComputed:"CURRENT_TIMESTAMP",   remarks: "")  {constraints(nullable:"false")}
        }
        addUniqueConstraint(columnNames:"stream_code",tableName:"hdsc_data_stream",constraintName: "hdsc_data_stream_u1")
    }
}