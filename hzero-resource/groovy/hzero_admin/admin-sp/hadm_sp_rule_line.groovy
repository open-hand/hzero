package script.db

databaseChangeLog(logicalFilePath: 'script/db/hadm_sp_rule_line.groovy') {
    changeSet(author: "hzero@hand-china.com", id: "2020-05-25-hadm_sp_rule_line") {
        def weight = 1
        if(helper.isSqlServer()){
            weight = 2
        } else if(helper.isOracle()){
            weight = 3
        }
        if(helper.dbType().isSupportSequence()){
            createSequence(sequenceName: 'hadm_sp_rule_line_s', startValue:"1")
        }
        createTable(tableName: "hadm_sp_rule_line", remarks: "Sharding Sphere分库分表配置行") {
            column(name: "rule_line_id", type: "bigint", autoIncrement: true ,   remarks: "表ID，主键，供其他表做外键")  {constraints(primaryKey: true)}
            column(name: "rule_header_id", type: "bigint",  remarks: "服务数据源ID,hadm_sp_rule_header.rule_header_id")  {constraints(nullable:"false")}
            column(name: "table_name", type: "varchar(" + 30 * weight + ")",  remarks: "表名")  {constraints(nullable:"false")}
            column(name: "actual_data_nodes", type: "varchar(" + 60 * weight + ")",  remarks: "实际数据节点")  {constraints(nullable:"true")}
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
        addUniqueConstraint(columnNames:"rule_header_id,table_name",tableName:"hadm_sp_rule_line",constraintName: "hadm_sp_rule_line_u1")
    }
}