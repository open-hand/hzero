package script.db

databaseChangeLog(logicalFilePath: 'script/db/hadm_gw_rate_limit.groovy') {
    changeSet(author: "hzero@hand-china.com", id: "2019-11-01-hadm_gw_rate_limit") {
        def weight = 1
        if(helper.isSqlServer()){
            weight = 2
        } else if(helper.isOracle()){
            weight = 3
        }
        if(helper.dbType().isSupportSequence()){
            createSequence(sequenceName: 'hadm_gw_rate_limit_s', startValue:"1")
        }
        createTable(tableName: "hadm_gw_rate_limit", remarks: "网关限流设置") {
            column(name: "rate_limit_id", type: "bigint", autoIncrement: true ,   remarks: "表ID，主键，供其他表做外键")  {constraints(primaryKey: true)} 
            column(name: "rate_limit_key", type: "varchar(" + 80 * weight + ")",  remarks: "代码")  {constraints(nullable:"false")}  
            column(name: "rate_limit_type", type: "varchar(" + 30 * weight + ")",  remarks: "限流方式，HADM.RATE_LIMIT_TYPE")  {constraints(nullable:"false")}
            column(name: "service_name", type: "varchar(" + 120 * weight + ")",  remarks: "网关微服务名称")  {constraints(nullable:"false")}
            column(name: "service_conf_label", type: "varchar(" + 240 * weight + ")",  remarks: "网关微服务配置标签")
            column(name: "service_conf_profile", type: "varchar(" + 240 * weight + ")",  remarks: "网关微服务配置Profile")
            column(name: "enabled_flag", type: "tinyint",   defaultValue:"1",   remarks: "是否启用。1启用，0未启用")  {constraints(nullable:"false")}  
            column(name: "refresh_status", type: "tinyint",   defaultValue:"-1",   remarks: "刷新状态")   
            column(name: "refresh_message", type: "varchar(" + 360 * weight + ")",  remarks: "刷新消息")   
            column(name: "refresh_time", type: "datetime",  remarks: "最后一次刷新时间")   
            column(name: "remark", type: "longtext",  remarks: "备注说明")   
            column(name: "object_version_number", type: "bigint",   defaultValue:"1",   remarks: "行版本号，用来处理锁")  {constraints(nullable:"false")}  
            column(name: "creation_date", type: "datetime",   defaultValueComputed:"CURRENT_TIMESTAMP",   remarks: "")  {constraints(nullable:"false")}  
            column(name: "created_by", type: "bigint",   defaultValue:"-1",   remarks: "")  {constraints(nullable:"false")}  
            column(name: "last_updated_by", type: "bigint",   defaultValue:"-1",   remarks: "")  {constraints(nullable:"false")}  
            column(name: "last_update_date", type: "datetime",   defaultValueComputed:"CURRENT_TIMESTAMP",   remarks: "")  {constraints(nullable:"false")}
        }
        addUniqueConstraint(columnNames:"rate_limit_key,rate_limit_type,service_name,service_conf_label,service_conf_profile",tableName:"hadm_gw_rate_limit",constraintName: "hadm_gw_rate_limit_u1")
    }
	
    changeSet(author: "hzero@hand-china.com", id: "2019-12-31-hadm_gw_rate_limit") {
        def weight = 1
        if(helper.isSqlServer()){
            weight = 2
        } else if(helper.isOracle()){
            weight = 3
        }

        dropNotNullConstraint(tableName: 'hadm_gw_rate_limit', columnName: 'service_name', columnDataType:"varchar(" + 120 * weight + ")")
    }
}