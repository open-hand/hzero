package script.db

databaseChangeLog(logicalFilePath: 'script/db/hadm_api_limit.groovy') {
    changeSet(author: "hzero@hand-china.com", id: "2019-12-30-hadm_api_limit") {
        def weight = 1
        if(helper.isSqlServer()){
            weight = 2
        } else if(helper.isOracle()){
            weight = 3
        }
        if(helper.dbType().isSupportSequence()){
            createSequence(sequenceName: 'hadm_api_limit_s', startValue:"1")
        }
        createTable(tableName: "hadm_api_limit", remarks: "api限制规则") {
            column(name: "api_limit_id", type: "bigint", autoIncrement: true ,   remarks: "表ID，主键，供其他表做外键")  {constraints(primaryKey: true)}
            column(name: "monitor_rule_id", type: "bigint",  remarks: "api埋点ID")  {constraints(nullable:"false")}
            column(name: "list_mode", type: "varchar(" + 30 * weight + ")",  remarks: "名单类型，BLACK黑名单/WHITE白名单")  {constraints(nullable:"true")}
            column(name: "value_list", type: "longtext",  remarks: "值名单，用逗号分割")  {constraints(nullable:"true")}
            column(name: "blacklist_threshold", type: "int", defaultValue: "2147483647", remarks: "黑名单阈值，单位时间窗口请求量超过该值后自动加入黑名单") {constraints(nullable:"true")}
            column(name: "enabled_flag", type: "tinyint",   defaultValue:"1",   remarks: "是否启用。1启用，0未启用")  {constraints(nullable:"false")}
            column(name: "object_version_number", type: "bigint",   defaultValue:"1",   remarks: "行版本号，用来处理锁")  {constraints(nullable:"false")}
            column(name: "creation_date", type: "datetime",   defaultValueComputed:"CURRENT_TIMESTAMP",   remarks: "")  {constraints(nullable:"false")}  
            column(name: "created_by", type: "bigint",   defaultValue:"-1",   remarks: "")  {constraints(nullable:"false")}  
            column(name: "last_updated_by", type: "bigint",   defaultValue:"-1",   remarks: "")  {constraints(nullable:"false")}  
            column(name: "last_update_date", type: "datetime",   defaultValueComputed:"CURRENT_TIMESTAMP",   remarks: "")  {constraints(nullable:"false")}  

        }

        addUniqueConstraint(columnNames:"monitor_rule_id",tableName:"hadm_api_limit",constraintName: "hadm_api_limit_u1")
    }
}