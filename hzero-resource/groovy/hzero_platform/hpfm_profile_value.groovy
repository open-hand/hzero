package script.db

databaseChangeLog(logicalFilePath: 'script/db/hpfm_profile_value.groovy') {
    changeSet(author: "hzero@hand-china.com", id: "2019-03-01-hpfm_profile_value") {
        def weight = 1
        if(helper.isSqlServer()){
            weight = 2
        } else if(helper.isOracle()){
            weight = 3
        }
        if(helper.dbType().isSupportSequence()){
            createSequence(sequenceName: 'hpfm_profile_value_s', startValue:"1")
        }
        createTable(tableName: "hpfm_profile_value", remarks: "") {
            column(name: "profile_value_id", type: "bigint", autoIncrement: true ,   remarks: "")  {constraints(primaryKey: true)}
            column(name: "profile_id", type: "bigint",  remarks: "配置ID")  {constraints(nullable:"false")}
            column(name: "level_code", type: "varchar(" + 30 * weight + ")",  remarks: "应用层级")  {constraints(nullable:"false")}
            column(name: "level_value", type: "varchar(" + 30 * weight + ")",  remarks: "层级值")
            column(name: "value", type: "varchar(" + 30 * weight + ")",  remarks: "配置值")
            column(name: "object_version_number", type: "bigint",   defaultValue:"1",   remarks: "")
            column(name: "created_by", type: "bigint",   defaultValue:"0",   remarks: "")
            column(name: "creation_date", type: "datetime",   defaultValueComputed:"CURRENT_TIMESTAMP",   remarks: "")
            column(name: "last_updated_by", type: "bigint",   defaultValue:"0",   remarks: "")
            column(name: "last_update_date", type: "datetime",   defaultValueComputed:"CURRENT_TIMESTAMP",   remarks: "")

        }
        createIndex(tableName: "hpfm_profile_value", indexName: "hpfm_profile_value_n1") {
            column(name: "profile_id")
        }

    }
    changeSet(author: "hzero@hand-china.com", id: "2019-10-30-hpfm_profile_value") {
        def weight = 1
        if(helper.isSqlServer()){
            weight = 2
        } else if(helper.isOracle()){
            weight = 3
        }
        modifyDataType(tableName: "hpfm_profile_value", columnName: 'value', newDataType: "varchar(" + 480 * weight + ")")
    }
    changeSet(author: "hzero@hand-china.com", id: "2020-06-11-hpfm_profile_value") {
        addColumn(tableName: 'hpfm_profile_value') {
            column(name: "tenant_id", type: "bigint", defaultValue: "0", remarks: "租户ID,hpfm_tenant.tenant_id") {
                constraints(nullable: "false")
            }
        }
    }
}