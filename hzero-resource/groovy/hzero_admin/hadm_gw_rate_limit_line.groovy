package script.db

databaseChangeLog(logicalFilePath: 'script/db/hadm_gw_rate_limit_line.groovy') {
    changeSet(author: "hzero@hand-china.com", id: "2019-11-01-hadm_gw_rate_limit_line") {
        def weight = 1
        if(helper.isSqlServer()){
            weight = 2
        } else if(helper.isOracle()){
            weight = 3
        }
        if(helper.dbType().isSupportSequence()){
            createSequence(sequenceName: 'hadm_gw_rate_limit_line_s', startValue:"1")
        }
        createTable(tableName: "hadm_gw_rate_limit_line", remarks: "网关限流设置行明细") {
            column(name: "rate_limit_line_id", type: "bigint", autoIncrement: true ,   remarks: "表ID，主键，供其他表做外键")  {constraints(primaryKey: true)} 
            column(name: "rate_limit_id", type: "bigint",  remarks: "网关限流设置ID")  {constraints(nullable:"false")}
            column(name: "service_route_id", type: "bigint",  remarks: "服务路由ID")  {constraints(nullable:"false")}
            column(name: "replenish_rate", type: "int",  remarks: "每秒流量限制值")  {constraints(nullable:"false")}
            column(name: "burst_capacity", type: "int",  remarks: "突发流量限制值")
            column(name: "rate_limit_dimension", type: "varchar(" + 30 * weight + ")",  remarks: "限流维度")
            column(name: "enabled_flag", type: "tinyint",   defaultValue:"1",   remarks: "是否启用。1启用，0未启用")  {constraints(nullable:"false")}
            column(name: "remark", type: "longtext",  remarks: "备注说明")   
            column(name: "object_version_number", type: "bigint",   defaultValue:"1",   remarks: "行版本号，用来处理锁")  {constraints(nullable:"false")}  
            column(name: "creation_date", type: "datetime",   defaultValueComputed:"CURRENT_TIMESTAMP",   remarks: "")  {constraints(nullable:"false")}  
            column(name: "created_by", type: "bigint",   defaultValue:"-1",   remarks: "")  {constraints(nullable:"false")}  
            column(name: "last_updated_by", type: "bigint",   defaultValue:"-1",   remarks: "")  {constraints(nullable:"false")}  
            column(name: "last_update_date", type: "datetime",   defaultValueComputed:"CURRENT_TIMESTAMP",   remarks: "")  {constraints(nullable:"false")}  

        }
        createIndex(tableName: "hadm_gw_rate_limit_line", indexName: "hadm_gw_rate_limit_line_n1") {
            column(name: "rate_limit_id")
        }
        addUniqueConstraint(columnNames:"service_route_id",tableName:"hadm_gw_rate_limit_line",constraintName: "hadm_gw_rate_limit_line_u1")
    }

    changeSet(author: "hzero@hand-china.com", id: "2020-06-02-hadm_gw_rate_limit_line") {
        def weight = 1
        if(helper.isSqlServer()){
            weight = 2
        } else if(helper.isOracle()){
            weight = 3
        }
        modifyDataType(tableName: "hadm_gw_rate_limit_line", columnName: 'rate_limit_dimension', newDataType: "varchar(" + 240 * weight + ")")
    }
}