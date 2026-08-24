package script.db

databaseChangeLog(logicalFilePath: 'script/db/hadm_url_rule.groovy') {
    changeSet(author: "hzero@hand-china.com", id: "2020-05-25-hadm_url_rule") {
        def weight = 1
        if(helper.isSqlServer()){
            weight = 2
        } else if(helper.isOracle()){
            weight = 3
        }
        if(helper.dbType().isSupportSequence()){
            createSequence(sequenceName: 'hadm_url_rule_s', startValue:"1")
        }
        createTable(tableName: "hadm_url_rule", remarks: "URL动态映射配置") {
            column(name: "url_rule_id", type: "bigint", autoIncrement: true ,   remarks: "表ID，主键，供其他表做外键")  {constraints(primaryKey: true)}
            column(name: "url_rule_code", type: "varchar(" + 60 * weight + ")",  remarks: "URL映射配置编码")  {constraints(nullable:"false")}
            column(name: "description", type: "varchar(" + 240 * weight + ")",  remarks: "配置描述")
            column(name: "source_service_code", type: "varchar(" + 60 * weight + ")",  remarks: "来源服务(hadm_service_route.service_code)")
            column(name: "source_url", type: "varchar(" + 240 * weight + ")",  remarks: "来源URL")
            column(name: "target_service_code", type: "varchar(" + 60 * weight + ")",  remarks: "目标服务(hadm_service_route.service_code)")
            column(name: "target_url", type: "varchar(" + 240 * weight + ")",  remarks: "目标URL")
            column(name: "source_tenant_id", type: "bigint",  remarks: "来源租户ID")
            column(name: "enabled_flag", type: "tinyint",   defaultValue:"0",   remarks: "启用标识")  {constraints(nullable:"false")}
            column(name: "object_version_number", type: "bigint",   defaultValue:"1",   remarks: "行版本号，用来处理锁")  {constraints(nullable:"false")}
            column(name: "creation_date", type: "datetime",   defaultValueComputed:"CURRENT_TIMESTAMP",   remarks: "")  {constraints(nullable:"false")}
            column(name: "created_by", type: "bigint",   defaultValue:"-1",   remarks: "")  {constraints(nullable:"false")}
            column(name: "last_updated_by", type: "bigint",   defaultValue:"-1",   remarks: "")  {constraints(nullable:"false")}
            column(name: "last_update_date", type: "datetime",   defaultValueComputed:"CURRENT_TIMESTAMP",   remarks: "")  {constraints(nullable:"false")}

        }

        addUniqueConstraint(columnNames:"source_service_code,source_url,source_tenant_id",tableName:"hadm_url_rule",constraintName: "hadm_url_rule_u1")
        addUniqueConstraint(columnNames:"url_rule_code",tableName:"hadm_url_rule",constraintName: "hadm_url_rule_u2")
    }
}