package script.db

databaseChangeLog(logicalFilePath: 'script/db/hadm_url_rule_user.groovy') {
    changeSet(author: "hzero@hand-china.com", id: "2020-05-25-hadm_url_rule_user") {
        def weight = 1
        if(helper.isSqlServer()){
            weight = 2
        } else if(helper.isOracle()){
            weight = 3
        }
        if(helper.dbType().isSupportSequence()){
            createSequence(sequenceName: 'hadm_url_rule_user_s', startValue:"1")
        }
        createTable(tableName: "hadm_url_rule_user", remarks: "URL动态映射用户配置") {
            column(name: "rule_user_id", type: "bigint", autoIncrement: true ,   remarks: "表ID，主键，供其他表做外键")  {constraints(primaryKey: true)}
            column(name: "url_rule_id", type: "bigint",  remarks: "hadm_url_rule.url_rule_id")  {constraints(nullable:"false")}
            column(name: "source_user_id", type: "bigint",  remarks: "来源用户ID")  {constraints(nullable:"false")}
            column(name: "object_version_number", type: "bigint",   defaultValue:"1",   remarks: "行版本号，用来处理锁")  {constraints(nullable:"false")}
            column(name: "creation_date", type: "datetime",   defaultValueComputed:"CURRENT_TIMESTAMP",   remarks: "")  {constraints(nullable:"false")}
            column(name: "created_by", type: "bigint",   defaultValue:"-1",   remarks: "")  {constraints(nullable:"false")}
            column(name: "last_updated_by", type: "bigint",   defaultValue:"-1",   remarks: "")  {constraints(nullable:"false")}
            column(name: "last_update_date", type: "datetime",   defaultValueComputed:"CURRENT_TIMESTAMP",   remarks: "")  {constraints(nullable:"false")}

        }
        createIndex(tableName: "hadm_url_rule_user", indexName: "hadm_url_rule_user_n1") {
            column(name: "url_rule_id")
        }

        addUniqueConstraint(columnNames:"url_rule_id,source_user_id",tableName:"hadm_url_rule_user",constraintName: "hadm_url_rule_user_u1")
    }
}