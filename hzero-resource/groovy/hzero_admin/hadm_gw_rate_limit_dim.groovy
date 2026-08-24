package script.db

databaseChangeLog(logicalFilePath: 'script/db/hadm_gw_rate_limit_dim.groovy') {
    changeSet(author: "hzero@hand-china.com", id: "2020-01-01-hadm_gw_rate_limit_dim") {
        def weight = 1
        if(helper.isSqlServer()){
            weight = 2
        } else if(helper.isOracle()){
            weight = 3
        }
        if(helper.dbType().isSupportSequence()){
            createSequence(sequenceName: 'hadm_gw_rate_limit_dim_s', startValue:"1")
        }
        createTable(tableName: "hadm_gw_rate_limit_dim", remarks: "网关限流设置行明细") {
            column(name: "rate_limit_dim_id", type: "bigint", autoIncrement: true ,   remarks: "表ID，主键，供其他表做外键")  {constraints(primaryKey: true)}
            column(name: "rate_limit_id", type: "bigint",  remarks: "限流配置ID，hadm_gw_rate_limit.rate_limit_id")  {constraints(nullable:"false")}
            column(name: "rate_limit_line_id", type: "bigint",  remarks: "限流配置行ID，hadm_gw_rate_limit_line.rate_limit_line_id")  {constraints(nullable:"false")}
            column(name: "replenish_rate", type: "int",  remarks: "每秒流量限制值")  {constraints(nullable:"false")}
            column(name: "burst_capacity", type: "int",  remarks: "突发流量限制值")
            column(name: "rate_limit_dimension", type: "varchar(" + 240 * weight + ")",  remarks: "维度模版，来源值集:HADM.GATEWAY_RATE_LIMIT_DIMENSION，如user,role")  {constraints(nullable:"false")}
            column(name: "dimension_key", type: "varchar(" + 240 * weight + ")",  remarks: "维度key，与模版对应的key值，如1,2表示user=1、role=2")  {constraints(nullable:"false")}
            column(name: "remark", type: "longtext",  remarks: "备注说明")   
            column(name: "object_version_number", type: "bigint",   defaultValue:"1",   remarks: "行版本号，用来处理锁")  {constraints(nullable:"false")}  
            column(name: "creation_date", type: "datetime",   defaultValueComputed:"CURRENT_TIMESTAMP",   remarks: "")  {constraints(nullable:"false")}  
            column(name: "created_by", type: "bigint",   defaultValue:"-1",   remarks: "")  {constraints(nullable:"false")}  
            column(name: "last_updated_by", type: "bigint",   defaultValue:"-1",   remarks: "")  {constraints(nullable:"false")}  
            column(name: "last_update_date", type: "datetime",   defaultValueComputed:"CURRENT_TIMESTAMP",   remarks: "")  {constraints(nullable:"false")}  

        }
        createIndex(tableName: "hadm_gw_rate_limit_dim", indexName: "hadm_gw_rate_limit_dim_n1") {
            column(name: "rate_limit_id")
            column(name: "rate_limit_line_id")
        }

    }
}