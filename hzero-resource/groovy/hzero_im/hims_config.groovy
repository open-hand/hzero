package script.db

databaseChangeLog(logicalFilePath: 'script/db/hims_config.groovy') {
    changeSet(author: "hzero@hand-china.com", id: "2020-02-14-hims_config") {
        def weight = 1
        if(helper.isSqlServer()){
            weight = 2
        } else if(helper.isOracle()){
            weight = 3
        }
        if(helper.dbType().isSupportSequence()){
            createSequence(sequenceName: 'hims_config_s', startValue:"1")
        }
        createTable(tableName: "hims_config", remarks: "即时通讯配置表") {
            column(name: "config_id", type: "bigint", autoIncrement: true ,   remarks: "表ID，主键")  {constraints(primaryKey: true)}
            column(name: "tenant_id", type: "bigint",   defaultValue:"0",   remarks: "租户id，hpfm_tenant.tenant_id")  {constraints(nullable:"false")}
            column(name: "config_code", type: "varchar(" + 30 * weight + ")",  remarks: "配置编码")  {constraints(nullable:"false")}
            column(name: "config_value", type: "varchar(" + 240 * weight + ")",  remarks: "配置值")  {constraints(nullable:"false")}
            column(name: "object_version_number", type: "bigint",   defaultValue:"1",   remarks: "")  {constraints(nullable:"false")}
            column(name: "created_by", type: "bigint",   defaultValue:"-1",   remarks: "")  {constraints(nullable:"false")}
            column(name: "creation_date", type: "datetime",   defaultValueComputed:"CURRENT_TIMESTAMP",   remarks: "")  {constraints(nullable:"false")}
            column(name: "last_updated_by", type: "bigint",   defaultValue:"-1",   remarks: "")  {constraints(nullable:"false")}
            column(name: "last_update_date", type: "datetime",   defaultValueComputed:"CURRENT_TIMESTAMP",   remarks: "")  {constraints(nullable:"false")}

        }
        addUniqueConstraint(columnNames:"config_code,tenant_id",tableName:"hims_config",constraintName: "hims_config_u1")
    }
}