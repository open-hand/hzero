package script.db

databaseChangeLog(logicalFilePath: 'script/db/hadm_sp_rule_header.groovy') {
    changeSet(author: "hzero@hand-china.com", id: "2020-05-25-hadm_sp_rule_header") {
        def weight = 1
        if(helper.isSqlServer()){
            weight = 2
        } else if(helper.isOracle()){
            weight = 3
        }
        if(helper.dbType().isSupportSequence()){
            createSequence(sequenceName: 'hadm_sp_rule_header_s', startValue:"1")
        }
        createTable(tableName: "hadm_sp_rule_header", remarks: "Sharding Sphere分库分表配置头") {
            column(name: "rule_header_id", type: "bigint", autoIncrement: true ,   remarks: "表ID，主键，供其他表做外键")  {constraints(primaryKey: true)}
            column(name: "datasource_group_id", type: "bigint",  remarks: "逻辑数据源组ID,hadm_sp_datasource_group.datasource_group_id")  {constraints(nullable:"false")}
            column(name: "proxy_schema_name", type: "varchar(" + 60 * weight + ")",  remarks: "代理数据源模式")  {constraints(nullable:"false")}
            column(name: "binding_tables", type: "longtext",  remarks: "表的绑定关系，头行表关系维护，数组形式，以;分割。")  {constraints(nullable:"true")}
            column(name: "broadcast_tables", type: "longtext",  remarks: "广播表列表，数组形式，以,分割")  {constraints(nullable:"true")}
            column(name: "db_sharding_exp", type: "varchar(" + 240 * weight + ")",  remarks: "分库表达式")  {constraints(nullable:"true")}
            column(name: "db_sharding_template", type: "varchar(" + 240 * weight + ")",  remarks: "分库表达式模版")  {constraints(nullable:"true")}
            column(name: "table_sharding_exp", type: "varchar(" + 240 * weight + ")",  remarks: "分表表达式")  {constraints(nullable:"true")}
            column(name: "table_sharding_template", type: "varchar(" + 240 * weight + ")",  remarks: "分表表达式模版")  {constraints(nullable:"true")}
            column(name: "key_generator_exp", type: "varchar(" + 240 * weight + ")",  remarks: "ID生成器表达式")  {constraints(nullable:"true")}
            column(name: "object_version_number", type: "bigint",   defaultValue:"1",   remarks: "行版本号，用来处理锁")  {constraints(nullable:"false")}
            column(name: "creation_date", type: "datetime",   defaultValueComputed:"CURRENT_TIMESTAMP",   remarks: "")  {constraints(nullable:"false")}
            column(name: "created_by", type: "bigint",   defaultValue:"-1",   remarks: "")  {constraints(nullable:"false")}
            column(name: "last_updated_by", type: "bigint",   defaultValue:"-1",   remarks: "")  {constraints(nullable:"false")}
            column(name: "last_update_date", type: "datetime",   defaultValueComputed:"CURRENT_TIMESTAMP",   remarks: "")  {constraints(nullable:"false")}
        }
    }
}